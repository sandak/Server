package controller;

/**
 * Implementing what every Command must have.
 * @author Guy Golan & Amit Sandak
 *
 */
public abstract class CommonCommand implements Command {

	protected Controller controller;	//the presenter who activated the command.
	
	
	public CommonCommand(Controller controller) {		//Ctor
		this.controller = controller;
	}
	
	public abstract void doCommand(String param);
}
