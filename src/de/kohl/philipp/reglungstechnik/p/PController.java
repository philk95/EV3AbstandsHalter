package de.kohl.philipp.reglungstechnik.p;

public class PController {

	public double calculate(PParameter parameter, double setpoint, double current) {
		double err = setpoint - current;

		return err * parameter.Kp;
	}
}
