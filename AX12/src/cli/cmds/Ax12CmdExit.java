package cli.cmds;

import cli.AX12MainConsole;

public class Ax12CmdExit extends Ax12Cmd{

	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		cli.requestStopMainLoop();
		System.out.println("Bye bye o/");
	}

	@Override
	public String getUsage() {
		return null;
	}

}
