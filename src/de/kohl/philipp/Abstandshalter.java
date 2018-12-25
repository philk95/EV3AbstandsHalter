package de.kohl.philipp;

import java.io.IOException;

import de.kohl.philipp.reglungstechnik.pid.PIDController;
import de.kohl.philipp.reglungstechnik.pid.PIDParameter;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class Abstandshalter implements IRSensorListener {

	Wheel wheel1 = WheeledChassis.modelWheel(Motor.A, 10).offset(-15);

	Wheel wheel2 = WheeledChassis.modelWheel(Motor.D, 10).offset(15);

	Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);

	MovePilot pilot = new MovePilot(chassis);
	private PIDParameter parameter = new PIDParameter(10.0, 0.0, 0.0);
	private PIDController controller = new PIDController();
	private IRSensorExtended irSensor = new IRSensorExtended(SensorPort.S1);

	private boolean moveForward = true;
	private double tOld = System.currentTimeMillis();
	private double oldSpeed = 0;

	public static void main(String[] args) throws IOException {
		Abstandshalter test = new Abstandshalter();
		test.init();
		// test.costum();
		test.run();
	}

	private void costum() {
		// try {
		for (int i = 0; i < 3; i++) {
			pilot.forward();
			// Thread.sleep(1000);
		}
		for (int i = 0; i < 3; i++) {
			pilot.backward();
			// Thread.sleep(1000);
			pilot.forward();
		}
		// } catch (InterruptedException e) {

		// }

	}

	private void init() {
		pilot.setLinearSpeed(pilot.getMaxLinearSpeed() * 0.5);
		pilot.setAngularSpeed(pilot.getMaxAngularSpeed() * 0.5);
		pilot.setLinearAcceleration(500);
		pilot.setAngularAcceleration(1500);
	}

	private void run() {
		Sound.twoBeeps();
		pilot.forward();

		Thread escapeListener = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (Button.ESCAPE.isDown()) {
						break;
					}
				}
				pilot.stop();

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

	@Override
	public void notify(float oldValue, float newValue) {
		double tNew = System.currentTimeMillis();
		double delta = tNew - tOld;
		System.out.println("Del: " + delta);
		double newSpeed = -1 * controller.calculate(parameter, 20, newValue, delta, false);

		double absSpeed = (Math.abs(newSpeed) + 1) * 10;

		System.out.println("Speed: " + absSpeed);
		pilot.setLinearAcceleration(absSpeed);
		pilot.setLinearSpeed(absSpeed);

		if (newSpeed <= 0) {
			if (moveForward || significantSpeedChange(absSpeed)) {
				pilot.backward();
				moveForward = false;
			}
		} else {
			if (!moveForward || significantSpeedChange(absSpeed)) {
				pilot.forward();
				moveForward = true;
			}
		}

		tOld = tNew;
		oldSpeed = absSpeed;
	}

	private boolean significantSpeedChange(double absSpeed) {
		return Math.abs(oldSpeed - absSpeed) > 100;
		
	}

}
