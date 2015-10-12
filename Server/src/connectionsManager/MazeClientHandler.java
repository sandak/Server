package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import controller.Controller;
import model.Model;

public class MazeClientHandler implements ClientHandler{

	Controller controller;
	HashMap<String, ClientHandler> clientsMap;
	
	public MazeClientHandler() {
		clientsMap = new HashMap<String, ClientHandler>();
	}
	
	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient, String clientId) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(inFromClient));
			PrintWriter out=new PrintWriter(outToClient);
			String line;
			while(!(line=in.readLine()).endsWith("exit")){
					if (line.contains("generate") )
					controller.generateClientHandler(in,out,clientId);
					if (line.contains("get maze") )
						controller.getMazeClientHandler(in,out);
				}	
			in.close();
			out.close();			
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void setController(Controller controller) {
		this.controller=controller;
	}




		
}
