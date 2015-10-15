package controller;

/**
 * Usually received from the View. Using the Model to move the player's
 * character around the maze.
 * 
 * @author Guy Golan && Amit Sandak.
 *
 */
public class MovmentRequest extends CommonCommand {

	public MovmentRequest(Controller controller) {
		super(controller);

	}

	@Override
	public void doCommand(String param) {

		String[] s = param.split(" ");
		if (s.length > 1) // checks the validity of the parameters
		{
			switch (s[0]) {
			case "UP":
				controller.getModel().MoveUP(s[1]);
				break;
			case "DOWN":
				controller.getModel().MoveDOWN(s[1]);
				break;
			case "LEFT":
				controller.getModel().MoveLEFT(s[1]);
				break;
			case "RIGHT":
				controller.getModel().MoveRIGHT(s[1]);
				break;
			case "LVLUP":
				controller.getModel().MoveLVLUP(s[1]);
				break;
			case "LVLDOWN":
				controller.getModel().MoveLVLDOWN(s[1]);
				break;
			default:
				if (controller.getProperties().isDebug())
					System.out.println("Invalid movement request: " + param);
				controller.getView().displayError("General Error");
				break;
			}

		} else {
			controller.getView().displayError("Missing parameters.");
		}

	}

}
