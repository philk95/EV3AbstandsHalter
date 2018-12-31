package de.kohl.philipp;

import java.io.IOException;

import de.kohl.philipp.reglungstechnik.pid.PIDController;
import de.kohl.philipp.reglungstechnik.pid.PIDParameter;
import de.kohl.philipp.remote.RemoteCommandReceiver;
import de.kohl.philipp.remote.RemoteCommandReceiverListener;
import de.kohl.philipp.remote.RemoteRegulatorValueTransfer;
import de.kohl.philipp.sensor.IRSensorExtended;
import de.kohl.philipp.sensor.IRSensorListener;
import lejos.hardware.Sound;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

public class Abstandshalter extends StandardConfiguration implements IRSensorListener, RemoteCommandReceiverListener {

	private Object lock = new Object();

	private UnregulatedMotor left = new UnregulatedMotor(MotorPort.D);
	private UnregulatedMotor right = new UnregulatedMotor(MotorPort.A);
	private PIDParameter parameter = new PIDParameter(6.0, 0.0, 0.0);
	private PIDController controller = new PIDController();
	private IRSensorExtended irSensor = new IRSensorExtended(SensorPort.S1);

	private RemoteRegulatorValueTransfer remoteValueTf;

	private boolean resetRegler = false;
	private double tOld = System.currentTimeMillis();

	@Override
	protected void init() {
		RemoteCommandReceiver commandReceiver = new RemoteCommandReceiver(1234, this);
		commandReceiver.start();

		remoteValueTf = new RemoteRegulatorValueTransfer(1235);
		remoteValueTf.start();

		irSensor.setListener(this);

		Sound.twoBeeps();
	}

	@Override
	protected void run() {

	}

	@Override
	protected void shutdown() {
		System.out.println("Shutting down!");
		remoteValueTf.close();
		irSensor.shutdown();
		System.out.println("Shutting down complete! Bye bye!");
	}

	@Override
	public void valueChanged(float oldValue, float newValue) {
		if (remoteValueTf.isConnected()) {
			try {
				remoteValueTf.send("" + newValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

//		System.out.println("Speed: " + absSpeed);

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
				// System.out.println("RemoteCommandError: " + e.getMessage());
			}
		} else {
			// System.out.println("RemoteCommandError: Invalid length " + splitted.length);
		}
	}
}
