package controller;

public class Clue extends CommonCommand {

	public Clue(Controller presenter) {		//Ctor
		super(presenter);
	}

	/**
	 * Using the model to solve and get a clue.
	 * @param param - parameters.
	 */
	@Override
	public void doCommand(String param) {
		
		String s[] = param.split(" ");
		
		if(s.length > 1)
		{
			presenter.getModel().clue(s[0],s[1]);
		}
		else
		{
			presenter.getView().displayError("Missing parameters.");
		}
	}


}
