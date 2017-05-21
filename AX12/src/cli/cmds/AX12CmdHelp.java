package cli.cmds;

import cli.AX12MainConsole;

public class AX12CmdHelp extends Ax12Cmd{
	
	public AX12CmdHelp() {
	}

	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		StringBuffer sb = new StringBuffer("Liste des commandes :\n");
		for (Ax12CmdDescription desc : Ax12Cmd.getAvailableCommands()) {
			sb.append(" - ");
			sb.append(desc.nom);
			sb.append(" : ");
			sb.append(desc.description);
			sb.append('\n');
		}
		System.out.print(sb.toString());
	}

	@Override
	public String getUsage() {
		return null;
	}

}
