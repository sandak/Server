package controller;

/**
 * Defines what the Command Save should do.
 * @author Guy Golan & Amit Sandak.
 *
 */
public class Save extends CommonCommand {

	public Save(Controller presenter) {	//Ctor
		super(presenter);
	}

	
	/**
	 * Using the model to save a Maze3d to a file.
	 * @param param - parameters.
	 */
	@Override
	public void doCommand(String param) {
		String s[] = param.split(" ");
		
		if(s.length > 2)
		{
			presenter.getModel().save(s[1],s[2]);
		}
		else
		{
			presenter.getView().displayError("Missing parameters.");
		}
		
	}
}
