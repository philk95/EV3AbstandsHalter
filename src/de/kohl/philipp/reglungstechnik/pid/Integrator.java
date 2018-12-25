package de.kohl.philipp.reglungstechnik.pid;

public class Integrator {
	double last_signal = 0;
	double integral = 0;

	public double integrate(double signal, double t, boolean reset) {
		if (!reset) {
			double offset = ((signal + last_signal) * t) / 2;
			integral += offset;
			last_signal = signal;
			return integral;
		} else {
			last_signal = 0;
			integral = 0;
			return 0;
		}
	}
}