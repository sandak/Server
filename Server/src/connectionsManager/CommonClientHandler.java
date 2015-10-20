package connectionsManager;

import controller.Controller;
 
/**
 * Defines a common controller for every client handler.
 * @author Guy Golan && Amit Sandak
 *
 */
public abstract class CommonClientHandler implements ClientHandler {
	protected Controller controller;

	public  void  setController(Controller controller)
	{this.controller = controller;}

}
