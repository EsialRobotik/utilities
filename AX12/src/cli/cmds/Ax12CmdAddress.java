package cli.cmds;

import ax12.AX12LinkException;
import cli.AX12MainConsole;

public class Ax12CmdAddress extends Ax12Cmd {
	
	private Integer address;
	private String cmd;

	public Ax12CmdAddress() {
		this(null, null);
	}
	
	public Ax12CmdAddress(String cmd, Integer address) {
		this.cmd = cmd;
		this.address = address;
	}
	
	public Ax12CmdAddress(Integer address) {
		this(null, address);
	}

	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		this.thowsNoAx12Exception(cli);
		if (cmd == null) {
			if (address == null) {
				System.out.println("Adresse courante : "+cli.getCurrentAx12().getAddress());
				System.out.println("Usage :"+getUsage());
			} else {
				try {
					cli.getCurrentAx12().setAddress(address);
				} catch (IllegalArgumentException e) {
					throw new Ax12CmdException("Erreur de changement d'adresse", e);
				}
			}	
		} else {
			if (cmd.equals("set")) {
				if (address == null) {
					throw new Ax12CmdException("L'adresse n'a pas été fournie");
				}
				try {
					cli.getCurrentAx12().writeAddress(address);
				} catch (AX12LinkException | IllegalArgumentException e) {
					throw new Ax12CmdException("Erreur d'écriture d'adresse", e);
				}
			} else {
				throw new Ax12CmdException("Mauvais argument");
			}
		}
	}

	@Override
	public String getUsage() {
		return "\n - [a] change l'adresse de communication du porgramme\n - set [a] change l'adresse sur l'AX12";
	}

	
	
}
