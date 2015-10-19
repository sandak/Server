package model;

import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import controller.Controller;
import controller.Properties;

/**
 * represents what every Observable Model should implement or have.
 * @author Guy Golan && Amit Sandak.
 *
 */
public abstract class ObservableCommonModel extends Observable implements Model{
	
	protected HashMap<String, Maze3d> mazeMap;						//a name -->> Maze map.
	protected HashMap<Maze3d, Solution<Position>> solutionMap;		//a name -->> solution map.
	protected HashMap<String, Position> charPositionMap;			//a name -->> character position map.
	protected ExecutorService threadPool;					//thread pool to manage all important threads.
	protected Properties properties;						//system properties.
	protected Controller controller;
	
	
	
	/**
	 * Ctor
	 */
	public ObservableCommonModel() {				//Ctor
		
		mazeMap = new HashMap<String, Maze3d>();						//new empty maps.
		solutionMap = new HashMap<Maze3d, Solution<Position>>();
		charPositionMap= new HashMap<String, Position>();
		
		threadPool = Executors.newCachedThreadPool(); //default
		properties = new Properties();
		properties.setDefaults();

	}

	
	public Controller getController() {
		return controller;
	}

@Override
	public void setController(Controller controller) {
		this.controller = controller;
	}


	public Properties getProperties() {
		return properties;
	}


	public void setProperties(Properties properties) {
		this.properties = properties;
		ExecutorService bufferdpool = threadPool;	//saving old thread pool.
		threadPool = Executors.newFixedThreadPool(properties.getMaxThreads());  //using the system properties get a fixed thread pool.
		bufferdpool.shutdown();		//letting all threads in the old thread pool to finish.
	}
	@Override
	public void notifyPresenter(String notify)
	{
		setChanged();
		notifyObservers(notify);
	}

}


