package de.kohl.philipp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.util.Scanner;

import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;

public class PCRemote {

	public static void main(String[] args) throws NotBoundException, UnknownHostException, IOException {
		BrickInfo[] bricks = BrickFinder.discover();

		if (bricks.length == 0) {
			throw new IllegalArgumentException("No brick found!");
		}

		Socket s = new Socket(bricks[0].getIPAddress(), 1234);
		System.out.println("Connected: " + s);
		s.setTcpNoDelay(true);

		String command = null;

		OutputStream outputStream = s.getOutputStream();
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print("Command: ");
			command = scanner.nextLine();
			if ("exit".equals(command)) {
				break;
			}
			outputStream.write(command.getBytes());
			outputStream.flush();
		}

		scanner.close();
		outputStream.close();
		s.close();
	}
}
