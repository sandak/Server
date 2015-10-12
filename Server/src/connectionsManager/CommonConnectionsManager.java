package connectionsManager;

import controller.Controller;
import controller.Properties;

public abstract class CommonConnectionsManager {

	protected Properties properties;
	protected Controller controller;
	

	public abstract void setController(Controller controller);
}
