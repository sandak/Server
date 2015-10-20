package boot;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import connectionsManager.ManagmentHandler;
import connectionsManager.MazeClientHandler;
import connectionsManager.MyConnectionsManager;
import controller.Controller;
import controller.Properties;
import model.Model;
import model.MyObservableModel;


public class BootServer {

	public static void main(String[] args) {
		String defaultXMLname = "properties.xml";
		Properties prop;
		try {
			FileInputStream in = new FileInputStream(defaultXMLname);		//tries to read the properties.xml default pathname.
			XMLDecoder decoder = new XMLDecoder(in);
			prop = (Properties)decoder.readObject();		//decoding the xml file.
			decoder.close();
					
		} catch (FileNotFoundException e) {				//if no properties.xml was found in directory, generating default properties.
			System.out.println("file not found, default properties will be loaded");
			prop = new Properties();
			prop.setDefaults();
		}
		
	
		Model model = new MyObservableModel();
		MyConnectionsManager ConnectionsManager = new MyConnectionsManager(new MazeClientHandler(),new ManagmentHandler());
		Controller controller = new Controller(model, ConnectionsManager);
		controller.setProperties(prop);
		controller.start();
		

	}

}
