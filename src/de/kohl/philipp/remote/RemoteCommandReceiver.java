package de.kohl.philipp.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteCommandReceiver extends Thread {

	private RemoteCommandReceiverListener listener;

	public RemoteCommandReceiver(RemoteCommandReceiverListener listener) {
		this.listener = listener;
		setDaemon(true);
	}

	@Override
	public void run() {
		try {
			ServerSocket cmdSock = new ServerSocket(1234);
			System.out.println("Waiting for Connection!");
			Socket s = cmdSock.accept();
			System.out.println("Connected!");

			InputStream inputStream = s.getInputStream();

			while (!s.isClosed()) {
				int length = inputStream.available();

				byte[] read = new byte[length];
				inputStream.read(read);

				String command = new String(read);
				listener.receivedCommand(command);
			}
			s.close();
			cmdSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
