package rpLidar.command;

import java.io.IOException;

import rpLidar.command.exception.LidarCommandFailedException;
import rpLidar.command.exception.LidarCommandNotFinishedException;
import rpLidar.command.response.LidarResponseHealth;
import rpLidar.command.response.LidarResponseHealth.HEALTH;
import rpLidar.link.RpLidarLink;

public class LidarCommandGetHealth extends LidarCommand {

	protected LidarResponseHealth health;
	
	public LidarCommandGetHealth(RpLidarLink link) {
		super(link);
		this.health = null;
	}

	@Override
	protected byte getCommandCode() {
		return (byte) 0x52;
	}

	@Override
	protected byte[] getCommandPayload() {
		return null;
	}

	@Override
	protected byte[] getExpectedResponseDescriptor() {
		return toByteArray(0xA5, 0x5A, 0x03, 0x00, 0x00, 0x00, 0x06);
	}
	
	@Override
	protected void executePreRequest() throws LidarCommandException, IOException {
		// Rien � faire
	}
	
	@Override
	protected void executeCustomPostRequest() throws LidarCommandException, IOException {
		if (this.executionException != null) {
			return;
		}
		
		byte[] buf = new byte[3];
		this.readToFillBufferFully(buf);
		
		LidarResponseHealth health = new LidarResponseHealth();
		
		health.errorCode = 0x00 | buf[2];
		health.errorCode |= buf[1] << 8;
	
		switch (buf[0]) {
			case (byte) 0x00:
				health.health = HEALTH.GOOD;
			break;
			case (byte) 0x01:
				health.health = HEALTH.WARNING;
			break;
			case (byte) 0x02:
				health.health = HEALTH.ERROR;
			break;
			default:
				throw new LidarCommandException("Le Lidar a donn� une r�ponse inattendue");
		}
		
		this.health = health;
	}
	
	/**
	 * R�cup�re la r�ponse de la commande
	 * 
	 * @return
	 * @throws LidarCommandFailedException
	 * @throws LidarCommandNotFinishedException
	 */
	public LidarResponseHealth getResult() throws LidarCommandFailedException, LidarCommandNotFinishedException {
		if (this.getCommandState() != COMMAND_STATE.FINISHED) {
			throw new LidarCommandNotFinishedException();
		}
		
		if (this.executionException != null || this.health == null) {
			throw new LidarCommandFailedException("La commande a �chou�", this.executionException);
		}
		
		return this.health;
	}

	@Override
	public String getCommandName() {
		return "GET_HEALTH";
	}

}
