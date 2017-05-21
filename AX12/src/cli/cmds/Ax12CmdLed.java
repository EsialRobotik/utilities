package cli.cmds;

import ax12.AX12LinkException;
import cli.AX12MainConsole;

public class Ax12CmdLed extends Ax12Cmd{

	private boolean allumer; 
	
	public Ax12CmdLed(Boolean allumer) {
		this.allumer = allumer;
	}
	
	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		this.thowsNoAx12Exception(cli);
		try {
			cli.getCurrentAx12().setLed(allumer);
		} catch (AX12LinkException e) {
			throw new Ax12CmdException("Erreur avec l'AX12 : "+e.getMessage(), e);
		}
	}

	@Override
	public String getUsage() {
		return "1 ou 0 / true / false pour allumer/éteindre la led";
	}
	
	
}
