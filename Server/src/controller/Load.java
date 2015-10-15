package controller;

/**
 * Defines what the Command Load should do.
 * 
 * @author Guy Golan & Amit Sandak
 *
 */
public class Load extends CommonCommand {

	public Load(Controller controller) { // Ctor
		super(controller);
	}

	/**
	 * Using the model to load a compressed Maze3d from a file.
	 * 
	 * @param param
	 *            - the Command's parameters.
	 */
	@Override
	public void doCommand(String param) {
		String s[] = param.split(" ");

		if (s.length > 2) {
			controller.getModel().load(s[1], s[2]);
		} else {
			controller.getView().displayError("Missing parameters.");
		}

	}

}
