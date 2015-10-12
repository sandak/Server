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
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import algorithms.mazeGenerators.Maze3d;
import controller.Controller;
import controller.Properties;
import io.MyCompressorOutputStream;

public class MyConnectionsManager extends CommonConnectionsManager{

	ServerSocket server;
	ClientHandler clinetHandler;
	ExecutorService threadpool;
	volatile HashMap<String, Socket> clientsMap;
	volatile boolean stop;
	protected Properties properties;
	
	Thread mainServerThread;
	
	public MyConnectionsManager(ClientHandler clinetHandler) {
		this.properties=new Properties();
		properties.setDefaults();
		this.clinetHandler=clinetHandler;
		clientsMap = new HashMap<String, Socket>();
	}
	
	
	public void start() throws Exception{
		server=new ServerSocket(properties.getClientPort());
		server.setSoTimeout(properties.getTimeOut());
		threadpool=Executors.newFixedThreadPool(properties.getMaxClients());
	
		mainServerThread=new Thread(new Runnable() {		
			int clientsHandled = 0;	
			@Override
			public void run() {
				while(!stop){
					try {
						final Socket someClient=server.accept();
						if(someClient!=null){
							threadpool.execute(new Runnable() {									
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
	
	public void close(){	
		try {
		stop=true;	
		// do not execute jobs in queue, continue to execute running threads
		System.out.println("shutting down");
		threadpool.shutdown();
		// wait 10 seconds over and over again until all running jobs have finished
		boolean allTasksCompleted=false;
	
			while(!(allTasksCompleted=threadpool.awaitTermination(10, TimeUnit.SECONDS)));
	
		
		System.out.println("all the tasks have finished");

		mainServerThread.join();		
		System.out.println("main server thread is done");
		
		server.close();
		System.out.println("server is safely closed");	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	



	public void generateClientHandler(BufferedReader in, PrintWriter out, String clientId) {	
		try {
		String parse, name;
		int x,y,z;
		out.println("what is the maze name?");
		out.flush();
		parse =in.readLine();
		name = parse.split(": ")[1]	;
		System.out.println(name);
		//////////////////
		out.println("what is the Axis x dimension?");
		out.flush();
		parse = in.readLine();
		System.out.println(parse);
		x = Integer.parseInt(parse.split(": ")[1]);
		///////////////////
		out.println("what is the Axis y dimension?");
		out.flush();
		parse = in.readLine();
		y = Integer.parseInt(parse.split(": ")[1]);
		//////////////////
		out.println("what is the Axis z dimension?");
		out.flush();
		parse = in.readLine();
		z = Integer.parseInt(parse.split(": ")[1]);
		///////////
		controller.update("generate 3d maze " + name + " " +x +" " + y + " " + z+ " " + clientId);
		out.println("ok");
		System.out.println("ok");
		out.flush();
	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}

	public void exit() {close();}


	public void setProperties(Properties prop) {
		this.properties = prop;
		
	}


	public void getMazeClientHandler(BufferedReader in, PrintWriter out) {
		try {
			String name;
			out.println("what is the maze name?");
		out.flush();
			name = in.readLine().split(": ")[1];
			System.out.println(name);
			Maze3d maze = controller.getMaze(name);
			if (maze == null)
				System.out.println("maze error");
			else
				{out.println("sending");
			out.flush();
		byte [] buffer = maze.toByteArray();
		//out.print(buffer);
		
		for (byte b : buffer) 
			out.write((int)b);		
		out.write(127);
		out.flush();
		}} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public void setController(Controller controller) {
		this.controller = controller;
		this.clinetHandler.setController(controller);
		
	}


	public void complete(String string) {
		System.out.println("out");
		Socket s = clientsMap.get(string);
		PrintWriter out;
		try {
			out = new PrintWriter(s.getOutputStream());
			out.println("ok");
		out.flush();
		out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	


}
