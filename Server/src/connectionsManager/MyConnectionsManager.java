package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hibernate.mapping.Set;

import controller.Controller;
import controller.Properties;

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

		mgmtStart();
	}

	private void mgmtStart() {
		serverStop = false;
		try {
			mgmtServer = new ServerSocket(properties.getManagmentPort());
			mgmtServer.setSoTimeout(properties.getTimeOut());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mgmtClientsThreadPool = Executors.newFixedThreadPool(properties.getMaxClients()); // change
																							// to
																							// properties.getmaxadmins

		mgmtServerThread = new Thread(new Runnable() {
			int adminsHandled = 0;

			@Override
			public void run() {
				System.out.println("managment port is open");
				while (!serverStop) {
					try {
						final Socket someAdmin = mgmtServer.accept();
						if (someAdmin != null) {
							mgmtClientsThreadPool.execute(new Runnable() {
								@Override
								public void run() {
									adminsHandled++;
									System.out.println("\tadmin is connected " + adminsHandled);
									mgmtHandler.handleClient(someAdmin);

								}
							});
						}
					} catch (SocketTimeoutException e) {
						System.out.println("no admins connected...");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("done accepting new admins.");
			} // end of the mainServerThread task
		});

		mgmtServerThread.start();

	}

	@Override
	public void gameServerStart() {
		syncAdmins("log:game service started.");
		gameServerStop = false;
		try {
			gameServer = new ServerSocket(properties.getClientPort());
			gameServer.setSoTimeout(properties.getTimeOut());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		threadPool = Executors.newFixedThreadPool(properties.getMaxClients());

		mainServerThread = new Thread(new Runnable() {
			int clientsHandled = 0;

			@Override
			public void run() {
				while (!gameServerStop) {
					try {
						final Socket someClient = gameServer.accept();
						if (someClient != null) {
							threadPool.execute(new Runnable() {
								@Override
								public void run() {
									clientsHandled++;
									System.out.println("\thandling client " + clientsHandled);
									clientsMap.put(someClient.getInetAddress().getHostAddress(), someClient);
									syncAdmins("clients");
									syncAdmins("log:new client connected - " + someClient.getInetAddress().getHostAddress());
									clientHandler.handleClient(someClient);
									clientsMap.remove(someClient.getInetAddress().getHostAddress()); 
									syncAdmins("clients");
									syncAdmins("log:client disconnected - "+someClient.getInetAddress().getHostAddress());
								}

							});
						}
					} catch (SocketTimeoutException e) {
						System.out.println("no client connected...");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("done accepting new clients.");
			} // end of the mainServerThread task
		});

		mainServerThread.start();

	}

	public ArrayList<String[]> getClientsList() {
		ArrayList<String[]> list =new ArrayList<String[]>() ;
		for (String string : clientsMap.keySet()) 
			list.add(new String[]{string,clientsMap.get(string).getInetAddress().getHostAddress(),clientsMap.get(string).getInetAddress().getHostName()});
		return list;
	}
	
	public void syncAdmin(String param, String hostAddress) {
		try {
			Socket theAdmin = new Socket(hostAddress, properties.getUpdatePort());
			if (properties.isDebug())
					System.out.println("connected to admin!");
			switch (param.split(":")[0])
			{
			case "clients":
				((ManagmentHandler)mgmtHandler).updateClientsStatusProtocol(theAdmin.getInputStream(),theAdmin.getOutputStream());
				break;
			case "log":
				((ManagmentHandler)mgmtHandler).updateLogProtocol(param.split(":")[1],theAdmin.getInputStream(),theAdmin.getOutputStream());
				break;
			case "status":
				((ManagmentHandler)mgmtHandler).updateStatusProtocol(theAdmin.getInputStream(),theAdmin.getOutputStream());
				break;
			case "shutting down":
				((ManagmentHandler)mgmtHandler).updateShutdownProtocol(theAdmin.getInputStream(),theAdmin.getOutputStream());
				break;
			}
				
				BufferedReader in=new BufferedReader(new InputStreamReader(theAdmin.getInputStream()));
				PrintWriter out=new PrintWriter(theAdmin.getOutputStream());
				out.println("exit");
				out.flush();
				in.close();
				out.close();
				theAdmin.close();
				
			
		} catch (IOException e) {
			// do nothing
		}
		
	}
	
	
	public void syncAdmins(String param) {
		mgmtClientsThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String string : registeredAdmins) {
				syncAdmin(param,string);
				}

				}
		});
	}
		

	@Override
	public void gameServerStop() {
		try {
			gameServerStop = true;
			// do not execute jobs in queue, continue to execute running threads
			//System.out.println("shutting down");
			syncAdmins("log:shutting down game service");
			threadPool.shutdown();
			// wait 10 seconds over and over again until all running jobs have
			// finished
			boolean allTasksCompleted = false;

			while (!(allTasksCompleted = threadPool.awaitTermination(10, TimeUnit.SECONDS)));
				

			//System.out.println("all the tasks have finished");
			syncAdmins("log:all the tasks have finished");

			mainServerThread.join();
			//System.out.println("main server thread is done");
			syncAdmins("log:game service thread is done");

			gameServer.close();
			//System.out.println("game session is safely closed");
			syncAdmins("log:game session is safely closed");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void exit() {
		gameServerStop();
		mgmtServerStop();
	}

	private void mgmtServerStop() {
		try {
			serverStop = true;
			// do not execute jobs in queue, continue to execute running threads
			System.out.println("shutting admin side down");
			mgmtClientsThreadPool.shutdown();
			// wait 10 seconds over and over again until all running jobs have
			// finished
			boolean allTasksCompleted = false;

			while (!(allTasksCompleted = mgmtClientsThreadPool.awaitTermination(5, TimeUnit.SECONDS)));
				

			System.out.println("all the admin tasks have finished");

			mgmtServerThread.join();
			System.out.println("mgmt server thread is done");

			mgmtServer.close();
			System.out.println("server is safely closed");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setProperties(Properties prop) {
		this.properties = prop;

	}

	@Override
	public void setController(Controller controller) {
		this.controller = controller;
		this.clientHandler.setController(controller);
		this.mgmtHandler.setController(controller);

	}

	public boolean getGameServerStatus() {
		return !gameServerStop;
	}

	public void register(String hostAddress) {
		registeredAdmins.add(hostAddress);
		syncAdmins("log:new admin registerd - "+hostAddress);
		syncAdmin("clients",hostAddress);
		
	}

	public void unregister(String hostAddress) {
		registeredAdmins.remove(hostAddress);
		syncAdmins("log:admin unregisterd - "+hostAddress);
		
	}
@Override
	public void kickClients(String[] list) {
	
		for (String string : list) {
			try {
				System.out.println(string);
				clientsMap.get(string).close();
				syncAdmins("log:client kicked - " + string);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}



}
