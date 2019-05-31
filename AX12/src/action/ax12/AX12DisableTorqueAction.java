package action.ax12;

import action.Action;
import action.ActionException;
import ax12.AX12;
import ax12.AX12Exception;
import ax12.AX12LinkException;

public class AX12DisableTorqueAction extends Action {
	
	protected AX12 ax12;
	protected boolean actionDone;
	
	public AX12DisableTorqueAction(AX12 ax12) {
		this.ax12 = ax12;
		this.actionDone = false;
	}
	
	@Override
	public void doAction() throws ActionException {
		try {
			this.ax12.disableTorque();
			this.actionDone = true;
		} catch (AX12LinkException | AX12Exception e) {
			throw new ActionException("Error applying position state", e);
		}
	}

	@Override
	public boolean actionDone() {
		return this.actionDone;
	}

	@Override
	public String getActionId() {
		return "torque";
	}

	@Override
	public String getActionValueAsString() {
		return "disable";
	}

	@Override
	public String getActionActuatorId() {
		return "" + this.ax12.getAddress();
	}
	
	public AX12 getAx12() {
		return this.ax12;
	}
}
