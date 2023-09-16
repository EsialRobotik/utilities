package rpLidar.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class DrawExample extends JFrame {

	private static final long serialVersionUID = 1L;
	JButton btnToggleScan;
	JPanel drawing;
	SerialPort port;
	OutputStreamWriter osw;
	
	protected Point[] points;
	int currentPoint;

	public static void main(String[] args) {
		new DrawExample(1000, 0.5);
	}
	
	public DrawExample(int profondeurPoints, double zoom) {
		points = new Point[profondeurPoints];
		for (int i=0; i<profondeurPoints; i++) {
			points[i] = null;
		}
		currentPoint = 0;
		List<SerialPort> liste = this.getAvailableSerialPortList();
		if (liste.isEmpty()) {
			System.out.println("Aucun port série.");
			System.exit(0);
		}
		
		port = liste.get(0);
		System.out.println(port.getName());
		try {
			port.setSerialPortParams(
					115200,
			        SerialPort.DATABITS_8,
			        SerialPort.STOPBITS_1,
			        SerialPort.PARITY_NONE
	        );
			port.disableReceiveTimeout();
			osw = new OutputStreamWriter(port.getOutputStream());
			osw.write("s");
			osw.flush();
			drawIg();
			CollectorThread ct = new CollectorThread(port.getInputStream(), this, zoom);
			ct.start();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(100);
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									DrawExample.this.repaint();
								}
							});
						} catch (InterruptedException e) {
							e.printStackTrace();
						}	
					}
				}
			}).start();
		} catch (IOException | UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		int middlex = this.getWidth() / 2;
		int middleY = this.getHeight() / 2;
		
		
		synchronized (points) {
			for (int i=0; i<points.length; i++) {
				Point p = points[i];
				if (p != null) {
					g.fillArc(middlex + p.x, middleY + p.y, 10, 10, 0, 360);
				}
			}	
		}
		
	}
	
	public void drawPoint(int x, int y) {
		synchronized (points) {
			points[currentPoint] = new Point(x, y);
			currentPoint++;
			if (currentPoint >= points.length) {
				currentPoint = 0;
			}	
		}
	}
	
	protected void drawIg() {
		this.setSize(new Dimension(800, 600));
		this.setLocale(null);
		
		drawing = new JPanel();
		this.add(drawing);
		
		this.setTitle("Test lidar draw");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					osw.write("h");
					osw.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
	}
	
	class CollectorThread extends Thread {
		
		protected InputStream is;
		protected double zoom;
		protected DrawExample de;
		
		public CollectorThread(InputStream is, DrawExample de, double zoom) {
			this.is = is;
			this.zoom = zoom;
			this.de = de;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Scanner scan = new Scanner(this.is);
			String[] parts;
			Double angle, distance;
			int x, y ;
			try {
				while (true) {
					if (!scan.hasNextLine()) {
						Thread.sleep(10);
						continue;
					}
					try {
						parts = scan.nextLine().split(";");
						angle = Double.parseDouble(parts[0]);
						angle = Math.toRadians(angle);
						distance = Double.parseDouble(parts[1]);
						x = (int) ((Math.cos(angle) * distance * zoom));
						y = (int) ((Math.sin(angle) * distance * zoom));
						de.drawPoint(x, y);
					} catch (NumberFormatException e) {
						
					}
					
				}	
			} catch (NoSuchElementException | InterruptedException e) {
				e.printStackTrace();
			}
			scan.close();
		}
	}
	
	private List<SerialPort> getAvailableSerialPortList(){
		@SuppressWarnings(	"unchecked")
		Enumeration<CommPortIdentifier> p = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier cpi;
		CommPort cp;
		List<SerialPort> liste = new ArrayList<>();
		
		while(p.hasMoreElements()){
			try {
				cpi = p.nextElement();
				if(cpi != null && !cpi.isCurrentlyOwned()){
					cp = cpi.open(RpLidarMain.class.getName(), 500);
					if(cp instanceof SerialPort){
						liste.add((SerialPort)cp);
					}
				}
			} catch (PortInUseException e) {
				e.printStackTrace();
			}
		}
		return liste;
	}
}
