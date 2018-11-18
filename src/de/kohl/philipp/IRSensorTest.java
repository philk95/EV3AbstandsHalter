package de.kohl.philipp;

import java.io.IOException;

import de.kohl.philipp.reglungstechnik.p.PController;
import de.kohl.philipp.reglungstechnik.p.PParameter;
import de.kohl.philipp.reglungstechnik.pid.PIDController;
import de.kohl.philipp.reglungstechnik.pid.PIDParameter;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.SensorPort;

public class IRSensorTest implements IRSensorListener {

	private static final NXTRegulatedMotor D = Motor.D;
	private static final NXTRegulatedMotor A = Motor.A;

	private PIDParameter parameter = new PIDParameter(30.0, 0.0, 0.0);
	private PIDController controller = new PIDController();
	private IRSensorExtended irSensor = new IRSensorExtended(SensorPort.S1);
	private double tOld;

	public static void main(String[] args) throws IOException {
		IRSensorTest test = new IRSensorTest();
		test.run();

	}

	private void run() {
		A.setAcceleration(6000);
		D.setAcceleration(6000);
		Sound.twoBeeps();

		Thread escapeListener = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (Button.ESCAPE.isDown()) {
						break;
					}
				}
//				pilot.stop();

				irSensor.close();
				System.exit(0);
			}

		});
		escapeListener.setDaemon(true);
		escapeListener.start();

		irSensor.setListener(this);

		while (true) {

		}

	}

	private void forward(float speed) {
		setSpeed(speed);
		A.forward();
		D.forward();
	}

	private void backwards(float speed) {
		setSpeed(speed * 50);
		A.backward();
		D.backward();
	}

	private void setSpeed(float speed) {
		A.setSpeed(speed);
		D.setSpeed(speed);
	}

	@Override
	public void notify(float oldValue, float newValue) {
		double tNew = System.currentTimeMillis();
		double newSpeed = -1 * controller.calculate(parameter, 10, newValue, tNew - tOld, false);
		System.out.println("New speed: " + newSpeed);

		if (newSpeed <= 0) {
			backwards(newValue);
		} else {
			forward(newValue);
		}

		tOld = tNew;
	}

}
