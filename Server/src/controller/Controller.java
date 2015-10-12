package controller;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import algorithms.mazeGenerators.Maze3d;
import connectionsManager.MyConnectionsManager;
import connectionsManager.View;
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
		commandMap.put("dir", new Dir(this));
		commandMap.put("generate", new Generate(this));
		commandMap.put("display", new Display(this));
		commandMap.put("save", new Save(this));
		commandMap.put("load", new Load(this));
		commandMap.put("maze", new Size(this));
		commandMap.put("file", new FileSize(this));
		commandMap.put("solve", new Solve(this));
		commandMap.put("exit", new Exit(this));
		commandMap.put("completedTask", new CompletedTask(this));
		commandMap.put("movementRequest", new MovmentRequest(this));
		commandMap.put("clue", new Clue(this));
		commandMap.put("propertiesUpdate", new PropertiesUpdate(this));

						
		
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
	 * informing the view and model of the system properties.
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

	public void generateClientHandler(BufferedReader in, PrintWriter out, String clientId) {
		connectionsMgmt.generateClientHandler(in,out,clientId);
		
	}


	public void getMazeClientHandler(BufferedReader in, PrintWriter out) {
		connectionsMgmt.getMazeClientHandler(in,out);
		
	}


	public Maze3d getMaze(String name) {
		return model.getMaze(name);
		
	}


	public void start() {
		try {
			connectionsMgmt.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public void complete(String string) {
		connectionsMgmt.complete(string);
		
	}
					
}
	

