package controller;

/**
 * Defines what the Command Exit should do.
 * 
 * @author Guy Golan & Amit Sandak
 *
 */
public class Exit extends CommonCommand {

	public Exit(Controller controller) { // Ctor
		super(controller);
	}

	/**
	 * Activates a chain of safe exit using the presenter.
	 */
	@Override
	public void doCommand(String param) {
		if (controller.getProperties().isDebug())
			System.out.println("PRESENTER EXIT");
		controller.getView().exit(); // safely exiting model and view.
		controller.getModel().exit();

	}

}
