package cli.cmds;

public class Ax12CmdDescription {

	public final Class<? extends Ax12Cmd> classe;
	public final String description;
	public final String nom;
	
	public Ax12CmdDescription(Class<? extends Ax12Cmd> classe, String nom, String description) {
		this.classe = classe;
		this.nom = nom;
		this.description = description;
	}
	
}
