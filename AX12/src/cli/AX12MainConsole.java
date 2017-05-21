package cli;

import java.util.Scanner;

import ax12.AX12;
import ax12.AX12LinkException;
import ax12.AX12LinkSerial;
import cli.cmds.Ax12Cmd;
import cli.cmds.Ax12CmdException;
import gnu.io.SerialPort;

/**
 * Pilotage des AX12 par la console
 * @author gryttix
 *
 */
public class AX12MainConsole {

	private AX12 ax12;
	private boolean continueMainLoop;
	private AX12LinkSerial sc;
	
	public AX12MainConsole() {
		SerialPort sp = AX12LinkSerial.getSerialPort(null);
		sc = null;
		if (sp != null) {
			try {
				sc = new AX12LinkSerial(sp, 115200);
				ax12 = new AX12(1, sc);
			} catch (AX12LinkException e) {
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * Boucle principale de la ligne de commande
	 * La fonction bloque jusqu'à la fin du programme
	 */
	public void mainLoop() {
		this.continueMainLoop = true;
		Scanner scan = new Scanner(System.in);
		String line;
		
		System.out.println("Démarré, tapez help pour la liste des commandes");
		while (this.continueMainLoop) {
			System.out.print(getInfos());
			line = scan.nextLine();
			if (line != null) {
				line = line.trim();
			}
			if (line.equals("")) {
				System.out.println("Tapez help pour la liste des commandes");
			} else {
				try {
					Ax12Cmd cmd = Ax12Cmd.buildAx12CmdFromParamettersString(line);
					if (cmd != null) {
						cmd.executeCmd(this);	
					}
				} catch (Ax12CmdException e) {
					System.err.println("Erreur : "+e.getMessage());
					e.printStackTrace();
				}	
			}
		}
		scan.close();
	}
	
	private String getInfos() {
		if (sc == null) {
			return "non-conecté>";
		} else {
			return sc.getUartName()+"@"+sc.getBaudRate()+"bps-addr"+ax12.getAddress()+">";
		}
	}
	
	public AX12 getCurrentAx12() {
		return this.ax12;
	}
	
	public AX12LinkSerial getAx12SerialCommunicator() {
		return this.sc;
	}
	
	public void requestStopMainLoop() {
		this.continueMainLoop = false;
	}
	
	public void setSerialCommunicator(AX12LinkSerial sc) {
		this.sc = sc;
	}
	
	
}
