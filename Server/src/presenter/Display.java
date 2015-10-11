package presenter;

/**
 * Defines what the Command Display should do.
 * @author Guy Golan & Amit Sandak
 *
 */
public class Display extends CommonCommand {

	public Display(Presenter presenter) {		//Ctor
		super(presenter);
	}
	
	/**
	 * According to the parameters, displays the Maze3d ,solution or the crossSection.
	 * @param param - parameters for the Command. 
	 */
	@Override
	public void doCommand(String param) {
		String[] s = param.split(" ");
		if ((s[0].equals("cross"))&&(s.length > 5))		//validates if there are no missing parameters
		{
			try{
				switch (s[3])
				{
					case "x":
					case "X":
						presenter.getView().displayCrossSectionByX(Integer.parseInt(s[4]),s[6]);
						break;
					case "y":
					case "Y":
						presenter.getView().displayCrossSectionByY(Integer.parseInt(s[4]),s[6]);
						break;
					case "z":		
					case "Z":
						presenter.getView().displayCrossSectionByZ(Integer.parseInt(s[4]),s[6]);
						break;
					default:
						presenter.getView().displayError("Invalid parameter");		//sending error to user.
				}
			}catch (NumberFormatException e) {
				
				presenter.getView().displayError("Invalid parameters.");

			}
				 
		}
		else if ((s[0].equals("solution"))&&(s.length > 1))		//checks if the first parameter is "solution"
		{
			presenter.getView().display(presenter.getModel().getSolution(s[1]));
		}
		else
			presenter.getView().display(presenter.getModel().getMaze(param));
	}

}
