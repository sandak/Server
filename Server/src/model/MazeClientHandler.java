package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class MazeClientHandler implements ClientHandler{

	Model model;
	HashMap<String, ClientHandler> clientsMap;
	
	public MazeClientHandler(Model model) {
		this.model = model;
		clientsMap = new HashMap<String, ClientHandler>();
	}
	
	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(inFromClient));
			PrintWriter out=new PrintWriter(outToClient);
			String line;
			while(!(line=in.readLine()).endsWith("exit")){
				if(line.startsWith("requset")){
					model.notifyPresenter(line);
					
				}				
			}
			in.close();
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void acceptNewClient(Socket someClient) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(someClient.getInputStream()));
			PrintWriter out=new PrintWriter(someClient.getOutputStream());
			String line;
			while(!(line=in.readLine()).endsWith("exit")){
				if(line.startsWith("requset")){
					model.notifyPresenter(line);
					
				}				
			}
			in.close();
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

		
	}

}
