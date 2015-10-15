package controller;

public class Clue extends CommonCommand {

	public Clue(Controller controller) { // Ctor
		super(controller);
	}

	/**
	 * Using the model to solve and get a clue.
	 * 
	 * @param param
	 *            - parameters.
	 */
	@Override
	public void doCommand(String param) {

		String s[] = param.split(" ");

		if (s.length > 1) {
			controller.getModel().clue(s[0], s[1]);
		} else {
			controller.getView().displayError("Missing parameters.");
		}
	}

}
