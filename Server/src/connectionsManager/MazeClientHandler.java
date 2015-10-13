package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import algorithms.mazeGenerators.Maze3d;
import controller.Controller;
import model.Model;

public class MazeClientHandler extends CommonClientHandler{
	
	public MazeClientHandler() {
	}
	
	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient, String clientId) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(inFromClient));
			PrintWriter out=new PrintWriter(outToClient);
			String line;
			while(!(line=in.readLine()).endsWith("exit")){
				
					if (line.contains("generate") )
						generateProtocol(in,out);
					if (line.contains("get maze") )
						getMazeProtocol(in,out);
				}	
			in.close();
			out.close();			
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void getMazeProtocol(BufferedReader in, PrintWriter out) {
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
		
		for (byte b : buffer) 
			out.write((int)b);		
		out.write(127);
		out.flush();
		}} catch (IOException e) {
			// do nothing
			e.printStackTrace();
		}
		
		
	}

	private void generateProtocol(BufferedReader in, PrintWriter out) {

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
			controller.update("generate 3d maze " + name + " " +x +" " + y + " " + z);
			out.println("ok");
			System.out.println("ok");
			out.flush();
		} catch (IOException e) {
				//do nothing
				e.printStackTrace();
			}
	}
			
}
