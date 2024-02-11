package rpLidar.clustering;

import java.util.List;

public class PointHelper {

	/**
	 * Convertit un point en coordonnées polaires en un point en coordonnées cartésiennes en prenant comme échelle l'unité
	 * @param pp
	 * @return
	 */
	public static Point polarToCartesian(PolarPoint pp) {
		double angle = pp.angleIsRad ? pp.angle : Math.toRadians(pp.angle);
		return new Point(
			Math.cos(angle) * pp.distance,
			Math.sin(angle) * pp.distance
		);
	}
	
	public static PolarPoint cartesianToPolar(Point p) {
		return cartesianToPolar(p, true);
	}
	
	/**
	 * Convertit un point en coordonnées cartésiennes en un point en coordonnées polairesen prenant comme échelle l'unité
	 * @param p
	 * @return
	 */
	public static PolarPoint cartesianToPolar(Point p, boolean angleIsRad) {
		double distance = Math.sqrt(p.x * p.x + p.y * p.y);
		double angle = 0.;
		if (p.x == 0.) {
			if (p.y > 0) {
				angle = Math.PI / 2.;
			} else {
				angle = Math.PI / 1.5;
			}
		} else {
			angle = Math.atan(p.y / p.x);
		}
		
		if (p.x > 0.) {
			if (p.y < 0.) {
				angle += Math.PI * 2.;
			}
		} else if (p.x < 0.) {
			angle += Math.PI;
		}
		return new PolarPoint(
			angleIsRad ? angle : Math.toDegrees(angle),
			distance,
			angleIsRad
		);
	}
	
	/**
	 * Caclule le barycentre d'une liste de points en coordonnées cartésiennes
	 * @param points
	 * @return
	 */
	public static Point computeBarycenter(List<Point> points) {
		double x = 0.;
		double y = 0.;
		for (Point p : points) {
			x += p.x;
			y += p.y;
		}
		
		return new Point(
			x / (double) points.size(),
			y / (double) points.size()
		);
	}
	
	/**
	 * Calcule le barycentre d'une liste de points en coordonnées polaires
	 * @param points
	 * @return
	 */
	public static Point computeBarycenterFromPolar(List<PolarPoint> points) {
		double x = 0.;
		double y = 0.;
		for (PolarPoint pp : points) {
			Point p = polarToCartesian(pp);
			x += p.x;
			y += p.y;
		}
		
		return new Point(
			x / (double) points.size(),
			y / (double) points.size()
		);
	}
	
}
