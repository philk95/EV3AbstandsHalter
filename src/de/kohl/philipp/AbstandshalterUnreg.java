package de.kohl.philipp;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import de.kohl.philipp.reglungstechnik.pid.PIDController;
import de.kohl.philipp.reglungstechnik.pid.PIDParameter;
import de.kohl.philipp.sensor.IRSensorExtended;
import de.kohl.philipp.sensor.IRSensorListener;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

public class AbstandshalterUnreg implements IRSensorListener {

	UnregulatedMotor left = new UnregulatedMotor(MotorPort.D);
	UnregulatedMotor right = new UnregulatedMotor(MotorPort.A);
	private PIDParameter parameter = new PIDParameter(6.0, 0.0, 10.0);
	private PIDController controller = new PIDController();
	private IRSensorExtended irSensor = new IRSensorExtended(SensorPort.S1);

	private double tOld = System.currentTimeMillis();

	public static void main(String[] args) throws IOException {
		AbstandshalterUnreg test = new AbstandshalterUnreg();
		test.costum();
		// test.run();
	}

	private void costum() {
		try {
			ServerSocket cmdSock = new ServerSocket(1234);
			System.out.println("Waiting for Connection!");
			Socket s = cmdSock.accept();
			System.out.println("Connected!");

			InputStream inputStream = s.getInputStream();
			int length = inputStream.available();

			byte[] read = new byte[length];
			inputStream.read(read);

			String test = new String(read);
			System.out.println(test);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s.close();
			cmdSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void run() {
		Sound.twoBeeps();

		Thread escapeListener = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (Button.ESCAPE.isDown()) {
						break;
					}
				}

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

}
