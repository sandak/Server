package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ASCIIArtClientHandler implements ClientHandler{

	ASCIIMaker am;
	public ASCIIArtClientHandler() {
		am=new ASCIIMaker();
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
					am.convertToAscii(inFromClient, outToClient);
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

}
