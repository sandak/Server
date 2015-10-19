package boot;

import connectionsManager.ManagmentHandler;
import connectionsManager.MazeClientHandler;
import connectionsManager.MyConnectionsManager;
import controller.Controller;
import model.Model;
import model.MyObservableModel;

public class BootServer {

	public static void main(String[] args) {
//		Form f = new Form(Properties.class, "properties");
//		f.run();
//		System.out.println((Properties)f.getObject());
		Model model = new MyObservableModel();
		MyConnectionsManager ConnectionsManager = new MyConnectionsManager(new MazeClientHandler(),new ManagmentHandler());
		Controller controller = new Controller(model, ConnectionsManager);
//		controller.setProperties((Properties)f.getObject());
		controller.start();
		

	}

}
