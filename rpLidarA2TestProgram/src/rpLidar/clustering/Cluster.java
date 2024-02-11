package rpLidar.clustering;

public class Cluster extends FixedArrayContainer<PolarPoint> {
	
	int currentIndex;
	
	public Cluster(int maxPoints) {
		super(maxPoints);
	}
}
