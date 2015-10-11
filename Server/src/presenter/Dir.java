package presenter;

import java.io.File;

/**
 * Defines what the Command Dir should do.
 * @author Guy Golan & Amit Sandak
 *
 */
public class Dir extends CommonCommand{

	public Dir(	Presenter Presenter) {		//Ctor
		super(Presenter);
	}
	
	/**
	 * Using type File to display the files in the path given
	 * @param param - the file path.
	 */
	@Override
	public void doCommand(String param) {

		if(param != null)
		{
			String[] n = new File(param).list();
			if(n!=null)
				presenter.getView().display(n);
			else
				presenter.getView().displayError("path error.");
		}
		else
		{
			presenter.getView().displayError("Missing parameters");
		}
		
	}

}
