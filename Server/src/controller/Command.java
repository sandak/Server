package controller;

/**
 * Defines what every command can do.
 * @author Guy Golan & Amit Sandak
 *
 */
public interface Command {

	/**
	 * Doing the command.
	 * @param param - parameters.
	 */
	void doCommand(String param);
	
}
