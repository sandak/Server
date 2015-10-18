package model;

import java.io.FileInputStream;
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
import controller.Controller;

/**
 *
 * Defines what MyObservableModel does.
 * 
 * @author Guy Golan & Amit Sandak.
 *
 */
public class MyObservableModel extends ObservableCommonModel {

	Timer timer;
	TimerTask task;
	
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
				System.out.println("log:starting from scratch maps");
		} finally {
			try {
				if (oos != null) // closing resources.
					oos.close();
			} catch (IOException e) {
				if (properties.isDebug())
					System.out.println("log:failed to close resources.");
				e.printStackTrace();
			}
		}

			timer = new Timer();
			task = new TimerTask() {
					@Override
					public void run() {	saveCash();}					
				};
				timer.scheduleAtFixedRate(task, 100*60*5 , 100*60*5);				
	}

	@Override
	public void generate(String name, int x, int y, int z) {
		if(controller!=null)
			this.controller.syncAdmins("log:generate algorithm started - " + name+" " +x+" "+y+ " "+z); 
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
			controller.syncAdmins("log:generate algorithm ended - " + name+" " +x+" "+y+ " "+z);
			notifyObservers("completedTask maze generated " + name );		//notifying the presenter that the maze was generated.
		} catch (InterruptedException | ExecutionException e) {
			// do nothing
			e.printStackTrace();
		}

	}

	protected void saveCash() {
		if(properties.isDebug()==true)
			controller.syncAdmins("log:saving cash.");
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
			// do nothing
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
			// do nothing
			e.printStackTrace();
		}
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
	public Solution<Position> getSolution(String mazeName) {
		Solution<Position> tmp = solutionMap.get(mazeMap.get(mazeName).toString());

		if (tmp != null) {
			return (tmp);
		} else {
			
			return null;
		}
	}

	@Override
	public void exit() { // safely terminating the existing threads. Also trying to save and cache all maps.
		timer.cancel();
		try {
			threadPool.shutdown();
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
				if (properties.isDebugMode())
					controller.syncAdmins("log:threads terminated violently!");
			}
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		saveCash();
	}


	@Override
	public void solve(String name, String algorithm) {
		if(solutionMap.get(mazeMap.get(name).toString())==null){			
		try {
			controller.syncAdmins("log:solve algorithm started - "+name+ ", "+algorithm);
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

			solutionMap.put(mazeMap.get(name).toString(), solution.get()); // inserting the Solution
													// into the solution map.
			controller.syncAdmins("log:solve algorithm ended - "+name+ ", "+algorithm);
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
				controller.syncAdmins("log:solve method interupted.");
				e.printStackTrace();
			}
		} catch (ExecutionException e) {
			if (properties.isDebug()) {
				controller.syncAdmins("log:solve method general error.");
				e.printStackTrace();
			}
		}
		}
	}



}
