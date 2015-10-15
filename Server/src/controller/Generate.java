package controller;

/**
 * Defines what the Command Generate can do.
 * 
 * @author Guy Golan & Amit Sandak
 *
 */
public class Generate extends CommonCommand {

	public Generate(Controller controller) { // Ctor
		super(controller);
	}

	/**
	 * Using the presenter to generate a Maze3d according to the parameters.
	 * 
	 * @param param
	 *            - the parameters.
	 */
	@Override
	public void doCommand(String param) {
		System.out.println(param);
		String[] s = param.split(" ");
		if (s[0].equals("3d") && s[1].equals("maze") && s.length > 5) // checks
																		// the
																		// validity
																		// of
																		// the
																		// parameters
		{
			try {
				controller.getModel().generate(s[2], Integer.parseInt(s[3]), Integer.parseInt(s[4]),
						Integer.parseInt(s[5]));
			} catch (NumberFormatException e) {

				// controller.getView().displayError("Invalid parameters.");//
				// TODO HANDLE ERRORS
			}
		} else {
			// controller.getView().displayError("Missing parameters.");// TODO
			// HANDLE ERRORS
		}

	}

}
