package de.kohl.philipp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;

import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;

public class PCRemote {

	public static void main(String[] args) throws NotBoundException, UnknownHostException, IOException {
		BrickInfo[] bricks = BrickFinder.discover();

		System.out.println(bricks.length);

		Socket s = new Socket(bricks[0].getIPAddress(), 1234);
		s.setTcpNoDelay(true);
		OutputStream outputStream = s.getOutputStream();
		outputStream.write("Test".getBytes());
		outputStream.flush();

		outputStream.close();
		s.close();
	}
}
