package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import ax12.AX12Link;
import ax12.AX12LinkSerial;
import esialrobotik.ia.actions.a2018.ActionAX12Abstract;
import esialrobotik.ia.actions.a2018.bras.BrasDroitRentrer;
import esialrobotik.ia.actions.a2018.bras.BrasDroitSortir;
import esialrobotik.ia.actions.a2018.bras.BrasGaucheRentrer;
import esialrobotik.ia.actions.a2018.bras.BrasGaucheSortir;
import esialrobotik.ia.actions.a2018.domotik.InterrupteurAllumer;
import esialrobotik.ia.actions.a2018.domotik.InterrupteurPreparer;
import esialrobotik.ia.actions.a2018.eau.LancementEauPropre;
import esialrobotik.ia.actions.a2018.eau.LargageEauSaleDroit;
import esialrobotik.ia.actions.a2018.eau.LargageEauSaleGauche;
import esialrobotik.ia.actions.a2018.eau.LargageEauSalePreparation;
import esialrobotik.ia.actions.a2018.eau.RangementTubes;
import esialrobotik.ia.actions.a2018.eau.Remplissage;
import esialrobotik.ia.actions.a2018.eau.RemplissagePreparation;
import esialrobotik.ia.actions.a2018.eau.RemplissageRangement;

public class ActionsPanel2019 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected AX12Link al;
	
	protected static final ActionAX12Abstract[] actions = new ActionAX12Abstract[] {
		new BrasDroitRentrer(),
		new BrasDroitSortir(),
		new BrasGaucheRentrer(),
		new BrasGaucheSortir(),
		new InterrupteurPreparer(),
		new InterrupteurAllumer(),
		new RemplissagePreparation(),
		new Remplissage(),
		new RemplissageRangement(),
		new LancementEauPropre(),
		new LargageEauSalePreparation(),
		new LargageEauSaleGauche(),
		new LargageEauSaleDroit(),
		new RangementTubes(),
	};
	
	public ActionsPanel2019(AX12LinkSerial ax12link) {
		super("AX actions");
		this.al = ax12link;
		this.makeGui();
	}
	
	private void makeGui() {
		this.setLayout(new GridLayout(0, 1));
		for (ActionAX12Abstract action : actions) {
			action.init(al);
			ActionButton ab = new ActionButton(action);
			JButton btn = new JButton(action.getClass().getSimpleName());
			btn.addActionListener(ab);
			this.add(btn);
		}
	}
	
	private class ActionButton implements ActionListener {
		ActionAX12Abstract action;
		
		public ActionButton(ActionAX12Abstract action) {
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			action.execute();
		}
	}
}
