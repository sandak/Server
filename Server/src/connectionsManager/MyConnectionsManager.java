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
	ExecutorService threadPool;
	ExecutorService mgmtClientsThreadPool;
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
									syncAdmins();
									clientHandler.handleClient(someClient);
									clientsMap.remove(someClient.getInetAddress().getHostAddress());
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

	private ArrayList<String[]> getClientsList() {
		ArrayList<String[]> list =new ArrayList<String[]>() ;
		for (String string : clientsMap.keySet()) 
			list.add(new String[]{string,clientsMap.get(string).getInetAddress().getHostAddress(),clientsMap.get(string).getInetAddress().getHostName()});
		return list;
	}
	
	private void syncAdmins() {
		mgmtClientsThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				for (String string : registeredAdmins) {
					try {
						Socket theAdmin = new Socket(string, properties.getUpdatePort());
						if (properties.isDebug())
							System.out.println("connected to server!");
						updateClientsStatusProtocol(theAdmin.getInputStream(),theAdmin.getOutputStream());
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

				}

		private void updateClientsStatusProtocol(InputStream inFromClient, OutputStream outToClient) {
			try {
			BufferedReader in=new BufferedReader(new InputStreamReader(inFromClient));
			PrintWriter out=new PrintWriter(outToClient);
			out.println("clients push");
			out.flush();
			in.readLine();//ready
			ArrayList<String[]> list = getClientsList();
			for (String[] strings : list) {
				for (String string : strings) {
					System.out.print(string +" ");
				}
				System.out.println(" ");
			}
			for (String[] strings : list) {
				out.println(strings.length);
				System.out.println(strings.length);
				for (String string : strings) {
					out.println(string);
				}
				out.println("client end");
			}
			out.println("list end");
			out.flush();
			in.readLine();//done
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				
			}
		});	
		
	}

	@Override
	public void gameServerStop() {
		try {
			gameServerStop = true;
			// do not execute jobs in queue, continue to execute running threads
			System.out.println("shutting down");
			threadPool.shutdown();
			// wait 10 seconds over and over again until all running jobs have
			// finished
			boolean allTasksCompleted = false;

			while (!(allTasksCompleted = threadPool.awaitTermination(10, TimeUnit.SECONDS)))
				

			System.out.println("all the tasks have finished");

			mainServerThread.join();
			System.out.println("main server thread is done");

			gameServer.close();
			System.out.println("game session is safely closed");
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

			while (!(allTasksCompleted = mgmtClientsThreadPool.awaitTermination(10, TimeUnit.SECONDS)))
				

			System.out.println("all the admin tasks have finished");

			mgmtServerThread.join();
			System.out.println("mgmt server thread is done");

			mgmtServer.close();
			System.out.println("server is safely closed");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		
	}

}
