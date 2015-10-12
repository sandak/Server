package model;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Maze3dGenerator;
import algorithms.mazeGenerators.MyMaze3dGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.SimpleMaze3dGenerator;
import algorithms.search.AStar;
import algorithms.search.BFS;
import algorithms.search.MazeAirDistance;
import algorithms.search.MazeDomain;
import algorithms.search.MazeManhattanDistance;
import algorithms.search.Searcher;
import algorithms.search.Solution;
import algorithms.search.State;
import io.MyCompressorOutputStream;
import io.MyDecompressorInputStream;

/**
 *
 * Defines what MyObservableModel does.
 * 
 * @author Guy Golan & Amit Sandak.
 *
 */
public class MyObservableModel extends ObservableCommonModel {

	@SuppressWarnings("unchecked")
	/**
	 * Ctor. Tries to get old maps from cached maps , if failed initializing new
	 * maps.
	 */
	public MyObservableModel() { // Ctor
		super();
		ObjectInputStream oos = null;
		try {
			oos = new ObjectInputStream(new GZIPInputStream(new FileInputStream("mazeMap.zip"))); // tries to read old cached maps.
			mazeMap = (HashMap<String, Maze3d>) oos.readObject();
			oos.close();
			oos = new ObjectInputStream(new GZIPInputStream(new FileInputStream("solutionMap.zip")));
			solutionMap = (HashMap<String, Solution<Position>>) oos.readObject();
		} catch (IOException | ClassNotFoundException e) {
			if (properties.isDebug())
				System.out.println("starting from scratch maps");
		} finally {
			try {
				if (oos != null) // closing resources.
					oos.close();
			} catch (IOException e) {
				if (properties.isDebug())
					System.out.println("failed to close resources.");
				e.printStackTrace();
			}
		}

	}

	@Override
	public void generate(String name, int x, int y, int z,String clientId) {
System.out.println("generate");
		Future<Maze3d> maze = threadPool.submit(new Callable<Maze3d>() {

			@Override
			public Maze3d call() throws Exception {
				Maze3dGenerator generator;
				switch (properties.getGenerateAlgorithm()) {
					case ("MyMaze3dGenerator"):
						generator = new MyMaze3dGenerator();
						break;
					case ("SimpleGenerator"):
						generator = new SimpleMaze3dGenerator();
						break;
					default:
						generator = new MyMaze3dGenerator();
				}
				return generator.generate(x, y, z);
			}
		});

		try {
			if (properties.isDebug()) {
				System.out.println(maze.get());
			}

			mazeMap.put(name, maze.get());			//inserting newly generated maze into the mazeMap.
			System.out.println(name + " , " + maze.get());
			charPositionMap.put(name, maze.get().getEntrance());	//updates the position map with the new starting position.
			setChanged();
			notifyObservers("completedTask maze generated " + name + " " + clientId);		//notifying the presenter that the maze was generated.
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Maze3d getMaze(String name) {
		Maze3d temp = mazeMap.get(name);

		if (temp != null) {
			return temp;
		} else {
			// return("Unavailable maze!");
			return null;
		}
	}

	@Override
	public void getCrossSectionByX(int index, String name) {
		Maze3d tmpMaze = mazeMap.get(name);

		if (tmpMaze != null) {
			// controller.display(arrIntToString(tmpMaze.getCrossSectionByX(index)));
		} else {
			// controller.display("Unavailable maze!");
		}
	}

	@Override
	public void getCrossSectionByY(int index, String name) {
		Maze3d tmpMaze = mazeMap.get(name);

		if (tmpMaze != null) {
			// controller.display(arrIntToString(tmpMaze.getCrossSectionByY(index)));
		} else {
			// controller.display("Unavailable maze!");
		}

	}

	@Override
	public void getCrossSectionByZ(int index, String name) {
		Maze3d tmpMaze = mazeMap.get(name);

		if (tmpMaze != null) {
			// controller.display(arrIntToString(tmpMaze.getCrossSectionByZ(index)));
		} else {
			// controller.display("Unavailable maze!");
		}

	}

	/**
	 * Converts a dou - dimensional array into a String.
	 * 
	 * @param arr
	 *            - a plain.
	 * @return a string containing the plain.
	 */
	@SuppressWarnings("unused")
	private String arrIntToString(int[][] arr) {
		String s = "";

		for (int[] i : arr) {
			for (int j : i) {
				s = s + j + " ";

			}
			s = s + "\n";
		}

		return s;
	}

	@Override
	public void save(String name, String fileName) {
		Maze3d tmpMaze = mazeMap.get(name);

		if (tmpMaze != null) {
			try {
				MyCompressorOutputStream tmpCompressor = new MyCompressorOutputStream(new FileOutputStream(fileName));
				tmpCompressor.write(tmpMaze.toByteArray());
				tmpCompressor.close(); // compressing the maze into and writing
										// it to the file.
				setChanged();
				notifyObservers("completedTask save");		//notifying the presenter that the save was completed.
				
			} catch (FileNotFoundException e) {
				setChanged();
				notifyObservers("completedTask error '" + fileName + "' is not a valid file name.");	//notifying the presenter that an error has occurred.
				
			} catch (IOException e) {
				setChanged();
				notifyObservers("completedTask error IO error.");	//notifying the presenter that an error has occurred.
				
			}
		} else {
			setChanged();
			notifyObservers("completedTask error '" + name + "' is an unavailable maze");	//notifying the presenter that an error has occurred.
			
		}
	}

	@Override
	public void load(String fileName, String name) {
		try {
			MyDecompressorInputStream tmpDecompressor = new MyDecompressorInputStream(new FileInputStream(fileName));
			byte[] buffer = new byte[35 * 35 * 35]; 
			if (tmpDecompressor.read(buffer) == -1) {
				Maze3d tmpMaze = new Maze3d(buffer);
				mazeMap.put(name, tmpMaze);
				setChanged();
				notifyObservers("completedTask load " + name);	//notifying the presenter that the load was completed.
				
				tmpDecompressor.close();		//closing resources.
				charPositionMap.put(name, tmpMaze.getEntrance());	//updates the charpositionMap for the entrance of the loaded maze.
			} else {
				setChanged();
				notifyObservers("completedTask error The requsted maze is too big!");	//notifying the presenter that an error has occurred.
				
			}
		} catch (FileNotFoundException e) {
			setChanged();
			notifyObservers("completedTask error wrong file path.");	//notifying the presenter that an error has occurred.
			
		} catch (IOException e) {
			setChanged();
			notifyObservers("completedTask error IO error.");		//notifying the presenter that an error has occurred.
			
		}

	}

	@Override
	public void mazeSize(String name) {

		Maze3d tempMaze = mazeMap.get(name);
		if (tempMaze != null) {
			int size = tempMaze.getxAxis() * tempMaze.getyAxis() * tempMaze.getzAxis() + 9;
			setChanged();
			notifyObservers("completedTask mazeSize " + size);		//notifying the presenter that the mazeSize was completed.
		} else {
			setChanged();
			notifyObservers("completedTask error '" + name + "' is unavailable maze");	//notifying the presenter that an error has occurred.
		}

	}

	@Override
	public void calculateFileSize(String name) {
		Maze3d tmpMaze = mazeMap.get(name);

		if (tmpMaze != null) {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			MyCompressorOutputStream compress = new MyCompressorOutputStream(buffer);
			try {
				compress.write(tmpMaze.toByteArray()); // trying to compress the
														// maze into the buffer.
				setChanged();
				notifyObservers("completedTask fileSize " + name + " " + buffer.size());	//notifying the presenter that the mazeSize was completed.
			} catch (IOException e) {
				setChanged();
				notifyObservers("completedTask error IO error.");		//notifying the presenter that an error has occurred.
			} finally {
				try {
					compress.close(); // closing resources.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			setChanged();
			notifyObservers("completedTask error '" + name + "' is unavailable maze");
			
		}

	}

	@Override
	public void clue(String name, String algorithm) {
		solve(name, algorithm);
		setChanged();
		notifyObservers("completedTask clue " + name);

	}

	@Override
	public Solution<Position> getSolution(String mazeName) {
		Solution<Position> tmp = solutionMap.get(mazeName);

		if (tmp != null) {
			return (tmp);
		} else {
			
			return null;
		}
	}

	@Override
	public void exit() { // safely terminating the existing threads. Also trying to save and cache all maps.
		try {
			threadPool.shutdown();
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
				// presenter.get.display("threads terminated violently!");
			}
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream("solutionMap.zip")));
			oos.writeObject(solutionMap);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			if (properties.isDebug())
				e.printStackTrace();
		} finally {
			try {
				oos.flush();
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream("mazeMap.zip")));
			oos.writeObject(mazeMap);
		} catch (IOException e) {

			if (properties.isDebug())
				e.printStackTrace();
		} finally {
			try {
				oos.flush();
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Position getCharPosition(String name) {
		return charPositionMap.get(name);
	}

	@Override
	public void MoveUP(String name) {
		Position current = charPositionMap.get(name);
		String[] moves = mazeMap.get(name).getPossibleMoves(current);
		for (String move : moves) {
			if (move.equals("BACKWARD")) {			//checks if up movement is possible.
				current.setY(current.getY() - 1);
				charPositionMap.put(name, current);		//updates the new character position.
				setChanged();
				notifyObservers("completedTask movement " + name);	//notifying the presenter that the movement was completed.
			}
		}
	}

	@Override
	public void MoveDOWN(String name) {
		Position current = charPositionMap.get(name);
		String[] moves = mazeMap.get(name).getPossibleMoves(current);
		for (String move : moves) {
			if (move.equals("FORWARD")) {		//checks if down movement is possible.
				current.setY(current.getY() + 1);
				charPositionMap.put(name, current);	//updates the new character position.
				setChanged();
				notifyObservers("completedTask movement " + name);	//notifying the presenter that the movement was completed.
			}
		}
	}

	@Override
	public void MoveLEFT(String name) {
		Position current = charPositionMap.get(name);
		String[] moves = mazeMap.get(name).getPossibleMoves(current);
		for (String move : moves) {
			if (move.equals("RIGHT")) {		//checks if left movement is possible.
				current.setZ(current.getZ() - 1);
				charPositionMap.put(name, current);	//updates the new character position.
				setChanged();
				notifyObservers("completedTask movement " + name);	//notifying the presenter that the movement was completed.
			}
		}
	}

	@Override
	public void MoveRIGHT(String name) {
		Position current = charPositionMap.get(name);
		String[] moves = mazeMap.get(name).getPossibleMoves(current);
		for (String move : moves) {
			if (move.equals("LEFT")) {			//checks if right movement is possible.
				current.setZ(current.getZ() + 1);
				charPositionMap.put(name, current);	//updates the new character position.
				setChanged();
				notifyObservers("completedTask movement " + name);	//notifying the presenter that the movement was completed.
			}
		}
	}

	@Override
	public void MoveLVLUP(String name) {

		Position current = charPositionMap.get(name);

		String[] moves = mazeMap.get(name).getPossibleMoves(current);
		for (String move : moves) {	
			if (move.equals("UP")) {			//checks if level up movement is possible.
				current.setX(current.getX() + 1);
				charPositionMap.put(name, current);	//updates the new character position.

				setChanged();
				notifyObservers("completedTask movement " + name);		//notifying the presenter that the movement was completed.

			}
		}
	}

	@Override
	public void MoveLVLDOWN(String name) {
		Position current = charPositionMap.get(name);
		String[] moves = mazeMap.get(name).getPossibleMoves(current);
		for (String move : moves) {
			if (move.equals("DOWN")) {		//checks if level down movement is possible.
				current.setX(current.getX() - 1);
				charPositionMap.put(name, current);		//updates the new character position.
				setChanged();
				notifyObservers("completedTask movement " + name);		//notifying the presenter that the movement was completed.
			}
		}
	}

	@Override
	public void solve(String name, String algorithm) {
		try {
			Future<Solution<Position>> solution = threadPool.submit((new Callable<Solution<Position>>() {

				@Override
				public Solution<Position> call() throws IllegalArgumentException {
					Maze3d tmpMaze = mazeMap.get(name);
					Searcher<Position> alg;
					if (tmpMaze != null) {

						switch (algorithm) // generating a Searcher according to
											// the parameters.
						{
							case "BFS":
								alg = new BFS<Position>();
								break;
							case "AstarManhattan":
								alg = new AStar<Position>(
										new MazeManhattanDistance(new State<Position>(tmpMaze.getExit())));
								break;
							case "AstarAirDistance":
								alg = new AStar<Position>(new MazeAirDistance(new State<Position>(tmpMaze.getExit())));
								break;
	
							default:
								throw new IllegalArgumentException("invalid search algorithm"); 
																				
						}
					} else
						throw new IllegalArgumentException("unavailable maze"); 
																			
					return alg.search(new MazeDomain(tmpMaze));
				}
			}));

			solutionMap.put(name, solution.get()); // inserting the Solution
													// into the solution map.

		} catch (IllegalArgumentException t) {		//catching the exception and notifying the presenter accordingly.
			setChanged();
			switch (t.getMessage()) {
				case ("invalid search algorithm"):
					notifyObservers("completedTask error " + algorithm+ " is not a valid algorithm!\nvalid algorithms are: BFS, AstarManhattan, AstarAirDistance.");
				case ("unavailable maze"):
					notifyObservers("completedTask error '" + name + "' is unavailable maze");
				default:
					notifyObservers("completedTask error general error!!");
			}

		} catch (InterruptedException e) {
			if (properties.isDebug()) {
				System.out.println("solve method interupted:");
				e.printStackTrace();
			}
		} catch (ExecutionException e) {
			if (properties.isDebug()) {
				System.out.println("solve method general error:");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void solution(String name, String algorithm) {		//this solution uses a timer and a timertask to move the character to the end of 
																//the maze.
		solve(name, algorithm);
		Solution<Position> course = solutionMap.get(name);

		Timer timer = new Timer();

		TimerTask task = new TimerTask() {
			int i = 0;

			@Override
			public void run() {
				if (i == course.getArr().size() - 1)
					this.cancel();

				charPositionMap.put(name, course.getArr().get(i++).getState());	//setting the new position in the map.
				setChanged();
				notifyObservers("completedTask movement " + name);
			}

		};

		timer.scheduleAtFixedRate(task, 0, 200);		

	}
}
