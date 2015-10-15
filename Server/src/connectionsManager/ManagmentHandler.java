package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

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
					if (line.contains("start server") )
						serverStart(in,out);
					if (line.contains("stop server") )
						serverStop(in,out);
					if (line.contains("register") )
						register(socket);
				}	
			in.close();
			out.close();			
			
		}catch(IOException e){
			e.printStackTrace();
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


			
}
