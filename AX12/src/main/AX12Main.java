package main;

import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import cli.AX12MainConsole;
import gui.AX12MainIG;

public class AX12Main {

	public static void main(String[] args) {
		
		AX12MainIG ig = null;
		if (!GraphicsEnvironment.isHeadless() && (args.length < 1 || !args[0].equals("cli"))) {
			ig = new AX12MainIG("AX12 ToolBox");
			ig.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ig.pack();
			ig.setVisible(true);
		}
		AX12MainConsole cons = new AX12MainConsole();
		if (ig != null) {
			cons.setSerialCommunicator(ig.getAx12Link());
		}
		cons.mainLoop();
	}
	
}
