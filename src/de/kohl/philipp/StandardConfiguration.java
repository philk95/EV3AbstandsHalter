package de.kohl.philipp;

import lejos.hardware.Button;

public abstract class StandardConfiguration {

	public void start() {
		init();
		while (Button.ESCAPE.isUp()) {
			run();
		}
		shutdown();
	}

	protected abstract void init();

	protected abstract void run();

	protected abstract void shutdown();
}
