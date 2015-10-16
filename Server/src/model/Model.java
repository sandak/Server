package model;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import controller.Controller;
import controller.Properties;

/**
 * Defines what every Model can do.
 * @author Guy Golan & Amit Sandak
 *
 */
public interface Model {

	/**
	 * Generate a Maze3d according to given sizes.
	 * @param name - tags the Maze3d in the hashMap by its name.
	 * @param x - the X dimension. 
	 * @param y - the Y dimension.
	 * @param z - the Z dimension.
	 */
	void generate(String name, int x, int y, int z);
	/**
	 * Displays a  Maze3d according to its name.
	 * @param name - the Maze3d name.
	 */
	Maze3d getMaze(String name);
	

	/**
	 * Display the solution (using the controller) of a Maze3d (by its name).
	 * @param name - Maze3d's name
	 */
	Solution<Position> getSolution(String name);
	/**
	 * Safely closing all resources.
	 */
	void exit();
	
	/**
	 * setting the model properties (usually through a Presenter).
	 * @param properties - properties.
	 */
	void setProperties(Properties properties);
	
	/**
	 * Regular get for the properties.
	 * @return Properties properties.
	 */
	Properties getProperties();
	
	/**
	 * Solving a searchable maze.
	 * @param name - name of the maze.
	 * @param algorithm - name of the searching algorithm.
	 */
	void solve(String name, String algorithm);
		
	void notifyPresenter(String notify);
	void setController(Controller controller);
}
