package de.kohl.philipp.remote.sender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class RemoteRegulatorValueTransfer extends Thread {

	private int port;
	private ServerSocket cmdSock;
	private Socket s;

	public RemoteRegulatorValueTransfer(int port) {
		this.port = port;
		setDaemon(true);
	}

	@Override
	public void run() {
		try {
			cmdSock = new ServerSocket(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (true) {
			if (!isConnected()) {
				waitForConnection();
			}
		}
	}

	private void waitForConnection() {
		try {
			s = cmdSock.accept();
			System.out.println("RemoteRegulatorValueTransfer connected!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return s != null && s.isConnected() && !s.isClosed();
	}

	public void send(String key, String value) throws IOException {
		if (!isConnected()) {
			throw new NullPointerException("No device connected! Connect first and then send data!");
		}

		String message = String.format("[%s:%s]", key, value);
		try {
			s.getOutputStream().write(message.getBytes());
			s.getOutputStream().flush();
		} catch (SocketException e) {
			System.out.println("Seems like client has disconnected. Release ressources!");
			s.close();
		}
	}

	public void close() {
		try {
			if (isConnected()) {
				s.close();
			}
			cmdSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
