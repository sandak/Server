package connectionsManager;

import controller.Controller;
import controller.Properties;


/**
 * Common fields for every MVC connectionsManager.
 * @author Guy Golan && Amit Sandak	
 *
 */
public abstract class CommonConnectionsManager implements ConnectionManager {

	protected Properties properties;
	protected Controller controller;
	

	public abstract void setController(Controller controller);
}
