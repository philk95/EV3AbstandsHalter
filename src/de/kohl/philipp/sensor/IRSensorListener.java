package de.kohl.philipp.sensor;

public interface IRSensorListener {
	void notify(float oldValue, float newValue);
}
