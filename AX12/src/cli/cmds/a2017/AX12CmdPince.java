package cli.cmds.a2017;

import ax12.AX12;
import ax12.AX12Exception;
import ax12.AX12LinkException;
import ax12.value.AX12Position;
import cli.AX12MainConsole;
import cli.cmds.Ax12Cmd;
import cli.cmds.Ax12CmdException;

public class AX12CmdPince extends Ax12Cmd{
	
	private String ordre;
	
	private enum ETAT {
		BRAS_LEVE(3, 60),
		BRAS_DEPOSER(3, 140),
		BRAS_BAISSE(3, 150),
		POIGNET_VERTICAL(2, 196),
		POIGNET_HORIZONTAL(2, 106),
		MAIN_FERMEE(1, 0),
		MAIN_DEPOSE(1, 20),
		MAIN_GRANDE_OUVERTE(1, 80);
		
		public final int addr;
		public final double angle;
		ETAT(int addr, double angle) {
			this.addr = addr;
			this.angle = angle;
		}
	}
	
	public AX12CmdPince() {
		this(null);
	}
	
	public AX12CmdPince(String ordre) {
		this.ordre = ordre;
	}
	
	@Override
	public void executeCmd(AX12MainConsole cli) throws Ax12CmdException {
		if (ordre == null) {
			System.out.println(getUsage());
		} else if (ordre.equals("r")) { // Ranger
			this.ranger(cli);
		} else if (ordre.equals("o")) { // Ouvrir main
			this.ouvrir(cli);
		} else if (ordre.equals("s")) { // Saisir
			this.saisir(cli);
		} else if (ordre.equals("b")) { // Baisser
			this.baisser(cli);
		} else if (ordre.equals("d")) { // Déposer
			this.deposer(cli);
		} 
	}
	
	void go(AX12MainConsole cli, ETAT e) {
		AX12 ax12 = cli.getCurrentAx12();
		int ad = ax12.getAddress();
		ax12.setAddress(e.addr);
		try {
			ax12.setServoPosition(AX12Position.buildFromDegrees(e.angle));
		} catch (AX12LinkException | AX12Exception e2) {
			e2.printStackTrace();
		}
		ax12.setAddress(ad);
	}
	
	private void attend(long sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public String getUsage() {
		return "Manipulation du bras du robot :\n r -> ranger la pince\n o -> baisser le bras et ouvrir grand la main\n s -> saisir le module\n b -> baisser le bras\n d -> déposer le module";
	}
	
	private void deposer(AX12MainConsole cli) {
		go(cli, ETAT.BRAS_DEPOSER);
		attend(200);
		go(cli, ETAT.POIGNET_HORIZONTAL);
		attend(500);
		go(cli, ETAT.MAIN_DEPOSE);	
	}
	
	private void baisser(AX12MainConsole cli) {
		go(cli, ETAT.BRAS_BAISSE);
	}
	
	private void saisir(AX12MainConsole cli) {
		go(cli, ETAT.MAIN_FERMEE);
	}
	
	private void ouvrir(AX12MainConsole cli) {
		go(cli, ETAT.BRAS_BAISSE);
		go(cli, ETAT.POIGNET_VERTICAL);
		attend(400);
		go(cli, ETAT.MAIN_GRANDE_OUVERTE);
	}
	
	private void ranger(AX12MainConsole cli) {
		go(cli, ETAT.MAIN_FERMEE);
		attend(200);
		go(cli, ETAT.POIGNET_VERTICAL);
		attend(400);
		go(cli, ETAT.BRAS_LEVE);
	}
}
