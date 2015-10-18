package controller;

import java.util.ArrayList;
import java.util.HashMap;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import connectionsManager.MyConnectionsManager;
import model.Model;

/**
 * The unit which observes the independent activity of the View and Model
 * @author Guy Golan && Amit Sandak.
 *
 */
public class Controller {
	private Model model;	//the model.
	private MyConnectionsManager connectionsMgmt;		//the view.
	private Properties properties;		//system properties.
	
	private HashMap<String, Command> commandMap;	//the presenter available commands in a map.
	
	
	public Controller(Model model, MyConnectionsManager connectionsMgmt) {
		super();
		this.setModel(model);
		this.setConnectionsMgmt(connectionsMgmt);
		
		properties = new Properties();	//creating default properties.
		properties.setDefaults();
		model.setProperties(properties);	//informing the model of the system properties.
		
		this.commandMap = new HashMap<String , Command>();		//inserting all the Commands into the map
		commandMap.put("generate", new Generate(this));
		commandMap.put("solve", new Solve(this));
						
	}

	
	public void update(String identifier) {
	
		
		Command c = commandMap.get(identifier.split(" ")[0]);
		
		if(c != null)
		{
			if(identifier.split(" ").length > 1)
			{
				c.doCommand(identifier.substring(identifier.indexOf(' ')+1));			//executing the command.
			}
			else if (!identifier.equals("exit"))
			{
				//getView().displayError("Missing parameters."); // TODO HANDLE ERRORS
			}
			else
			{
				c.doCommand("");
			}
		}
		else
		{
			//getView().displayError(identifier + " is not a valid command.");	// TODO HANDLE ERRORS
		}
		
	}
								//-------------GETTERS & SETTERS-----------------
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
		model.setController(this);
	}



	public void setConnectionsMgmt(MyConnectionsManager connectionsMgmt ) {
		this.connectionsMgmt = connectionsMgmt;
		connectionsMgmt.setController(this);
	}

	public Properties getProperties() {
		return properties;
	}

	public void setDebugMode(boolean b) {
		properties.setDebug(b);
		
	}
	//--------------------------------------------------------------------------------
	/**
	 * informing the ConnectionsManager and model of the system properties.
	 * @param prop - properties.
	 */
	public void setProperties(Properties prop) {
		this.properties = prop;
		if (model != null)
			this.model.setProperties(prop);
		if (connectionsMgmt!=null)
			this.connectionsMgmt.setProperties(prop);
		
	}

	public void closeConnections() {
		connectionsMgmt.exit();
		
	}

	public Maze3d getMaze(String name) {
		return model.getMaze(name);
		
	}


	public void start() {
		try {
			connectionsMgmt.mgmtStart();
			connectionsMgmt.gameServerStart();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public void gameServerStop() {
		connectionsMgmt.gameServerStop();
		
	}
	
	public void gameServerStart() {
		connectionsMgmt.gameServerStart();
		
	}


	public boolean getStatus() {
		return connectionsMgmt.getGameServerStatus();
	}


	public Solution<Position> getSolution(String name) {
		return model.getSolution(name);
	}


	public void register(String hostAddress) {
		connectionsMgmt.register(hostAddress);
	}


	public void unregister(String hostAddress) {
		connectionsMgmt.unregister(hostAddress);
		
	}


	public ArrayList<String[]> getClientsList() {
		return connectionsMgmt.getClientsList();
	}


	public void syncAdmin(String param ,String hostAddress) {
		connectionsMgmt.syncAdmin(param, hostAddress);
		
	}


	public void kickClients(String[] list) {
		connectionsMgmt.kickClients(list);
		
	}


	public void syncAdmins(String string) {
		connectionsMgmt.syncAdmins(string);
		
	}


	public void exit() {
		connectionsMgmt.syncAdmins("shutting down");
		model.exit();
		connectionsMgmt.exit();
		
	}
				
}
	


