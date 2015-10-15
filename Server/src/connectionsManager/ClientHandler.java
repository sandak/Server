package connectionsManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import controller.Controller;

public interface ClientHandler {
	void handleClient(Socket socket);




}
