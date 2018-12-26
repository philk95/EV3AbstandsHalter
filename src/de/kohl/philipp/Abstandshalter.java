package de.kohl.philipp;

import java.io.IOException;

import de.kohl.philipp.escape.EscapeListener;
import de.kohl.philipp.reglungstechnik.pid.PIDController;
import de.kohl.philipp.reglungstechnik.pid.PIDParameter;
import de.kohl.philipp.remote.RemoteCommandReceiver;
import de.kohl.philipp.remote.RemoteCommandReceiverListener;
import de.kohl.philipp.sensor.IRSensorExtended;
import de.kohl.philipp.sensor.IRSensorListener;
import lejos.hardware.Sound;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

public class Abstandshalter implements IRSensorListener, RemoteCommandReceiverListener {

	private Object lock = new Object();

	private UnregulatedMotor left = new UnregulatedMotor(MotorPort.D);
	private UnregulatedMotor right = new UnregulatedMotor(MotorPort.A);
	private PIDParameter parameter = new PIDParameter(6.0, 0.0, 0.0);
	private PIDController controller = new PIDController();
	private IRSensorExtended irSensor = new IRSensorExtended(SensorPort.S1);

	private boolean resetRegler = false;
	private double tOld = System.currentTimeMillis();

	public static void main(String[] args) throws IOException {
		Abstandshalter test = new Abstandshalter();
		test.run();
	}

	private void run() {
		EscapeListener exitOnEscape = new EscapeListener();
		exitOnEscape.start();

		RemoteCommandReceiver commandReceiver = new RemoteCommandReceiver(this);
		commandReceiver.start();

		Sound.twoBeeps();

		irSensor.setListener(this);

		while (true) {

		}
	}

	@Override
	public void notify(float oldValue, float newValue) {
		double tNew = System.currentTimeMillis();
		double delta = tNew - tOld;
		double newSpeed = 0;
		synchronized (lock) {
			newSpeed = -1 * controller.calculate(parameter, 20, newValue, delta, resetRegler);
			resetRegler = false;
		}

		double absSpeed = (Math.abs(newSpeed));

		if (absSpeed > 100) {
			absSpeed = 100;
		}

		System.out.println("Speed: " + absSpeed);

		setSpeed(absSpeed);

		if (newSpeed <= 0) {
			backward();
		} else {
			forward();
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

	@Override
	public void receivedCommand(String command) {
		String[] splitted = command.split(";");
		if (splitted.length == 3) {
			try {
				synchronized (lock) {
					float kp = Float.parseFloat(splitted[0]);
					float tv = Float.parseFloat(splitted[1]);
					float tn = Float.parseFloat(splitted[2]);
					parameter = new PIDParameter(kp, tv, tn);
					resetRegler = true;
					System.out.println("Received new parameter: " + parameter);
				}
			} catch (NumberFormatException e) {
				//System.out.println("RemoteCommandError: " + e.getMessage());
			}
		} else {
			//System.out.println("RemoteCommandError: Invalid length " + splitted.length);
		}
	}

}
