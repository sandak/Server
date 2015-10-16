package connectionsManager;

public interface ConnectionManager {
	
	public void gameServerStart();
	public void gameServerStop();
	public void exit();
	public void kickClients(String[] list) ;
}
