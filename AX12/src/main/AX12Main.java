package main;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ax12.AX12LinkSerial;
import cli.AX12MainConsole;
import gui.AX12MainIG;

public class AX12Main {

	public static void main(String[] args) {
		Args parsedArgs = AX12Main.parseparseArgs(args);
		
		AX12MainIG ig = null;
		if (!GraphicsEnvironment.isHeadless() && !parsedArgs.forceHeadless) {
			ig = new AX12MainIG("AX12 ToolBox", false);
			ig.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ig.pack();
			ig.setVisible(true);
			System.out.println("GUI lancée");
		} else {
			System.out.println("GUI non lancée");
		}
		
		AX12MainConsole cons = new AX12MainConsole(parsedArgs.serialPort);
		if (ig != null) {
			cons.setSerialCommunicator(ig.getAx12Link());
		}

		try {
			System.out.println("Serveur HTTP lancé ; wwwroot : " + parsedArgs.htmlDir.getCanonicalPath() + " ; jsondir : "+parsedArgs.jsonDir.getCanonicalPath());
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
	
	protected static Args parseparseArgs(String[] args) {
		Options options = new Options();
		options.addOption("j", "json-dir", true, "Répertoire contenant les fichiers json des actions");
		options.addOption("h", "html-dir", true, "Répertoire contenant les fichiers html du serveur");
		options.addOption("l", "headless", false, "Lance le programme sens interface graphique");
		options.addOption("p", "serial-port", true, "Force le port série à utiliser");
		
		CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            
            Args a = new Args();
            a.jsonDir = new File("./html/data");
            a.htmlDir = new File("./html");
            a.forceHeadless = cmd.hasOption("headless");
            a.serialPort = null;
            
            if (cmd.hasOption("serial-port")) {
            	a.serialPort = cmd.getOptionValue("serial-port");
            }
            
            if(cmd.hasOption("json-dir")) {
            	a.jsonDir = new File(cmd.getOptionValue("json-dir"));
            	if (!a.jsonDir.exists()) {
            		throw new IllegalArgumentException("Le répertoire "+a.jsonDir.getPath()+" n'existe pas");
            	}
            	if (!a.jsonDir.isDirectory()) {
            		throw new IllegalArgumentException("Le chamin donné n'est pas un répertoire "+a.jsonDir.getPath());
            	}
            }
            
            if(cmd.hasOption("html-dir")) {
            	a.htmlDir = new File(cmd.getOptionValue("html-dir"));
            	if (!a.htmlDir.exists()) {
            		throw new IllegalArgumentException("Le répertoire "+a.jsonDir.getPath()+" n'existe pas");
            	}
            	if (!a.htmlDir.isDirectory()) {
            		throw new IllegalArgumentException("Le chamin donné n'est pas un répertoire "+a.jsonDir.getPath());
            	}
            }
            return a;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("AX12Main", options);
            System.exit(1);
        } catch (IllegalArgumentException e) {
        	System.out.println(e.getMessage());
        	System.exit(2);
        }
        return null;
	}
	
	protected static class Args {
		public File jsonDir;
		public File htmlDir;
		public boolean forceHeadless;
		public String serialPort;
	}
	
}
