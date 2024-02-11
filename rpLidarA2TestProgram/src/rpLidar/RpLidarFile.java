package rpLidar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rpLidar.clustering.PolarPoint;
import rpLidar.command.response.LidarResponseHealth;
import rpLidar.command.response.LidarResponseInfo;
import rpLidar.command.response.LidarResponseSampleRate;

public class RpLidarFile implements RpLidarInterface {
	
	List<RpLidarScanHandlerInterface> scanHandlers;
	protected boolean scanIsRunning;
	protected List<PolarPoint> points;
	
	/**
	 * Offset d'angle des points lus en radians
	 */
	protected double rotationOffset;
	
	protected final Pattern LIGNE_POINT = Pattern.compile("^(-?\\d+\\.?\\d*);(-?\\d+\\.?\\d*);(-?\\d+\\.?\\d*);(-?\\d+\\.?\\d*);(-?\\d+\\.?\\d*)\\s*$");

	/**
	 * 
	 * @param f Fichier contenant les points exportés
	 * @param rotationOffset Offset de l'angle des points lus en degrés
	 */
	public RpLidarFile(File f, double rotationOffset) {
		this.scanHandlers = new ArrayList<RpLidarScanHandlerInterface>();
		this.scanIsRunning = false;
		this.rotationOffset = Math.toRadians(rotationOffset);
		points = new ArrayList<PolarPoint>();
		this.parseFile(f);
	}

	public List<PolarPoint> getPoints() {
		return this.points;
	}

	@Override
	public LidarResponseHealth getHealth() throws LidarException {
		LidarResponseHealth lhr = new LidarResponseHealth();
		lhr.errorCode = 0;
		lhr.health = LidarResponseHealth.HEALTH.GOOD;
		return lhr;
	}

	@Override
	public void startScan() throws LidarException {
		this.scanIsRunning = true;
		this.dispatchFileAsync();
	}

	@Override
	public void startScanExpress() throws LidarException {
		this.scanIsRunning = true;
		this.dispatchFileAsync();
	}

	@Override
	public void stopScan() throws LidarException {
		this.scanIsRunning = false;
	}

	@Override
	public void reset() throws LidarException {
		this.scanIsRunning = false;
	}

	@Override
	public boolean scanIsRunning() {
		return this.scanIsRunning;
	}

	@Override
	public LidarResponseSampleRate getSampleRate() throws LidarException {
		LidarResponseSampleRate sr = new LidarResponseSampleRate();
		sr.timeExpress = -1;
		sr.timeStandard = -1;
		return sr;
	}

	@Override
	public LidarResponseInfo getInfos() throws LidarException {
		LidarResponseInfo infos = new LidarResponseInfo();
		infos.modelId = -1;
		infos.firmwareMinor = -1;
		infos.firmwareMajor = -1;
		infos.hardwareVersion = -1;
		return infos;
	}
	
	protected void parseFile(File f) {
		Scanner scan;
		try {
			scan = new Scanner(f);
			while (scan.hasNextLine()) {
				Matcher m = LIGNE_POINT.matcher(scan.nextLine());
				if (m.matches()) {
					points.add(
						new PolarPoint(
							Double.parseDouble(m.group(5)),
							Double.parseDouble(m.group(4)),
							true
						)
					);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected void dispatchFileAsync() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				for (PolarPoint p : points) {
					for (RpLidarScanHandlerInterface sh : scanHandlers) {
						sh.handleLidarScan(64, false, Math.toDegrees(p.angle + rotationOffset), p.distance);	
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
	}

	@Override
	public void addScanHandler(RpLidarScanHandlerInterface scanHandler) {
		this.scanHandlers.add(scanHandler);
	}

}
