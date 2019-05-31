package cli.cmds;

import ax12.AX12Exception;
import ax12.AX12LinkException;
import ax12.value.AX12Position;
import cli.AX12MainConsole;

public class Ax12CmdGoal extends Ax12Cmd{

	private Double goal;
	
	public Ax12CmdGoal(Double goal) {
		this.goal = goal;
	}
	
	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		if (goal == null) {
			System.out.println("Usage : ");
		} else {
			try {
				cli.getCurrentAx12().setServoPosition(AX12Position.buildFromDegrees(this.goal));
			} catch (IllegalArgumentException | AX12LinkException | AX12Exception e) {
				throw new Ax12CmdException("Erreur avec l'AX12 : "+e.getMessage(), e);
			}	
		}
	}

	@Override
	public String getUsage() {
		return "Usage : indiquer l'angle en degrés dans la plage [0; 300]";
	}
	
	

}
