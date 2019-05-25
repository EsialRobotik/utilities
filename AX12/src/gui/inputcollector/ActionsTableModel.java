package gui.inputcollector;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import action.Action;
import action.ActionOrchestrator;


public class ActionsTableModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<RowMapping> mapping;
	protected ActionOrchestrator actionOrchestrator;
	
	private static final int COLUMN_ACTION_POOL = 0;
	private static final int COLUMN_ACTION_TYPE = 1;
	private static final int COLUMN_ACTION_VALUE = 2;
	
	public ActionsTableModel(ActionOrchestrator orchestrator) {
		this.actionOrchestrator = orchestrator;
		mapping = new ArrayList<>();
		this.updateRowMapping();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return mapping.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		RowMapping mapping = this.mapping.get(rowIndex);
		if (mapping.type == RowMapping.ROW_TYPE.ACTION_POOL) {
			if (columnIndex == 0) {
				return "Action Pool #" + mapping.actionPoolIdx;
			}
		} else {
			Action action = this.actionOrchestrator.getPoolAction(mapping.actionPoolIdx, mapping.actionIdx);
			switch (columnIndex) {
				case COLUMN_ACTION_TYPE:
					return action.getActionId() + " " + action.getActionActuatorId();
				case COLUMN_ACTION_VALUE:
					return action.getActionValueAsString();
			}
		}
		
		return null;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case COLUMN_ACTION_POOL:
				return "Pool";
			case COLUMN_ACTION_TYPE:
				return "Action";
			case COLUMN_ACTION_VALUE:
				return "Value";
		}
		return "-";
	}
	
	public void updateRowMapping() {
		this.mapping.clear();
		int count = this.actionOrchestrator.getActionPoolCount();
		for (int i=0; i<count; i++) {
			this.mapping.add(new RowMapping(RowMapping.ROW_TYPE.ACTION_POOL, i, -1));
			
			int actionCount = this.actionOrchestrator.getPoolActionCount(i);
			for (int j=0; j<actionCount; j++) {
				this.mapping.add(new RowMapping(RowMapping.ROW_TYPE.ACTION, i, j));	
			}
		}
		
	}
	
	public int getPoolIndexForRow(int row) {
		if (row >= 0 && row < this.mapping.size()) {
			return mapping.get(row).actionPoolIdx;
		}
			
		return -1;
	}
	
	public int[] getRowsForPoolIndex(int poolIndex) {
		int startingRow = -1;
		for (int i=0; i<this.mapping.size(); i++) {
			RowMapping rm = mapping.get(i);
			if (rm.actionPoolIdx == poolIndex && rm.type == gui.inputcollector.ActionsTableModel.RowMapping.ROW_TYPE.ACTION_POOL) {
				startingRow = i;
				break;
			}
		}
		
		if (startingRow == -1) {
			return new int[]{-1, -1};
		}
		
		int endRow = startingRow;
		for (int i=startingRow + 1; i<this.mapping.size(); i++) {
			RowMapping rm = mapping.get(i);
			if (rm.actionPoolIdx != poolIndex) {
				endRow = i - 1;
				break;
			}
		}
		
		return new int[]{startingRow, endRow};
	}
	
	private static class RowMapping {
		
		enum ROW_TYPE {
			ACTION_POOL,
			ACTION;
		}
		
		ROW_TYPE type;
		int actionPoolIdx;
		int actionIdx;
		
		public RowMapping(ROW_TYPE type, int actionPoolIdx, int actionIdx) {
			this.type = type;
			this.actionPoolIdx = actionPoolIdx;
			this.actionIdx = actionIdx;
		}
		
	}

}
