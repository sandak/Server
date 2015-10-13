package connectionsManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import controller.Controller;
import controller.Properties;

public class MyConnectionsManager extends CommonConnectionsManager{

	ServerSocket gameServer;
	ServerSocket mgmtServer;
	CommonClientHandler clinetHandler;
	CommonClientHandler mgmtHandler;
	ExecutorService threadPool;
	ExecutorService mgmtClientsThreadPool;
	volatile HashMap<String, Socket> clientsMap;
	volatile HashMap<String, Socket> adminsMap;
	volatile boolean gameServerStop;
	volatile boolean serverStop;
	protected Properties properties;
	
	Thread mainServerThread;
	Thread mgmtServerThread;
	
	public MyConnectionsManager(CommonClientHandler clinetHandler,CommonClientHandler mgmtHandler) {
		this.properties=new Properties();
		properties.setDefaults();
		this.clinetHandler=clinetHandler;
		this.mgmtHandler = mgmtHandler;
		clientsMap = new HashMap<String, Socket>();
		adminsMap = new HashMap<String, Socket>();

		mgmtStart();
	}
	
	private void mgmtStart() {	
		serverStop = false;
		try {
			mgmtServer=new ServerSocket(properties.getManagmentPort());
			mgmtServer.setSoTimeout(properties.getTimeOut());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mgmtClientsThreadPool=Executors.newFixedThreadPool(properties.getMaxClients());

		mgmtServerThread=new Thread(new Runnable() {		
			int adminsHandled = 0;	
			@Override
			public void run() {
				System.out.println("managment port is open");
				while(!serverStop){
					try {
						final Socket someClient=mgmtServer.accept();
						if(someClient!=null){
							mgmtClientsThreadPool.execute(new Runnable() {									
								@Override
								public void run() {
									try{
									adminsHandled++;
									System.out.println("\tadmin is connected "+adminsHandled);
									adminsMap.put(someClient.getInetAddress().getHostAddress(), someClient);
									InputStream in = someClient.getInputStream();
									OutputStream out = someClient.getOutputStream();
									mgmtHandler.handleClient(in,out,someClient.getInetAddress().getHostAddress());		
									}catch (IOException e)
									{}
								}
							});								
						}
					}
					catch (SocketTimeoutException e){
						System.out.println("no clinet connected...");
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("done accepting new clients.");
			} // end of the mainServerThread task
		});
		
		mgmtServerThread.start();
		
	}

	@Override
	public void gameServerStart(){
		try {
			gameServer=new ServerSocket(properties.getClientPort());
			gameServer.setSoTimeout(properties.getTimeOut());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		threadPool=Executors.newFixedThreadPool(properties.getMaxClients());
	
		mainServerThread=new Thread(new Runnable() {		
			int clientsHandled = 0;	
			@Override
			public void run() {
				while(!gameServerStop){
					try {
						final Socket someClient=gameServer.accept();
						if(someClient!=null){
							threadPool.execute(new Runnable() {									
								@Override
								public void run() {
									try{
									clientsHandled++;
									System.out.println("\thandling client "+clientsHandled);
									clientsMap.put(someClient.getInetAddress().getHostAddress(), someClient);
									InputStream in = someClient.getInputStream();
									OutputStream out = someClient.getOutputStream();
									clinetHandler.handleClient(in,out,someClient.getInetAddress().getHostAddress());		
									}catch (IOException e)
									{}
								}
							});								
						}
					}
					catch (SocketTimeoutException e){
						System.out.println("no clinet connected...");
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("done accepting new clients.");
			} // end of the mainServerThread task
		});
		
		mainServerThread.start();
		
	}
	@Override
	public void gameServerStop(){	
		try {
		gameServerStop=true;	
		// do not execute jobs in queue, continue to execute running threads
		System.out.println("shutting down");
		threadPool.shutdown();
		// wait 10 seconds over and over again until all running jobs have finished
		boolean allTasksCompleted=false;
	
			while(!(allTasksCompleted=threadPool.awaitTermination(10, TimeUnit.SECONDS)));
	
		
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
			serverStop=true;	
			// do not execute jobs in queue, continue to execute running threads
			System.out.println("shutting admin side down");
			mgmtClientsThreadPool.shutdown();
			// wait 10 seconds over and over again until all running jobs have finished
			boolean allTasksCompleted=false;
		
				while(!(allTasksCompleted=mgmtClientsThreadPool.awaitTermination(10, TimeUnit.SECONDS)));
		
			
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
		this.clinetHandler.setController(controller);
		this.mgmtHandler.setController(controller);
		
	}

	public boolean getGameServerStatus() {
		return !gameServerStop;
	}




	


}
