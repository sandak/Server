package connectionsManager;

import java.net.Socket;

/**
 * Used to define/handle different server protocols.
 * @author Guy Golan && Amit Sandak
 *
 */
public interface ClientHandler {
	
	/**
	 * define and implement the protocol.
	 * @param socket - the client's socket.
	 */
	void handleClient(Socket socket);

}
