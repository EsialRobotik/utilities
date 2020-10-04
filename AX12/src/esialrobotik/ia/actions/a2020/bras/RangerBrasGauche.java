package esialrobotik.ia.actions.a2020.bras;

import esialrobotik.ia.actions.a2020.ActionAX12Abstract;

public class RangerBrasGauche extends ActionAX12Abstract {

	@Override
	protected void childExecution() {
		this.go(ACTION_AX12.BRAS_GAUCHE_RANGER);
	}

}
