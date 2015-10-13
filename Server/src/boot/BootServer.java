package boot;

import connectionsManager.ManagmentHandler;
import connectionsManager.MazeClientHandler;
import connectionsManager.MyConnectionsManager;
import controller.Controller;
import model.Model;
import model.MyObservableModel;

public class BootServer {

	public static void main(String[] args) {
		Model model = new MyObservableModel();
		MyConnectionsManager ConnectionsManager = new MyConnectionsManager(new MazeClientHandler(),new ManagmentHandler());
		Controller controller = new Controller(model, ConnectionsManager);
		controller.start();
		

	}

}
