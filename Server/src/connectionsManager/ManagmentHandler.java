package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import algorithms.mazeGenerators.Maze3d;

public class ManagmentHandler extends CommonClientHandler{
	
	public ManagmentHandler() {
	}
	
	@Override
	public void handleClient(Socket socket) {
		try{
			
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out=new PrintWriter(socket.getOutputStream());
			String line;
			while(!(line=in.readLine()).endsWith("exit")){
				
					if (line.contains("get status") )
						getStatus(in,out);
					if (line.contains("get data") )
						getData(socket);
					if (line.contains("start server") )
						serverStart(in,out);
					if (line.contains("stop server") )
						serverStop(in,out);
					if (line.contains("register") )
						register(socket);
					if (line.contains("unregister") )
						unregister(socket);
					if (line.contains("kick request") )
						kickRequest(socket);
					if (line.contains("shutdown") )
						shutdown(socket);
				}	
			in.close();
			out.close();			
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void shutdown(Socket socket) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out=new PrintWriter(socket.getOutputStream());
			out.println("ok");
			out.flush();
			controller.exit();
		
			
			
			}catch (IOException e)
			{
				////
			}			
		
	}

	private void getData(Socket socket) {
		
		
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out=new PrintWriter(socket.getOutputStream());
			out.println("ok");
			out.flush();
			controller.syncAdmin("clients",socket.getInetAddress().getHostAddress());
			controller.syncAdmin("status",socket.getInetAddress().getHostAddress());
		
			
			
			}catch (IOException e)
			{
				////
			}			
	}

	private void kickRequest(Socket socket) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out=new PrintWriter(socket.getOutputStream());
			out.println("ok");
			out.flush();
			in.readLine();
			out.println("ready");
			out.flush();
			String parse = in.readLine();
			controller.kickClients(parse.split(":"));
			out.println("done");
			out.flush();
			
			}catch (IOException e)
			{
				////
			}		
	
		
	}

	private void unregister(Socket socket) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out=new PrintWriter(socket.getOutputStream());
			controller.unregister(socket.getInetAddress().getHostAddress());
			out.println("ok");
			out.flush();
			}catch (IOException e)
			{
				////
			}		
	}

	private void register(Socket socket) {
		try{
		BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out=new PrintWriter(socket.getOutputStream());
		controller.register(socket.getInetAddress().getHostAddress());
		out.println("ok");
		out.flush();
		}catch (IOException e)
		{
			////
		}
		controller.syncAdmin("clients",socket.getInetAddress().getHostAddress());
	}

	private void serverStop(BufferedReader in, PrintWriter out) {
		controller.gameServerStop();
		out.println("ok");
		out.flush();
		
	}

	private void getStatus(BufferedReader in, PrintWriter out) {
		boolean status = controller.getStatus();
		if (status == true)
			out.println("online");
		else
			out.println("offline");
			out.flush();
		
	}

	private void serverStart(BufferedReader in, PrintWriter out) {
		controller.gameServerStart();
		out.println("ok");
		out.flush();
	}

	public void updateLogProtocol(String string, InputStream inputStream, OutputStream outputStream)
	{
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter out=new PrintWriter(outputStream);
			out.println("log push");
			out.flush();
			in.readLine();//ready
			out.println(string);
			out.flush();
			in.readLine();//done
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	public void updateClientsStatusProtocol(InputStream inputStream, OutputStream outputStream) {
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter out=new PrintWriter(outputStream);
			out.println("clients push");
			out.flush();
			in.readLine();//ready
			ArrayList<String[]> list = controller.getClientsList();
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

	public void updateStatusProtocol(InputStream inputStream, OutputStream outputStream) {
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter out=new PrintWriter(outputStream);
			out.println("status push");
			out.flush();
			in.readLine();//ready
			boolean status = controller.getStatus();
			if (status == true)
				out.println("online");
			else
				out.println("offline");
				out.flush();
			out.flush();
			in.readLine();//done
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	public void updateShutdownProtocol(InputStream inputStream, OutputStream outputStream) {
		try {
			BufferedReader in=new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter out=new PrintWriter(outputStream);
			out.println("status push");
			out.flush();
			in.readLine();//ready
				out.println("offline");
				out.flush();
			out.println("shutdown push");
			out.flush();
			in.readLine();//ok
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}
