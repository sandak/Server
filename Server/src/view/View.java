package view;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import presenter.Presenter;
import presenter.Properties;

/**
 * The Interface of View in the MVP.
 * 
 * @author Guy Golan && Amit Sandak.
 */
public interface View {

/**
 * Display an array of strings
 *
 * @param strings the strings array
 */
	public void display(String[] strings);
	
	/**
	 * Display a string.
	 *
	 * @param string the string to display
	 */
	public void display(String string);
	
	/**
	 * Display a Maze3d.
	 *
	 * @param maze the Maze3d to display
	 */
	public void display(Maze3d maze);
	
	/**
	 * Exit from view.
	 */
	public void exit() ;
	
	/**
	 * Start the view.
	 */
	public void start();
	
	/**
	 * Exit request. the notify exit protocol in the MVP.
	 * this request will be processed in the presenter.
	 */
	void exitRequest();
	
	/**
	 * Display the character position.
	 *
	 * @param charPosition the character position to display
	 */
	public void display(Position charPosition);
	
	/**
	 * Display error.
	 *
	 * @param message the error message
	 */
	public void displayError(String message);
	
	/**
	 * Display the cross section by x.
	 *
	 * @param index the index of the requested cross
	 * @param name the name of the maze 
	 */
	public void displayCrossSectionByX(int index, String name);
	
	/**
	 * Display the cross section by y.
	 *
	 * @param index the index of the requested cross
	 * @param name the name of the maze 
	 */
	public void displayCrossSectionByY(int index, String name);
	
	/**
	 * Display the cross section by z.
	 *
	 * @param index the index of the requested cross
	 * @param name the name of the maze 
	 */
	public void displayCrossSectionByZ(int index, String name);
	
	/**
	 * Display the solution of the maze.
	 *
	 * @param result the solution to display
	 */
	public void display(Solution<Position> result);
	
	/**
	 * Sets the properties.
	 *
	 * @param prop the new properties
	 */
	public void setProperties(Properties prop);
	

}
