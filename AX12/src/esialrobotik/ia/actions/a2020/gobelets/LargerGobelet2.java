package esialrobotik.ia.actions.a2020.gobelets;

import esialrobotik.ia.actions.a2020.ActionAX12Abstract;

public class LargerGobelet2 extends ActionAX12Abstract {

	@Override
	protected void childExecution() {
		this.go(ACTION_AX12.RELACHER_GOBELET2);
	}

}
