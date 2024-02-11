package rpLidar.clustering;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rpLidar.LidarException;
import rpLidar.RpLidarA2;
import rpLidar.RpLidarScanHandlerInterface;

public class RenderingFrame extends JFrame implements RpLidarScanHandlerInterface {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected JButton btnStartStop;
		protected JButton btnReset;
		protected JButton btnClear;
		protected JTextField txtDistanceMax;
		
		protected RpLidarA2 lidar;
		protected JPanel panel;
		protected Graphics graphics;
		
		protected int retention = 1000;
		protected int[] posX;
		protected int[] posY;
		protected int offset;
		protected int dmax;
		
		public RenderingFrame(RpLidarA2 lidar) {
			super("RpLidarA2");
			this.lidar = lidar;
			lidar.addScanHandler(this);

			this.retention = 1000;
			this.offset = 0;
			this.posX = new int[this.retention];
			this.posY = new int[this.retention];

			this.initIG();
		}
		
		protected void initIG() {
			this.setSize(new Dimension(640, 640));
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			btnStartStop = new JButton("Start");
			btnStartStop.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (RenderingFrame.this.lidar.scanIsRunning()) {
						try {
							RenderingFrame.this.lidar.stopScan();
							RenderingFrame.this.btnStartStop.setText("Start");
						} catch (LidarException e1) {
							e1.printStackTrace();
						}
					} else {
						try {
							RenderingFrame.this.lidar.startScan();
							RenderingFrame.this.btnStartStop.setText("Stop");
						} catch (LidarException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			btnReset = new JButton("Reset");
			btnReset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						RenderingFrame.this.lidar.reset();
					} catch (LidarException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			btnClear = new JButton("Clear");
			btnClear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.getGraphics().fillRect(0, 0, panel.getWidth(), panel.getHeight());
					repaint();
				}
			});
			
			txtDistanceMax = new JTextField(6); // par défaut 10 mètres (10k mm)
			txtDistanceMax.setText("10000");
			txtDistanceMax.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {}
				@Override
				public void keyReleased(KeyEvent e) {
					refreshDmax();
				}
				@Override
				public void keyPressed(KeyEvent e) {}
			});
			refreshDmax();
			
			this.setResizable(false);
			
			panel = new JPanel();
			graphics = panel.getGraphics();
			panel.add(btnStartStop);
			panel.add(btnReset);
			panel.add(btnClear);
			panel.add(new JLabel("Dmax:"));
			panel.add(txtDistanceMax);
			this.add(panel);
		}

		@Override
		public void handleLidarScan(int quality, boolean nouvelleRotation, double angle, double distance) {
			// c'est pas à la fenêtre de gérer ça
		}
		
		@Override
		public void repaint() {
			super.repaint();
		}

		@Override
		public void scanStopped(SCAN_STOP_REASON reason) {
			btnStartStop.setText("Start");
		}

		@Override
		public void lidarRecoverableErrorOccured(RECOVERRABLE_ERROR error) {
			System.out.println("Recoverable error : "+error);
		}
		
		public void refreshDmax() {
			try {
				this.dmax = Integer.parseInt(txtDistanceMax.getText().trim());
			} catch (NumberFormatException e) {
				// rien
			}
		}
}
