package de.kohl.philipp.escape;

import lejos.hardware.Button;

public class EscapeListener extends Thread {

	public EscapeListener() {
		setDaemon(true);
	}

	@Override
	public void run() {
		while (true) {
			if (Button.ESCAPE.isDown()) {
				break;
			}
		}

		System.exit(0);
	}
}
