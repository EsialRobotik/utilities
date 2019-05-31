package cli.cmds;

public class Ax12CmdException extends Exception {

	private static final long serialVersionUID = 1L;

	public Ax12CmdException(String raison) {
		this(raison, null);
	}
	
	public Ax12CmdException(String raison, Throwable previous) {
		super(raison, previous);
	}
	
}
