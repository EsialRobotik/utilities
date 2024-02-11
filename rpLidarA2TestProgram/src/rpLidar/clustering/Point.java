package rpLidar.clustering;

public class Point {

	public double x;
	public double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return this.x + " x ; " + this.y + " y";
	}
	
	public java.awt.Point toAwtPoint(int offsetX, int offsetY) {
		return new java.awt.Point(
			offsetX + (int) Math.round(this.x),
			offsetY + (int) Math.round(this.y)
		);
	}
	
	/**
	 * Renvoie une copie de l'instance actuelle dont les coordonnées ont été multipliées par l'échelle donnée
	 * @param echelle
	 * @return
	 */
	public Point scale(double echelle) {
		return new Point(this.x * echelle, this.y * echelle);
	}
}
