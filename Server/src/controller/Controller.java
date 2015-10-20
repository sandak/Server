package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import connectionsManager.MyConnectionsManager;
import model.Model;

/**
 * The unit which controls the independent activity of the View and Model
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

	
	/**
	 * update activates the received command.
	 * @param identifier - a string containing the command to be activated and its parameters.
	 */
	public void update(String identifier) {
	
		
		Command c = commandMap.get(identifier.split(" ")[0]);
		
		if(c != null)
		{
			if(identifier.split(" ").length > 1)
			{
				c.doCommand(identifier.substring(identifier.indexOf(' ')+1));			//executing the command.
			}
			
			
		}
		
		
	}
								//-------------GETTERS & SETTERS-----------------
	public Model getModel() {
		return model;
	}

	/**
	 * setting the model to the given model .
	 * also notifying the received model of its controller.
	 * @param model - the model
	 */
	public void setModel(Model model) {
		this.model = model;
		model.setController(this);
	}


	/**
	 * setting the connectionManager to the given connectionManager .
	 * also notifying the received connectionManager of its controller.
	 * @param connectionManager - the connectionManager
	 */
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
	/**
	 * closing the connections in an orderly fashion using the connections manager.
	 */
	public void closeConnections() {
		connectionsMgmt.exit();
		
	}

	/**
	 * using the model to get a stored Maze3d
	 * @param name - the name of the maze.
	 * @return the desired maze.
	 */
	public Maze3d getMaze(String name) {
		return model.getMaze(name);
		
	}


	/**
	 * starting the listening process to the admins and clients.
	 */
	public void start() {
		try {
			connectionsMgmt.mgmtStart();
			connectionsMgmt.gameServerStart();
		} catch (Exception e) {
			if(properties.isDebug())
				e.printStackTrace();
		}
		
	}


	/**
	 * stops the game service.
	 */
	public void gameServerStop() {
		connectionsMgmt.gameServerStop();
		
	}
	/**
	 * stops the managment service.
	 */
	public void gameServerStart() {
		connectionsMgmt.gameServerStart();
		
	}

	/**
	 * using the connectionManager to get the game server status.
	 * @return
	 */
	public boolean getStatus() {
		return connectionsMgmt.getGameServerStatus();
	}

/**
 * using the model to get a stored solution.
 * @param name - the name of the maze.
 * @return - the solution.
 */
	public Solution<Position> getSolution(String name) {
		return model.getSolution(name);
	}


	/**
	 * registering a new admin to the server.
	 * @param hostAddress
	 */
	public void register(String hostAddress) {
		connectionsMgmt.register(hostAddress);
	}

	/**
	 * unregistering an admin from the server.
	 * @param hostAddress
	 */
	public void unregister(String hostAddress) {
		connectionsMgmt.unregister(hostAddress);
		
	}


	/**
	 * getting the current clients list from the connectionManager.
	 * @return
	 */
	public ArrayList<String[]> getClientsList() {
		return connectionsMgmt.getClientsList();
	}


	/**
	 * using the connectionManager to sync data to the admin.
	 * @param param - the nature of the sync action.
	 * @param hostAddress - the ip if the desired admin.
	 */
	public void syncAdmin(String param ,String hostAddress) {
		connectionsMgmt.syncAdmin(param, hostAddress);
		
	}


	/**
	 * using the connectionManager to kick clients from the server.
	 * @param list - the ip of the desired clients.
	 */
	public void kickClients(String[] list) {
		connectionsMgmt.kickClients(list);
		
	}


	/**
	 * using the connectionManager to sync information about the server to the different admins.
	 * @param string
	 */
	public void syncAdmins(String string) {
		connectionsMgmt.syncAdmins(string);
		
	}


	/**
	 * exits the server in an orderly fashion 
	 */
	public void exit() {
		connectionsMgmt.syncAdmins("shutting down"); // notifying the connected admin of the server shutdown.
		
		Timer t = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				model.exit();		//shutting the model down
				connectionsMgmt.exit(); // shutting the connections down.
				t.cancel();
			}
		};
		t.scheduleAtFixedRate(task, 1000*3, 1000*3);
		
	}
				
}
	


