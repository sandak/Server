package controller;

/**
 * Defines what the Command Solve should do.
 * @author Guy Golan & Amit Sandak.
 *
 */
public class Solve extends CommonCommand {

	public Solve(Controller presenter) {		//Ctor
		super(presenter);
	}

	/**
	 * Using the model to solve a Maze3d.
	 * @param param - parameters.
	 */
	@Override
	public void doCommand(String param) {
		
		String s[] = param.split(" ");
		
		if(s.length > 1)
		{
			presenter.getModel().solution(s[0],s[1]);
		}
		else
		{
			//presenter.getView().displayError("Missing parameters.");
		}
	}

}
