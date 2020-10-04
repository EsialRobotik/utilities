package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import ax12.AX12Link;
import ax12.AX12LinkSerial;
import esialrobotik.ia.actions.a2020.ActionAX12Abstract;
import esialrobotik.ia.actions.a2020.bras.BaisserBrasDroit;
import esialrobotik.ia.actions.a2020.bras.BaisserBrasGauche;
import esialrobotik.ia.actions.a2020.bras.RangerBrasDroit;
import esialrobotik.ia.actions.a2020.bras.RangerBrasGauche;
import esialrobotik.ia.actions.a2020.gobelets.LargerGobelet1;
import esialrobotik.ia.actions.a2020.gobelets.LargerGobelet2;
import esialrobotik.ia.actions.a2020.gobelets.LargerGobelet3;
import esialrobotik.ia.actions.a2020.gobelets.LargerGobelet4;
import esialrobotik.ia.actions.a2020.gobelets.LargerGobelet5;
import esialrobotik.ia.actions.a2020.gobelets.LeverGobelets;
import esialrobotik.ia.actions.a2020.gobelets.PreparerLargage;
import esialrobotik.ia.actions.a2020.gobelets.PreparerRamassage;
import esialrobotik.ia.actions.a2020.gobelets.ToutRamasser;

public class ActionsPanel2020 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected AX12Link al;
	
	protected static final ActionAX12Abstract[] actions = new ActionAX12Abstract[] {
		new PreparerRamassage(),
		new ToutRamasser(),
		new PreparerLargage(),
		new LargerGobelet1(),
		new LargerGobelet2(),
		new LargerGobelet3(),
		new LargerGobelet4(),
		new LargerGobelet5(),
		new LeverGobelets(),
		new BaisserBrasGauche(),
		new RangerBrasGauche(),
		new BaisserBrasDroit(),
		new RangerBrasDroit(),
	};
	
	public ActionsPanel2020(AX12LinkSerial ax12link) {
		super("AX actions 2020");
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
