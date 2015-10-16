package connectionsManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import algorithms.search.State;
import controller.Controller;
import model.Model;

public class MazeClientHandler extends CommonClientHandler{
	
	public MazeClientHandler() {
	}
	
	@Override
	public void handleClient(Socket socket) {
		try{
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out=new PrintWriter(socket.getOutputStream());
			String line;
			while(!(line=in.readLine()).endsWith("exit")){
				
					if (line.contains("generate") )
						generateProtocol(in,out);
					if (line.contains("get maze") )
						getMazeProtocol(in,out);
					if (line.contains("solve maze") )
						getSolutionProtocol(in,out);
				}	
			in.close();
			out.close();			
			
		}catch(SocketException e){
			if (controller.getProperties().isDebug())
				System.out.println("client kicked!");
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}

	private void getSolutionProtocol(BufferedReader in, PrintWriter out) {
		try {
			String name,algorithm;
			out.println("what is the maze name?");
			out.flush();
			name = in.readLine().split(": ")[1];
			out.println("what is the algorithm?");
			out.flush();
				algorithm = in.readLine().split(": ")[1];
				controller.update("solve "+name+" "+algorithm);
				Solution<Position> solution = controller.getSolution(name);
			if (solution == null)
				System.out.println("solution error");
			else
				{out.println("sending");
				out.flush();
				
//				   ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			        ObjectOutputStream o = new ObjectOutputStream(bos);
//			        o.writeObject(solution);
//			        byte[] buffer =  bos.toByteArray();
				
				byte[] buffer = toByteArray(solution);
//			        
			    	for (byte b : buffer) 
						out.write((int)b);		
					out.write(127);
					out.flush();
			        
		
		}} catch (IOException e) {
			// do nothing
			e.printStackTrace();
		}
		
	}
	
	

	protected byte[] toByteArray(Solution<Position> solution) {
		byte[] b=new  byte[3*solution.getArr().size()];
		ArrayList<State<Position>> arr = solution.getArr();
		int i =0;
		for (State<Position> statePosition : arr) {
			b[i++] = statePosition.getState().toByteArray()[0];
			b[i++] = statePosition.getState().toByteArray()[1];
			b[i++] = statePosition.getState().toByteArray()[2];
		}
		return b;
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
