package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The type handling a manager client.
 * Defines the protocol. 
 * @author Guy Golan && Amit Sandak
 *
 */
public class ManagmentHandler extends CommonClientHandler{
	
	public ManagmentHandler() {
	}
	
	@Override
	public void handleClient(Socket socket) {
		try{
			
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter outToClient=new PrintWriter(socket.getOutputStream());			//extracting IO from the socket.
			String line;
			
			//runs until the client send exit.
			while(!(line=inFromClient.readLine()).endsWith("exit")){
				
					if (line.contains("get status") )		//variety of protocol options for the manager to invoke. 
						getStatus(inFromClient,outToClient);
					if (line.contains("get data") )
						getData(socket);
					if (line.contains("start server") )
						serverStart(inFromClient,outToClient);
					if (line.contains("stop server") )
						serverStop(inFromClient,outToClient);
					if (line.contains("unregister") )
						unregister(socket);
					else if (line.contains("register") )
						register(socket);
					if (line.contains("kick request") )
						kickRequest(socket);
					if (line.contains("shutdown") )
						shutdown(socket);
				}	
			inFromClient.close();
			outToClient.close();			
			
		}catch(IOException e){
			if(controller.getProperties().isDebug())
				e.printStackTrace();
		}
	}

	/**
	 * the method that handle the server shutdown protocol.
	 * @param socket - the admin client's socket.
	 */
	private void shutdown(Socket socket) {
		try{
			@SuppressWarnings("unused")
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter outToClient=new PrintWriter(socket.getOutputStream());
			outToClient.println("ok");
			outToClient.flush();
			controller.exit();  //invoking an ordered server shutdown
		
			
			
			}catch (IOException e)
			{
				if(controller.getProperties().isDebug())
					e.printStackTrace();
			}			
		
	}

	/**
	 * the method that handle the get data protocol.
	 * invoked by the manager client in order to receive an update from the server about the game
	 * service status (active or not) and also about the current active game clients.
	 * @param socket - the admin client's socket
	 */
	private void getData(Socket socket) {
		
		
		try{
			@SuppressWarnings("unused")
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter outToClient=new PrintWriter(socket.getOutputStream());
			outToClient.println("ok");
			outToClient.flush();
			controller.syncAdmin("clients",socket.getInetAddress().getHostAddress()); //syncing the admin of the server current client 
																					//and the service status
			controller.syncAdmin("status",socket.getInetAddress().getHostAddress());
		
			
			
			}catch (IOException e)
			{
				if(controller.getProperties().isDebug())
					e.printStackTrace();
			}			
	}

	/**
	 * invoked by the admin client in order to remove from the server certain clients.
	 * contains the kick protocol.
	 * @param socket - the admin client's socket.
	 */
	private void kickRequest(Socket socket) {
		try{
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter outToClient=new PrintWriter(socket.getOutputStream());
			outToClient.println("ok");
			outToClient.flush();
			inFromClient.readLine();
			outToClient.println("ready");
			outToClient.flush();
			String parse = inFromClient.readLine();
			controller.kickClients(parse.split(":"));
			outToClient.println("done");
			outToClient.flush();
			
			}catch (IOException e)
			{
				if(this.controller.getProperties().isDebug())
					e.printStackTrace();
			}		
	
		
	}

	/**
	 * define the unregister protocol.
	 * @param socket - the admin client's socket.
	 */
	private void unregister(Socket socket) {
		try{
			@SuppressWarnings("unused")
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter outToClient=new PrintWriter(socket.getOutputStream());
			controller.unregister(socket.getInetAddress().getHostAddress());
			outToClient.println("ok");
			outToClient.flush();
			}catch (IOException e)
			{
				if(this.controller.getProperties().isDebug())
					e.printStackTrace();
			}		
	}

	/**
	 * define the register protocol.
	 * @param socket - the admin client's socket.
	 */
	private void register(Socket socket) {
		try{
			@SuppressWarnings("unused")
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter outToClient=new PrintWriter(socket.getOutputStream());
			controller.register(socket.getInetAddress().getHostAddress()); //register this admin client to server.
			outToClient.println("ok");
			outToClient.flush();
			}catch (IOException e)
			{
				if(this.controller.getProperties().isDebug())
					e.printStackTrace();
			}
		controller.syncAdmin("clients",socket.getInetAddress().getHostAddress());
	}

	
	/**
	 * Stopping the game service 
	 * @param inFromClient - input stream from the client's socket.
	 * @param outToClient - output stream from the client's socket.
	 */
	private void serverStop(BufferedReader inFromClient, PrintWriter outToClient) {
		controller.gameServerStop();
		outToClient.println("ok");
		outToClient.flush();
		
	}

	
	/**
	 * invoked by the admin client in order to get the game service status.
	 * @param inFromServer - input stream from the client's socket.
	 * @param outToServer - output stream from the client's socket.
	 */
	private void getStatus(BufferedReader inFromServer, PrintWriter outToServer) {
		boolean status = controller.getStatus();
		if (status == true)
			outToServer.println("online");
		else
			outToServer.println("offline");
		outToServer.flush();
		
	}

	/**
	 * Starting the game service.
	 * @param inFromServer - input stream from the client's socket.
	 * @param outToServer - output stream from the client's socket.
	 */
	private void serverStart(BufferedReader inFromServer, PrintWriter outToServer) {
		controller.gameServerStart();
		outToServer.println("ok");
		outToServer.flush();
	}

	
	/**
	 * invoked by the connection manager in order to push and update the admin client of a new log entry.
	 * @param string - the added log entry
	 * @param inputStream - input from the admin client's socket
	 * @param outputStream - output from the admin client's socket
	 */
	public void updateLogProtocol(String string, InputStream inputStream, OutputStream outputStream)
	{
		try {
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter outToClient=new PrintWriter(outputStream);
			outToClient.println("log push");
			outToClient.flush();
			inFromClient.readLine();//ready received form the admin client
			outToClient.println(string);
			outToClient.flush();
			inFromClient.readLine();//done
			
			} catch (IOException e) {
				if(this.controller.getProperties().isDebug())
					e.printStackTrace();
			}	
	}
	
	
	/**
	 * invoked by the connection manager in order to push and update the admin client of the updated game clients list.
	 * @param inputStream - input from the admin client's socket
	 * @param outputStream - output from the admin client's socket
	 */
	public void updateClientsStatusProtocol(InputStream inputStream, OutputStream outputStream) {
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter out=new PrintWriter(outputStream);
			out.println("clients push");
			out.flush();
			in.readLine();//ready
			ArrayList<String[]> list = controller.getClientsList(); //receiving from the model the current client's list.
			if(this.controller.getProperties().isDebug())
			{
				for (String[] strings : list) {
					for (String string : strings) {
						System.out.print(string +" ");
					}
					System.out.println(" ");
				}
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
				if(this.controller.getProperties().isDebug())
					e.printStackTrace();
			}	
	}

	
	/**
	 * invoked by the connection manager in order to push and update the admin client of the updated game service status.
	 * @param inputStream - input from the admin client's socket
	 * @param outputStream - output from the admin client's socket
	 */
	public void updateStatusProtocol(InputStream inputStream, OutputStream outputStream) {
		try {
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter outToClient=new PrintWriter(outputStream);
			outToClient.println("status push");
			outToClient.flush();
			inFromClient.readLine();//ready
			boolean status = controller.getStatus(); //getting the current game service status.
			if (status == true)
				outToClient.println("online");
			else
				outToClient.println("offline");
			outToClient.flush();
			outToClient.flush();
			inFromClient.readLine();//done
			
			} catch (IOException e) {
				if(this.controller.getProperties().isDebug())
					e.printStackTrace();
			}
		
	}

	/**
	 * invoked by the connection manager in order to push and update the admin client of the server's shutting down process .
	 * @param inputStream - input from the admin client's socket
	 * @param outputStream - output from the admin client's socket
	 */
	public void updateShutdownProtocol(InputStream inputStream, OutputStream outputStream) {
		try {
			BufferedReader inFromClient=new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter outToClient=new PrintWriter(outputStream);
			outToClient.println("status push");
			outToClient.flush();
			inFromClient.readLine();//ready
			outToClient.println("offline");
			outToClient.flush();
			outToClient.println("shutdown push");
			outToClient.flush();
			inFromClient.readLine();//ok
			
			} catch (IOException e) {
				if(this.controller.getProperties().isDebug())
					e.printStackTrace();
			}
		
	}
}
