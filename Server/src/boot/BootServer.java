package boot;

import connectionsManager.MazeClientHandler;
import connectionsManager.MyConnectionsManager;
import controller.Controller;
import model.Model;
import model.MyObservableModel;

public class BootServer {

	public static void main(String[] args) {
		Model model = new MyObservableModel();
		MyConnectionsManager ConnectionsManager = new MyConnectionsManager(new MazeClientHandler());
		Controller controller = new Controller(model, ConnectionsManager);
		controller.start();
		

	}

}
