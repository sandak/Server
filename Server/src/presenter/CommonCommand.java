package presenter;

/**
 * Implementing what every Command must have.
 * @author Guy Golan & Amit Sandak
 *
 */
public abstract class CommonCommand implements Command {

	protected Presenter presenter;	//the presenter who activated the command.
	
	
	public CommonCommand(Presenter presenter) {		//Ctor
		this.presenter = presenter;
	}
	
	public abstract void doCommand(String param);
}
