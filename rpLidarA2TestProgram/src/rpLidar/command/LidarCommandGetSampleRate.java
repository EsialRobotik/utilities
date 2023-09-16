package rpLidar.command;

import java.io.IOException;

import rpLidar.command.exception.LidarCommandFailedException;
import rpLidar.command.exception.LidarCommandNotFinishedException;
import rpLidar.command.response.LidarResponseSampleRate;
import rpLidar.link.RpLidarLink;
import rpLidar.utils.LidarHelper;

public class LidarCommandGetSampleRate extends LidarCommand {

	protected LidarResponseSampleRate sampleRate;
	
	public LidarCommandGetSampleRate(RpLidarLink link) {
		super(link);
		this.sampleRate = null;
	}	
	
	@Override
	protected byte getCommandCode() {
		return (byte) 0x59;
	}

	@Override
	protected byte[] getCommandPayload() {
		return null;
	}

	@Override
	protected byte[] getExpectedResponseDescriptor() {
		return toByteArray(0xA5, 0x5A, 0x04, 0x00, 0x00, 0x00, 0x15);
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
		
		byte[] buf = new byte[4];
		this.readToFillBufferFully(buf);
		
		LidarResponseSampleRate sampleRate = new LidarResponseSampleRate();
		sampleRate.timeStandard = LidarHelper.intFromBytes(buf[0],  buf[1]);
		sampleRate.timeExpress = LidarHelper.intFromBytes(buf[2],  buf[3]);
		
		this.sampleRate = sampleRate;
	}

	/**
	 * R�cup�re la r�ponse de la commande
	 * 
	 * @return
	 * @throws LidarCommandFailedException
	 * @throws LidarCommandNotFinishedException
	 */
	public LidarResponseSampleRate getResult() throws LidarCommandFailedException, LidarCommandNotFinishedException {
		if (this.getCommandState() != COMMAND_STATE.FINISHED) {
			throw new LidarCommandNotFinishedException();
		}
		
		if (this.executionException != null || this.sampleRate == null) {
			throw new LidarCommandFailedException("La commande a �chou�", this.executionException);
		}
		
		
		return this.sampleRate;
	}

	@Override
	public String getCommandName() {
		return "GET_SAMPLERATE";
	}
	
}
