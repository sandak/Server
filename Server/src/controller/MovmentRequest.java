package controller;

/**
 * Usually received from the View.
 * Using the Model to move the player's character around the maze.
 * @author Guy Golan && Amit Sandak.
 *
 */
public class MovmentRequest extends CommonCommand {

	public MovmentRequest(Controller presenter) {
		super(presenter);
		
	}

	@Override
	public void doCommand(String param) {
	
		String[] s = param.split(" ");
		if( s.length > 1)	//checks the validity of the parameters
		{
			switch (s[0])
			{
				case "UP":
					presenter.getModel().MoveUP(s[1]);
					break;
				case "DOWN":
					presenter.getModel().MoveDOWN(s[1]);
					break;
				case "LEFT":
					presenter.getModel().MoveLEFT(s[1]);
					break;
				case "RIGHT":
					presenter.getModel().MoveRIGHT(s[1]);
					break;
				case "LVLUP":
					presenter.getModel().MoveLVLUP(s[1]);
					break;
				case "LVLDOWN":
					presenter.getModel().MoveLVLDOWN(s[1]);
					break;
				default:
					if (presenter.getProperties().isDebug())
						System.out.println("Invalid movement request: " + param);
					presenter.getView().displayError("General Error");
					break;
			}

		}
		else
		{
			presenter.getView().displayError("Missing parameters.");
		}
		
	}

}
