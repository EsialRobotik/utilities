package cli.cmds.a2018;

import cli.AX12MainConsole;
import cli.cmds.Ax12Cmd;
import cli.cmds.Ax12CmdException;
import esialrobotik.ia.actions.a2018.bras.BrasDroitRentrer;
import esialrobotik.ia.actions.a2018.bras.BrasDroitSortir;
import esialrobotik.ia.actions.a2018.bras.BrasGaucheRentrer;
import esialrobotik.ia.actions.a2018.bras.BrasGaucheSortir;

public class AX12CmdBras extends Ax12Cmd {
	
	private String ordre;
	
	public AX12CmdBras() {
		this(null);
	}

	public AX12CmdBras(String ordre) {
		this.ordre = ordre;
	}
	
	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		if (ordre == null) {
			System.out.println(getUsage());
		} else if (ordre.equals("gb")) { // Baisser bras gauche
			this.brasGaucheBaisser(cli);
		} else if (ordre.equals("gl")) { // Lever bras gauche
			this.brasGaucheLever(cli);
		} else if (ordre.equals("db")) { // Baisser bras doir
			this.brasDroitBaisser(cli);
		} else if (ordre.equals("dl")) { // Lever bras droit
			this.brasDroitLever(cli);
		}
	}

	@Override
	public String getUsage() {
		return "Manipulation des bras du robot :\n"
				+ " gb -> baisser le bras gauche\n"
				+ " gl -> lever le bras gauches\n"
				+ " db -> baisser le bras droit\n"
				+ " dl -> lever le bras droit";
	}
	
	private void brasGaucheBaisser(AX12MainConsole cli) {
		new BrasGaucheSortir().init(cli.getAx12SerialCommunicator()).execute();
	}
	
	private void brasGaucheLever(AX12MainConsole cli) {
		new BrasGaucheRentrer().init(cli.getAx12SerialCommunicator()).execute();
	}
	
	private void brasDroitBaisser(AX12MainConsole cli) {
		new BrasDroitSortir().init(cli.getAx12SerialCommunicator()).execute();
	}
	
	private void brasDroitLever(AX12MainConsole cli) {
		new BrasDroitRentrer().init(cli.getAx12SerialCommunicator()).execute();
	}

}
