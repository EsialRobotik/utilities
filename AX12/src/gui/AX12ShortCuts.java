package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;

import ax12.AX12;
import ax12.AX12Exception;
import ax12.AX12LinkException;
import ax12.AX12LinkSerial;
import ax12.value.AX12Position;

public class AX12ShortCuts extends JFrame {

	// les position "gauche" et "droit" s'apprécient en regardant le robot de face
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected enum ORIENTATION {
		DROIT(151.5),
		VIDANGE_GAUCHE(248.1),
		VIDANGE_DROIT(52.5),
		LANCEUR_GAUCHE(146.8),
		LANCEUR_DROIT(146.9),
		HORIZONTAL_GAUCHE(240.2),
		HORIZONTAL_DROIT(60.3);
		
		
		public final double val;
		private ORIENTATION(double val) {
			this.val = val;
		}
	}
	
	protected enum RAIL {
		GARAGE(267.7),
		REMPLISSAGE_1(173.0),
		REMPLISSAGE_2(66.4),
		MILIEU_VIDANGE(131.4),
		LANCEUR_GAUCHE(300.0),
		LANCEUR_DROIT(183.9),
		EXTREME_GAUCHE(0.0);
		
		public final double val;
		private RAIL(double val) {
			this.val = val;
		}
	}
	
	protected enum PENTE {
		HORIZONTALE(142.3),
		QUASI_HORIZONTALE(150.0),
		VERTICALE(231.7),
		DOUCE(139.6),
		FORTE_GAUCHE(127.2),
		FORTE_DROITE(124.5),
		REMPLISSAGE(242.3),
		INNTERRUPTEUR(190.0);
		
		public final double val;
		private PENTE(double val) {
			this.val = val;
		}
	}
	
	protected enum BRAS_GAUCHE {
		SORTI(145.7),
		RENTRE(242.0);
		
		public final double val;
		private BRAS_GAUCHE(double val) {
			this.val = val;
		}
	}
	
	protected enum BRAS_DROIT {
		SORTI(247.7),
		RENTRE(153.5);
		
		public final double val;
		private BRAS_DROIT(double val) {
			this.val = val;
		}
	}
	
	protected ButtonDef[] defs = {
		new ButtonDef("Garage", RAIL.GARAGE, PENTE.VERTICALE, ORIENTATION.DROIT, null, null, 0),
		new ButtonDef("Remplissage 1", RAIL.REMPLISSAGE_1, PENTE.REMPLISSAGE, null, null, null, 1),
		new ButtonDef("Remplissage 2", RAIL.REMPLISSAGE_2, PENTE.REMPLISSAGE, null, null, null, 1),
		new ButtonDef("Preparation largage", RAIL.MILIEU_VIDANGE, PENTE.VERTICALE, ORIENTATION.DROIT, null, null, 2),
		new ButtonDef("Largage gauche", null, null, ORIENTATION.VIDANGE_GAUCHE, null, null, 2),
		new ButtonDef("Largage droit", null, null, ORIENTATION.VIDANGE_DROIT, null, null, 2),
		new ButtonDef("Préparation lanceur droit", RAIL.LANCEUR_DROIT, PENTE.QUASI_HORIZONTALE, ORIENTATION.LANCEUR_DROIT, null, null, 3),
		new ButtonDef("Lanceur droit", null, PENTE.FORTE_DROITE, null, null, null, 3),
		new ButtonDef("Préparation lanceur gauche", RAIL.LANCEUR_GAUCHE, PENTE.QUASI_HORIZONTALE, ORIENTATION.LANCEUR_GAUCHE, null, null, 3),
		new ButtonDef("Lanceur gauche", null, PENTE.FORTE_GAUCHE, null, null, null, 3),
		new ButtonDef("Allumer lanceur", null, null, null, null, null, 4),
		new ButtonDef("Eteindre lanceur", null, null, null, null, null, 4),
		new ButtonDef("Bras gauche abaisser", null, null, null, BRAS_GAUCHE.SORTI, null, 5),
		new ButtonDef("Bras gauche remonter", null, null, null, BRAS_GAUCHE.RENTRE, null, 5),
		new ButtonDef("Bras droit abaisser", null, null, null, null,BRAS_DROIT.SORTI , 5),
		new ButtonDef("Bras droit remonter", null, null, null, null, BRAS_DROIT.RENTRE, 5),
		new ButtonDef("Preparer interrupteur", RAIL.MILIEU_VIDANGE, PENTE.VERTICALE, ORIENTATION.DROIT, null, null, 6),
		new ButtonDef("ALlumer", RAIL.MILIEU_VIDANGE, PENTE.INNTERRUPTEUR, ORIENTATION.DROIT, null, null, 6),
	};
	
	protected JButton btns[];
	
	protected AX12LinkSerial ax12Link;
	protected AX12 rail;
	protected AX12 pente;
	protected AX12 orientation;
	protected AX12 brasGauche;
	protected AX12 brasDroit;
	
	public AX12ShortCuts(AX12LinkSerial ax12link) {
		super("AX shotcuts");
		this.ax12Link = ax12link;
		this.rail = new AX12(1, ax12link);
		this.pente = new AX12(2, ax12link);
		this.orientation = new AX12(3, ax12link);
		this.brasGauche = new AX12(4, ax12link);
		this.brasDroit = new AX12(5, ax12link);
		this.makeGui();
	}
	
	protected void makeGui() {
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.anchor = GridBagConstraints.WEST;
		
		Map<Integer, Integer> colonnesParLigne = new HashMap<>();
		
		for (int i=0; i<defs.length; i++) {
			ButtonDef bd = defs[i];
			JButton b = new JButton(bd.titre);
			b.addActionListener(bd);
			
			gbc.gridy = bd.ligne;
			gbc.gridx = 0;
			if (colonnesParLigne.containsKey(defs[i].ligne)) {
				gbc.gridx = colonnesParLigne.get(bd.ligne);
			}
			colonnesParLigne.put(bd.ligne, gbc.gridx+1);
			
			this.add(b, gbc);
		}
		
		
	}
	
	protected class ButtonDef implements ActionListener {
		
		String titre;
		RAIL rail;
		PENTE pente;
		ORIENTATION orientation;
		BRAS_GAUCHE brasGauche;
		BRAS_DROIT brasDroit;
		int ligne;
		
		public ButtonDef(String titre, RAIL rail, PENTE pente, ORIENTATION orientation, BRAS_GAUCHE brasGauche, BRAS_DROIT brasDroit, int ligne) {
			this.titre = titre;
			this.rail = rail;
			this.pente = pente;
			this.orientation = orientation;
			this.brasGauche = brasGauche;
			this.brasDroit = brasDroit;
			this.ligne = ligne;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				if (rail != null) {
					AX12ShortCuts.this.rail.setServoPosition(AX12Position.buildFromDegrees(rail.val));	
				}
				if (pente != null) {
					AX12ShortCuts.this.pente.setServoPosition(AX12Position.buildFromDegrees(pente.val));
					AX12ShortCuts.this.pente.setCwComplianceSlope(99);
					AX12ShortCuts.this.pente.setCcwComplianceSlope(99);
				}
				if (orientation != null) {
					AX12ShortCuts.this.orientation.setServoPosition(AX12Position.buildFromDegrees(orientation.val));	
				}
				if (this.ligne == 4) {
					ax12Link.enableDtr(this.titre.toLowerCase().contains("allumer"));
				}
				if (this.brasGauche != null) {
					AX12ShortCuts.this.brasGauche.setServoPosition(AX12Position.buildFromDegrees(brasGauche.val));
				}
				if (this.brasDroit != null) {
					AX12ShortCuts.this.brasDroit.setServoPosition(AX12Position.buildFromDegrees(brasDroit.val));
				}
			} catch (AX12Exception e) {
				
			} catch (AX12LinkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
