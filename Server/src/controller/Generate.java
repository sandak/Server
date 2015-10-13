package controller;

/**
 * Defines what the Command Generate can do.
 * @author Guy Golan & Amit Sandak
 *
 */
public class Generate extends CommonCommand {

	public Generate(Controller presenter) {		//Ctor
		super(presenter);
	}
	
	
	/**
	 * Using the presenter to generate a Maze3d according to the parameters.
	 * @param param - the parameters.
	 */
	@Override
	public void doCommand(String param) {
		System.out.println(param);
		String[] s = param.split(" ");
		if(s[0].equals("3d") && s[1].equals("maze") && s.length > 5)	//checks the validity of the parameters
		{
			try {
				presenter.getModel().generate(s[2],Integer.parseInt(s[3]),Integer.parseInt(s[4]),Integer.parseInt(s[5]));
			} catch (NumberFormatException e) {
				
			//	presenter.getView().displayError("Invalid parameters.");// TODO HANDLE ERRORS
			}
		}
		else
		{
			//presenter.getView().displayError("Missing parameters.");// TODO HANDLE ERRORS
		}
		

	}

}
