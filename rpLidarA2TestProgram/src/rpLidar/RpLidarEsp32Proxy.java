package rpLidar;

import java.io.IOException;

import rpLidar.command.response.LidarResponseHealth;
import rpLidar.command.response.LidarResponseInfo;
import rpLidar.command.response.LidarResponseSampleRate;
import rpLidar.link.RpLidarLink;

public class RpLidarEsp32Proxy extends RpLidarA2 {

	RpLidarScanHandlerInterface scanHandler;
	
	public RpLidarEsp32Proxy(RpLidarLink link) throws IOException {
		super(link);
	}

	@Override
	public LidarResponseHealth getHealth() {
		return null;
	}
	
	@Override
	public boolean scanIsRunning() {
		synchronized (this) {
			return this.commandIsRunning;
		}
	}

	public void startScan(RpLidarScanHandlerInterface scanHandler) throws LidarException {
		this.launchCommand(new RpLidarEsp32ProxyScanCommand(link, scanHandler), true, false);
	}

	@Override
	public void startScanExpress(RpLidarScanHandlerInterface scanHandler) throws LidarException {
		this.startScan(scanHandler);
	}

	@Override
	public void stopScan() throws LidarException {
		synchronized (this) {
			if (this.commandIsRunning) {
				this.runningCommand.terminate();
				this.commandIsRunning = false;
			}
		}
		try {
			this.link.write("h\n".getBytes());
			this.link.flush();
		} catch (IOException e) {
			throw new LidarException("Pas réussi à arrêter", e);
		}
	}

	@Override
	public void reset() throws LidarException {
		this.stopScan();
	}

	@Override
	public LidarResponseSampleRate getSampleRate() {
		return null;
	}

	@Override
	public LidarResponseInfo getInfos() throws LidarException {
		return null;
	}
}
