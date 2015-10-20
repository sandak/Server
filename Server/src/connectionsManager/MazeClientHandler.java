package connectionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import algorithms.search.State;

/**
 * The type handling a gamer client. Defines the protocol.
 * 
 * @author Guy Golan && Amit Sandak
 *
 */
public class MazeClientHandler extends CommonClientHandler {

	public MazeClientHandler() {
	}

	@Override
	public void handleClient(Socket socket) {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter outToClient = new PrintWriter(socket.getOutputStream());
			String line;
			// handling the client until received 'exit'.
			while (!(line = inFromClient.readLine()).endsWith("exit")) {

				if (line.contains("generate"))
					generateProtocol(inFromClient, outToClient);
				if (line.contains("get maze"))
					getMazeProtocol(inFromClient, outToClient);
				if (line.contains("solve maze"))
					getSolutionProtocol(inFromClient, outToClient);
			}
			inFromClient.close();
			outToClient.close();

		} catch (SocketException e) {
			if (controller.getProperties().isDebug())
				System.out.println("client kicked!");
		} catch (IOException e) {
			if (controller.getProperties().isDebug())
				e.printStackTrace();
		}

	}

	/**
	 * define the protocol that solves mazes.
	 * @param inFromClient - input from the client's socket.
	 * @param outToClient - output from the client's socket.
	 */
	private void getSolutionProtocol(BufferedReader inFromClient, PrintWriter outToClient) {
		try {
			String name, algorithm;
			outToClient.println("what is the maze name?");
			outToClient.flush();
			
			name = inFromClient.readLine().split(": ")[1];
			outToClient.println("what is the algorithm?");
			outToClient.flush();
			algorithm = inFromClient.readLine().split(": ")[1];
			
			controller.update("solve " + name + " " + algorithm);
			
			Solution<Position> solution = controller.getSolution(name); //getting back from the model the solution.
			
			if (solution == null){
				
				if(this.controller.getProperties().isDebug())
					System.out.println("solution error");
			}
			else {
				 
				outToClient.println("sending");
				outToClient.flush();

				byte[] buffer = toByteArray(solution);

				for (byte b : buffer)
					outToClient.write((int) b);
				outToClient.write(127);
				outToClient.flush();

			}
		} catch (IOException e) {
			if (this.controller.getProperties().isDebug())
				e.printStackTrace();
		}

	}

	/**
	 * converting a Solution(Position) into a byte array. 
	 * @param solution - a solution containing states over Positions.
	 * @return b - byte array.
	 */
	protected byte[] toByteArray(Solution<Position> solution) {
		byte[] b = new byte[3 * solution.getArr().size()];
		ArrayList<State<Position>> arr = solution.getArr();
		int i = 0;
		for (State<Position> statePosition : arr) {
			b[i++] = statePosition.getState().toByteArray()[0];
			b[i++] = statePosition.getState().toByteArray()[1];
			b[i++] = statePosition.getState().toByteArray()[2];
		}
		return b;
	}

	/**
	 * define the protocol that get mazes from the model.
	 * @param inFromClient - input from the client's socket.
	 * @param outToClient - output from the client's socket.
	 */
	private void getMazeProtocol(BufferedReader inFromClient, PrintWriter outToClient) {
		try {
			String name;
			outToClient.println("what is the maze name?");
			outToClient.flush();
			name = inFromClient.readLine().split(": ")[1];
			
			Maze3d maze = controller.getMaze(name); //getting the maze from the model.
			if (maze == null)
			{
				if(this.controller.getProperties().isDebug())
					System.out.println("maze error");
			}
			else {
				outToClient.println("sending");
				outToClient.flush();
				byte[] buffer = maze.toByteArray();

				for (byte b : buffer)
					outToClient.write((int) b);	//sending the maze to the gamer client in a byte array.
				outToClient.write(127);
				outToClient.flush();
			}
		} catch (IOException e) {
			if(this.controller.getProperties().isDebug())
				e.printStackTrace();
		}

	}

	/**
	 * define the protocol that generates mazes.
	 * @param inFromClient - input from the client's socket.
	 * @param outToClient - output from the client's socket.
	 */
	private void generateProtocol(BufferedReader inFromClient, PrintWriter outToClient) {

		try {
			String parse, name;
			int x, y, z;
			outToClient.println("what is the maze name?");
			outToClient.flush();
			parse = inFromClient.readLine();
			name = parse.split(": ")[1];
			if(this.controller.getProperties().isDebug())
				System.out.println(name);
			//////////////////
			outToClient.println("what is the Axis x dimension?");
			outToClient.flush();
			parse = inFromClient.readLine();
			if(this.controller.getProperties().isDebug())
				System.out.println(parse);
			x = Integer.parseInt(parse.split(": ")[1]);
			///////////////////
			outToClient.println("what is the Axis y dimension?");
			outToClient.flush();
			parse = inFromClient.readLine();
			y = Integer.parseInt(parse.split(": ")[1]);
			//////////////////
			outToClient.println("what is the Axis z dimension?");
			outToClient.flush();
			parse = inFromClient.readLine();
			z = Integer.parseInt(parse.split(": ")[1]);
			///////////
			controller.update("generate 3d maze " + name + " " + x + " " + y + " " + z);
			outToClient.println("ok");
			if(this.controller.getProperties().isDebug())
				System.out.println("ok");
			outToClient.flush();
		} catch (IOException e) {
			if(this.controller.getProperties().isDebug())
				e.printStackTrace();
		}
	}

}
