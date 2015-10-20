package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import controller.Controller;
import controller.Properties;

/**
 * the connection manager is in charge of the whole server connection, the game
 * client's and the admins alike. it has 2 different ServerSockets for each
 * client type and two different client handlers for each one. contains a hash
 * map and a list of the currently connected clients and admins.
 * 
 * @author Guy Golan && Amit Sandak
 *
 */
public class MyConnectionsManager extends CommonConnectionsManager {

	ServerSocket gameServer;
	ServerSocket mgmtServer;
	CommonClientHandler clientHandler;
	CommonClientHandler mgmtHandler;
	volatile ExecutorService threadPool;
	volatile ExecutorService mgmtClientsThreadPool;
	volatile HashMap<String, Socket> clientsMap;
	volatile ArrayList<String> registeredAdmins;
	volatile boolean gameServerStop;
	volatile boolean serverStop;
	protected Properties properties;
	Thread mainServerThread;
	Thread mgmtServerThread;

	public MyConnectionsManager(CommonClientHandler clientHandler, CommonClientHandler mgmtHandler) {
		this.properties = new Properties();
		properties.setDefaults();
		this.clientHandler = clientHandler;
		this.mgmtHandler = mgmtHandler;
		clientsMap = new HashMap<String, Socket>();
		registeredAdmins = new ArrayList<String>();

	}

	/**
	 * starting the admin client's thread listening process.
	 */
	public void mgmtStart() {
		serverStop = false;
		try {
			mgmtServer = new ServerSocket(properties.getManagmentPort());
			mgmtServer.setSoTimeout(properties.getTimeOut());
		} catch (IOException e1) {
			if (this.controller.getProperties().isDebug())
				e1.printStackTrace();
		}
		mgmtClientsThreadPool = Executors.newFixedThreadPool(properties.getMaxClients()); //setting the max admins possible.

		
		mgmtServerThread = new Thread(new Runnable() {
			

			@Override
			public void run() {
				if(controller.getProperties().isDebug())
					System.out.println("managment port is open");
				while (!serverStop) {
					try {
						final Socket someAdmin = mgmtServer.accept();	//accepting a new admin
						if (someAdmin != null) {
							mgmtClientsThreadPool.execute(new Runnable() {	//handling the admin client in a new thread in the threadpool.
								@Override
								public void run() {
									try {
										
										if(controller.getProperties().isDebug())
											System.out.println("\tadmin is connected");
										mgmtHandler.handleClient(someAdmin);	//handling the admin client in an outsourced policy.
										someAdmin.close(); //done handling the admin client, closing it's socket.
									} catch (IOException e) {
										if(controller.getProperties().isDebug())
											e.printStackTrace();
									}

								}
							});
						}
					} catch (SocketTimeoutException e) {
						if(controller.getProperties().isDebug())
							System.out.println("no admins connected...");
					} catch (IOException e) {
						if(controller.getProperties().isDebug())
							e.printStackTrace();
					}
				}
				if(controller.getProperties().isDebug())
					System.out.println("done accepting new admins.");
			} // end of the mainServerThread task
		});

		mgmtServerThread.start();

	}

	@Override
	public void gameServerStart() {
		syncAdmins("log:game service started."); //pushing an event to all admins.
		gameServerStop = false;
		syncAdmins("status");  //pushing an update of the newly online service to all admins.
		try {
			gameServer = new ServerSocket(properties.getClientPort());
			gameServer.setSoTimeout(properties.getTimeOut());
		} catch (IOException e1) {
			if(controller.getProperties().isDebug())
				e1.printStackTrace();
		}
		threadPool = Executors.newFixedThreadPool(properties.getMaxClients()); //setting the max gamer clients.

		mainServerThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!gameServerStop) {
					try {
						final Socket someClient = gameServer.accept(); //accepting a new gamer client.
						if (someClient != null) {
							threadPool.execute(new Runnable() {
								@Override
								public void run() {
									try {
										clientsMap.put(someClient.getInetAddress().getHostAddress(), someClient); //inserting the new client to the list
										syncAdmins("clients"); //pushing the client list to the admins.
										syncAdmins("log:new client connected - "+ someClient.getInetAddress().getHostAddress()); // pushing a log entry to all admins about the new client.
										clientHandler.handleClient(someClient);  //handling the game client with an outsourced policy.
										clientsMap.remove(someClient.getInetAddress().getHostAddress()); //removing the client from the list after handling him.
										syncAdmins("clients");	//pushing the client list update to all admins.
										syncAdmins("log:client disconnected - " + someClient.getInetAddress().getHostAddress()); //pushing the log entry about the client disconnection.
										someClient.close(); //closing the socket
									} catch (IOException e) {
										if(controller.getProperties().isDebug())
											e.printStackTrace();
									}
								}

							});
						}
					} catch (SocketTimeoutException e) {
						if(controller.getProperties().isDebug())
							System.out.println("no client connected...");
					} catch (IOException e) {
						if(controller.getProperties().isDebug())
							e.printStackTrace();
					}
				}
				if(controller.getProperties().isDebug())
					System.out.println("done accepting new clients.");
			} // end of the mainServerThread task
		});

		mainServerThread.start();

	}

	/**
	 * invoked from the different client handlers to receive the current client list.
	 * @return list - containing information about each connected client.
	 */
	public ArrayList<String[]> getClientsList() {
		ArrayList<String[]> list = new ArrayList<String[]>();
		for (String string : clientsMap.keySet())
			list.add(new String[] { string, clientsMap.get(string).getInetAddress().getHostAddress(),
					clientsMap.get(string).getInetAddress().getHostName() });
		return list;
	}

	/**
	 * invoked from different methods in order to update a single admin about an event.
	 * @param param - what it the update nature (log update, status update, clientlist update...).
	 * @param hostAddress - the ip of the desired admin to update.
	 */
	public void syncAdmin(String param, String hostAddress) {
		try {
			Socket theAdmin = new Socket(hostAddress, properties.getUpdatePort()); //creating a connection to the desired admin.
			if (properties.isDebug())
				System.out.println("connected to admin!");
			switch (param.split(":")[0]) {
			case "clients":			//client list push update.
				((ManagmentHandler) mgmtHandler).updateClientsStatusProtocol(theAdmin.getInputStream(),
						theAdmin.getOutputStream());
				break;
			case "log":				//log event push update.
				((ManagmentHandler) mgmtHandler).updateLogProtocol(param.split(":")[1], theAdmin.getInputStream(),
						theAdmin.getOutputStream());
				break;
			case "status":			//service status push update.
				((ManagmentHandler) mgmtHandler).updateStatusProtocol(theAdmin.getInputStream(),
						theAdmin.getOutputStream());
				break;
			case "shutting down":	//server shutting down push update.
				((ManagmentHandler) mgmtHandler).updateShutdownProtocol(theAdmin.getInputStream(),
						theAdmin.getOutputStream());
				break;
			}

			BufferedReader inFromAdmin = new BufferedReader(new InputStreamReader(theAdmin.getInputStream()));
			PrintWriter outToAdmin = new PrintWriter(theAdmin.getOutputStream());
			outToAdmin.println("exit"); //to finish the protocol on the admin side.
			if(controller.getProperties().isDebug())
				System.out.println("exit in server side");
			outToAdmin.flush();
			inFromAdmin.close();
			outToAdmin.close();
			theAdmin.close();

		} catch (IOException e) {
			if(controller.getProperties().isDebug())
				e.printStackTrace();
		}

	}

	/**
	 * invoking a single syncadmin foreach registered admin.
	 * @param param - what it the update nature (log update, status update, clientlist update...).
	 */
	public void syncAdmins(String param) {
		try {
			mgmtClientsThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					for (String string : registeredAdmins) {
						syncAdmin(param, string);
					}

				}
			});
		} catch (RejectedExecutionException e) {
			if(controller.getProperties().isDebug())
				e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void gameServerStop() {
		try {
			gameServerStop = true;
			syncAdmins("status"); //Synchronizing the admins of the newly stopped game service.
			
			syncAdmins("log:shutting down game service"); //synchronizing the admins about the shutdown process.
			threadPool.shutdown(); 
			// wait 10 seconds over and over again until all running jobs have finished
			boolean allTasksCompleted = false;

			while (!(allTasksCompleted = threadPool.awaitTermination(10, TimeUnit.SECONDS)))
				;

			syncAdmins("log:all the tasks have finished");

			mainServerThread.join();
			syncAdmins("log:game service thread is done");

			gameServer.close();
			syncAdmins("log:game session is safely closed");
		} catch (InterruptedException e) {
			if(controller.getProperties().isDebug())
				e.printStackTrace();
		} catch (IOException e) {
			if(controller.getProperties().isDebug())
				e.printStackTrace();
		}
	}

	/**
	 * invoked when the server is shutting down.
	 * and stops the admin service and game service.
	 */
	public void exit() {
		gameServerStop();
		mgmtServerStop();
	}

	/**
	 * Stops the management client's service.
	 */
	private void mgmtServerStop() {
		try {
			serverStop = true;
			// do not execute jobs in queue, continue to execute running threads
			if(controller.getProperties().isDebug())
				System.out.println("shutting admin side down");
			mgmtClientsThreadPool.shutdown();
			// wait 10 seconds over and over again until all running jobs have
			// finished
			@SuppressWarnings("unused")
			boolean allTasksCompleted = false;

			while (!(allTasksCompleted = mgmtClientsThreadPool.awaitTermination(5, TimeUnit.SECONDS)))
				;

			if(controller.getProperties().isDebug())
				System.out.println("all the tasks have finished");
			if(controller.getProperties().isDebug())
				System.out.println("all the admin tasks have finished");

			mgmtServerThread.join();
			if(controller.getProperties().isDebug())
				System.out.println("mgmt server thread is done");

			mgmtServer.close();
			if(controller.getProperties().isDebug())
				System.out.println("server is safely closed");
		} catch (InterruptedException e) {
			if(controller.getProperties().isDebug())
				e.printStackTrace();
			
		} catch (IOException e) {
			if(controller.getProperties().isDebug())
				e.printStackTrace();
		}

	}

	public void setProperties(Properties prop) {
		this.properties = prop;

	}

	/**
	 * setting the controller to the client handlers aswell.
	 */
	@Override
	public void setController(Controller controller) {
		this.controller = controller;
		this.clientHandler.setController(controller);
		this.mgmtHandler.setController(controller);

	}

	
	public boolean getGameServerStatus() {
		return !gameServerStop;
	}

	/**
	 * registering a new admin into the system.
	 * @param hostAddress - the ip of the admin.
	 */
	public void register(String hostAddress) {
		registeredAdmins.add(hostAddress);
		syncAdmins("log:new admin registerd - " + hostAddress); //update push of the newly registered admin.
		syncAdmin("clients", hostAddress); //push update for the new admin about the current clients list.

	}

	/**
	 * unregistering an admin from the server.
	 * @param hostAddress - the ip of the unregistering admin.
	 */
	public void unregister(String hostAddress) {
		registeredAdmins.remove(hostAddress);
		syncAdmins("log:admin unregisterd - " + hostAddress); // update push for every admin about the admin that has left.

	}

	@Override
	public void kickClients(String[] list) {

		for (String string : list) {
			try {
				if(controller.getProperties().isDebug())
					System.out.println(string);
				clientsMap.get(string).close();
				syncAdmins("log:client kicked - " + string);
			} catch (IOException e) {
				if(controller.getProperties().isDebug())
					e.printStackTrace();
			}
		}

	}

}
