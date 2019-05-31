package cli.cmds.a2017;

import ax12.AX12;
import ax12.AX12Exception;
import ax12.AX12LinkException;
import ax12.value.AX12Position;
import cli.AX12MainConsole;
import cli.cmds.Ax12Cmd;
import cli.cmds.Ax12CmdException;

public class AX12CmdRammasseMinerai extends Ax12Cmd {

	protected String cmd; 
	
	public AX12CmdRammasseMinerai() {
		this(null);
	}
	
	public AX12CmdRammasseMinerai(String cmd) {
		this.cmd = cmd;
	}
	
	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		if (this.cmd == null) {
			System.out.println("Usage : "+getUsage());
		} else {
			if (cmd.equals("l")) { // Levage
				go(cli, 154.5);
			} else if (cmd.equals("c")) { // Curage
				go(cli, 237.1); 
			} else if (cmd.equals("r")) { // Ramassage
				go(cli, 241.1);
			} else {
				throw new Ax12CmdException("La commande donnée au collecteur de minerai n'est pas valide");
			}
		}
	}

	@Override
	public String getUsage() {
		return "Abaisse ou lève le collecteur de minerai :\n - r : ramasse\n - l : lève\n - c : curagme, met le collecteur à l'horizontale";
	}

	private void go(AX12MainConsole cli, double angle) throws Ax12CmdException {
		AX12 a12 = cli.getCurrentAx12(); 
		int ad = a12.getAddress();
		a12.setAddress(4);
		try {
			a12.setServoPosition(AX12Position.buildFromDegrees(angle));
		} catch (AX12LinkException | AX12Exception e) {
			throw new Ax12CmdException("Erreur de commande du collecteur de minerai", e);
		}
		a12.setAddress(ad);
		
	}
}
