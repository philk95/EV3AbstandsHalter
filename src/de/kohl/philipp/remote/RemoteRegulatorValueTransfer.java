package de.kohl.philipp.remote;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteRegulatorValueTransfer extends Thread {

	private int port;
	private OutputStream outputStream;
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
			s = cmdSock.accept();
			System.out.println("RemoteRegulatorValueTransfer connected!");
			outputStream = s.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return s != null && s.isConnected() && !s.isClosed();
	}

	public void send(String command) throws IOException {
		if (!isConnected()) {
			throw new NullPointerException("No device connected! Connect first and then send data!");
		}

		s.getOutputStream().write(command.getBytes());
		s.getOutputStream().flush();
	}

	public void close() {
		try {
			outputStream.close();
			s.close();
			cmdSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
