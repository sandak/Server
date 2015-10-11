package model;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface ClientHandler {
	void handleClient(InputStream inFromClient, OutputStream outToClient);

	void acceptNewClient(Socket someClient);
}
