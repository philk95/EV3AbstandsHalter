package de.kohl.philipp.reglungstechnik.pid;

public class PIDController {
	private Integrator integrator = null;
	private double oldErr;

	public PIDController() {
		integrator = new Integrator();
	}

	public double calculate(PIDParameter p, double setpoint, double current, double t, boolean reset) {
		double err = setpoint - current;
		double integral = integrator.integrate(err, t, reset);
		double result = err;
		if (p.Tn > 0.0) { // Verhindere Division durch 0 ...
			result += integral / p.Tn;
		}
		double d = (err - oldErr) / t;
		result += p.Tv * d;

		oldErr = err;

		return result * p.Kp;
	}
}
