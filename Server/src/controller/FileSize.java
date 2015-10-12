package controller;

/**
 * Defines what the Command FileSize should do.
 * @author Guy Golan & Amit Sandak
 *
 */
public class FileSize extends CommonCommand {

	public FileSize(Controller presenter) {	//Ctor
		super(presenter);
	}

	/**
	 * Using the model to check what the given Maze3d(name) size in a file. 
	 * @param name - the Maze3d's name.
	 */
	@Override
	public void doCommand(String name) {

		String s[] = name.split(" ");
		
		if(s.length > 1)
		{
			presenter.getModel().calculateFileSize(s[1]);
		}
		else
		{
			presenter.getView().displayError("Missing parameters.");
		}

	}

	

}
