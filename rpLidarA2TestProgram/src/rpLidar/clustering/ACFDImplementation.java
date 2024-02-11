package rpLidar.clustering;

import rpLidar.RpLidarScanHandlerInterface;

/**
 * Array Clustering For Dummies Implementation
 * @author gryttix
 *
 */
public class ACFDImplementation implements RpLidarScanHandlerInterface {

	final int POINTS_PAR_DEGRES = 64;
	final int MAX_POINTS_PER_CLUSTER = 100;
	int minPointsPerCLuster;
	int maxPointsPerCluster;
	int maxClusterCount;
	double ignoreDistanceAbove;
	Double points[];
	
	int maxPointsToSkip = 100;
	double MaxDdistanceBetween2PointsMm = 10.;
	double MaxAngleBetween2PointsDegrees = 1.;
	
	/**
	 * 
	 * @param ignoreDistanceAbove
	 * @param minPointsPerCluster
	 * @param maxPointsPerCluster
	 * @param maxClusterCount
	 * @param maxDistanceBetween2Pointsmm
	 * @param maxAngleBetween2PointsDegrees
	 * @param maxPointsToSkip
	 */
	public ACFDImplementation(double ignoreDistanceAbove, int minPointsPerCluster, int maxPointsPerCluster, int maxClusterCount, double maxDistanceBetween2Pointsmm, double maxAngleBetween2PointsDegrees, int maxPointsToSkip) {
		this.ignoreDistanceAbove = ignoreDistanceAbove;
		this.MaxDdistanceBetween2PointsMm = maxDistanceBetween2Pointsmm;
		this.MaxAngleBetween2PointsDegrees = maxAngleBetween2PointsDegrees;
		this.maxPointsToSkip = maxPointsToSkip;
		this.points = new Double[360 * POINTS_PAR_DEGRES];
		for (int i=0; i<points.length; i++) {
			this.points[i] = 0.;
		}
		this.minPointsPerCLuster = minPointsPerCluster;
		this.maxPointsPerCluster = maxPointsPerCluster;
		this.maxClusterCount = maxClusterCount;
	}

	@Override
	public void handleLidarScan(int quality, boolean nouvelleRotation, double angle, double distance) {
		if (distance > this.ignoreDistanceAbove) {
			return;
		}

		int index = (int) (angle * POINTS_PAR_DEGRES);
		
		synchronized (points) {
			if (index > points.length) {
				return;
			}

			points[index] = distance;	
		}
	}
	
	/**
	 * Effectue le partitionnement des points lus
	 * @return void
	 */
	public ClustersBag doClustering() {
		ClustersBag cb = new ClustersBag(this.maxClusterCount);
		int emptyPoints = 0;

		Cluster cluster = new Cluster(this.maxPointsPerCluster);
		
		// Sécurisation du tableau de points le temps de l'analyse
		synchronized (points) {
			// On parcourt tous les points jusqu'à arriver au bout de la liste ou avoir trouvé le nombre max de clusters
			for (int i=0; i<points.length; i++) {
				double angle = (double) i / (double) POINTS_PAR_DEGRES;
				double distance = points[i];
				points[i] = 0.;
				if (cluster.isEmpty()) {
					if (distance > 0.1) {
						cluster.add(new PolarPoint(angle, distance, false));
						emptyPoints = 0;
					}
				} else {
					// Si le point lu est valide, on regarde s'il faut le rajouter au cluster
					if (distance > 0.1) {
						PolarPoint lastPoint = cluster.getLast();
						// Le point lu doit être assez proche du point courant
						if (
								Math.abs(lastPoint.angle - angle) < MaxAngleBetween2PointsDegrees &&
								Math.abs(lastPoint.distance - distance) < MaxDdistanceBetween2PointsMm
						) {
							cluster.add(new PolarPoint(angle, distance, false));
							emptyPoints = 0;
						// Si le point lu est trop éloigné, on le zappe et on incrémente le compteur de poitns ignorés
						} else {
							emptyPoints++;
						}
					} else {
						emptyPoints++;
					}
					
					// Si on a zappé trop de points, on ajoute potentiellement le cluster courant à la liste et on en crée un nouveau
					if (emptyPoints > this.maxPointsToSkip) {
						emptyPoints = 0;
						// S'il contient assez de points on le rajoute à la liste de clusters
						if (cluster.count() >= this.minPointsPerCLuster) {
							cb.add(cluster);	
						}
						cluster = new Cluster(this.maxPointsPerCluster);
					}
				}

				// Si le cluster courant est plein
				if (!cluster.canAdd()) {
					// S'il contient assez de points on le rajoute à la liste de clusters
					if (cluster.count() >= this.minPointsPerCLuster) {
						cb.add(cluster);	
					}
					cluster = new Cluster(this.maxPointsPerCluster);
				}
				
				// Si le sac de clusters est plein, on arrête là notre analyse
				if (!cb.canAdd()) {
					System.out.println("sac plein");
					break;
				}
			}
		}
		
		// Si la lecture s'est arrêtée alors qu'on remplissait un cluster, on le rajoute s'il contient le nombre minimum de points requis
		if (!cluster.isEmpty() && cluster.count() >= this.minPointsPerCLuster) {
			cb.add(cluster);
		}
		
		return cb;
	}
	
	@Override
	public void scanStopped(SCAN_STOP_REASON reason) {
		// TDOO
	}

	@Override
	public void lidarRecoverableErrorOccured(RECOVERRABLE_ERROR error) {
		System.out.println("Recoverable error : "+error);
	}
}
