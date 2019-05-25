package cli.cmds.a2018;

import cli.AX12MainConsole;
import cli.cmds.Ax12Cmd;
import cli.cmds.Ax12CmdException;
import esialrobotik.ia.actions.a2018.bras.BrasDroitRentrer;
import esialrobotik.ia.actions.a2018.bras.BrasDroitSortir;
import esialrobotik.ia.actions.a2018.bras.BrasGaucheRentrer;
import esialrobotik.ia.actions.a2018.bras.BrasGaucheSortir;
import esialrobotik.ia.actions.a2018.eau.LancementEauPropre;
import esialrobotik.ia.actions.a2018.eau.LargageEauSaleDroit;
import esialrobotik.ia.actions.a2018.eau.LargageEauSaleGauche;
import esialrobotik.ia.actions.a2018.eau.LargageEauSalePreparation;
import esialrobotik.ia.actions.a2018.eau.RangementTubes;
import esialrobotik.ia.actions.a2018.eau.Remplissage;
import esialrobotik.ia.actions.a2018.eau.RemplissagePreparation;
import esialrobotik.ia.actions.a2018.eau.RemplissageRangement;

public class AX12CmdEau extends Ax12Cmd {

private String ordre;
	
	public AX12CmdEau() {
		this(null);
	}

	public AX12CmdEau(String ordre) {
		this.ordre = ordre;
	}
	
	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		if (ordre == null) {
			System.out.println(getUsage());
		} else if (ordre.equals("l")) { // Lancer les balles
			new LancementEauPropre().init(cli.getAx12SerialCommunicator()).execute();
		} else if (ordre.equals("ld")) { // Larguer droit
			new LargageEauSaleDroit().init(cli.getAx12SerialCommunicator()).execute();
		} else if (ordre.equals("lg")) { // Larguer gauche
			new LargageEauSaleGauche().init(cli.getAx12SerialCommunicator()).execute();
		} else if (ordre.equals("lp")) { // Préparation largage
			new LargageEauSalePreparation().init(cli.getAx12SerialCommunicator()).execute();
		} else if (ordre.equals("g")) { // Ranger les tubes
			new RangementTubes().init(cli.getAx12SerialCommunicator()).execute();
		} else if (ordre.equals("r")) { // Remplir les tubes
			new Remplissage().init(cli.getAx12SerialCommunicator()).execute();
		} else if (ordre.equals("pr")) { // Préparer le remplissage
			new RemplissagePreparation().init(cli.getAx12SerialCommunicator()).execute();
		} else if (ordre.equals("rr")) { // Rangement des tubes après remplissage
			new RemplissageRangement().init(cli.getAx12SerialCommunicator()).execute();
		}
	}

	@Override
	public String getUsage() {
		return "Manipulation de l'eau avec le robot :\n"
				+ " pr -> Preparer remplissage\n"
				+ " r  -> Remplir les tubes\n"
				+ " rr -> Transport apres remplissage\n"
				+ " l  -> Lancer l'eau\n"
				+ " lp -> Preparation largage\n"
				+ " ld -> Largage droit\n"
				+ " lg -> Largage gauche\n"
				+ " g  -> Garage\n";
	}

}
