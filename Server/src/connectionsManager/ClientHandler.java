package connectionsManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import controller.Controller;

public interface ClientHandler {
	void handleClient(InputStream inFromClient, OutputStream outToClient, String clientId);

	void setController(Controller controller);


}
