package main;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import ax12.AX12LinkSerial;
import cli.AX12MainConsole;
import gui.AX12MainIG;
import web.AX12Http;

public class AX12Main {

	public static void main(String[] args) {
		AX12MainIG ig = null;
		if (!GraphicsEnvironment.isHeadless() && (args.length < 1 || !args[0].equals("cli"))) {
			ig = new AX12MainIG("AX12 ToolBox", false);
			ig.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ig.pack();
			ig.setVisible(true);
			System.out.println("GUI lancée");
		} else {
			System.out.println("GUI non lancée");
		}
		
		String serialPortName = null;
		if (args.length > 1) {
			serialPortName = args[1];
		}
		AX12MainConsole cons = new AX12MainConsole(serialPortName);
		if (ig != null) {
			cons.setSerialCommunicator(ig.getAx12Link());
		}
		
		try {
			File f = (new File("./html")).getCanonicalFile();
			File data = (new File("./html/data")).getCanonicalFile();
			new AX12Http(f, data, cons.getAx12SerialCommunicator());
			System.out.println("Serveur HTTP lancé ; wwwroot : " + f.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AX12LinkSerial link = cons.getAx12SerialCommunicator();
		if (link != null) {
			link.enableDtr(false);
			link.enableRts(false);	
		}
		
		System.out.println("Console lancée");
		cons.mainLoop();
	}
	
}
