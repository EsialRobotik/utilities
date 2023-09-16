package rpLidar.tests;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rpLidar.LidarException;
import rpLidar.RpLidarA2;
import rpLidar.RpLidarScanHandlerInterface;
import rpLidar.utils.ExportFichier;
import rpLidar.utils.ExportValue;

public class DrawFrame extends JFrame implements RpLidarScanHandlerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JButton btnStartStop;
	protected JButton btnReset;
	protected JButton btnClear;
	protected JButton btnExportFichier;
	protected JTextField txtDistanceMax;
	
	protected RpLidarA2 lidar;
	protected JPanel panel;
	protected Graphics graphics;
	
	protected int retention = 1000;
	protected int[] posX;
	protected int[] posY;
	protected int offset;
	protected int dmax;
	
	protected ExportFichier exporteur;
	
	public DrawFrame(RpLidarA2 lidar) {
		super("RpLidarA2");
		this.lidar = lidar;

		this.retention = 1000;
		this.offset = 0;
		this.posX = new int[this.retention];
		this.posY = new int[this.retention];
		this.exporteur = new ExportFichier(new File("C:/users/gryttix/desktop"));

		this.initIG();
	}
	
	protected void initIG() {
		this.setSize(new Dimension(640, 640));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (DrawFrame.this.lidar.scanIsRunning()) {
					try {
						DrawFrame.this.lidar.stopScan();
						DrawFrame.this.btnStartStop.setText("Start");
					} catch (LidarException e1) {
						e1.printStackTrace();
					}
				} else {
					try {
						DrawFrame.this.lidar.startScan(DrawFrame.this);
						DrawFrame.this.btnStartStop.setText("Stop");
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
					DrawFrame.this.lidar.reset();
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
		btnExportFichier = new JButton("START Export fichier");
		btnExportFichier.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleExportFichier();
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
		panel.add(btnExportFichier);
		panel.add(new JLabel("Dmax:"));
		panel.add(txtDistanceMax);
		this.add(panel);
	}

	@Override
	public void handleLidarScan(int quality, boolean nouvelleRotation, double angle, double distance) {
		if (distance > dmax) {
			return;
		}
		int centerX = 320;
		int centerY = 340;
		double maxDistance = 10.;
		
		angle = (angle-90.)*Math.PI/180.;
		double distanceX = (Math.cos(angle) * distance);
		double distanceY = (Math.sin(angle) * distance);
		int x = centerX + (int) (distanceX / maxDistance);
		int y = centerY + (int) (distanceY / maxDistance);
		
		panel.getGraphics().setColor(Color.BLACK);
		panel.getGraphics().drawRect(x, y, 1, 1);
		panel.getGraphics().setColor(Color.RED);
		panel.getGraphics().drawRect(centerX-3, centerY-3, 7, 7);
		panel.getGraphics().fillRect(centerX-3, centerY-3, 7, 7);
		
		if (this.exporteur.exportIsRunngin()) {
			this.exporteur.exportAsync(new ExportValue(distanceX, distanceY, quality, distance, angle));
		}
	}
	
	@Override
	public void repaint() {
		super.repaint();
	}

	@Override
	public void scanStopped(SCAN_STOP_REASON reason) {
		System.out.println(reason);
		btnStartStop.setText("Start");
	}

	@Override
	public void lidarRecoverableErrorOccured(RECOVERRABLE_ERROR error) {
		System.out.println("Recoverable error : "+error);
	}
	
	public void toggleExportFichier()
	{
		if (this.exporteur.exportIsRunngin()) {
			this.exporteur.stopExport();
			btnExportFichier.setText("START Export fichier");
		} else {
			try {
				this.exporteur.startExport();
				btnExportFichier.setText("STOP export fichier");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void refreshDmax() {
		try {
			this.dmax = Integer.parseInt(txtDistanceMax.getText().trim());
		} catch (NumberFormatException e) {
			// rien
		}
	}
}
