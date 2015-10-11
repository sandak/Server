package presenter;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

/**
 * Usually notified from the model when a regular command was completed or not,
 * this command activates and tells the presenter what to do.
 * @author Guy Golan & Amit Sandak.
 *
 */
public class CompletedTask extends CommonCommand {

	public CompletedTask(Presenter presenter) {
		super(presenter);

	}

	@Override
	public void doCommand(String param) {
		String[] s = param.split(" ");
		if ((s[0].equals("maze")) && (s.length > 2)) // validates if there are no missing parameters
		{
			Maze3d maze = presenter.getModel().getMaze(s[2]);
			if (maze != null) {
				presenter.getView().display(s[2] + " maze generated.");			//displaying a 'maze generated' message.
				presenter.getView().display(presenter.getModel().getMaze(s[2]));	//displaying the newly generated maze.
				presenter.getView().display(presenter.getModel().getCharPosition(s[2]));	//displaying the player's character.
			} else
				presenter.getView().displayError("Unavailable maze!");		//sending an error to the user.

		} else if ((s[0].equals("clue")) && (s.length > 1)) // checks if the first parameter is "solution".
		{
			Solution<Position> result = presenter.getModel().getSolution(s[1]);	
			if (result != null)
				presenter.getView().display(result);	//displaying the solution.
			else
				presenter.getView().displayError("Unavailable solution!");		//sending an error to the user.
		} else if (s[0].equals("save")) {
			presenter.getView().display("Save completed.");		//message display.
		} else if (s[0].equals("load")) {
			
			presenter.getView().display("Load completed.");						//message display.
			presenter.getView().display(presenter.getModel().getMaze(s[1]));	//displaying newly loaded maze.
			presenter.getView().display(presenter.getModel().getMaze(s[1]).getEntrance());	//displaying character from entrance.
			
		} else if ((s[0].equals("fileSize")) && (s.length > 2)) {
			presenter.getView().display(s[1] + " file size is: " + s[2]);	//displaying message.
		} else if ((s[0].equals("mazeSize")) && (s.length > 1)) {
			presenter.getView().display("maze size is: " + s[1]);	//displaying message.
		} else if (s[0].equals("error") && (s.length > 1)) {
			presenter.getView().displayError(param.substring(6));	//display the error to user.
		} else if (s[0].equals("movement") && (s.length > 1)) {
			presenter.getView().display(presenter.getModel().getCharPosition(s[1]));	//displaying the newly changed character position.
			if (presenter.getProperties().isDebug() == true)
				System.out.println("position updated");
		}
	}
}
