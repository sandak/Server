package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import controller.Controller;

public class ManagmentHandler implements ClientHandler {

	public ManagmentHandler() {
	
	}
	
	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(inFromClient));
			PrintWriter out=new PrintWriter(outToClient);
			String line;
			while(!(line=in.readLine()).endsWith("exit")){
				if(line.equals("get image")){
					out.println("ok");
					out.flush();
					//am.convertToAscii(inFromClient, outToClient);
					out.println("done");
					out.flush();
				}				
			}
			in.close();
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void setController(Controller controller) {
		// TODO Auto-generated method stub
		
	}



}
