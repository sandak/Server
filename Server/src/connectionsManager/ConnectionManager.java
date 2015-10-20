package connectionsManager;

/**
 * Defines the abilities of a ConnectionManager.
 * @author guy
 *
 */
public interface ConnectionManager {
	
	/**
	 * Starts the game service.
	 */
	public void gameServerStart();
	
	/**
	 * Stops the game service.
	 */
	public void gameServerStop();
	
	/**
	 * exiting the connections.
	 */
	public void exit();
	
	/**
	 * receives a list of client's ip and removes them from the server.
	 * @param list - list of clients to be kicked from the server
	 */
	public void kickClients(String[] list) ;
}
