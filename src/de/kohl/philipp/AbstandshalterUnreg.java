package de.kohl.philipp;

import java.io.IOException;

import de.kohl.philipp.reglungstechnik.pid.PIDController;
import de.kohl.philipp.reglungstechnik.pid.PIDParameter;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

public class AbstandshalterUnreg implements IRSensorListener {

	UnregulatedMotor left = new UnregulatedMotor(MotorPort.D);
	UnregulatedMotor right = new UnregulatedMotor(MotorPort.A);
	private PIDParameter parameter = new PIDParameter(6.0, 0.0, 0.0);
	private PIDController controller = new PIDController();
	private IRSensorExtended irSensor = new IRSensorExtended(SensorPort.S1);

	private boolean moveForward = true;
	private double tOld = System.currentTimeMillis();

	public static void main(String[] args) throws IOException {
		AbstandshalterUnreg test = new AbstandshalterUnreg();
		test.init();
		test.run();
	}

	private void init() {
//		pilot.setLinearSpeed(pilot.getMaxLinearSpeed() * 0.5);
//		pilot.setAngularSpeed(pilot.getMaxAngularSpeed() * 0.5);
//		pilot.setLinearAcceleration(500);
//		pilot.setAngularAcceleration(1500);
	}

	private void run() {
		Sound.twoBeeps();
		// pilot.forward();

		Thread escapeListener = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (Button.ESCAPE.isDown()) {
						break;
					}
				}
				// pilot.stop();

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
		double newSpeed = -1 * controller.calculate(parameter, 20, newValue, delta, false);

		double absSpeed = (Math.abs(newSpeed));

		if (absSpeed > 100) {
			absSpeed = 100;
		}

		System.out.println("Speed: " + absSpeed);

		setSpeed(absSpeed);
//		pilot.setLinearAcceleration(absSpeed);
//		pilot.setLinearSpeed(absSpeed);

		if (newSpeed <= 0) {
			if (moveForward) {
				backward();
				moveForward = false;
			}
		} else {
			if (!moveForward) {
				forward();
				moveForward = true;
			}
		}

		tOld = tNew;
	}

	private void setSpeed(double absSpeed) {
		left.setPower((int) absSpeed);
		right.setPower((int) absSpeed);

	}

	private void forward() {
		left.forward();
		right.forward();

	}

	private void backward() {
		left.backward();
		right.backward();

	}

}
