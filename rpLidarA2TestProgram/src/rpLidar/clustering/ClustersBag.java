package rpLidar.clustering;

public class ClustersBag extends FixedArrayContainer<Cluster> {
	
	Cluster clusters[];

	public ClustersBag(int maxSize) {
		super(maxSize);
	}
}
