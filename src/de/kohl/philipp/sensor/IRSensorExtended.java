package de.kohl.philipp.sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;

public class IRSensorExtended extends EV3IRSensor implements Runnable {

	private IRSensorListener listener;
	private SampleProvider sampleProvider;
	private Thread t;
	private boolean running = true;

	public IRSensorExtended(Port port) {
		super(port);
		this.sampleProvider = getDistanceMode();
		this.t = new Thread(this);
		this.t.setDaemon(true);
		this.t.start();
	}

	@Override
	public void run() {
		float[] sample = new float[sampleProvider.sampleSize()];
		float oldValue = -1;
		float newValue = -2;

		while (running) {
			sampleProvider.fetchSample(sample, 0);
			newValue = sample[0];

			if (newValue == Float.MAX_VALUE || Float.isInfinite(newValue)) {
				newValue = 50;
			}

			if (newValue != oldValue) {
				notifyListener(oldValue, newValue);
				oldValue = newValue;
			}
		}

	}

	private void notifyListener(float oldValue, float newValue) {
		if (listener != null) {
			listener.valueChanged(oldValue, newValue);
		}

	}

	public void setListener(IRSensorListener listener) {
		this.listener = listener;
	}

	public void shutdown() {
		running = false;
		close();
	}
}
