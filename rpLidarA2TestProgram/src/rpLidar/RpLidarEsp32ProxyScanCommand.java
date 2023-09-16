package rpLidar;

import java.io.IOException;

import rpLidar.command.LidarCommand;
import rpLidar.command.LidarCommandException;
import rpLidar.command.exception.LidarCommandFailedException;
import rpLidar.link.RpLidarLink;

public class RpLidarEsp32ProxyScanCommand extends LidarCommand {
	
	protected RpLidarScanHandlerInterface scanHandler;
	protected long pointsLus;

	public RpLidarEsp32ProxyScanCommand(RpLidarLink link, RpLidarScanHandlerInterface scanHandler) {
		super(link);
		this.scanHandler = scanHandler;
		this.pointsLus = 0;
	}

	@Override
	protected byte getCommandCode() {
		return 0;
	}

	@Override
	protected byte[] getCommandPayload() {
		return null;
	}

	@Override
	public String getCommandName() {
		return "ESP32PROXYSCAN";
	}

	@Override
	protected byte[] getExpectedResponseDescriptor() {
		return null;
	}

	@Override
	protected void executePreRequest() throws LidarCommandException, IOException {
		this.link.write("s\n".getBytes(), 0, 2);
		this.link.flush();
	}
	
	
	@Override
	protected void executeRequest() throws LidarCommandException {
		long lastSerialSizePRint = System.currentTimeMillis();
		long pointsLusRelatif = 0;
		double pointsLusSeconde = 0.;
		String l;
		while (this.canContinue()) {
			try {
				l = this.link.tryToReadLine(200);
			} catch (IOException e) {
				e.printStackTrace();
				throw new LidarCommandFailedException(e.getMessage(), e);
			}
			if (l != null) {
				String[] parts = l.split(";");
				try {
					Double angle = Double.parseDouble(parts[0].trim());
					Double distance = Double.parseDouble(parts[1].trim());
					pointsLus++;
					pointsLusRelatif++;
					RpLidarEsp32ProxyScanCommand.this.scanHandler.handleLidarScan(64, false, angle, distance);	
				} catch (NumberFormatException e) {
					// pas grave
				}
			}
			if (lastSerialSizePRint + 1000 < System.currentTimeMillis()) {
				lastSerialSizePRint = System.currentTimeMillis();
				System.out.println("Serial buf in size : "+this.link.getReadAvailableSize()+" / cumul points lus : "+pointsLus+" / points/seconde : "+pointsLusRelatif);
				pointsLusRelatif = 0;
			}
		}
		try {
			this.link.write("h\n".getBytes(), 0, 2);
			this.link.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new LidarCommandFailedException(e.getMessage(), e);
		}
	}

	@Override
	protected void executeCustomPostRequest() throws LidarCommandException, IOException {
	}
	
}
