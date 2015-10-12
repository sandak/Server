package controller;

/**
 * Implementing what every Command must have.
 * @author Guy Golan & Amit Sandak
 *
 */
public abstract class CommonCommand implements Command {

	protected Controller presenter;	//the presenter who activated the command.
	
	
	public CommonCommand(Controller presenter) {		//Ctor
		this.presenter = presenter;
	}
	
	public abstract void doCommand(String param);
}
