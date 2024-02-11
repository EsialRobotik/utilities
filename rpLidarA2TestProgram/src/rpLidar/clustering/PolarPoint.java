package rpLidar.clustering;

public class PolarPoint {

	public boolean angleIsRad;

	public double angle;
	
	public double distance;

	public PolarPoint(double angle, double distance, boolean angleIsRad) {
		this.angle = angle;
		this.distance = distance;
		this.angleIsRad = angleIsRad;
	}
	
	public String toString() {
		return this.angle + (angleIsRad ? "rad" : "deg") + " / " + this.distance;
	}
	
	/**
	 * Renvoie une copie de l'instance actuelle avec sa distance est multipliée apr l'échelle
	 * @param echelle
	 * @return
	 */
	public PolarPoint scale(double echelle) {
		return new PolarPoint(this.angle, this.distance * echelle, this.angleIsRad);
	}
	
}
