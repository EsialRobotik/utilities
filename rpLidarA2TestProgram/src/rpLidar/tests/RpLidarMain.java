package rpLidar.tests;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import rpLidar.RpLidarA2;
import rpLidar.RpLidarEsp32Proxy;
import rpLidar.RpLidarScanHandlerInterface;
import rpLidar.link.RpLidarLink;
import rpLidar.utils.PaquetSniffer;

public class RpLidarMain implements RpLidarScanHandlerInterface {
	
	protected long mesuresRecues;
	protected long lastOutputTime;
	protected long scanSTartTime;
	
	public static void main(String[] args) {
		RpLidarMain main = new RpLidarMain();
		main.graphicalInterface(true);
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

	@Override
	public void handleLidarScan(int quality, boolean nouvelleRotation, double angle, double distance) {
		this.mesuresRecues++;
		if (this.mesuresRecues % 5000 == 0) {
			double speed =  5000000. / ((double)(System.currentTimeMillis() - this.lastOutputTime));
			long time = (System.currentTimeMillis() - this.scanSTartTime) / 1000;
			System.out.println("Tps exécution "+time+" secondes ; "+this.mesuresRecues+" mesures reçues (env "+Math.round(speed)+" / seconde)");
			this.lastOutputTime = System.currentTimeMillis();
		}
	}

	@Override
	public void scanStopped(SCAN_STOP_REASON reason) {
		System.out.println("Scan stoppé :"+reason);
	}

	@Override
	public void lidarRecoverableErrorOccured(RECOVERRABLE_ERROR error) {
		System.out.println("Recoverrable error : "+error);
	}
	
	public void snifSerial(SerialPort port) {
		RpLidarLink link;
		try {
			link = new RpLidarLink(port);
			PaquetSniffer sniffer = new PaquetSniffer(link);
			sniffer.sniffRequests();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void mainProgramm(SerialPort sp) {
		try {
			RpLidarLink rplink = new RpLidarLink(sp);
			RpLidarA2 lidar = new RpLidarA2(rplink);
			
			rplink.enableRotation(false);
			lidar.reset();
			Thread.sleep(1000);
			this.mesuresRecues = 0;
			this.lastOutputTime = System.currentTimeMillis();
			this.scanSTartTime = this.lastOutputTime;
			lidar.startScan(this);
			Thread.sleep(100);
			lidar.waitScanToTerminate();
			lidar.reset();			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void graphicalInterface(boolean useProxy) {
		try {
			RpLidarLink link = this.mountLink();
			DrawFrame df = new DrawFrame(useProxy ? new RpLidarEsp32Proxy(link) : new RpLidarA2(link));	
			df.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SerialPort getPort(String portName) {
		List<SerialPort> liste = this.getAvailableSerialPortList();
		if (liste.isEmpty()) {
			System.out.println("Aucun port série.");
			System.exit(0);
		}

		SerialPort sp = null;
		if (portName == null) {
			sp = liste.get(0);
		} else {
			for (SerialPort s : liste) {
				if (s.getName().equals(portName)) {
					sp = s;
				} else {
					s.close();
				}
			}
		}
		if (sp == null) {
			System.err.println("Port "+portName+"non trouvé");
			System.exit(1);
		}
		System.out.println("Utilisation du port "+liste.get(0).getName());
		return sp;
	}
	
	public RpLidarLink mountLink() {
		List<SerialPort> liste = this.getAvailableSerialPortList();
		if (liste.isEmpty()) {
			System.out.println("Aucun port série");
			System.exit(0);
		}
		
		try {
			System.out.println("Utilisation du port "+liste.get(0).getName());
			return new RpLidarLink(liste.get(0));
		} catch (IOException e) {
			System.err.print(e);
			System.exit(0);
		}
		return null;
	}

}
