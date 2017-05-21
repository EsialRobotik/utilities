package cli.cmds;

public class Ax12CmdException extends Exception {

	public Ax12CmdException(String raison) {
		this(raison, null);
	}
	
	public Ax12CmdException(String raison, Throwable previous) {
		super(raison, previous);
	}
	
}
