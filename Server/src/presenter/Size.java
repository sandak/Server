package presenter;

/**
 * Defines what the Command Size should do.
 * @author Guy Golan & Amit Sandak.
 *
 */
public class Size extends CommonCommand {

	public Size(Presenter presenter) {	//Ctor
		super(presenter);
	}

	/**
	 * Using the model to size a Maze3d.
	 * @param name - the name of the Maze3d.
	 */
	@Override
	public void doCommand(String name) {
		
		String s[] = name.split(" ");
		
		if(s.length > 1)
		{
			presenter.getModel().mazeSize(s[1]);
		}
		else
		{
			presenter.getView().displayError("Missing parameters.");
		}

	}

}
