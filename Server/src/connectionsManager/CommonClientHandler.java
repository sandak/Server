package connectionsManager;

import controller.Controller;
 
public abstract class CommonClientHandler implements ClientHandler {
	protected Controller controller;

	public  void  setController(Controller controller)
	{this.controller = controller;}

}
