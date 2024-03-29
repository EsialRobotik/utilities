package rpLidar.command;

import java.io.IOException;

import rpLidar.link.RpLidarLink;

public class LidarCommandReset extends LidarCommand {

	public LidarCommandReset(RpLidarLink link) {
		super(link);
	}

	@Override
	protected byte getCommandCode() {
		return (byte) 0x40;
	}

	@Override
	protected byte[] getCommandPayload() {
		return null;
	}

	@Override
	protected byte[] getExpectedResponseDescriptor() {
		return EMPTY_BYTE_ARRAY;
	}
	
	@Override
	protected void executePreRequest() throws LidarCommandException, IOException {
		this.stopRotation();
	}
	
	@Override
	protected void executeCustomPostRequest() throws LidarCommandException, IOException {
		this.stopRotation(); // Pour �tre s�r
		this.link.cleanInput();
	}

	@Override
	public String getCommandName() {
		return "RESET";
	}
	
}
