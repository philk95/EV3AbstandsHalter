package de.kohl.philipp.reglungstechnik.pid;

public class PIDParameter {
	public PIDParameter(double kp, double tv, double tn) {
		this.Kp = kp;
		this.Tn = tn;
		this.Tv = tv;
	}

	public double Kp;
	public double Tv;
	public double Tn;
}
