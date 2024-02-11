package rpLidar.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gnu.io.SerialPort;
import rpLidar.RpLidarEsp32Proxy;
import rpLidar.RpLidarFile;
import rpLidar.RpLidarInterface;
import rpLidar.clustering.ACFDImplementation;
import rpLidar.clustering.Cluster;
import rpLidar.clustering.ClustersBag;
import rpLidar.clustering.Point;
import rpLidar.clustering.PointHelper;
import rpLidar.clustering.PolarPoint;
import rpLidar.link.RpLidarLink;

public class ClusteringfTestMain {
	
	ACFDImplementation clustering;
	RpLidarFile lidar;
	DrawFrame drawFrame;
	ClustersBag cb;

	public static void main(String[] argv) {
//		tsestClusteringStatic();
//		testPointDrawing();
		doDetectionTestFromLidarProxy();
	}
	
	/**
	 * Effectue une simlation de détection depuis un fichier statique
	 */
	public static void doDetectionTestFromFile() {
		RpLidarFile lidar = new RpLidarFile(new File("c:/users/gryttix/Desktop/export_2023-16-09_16-49-02.log"), 90);
		System.out.println(lidar.getPoints().size()+ " points chargés");
		ClusteringfTestMain m = new ClusteringfTestMain(lidar);
		m.doTest();
	}
	
	/**
	 * Effecture une simulation de détection depuis un vrai lidar
	 */
	public static void doDetectionTestFromLidarProxy() {
		SerialPort sp = RpLidarMain.getPort(null);
		if (sp == null) {
			return;
		}
		RpLidarLink rplink;
		try {
			rplink = new RpLidarLink(sp);
			RpLidarEsp32Proxy lidar = new RpLidarEsp32Proxy(rplink);
			ClusteringfTestMain m = new ClusteringfTestMain(lidar);
			m.doTest();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public ClusteringfTestMain(RpLidarInterface lidar) {
		clustering = new ACFDImplementation(
			/* distance max */ 1500.,
			/* min points par cluster */ 4,
			/* max points par cluster */ 30,
			/* max enemis */ 20,
			/* distante max entre 2 points mm */ 50.,
			/* angle max entre 2 pts degrés */ 10.,
			/* max points vides entre 2 points */ 250
		);
		cb = new ClustersBag(10);
		drawFrame = new DrawFrame(lidar);
		lidar.addScanHandler(clustering);
		drawFrame.setVisible(true);
	}
	
	public void doTest() {
		drawFrame.toggleStart();
	
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (cb) {
						cb = clustering.doClustering();
						System.out.println(cb.count() + " clusters trouvés");
					}

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							synchronized (cb) {
								drawFrame.setEnemies(cb);
							}
						}
					});
				}
			}
		});
		t.run();
	}
	
	/**
	 * Teste l'algo de clustering avec des points fixes
	 */
	public static void testClusteringStatic() {
		ACFDImplementation clustering = new ACFDImplementation(2000., 3, 30, 1, 10., 1., 100);

		Point[] ps = new Point[] {
			new Point(5, 100),
			new Point(5, 101),
			new Point(5, 102),
			new Point(6, 101),
			new Point(160, 975),
		};

		for (int i=0; i<ps.length; i++) {
			PolarPoint pp = PointHelper.cartesianToPolar(ps[i]);
			clustering.handleLidarScan(64, false, Math.toDegrees(pp.angle), pp.distance);
		}

		List<Cluster> cbs = clustering.doClustering().asList();

		for (Cluster c : cbs) {
			System.out.println("Cluster :");
			List<Point> cps = new ArrayList<Point>();
			for (PolarPoint p : c.asList()) {
				Point cp = PointHelper.polarToCartesian(p);
				System.out.println("  " + cp);
				cps.add(cp);
			}
			System.out.print(" centre : ");
			System.out.println(PointHelper.computeBarycenter(cps));
		}
	}
	
	public static void testPointDrawing() {
		JFrame f = new TestDrawingFrame("Test drawing");
		f.setSize(new Dimension(1024, 768));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public static class TestDrawingFrame extends JFrame
	{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TestDrawingFrame(String title) {
			super(title);
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			List<Point> ps = new ArrayList<Point>();
			for (Point tmpp : new Point[] {
					new Point(-22.5203030512206, -548.529031491502),
					new Point(-19.59071468295996, -548.550323633163),
					new Point(-18.4521625882648, -549.906166173524),
					new Point(-13.4773112891185, -549.380107574505),
					new Point(-8.64585681651165, -550.334332495185),
					new Point(-5.09342954769816, -550.734467365897),
					new Point(-1.548104716461287, -551.983373965775),
					new Point(6.59362122709924, -554.600462160485),
					new Point(8.50323585036635, -556.418212795446),
					new Point(15.71541137787802, -557.798874093316),
					new Point(16.66281358750355, -558.485049728538),
					new Point(19.0881865669772, -558.594723036745),
					new Point(23.694142483972, -558.07105232187),
					new Point(28.409489186053, -556.836046515993),
					new Point(31.86500435386518, -557.939044632163),
					new Point(34.8718213951961, -558.554871162455),
					new Point(43.0770092825507, -563.300733141477),
				}) {
				ps.add(tmpp);
			}
			
			List<PolarPoint> psPolar = new ArrayList<>();
			for(Point p : ps) {
				psPolar.add(PointHelper.cartesianToPolar(p));
			}
			
			

			int centerX = this.getSize().width / 2;
			int centerY = this.getSize().height / 2;
			double echelle = 0.4;
			g.setColor(Color.RED);
			g.drawRoundRect(centerX, centerY, 20, 20, 20, 20);

			g.setColor(Color.BLUE);
			for (Point p : ps) {
				java.awt.Point pawt = p.scale(echelle).toAwtPoint(centerX, centerY);
				g.drawRoundRect(pawt.x, pawt.y, 20, 20, 20, 20);
			}

			g.setColor(Color.YELLOW);
			java.awt.Point pawt = PointHelper.computeBarycenter(ps).scale(echelle).toAwtPoint(centerX, centerY);
			g.drawRoundRect(pawt.x, pawt.y, 20, 20, 20, 20);

			g.setColor(Color.PINK);
			java.awt.Point polarawt = PointHelper.computeBarycenterFromPolar(psPolar).scale(echelle).toAwtPoint(centerX, centerY);
			g.drawRoundRect(polarawt.x, polarawt.y, 20, 20, 20, 20);
		}
	}
}
