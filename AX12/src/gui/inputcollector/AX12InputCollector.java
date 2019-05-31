package gui.inputcollector;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;

import action.ActionPool;
import action.ax12.AX12PositionAction;
import action.ax12.PumpAction;
import action.ax12.PumpAction.PUMP;
import action.ax12.WaitingAction;
import action.ActionOrchestrator;
import action.ActionOrchestratorHelper;
import ax12.AX12;
import ax12.AX12Exception;
import ax12.AX12Link;
import ax12.AX12LinkException;
import ax12.value.AX12Compliance;

public class AX12InputCollector extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ActionOrchestrator orchestrator;
	protected AX12Link ax12Link;
	protected AX12 broadcast;
	protected List<AX12> currentAX12;
	protected File loaded_file;
	protected boolean last_changes_saved;
	
	protected JTable tbl_actions;
	protected ActionsTableModel atm;
	protected JTextField txt_ax12_list;
	protected JButton btn_set_ax12;
	protected JButton btn_record_new_pool;
	protected JButton btn_record_current_pool;
	protected JLabel lib_liste_ax12;
	protected JButton btn_replay_pool;
	protected JButton btn_disable_torque;
	protected JButton btn_play_all;
	protected JButton btn_insert_delay;
	protected JTextField txt_delay;
	protected JButton btn_insert_pump_action;
	protected JComboBox<PUMP> cbo_pump_id;
	protected JComboBox<String> cbo_pump_action;
	protected JButton btn_move_pool_up;
	protected JButton btn_move_pool_down;
	protected JButton btn_remove_pool;
	protected JSlider slider_elasticity;
	protected JSlider slider_speed;
	protected JSlider slider_compliance;
	protected JButton btn_save;
	protected JButton btn_save_to;
	protected JButton btn_load;
	protected JLabel lib_loaded_file;
	protected JFileChooser jfc_json;

	public AX12InputCollector(AX12Link ax12Link) {
		super("Input collector");
		this.ax12Link = ax12Link;
		this.broadcast = new AX12(AX12.AX12_ADDRESS_BROADCAST, ax12Link);
		this.currentAX12 = new ArrayList<>();
		this.orchestrator = new ActionOrchestrator();
		this.atm = new ActionsTableModel(orchestrator);
		this.loaded_file = null;
		this.last_changes_saved = true; // To avoid a message on first file loading
		initGui();
		connectListeners();
		drawGui();
	}
	
	public void initGui() {
		txt_ax12_list = new JTextField("1, 2, 3, 4", 10);
		btn_set_ax12 = new JButton("Set AX12");
		btn_record_new_pool = new JButton("Record new pool");
		btn_record_current_pool = new JButton("Record current pool");
		tbl_actions = new JTable(atm);
		btn_replay_pool = new JButton("Replay pool");
		btn_disable_torque = new JButton("Disable torque");
		btn_play_all = new JButton("Play all");
		btn_insert_delay = new JButton("Insert delay ms");
		txt_delay = new JTextField("1000", 10);
		btn_insert_pump_action = new JButton("Insert pump action");
		cbo_pump_action = new JComboBox<>(new String[]{"ON", "OFF"});
		cbo_pump_id = new JComboBox<>(PUMP.values());
		btn_move_pool_up = new JButton("Move pool up");
		btn_remove_pool = new JButton("Remove pool");
		btn_move_pool_down = new JButton("Move pool down");
		slider_elasticity = new JSlider(JSlider.HORIZONTAL, 0, 255, 128);
		slider_speed = new JSlider(JSlider.HORIZONTAL, 0, 1023, 512);
		slider_compliance = new JSlider(JSlider.HORIZONTAL, 1, 7, 3);
		btn_save = new JButton("Save");
		btn_save_to = new JButton("Save to");
		btn_load = new JButton("Load");
		lib_loaded_file = new JLabel("No file loaded yet.");
		jfc_json = new JFileChooser();
		jfc_json.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc_json.setMultiSelectionEnabled(false);
		jfc_json.setFileFilter(new javax.swing.filechooser.FileFilter() {
			
			@Override
			public String getDescription() {
				return "Fichier JSON";
			}
			
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".json");
			}
		});

		lib_liste_ax12 = new JLabel("Ancun ax12");
	}
	
	public void connectListeners() {
		btn_set_ax12.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadAX12FromList();
			}
		});
		
		btn_record_new_pool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recordPool(false);
			}
		});
		
		btn_record_current_pool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recordPool(true);
			}
		});
		
		btn_replay_pool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				replaySelectedPool();
			}
		});
		
		btn_disable_torque.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disableTorque();
			}
		});
		
		btn_play_all.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				orchestrator.playAllPool();
			}
		});
		
		btn_insert_delay.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				insertDelay();
			}
		});
		
		btn_insert_pump_action.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertPumpAction();
			}
		});
		
		btn_move_pool_up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveSelectedPool("UP");
			}
		});
		
		btn_remove_pool.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveSelectedPool("REMOVE");
			}
		});
		
		btn_move_pool_down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveSelectedPool("DOWN");
			}
		});
		
		btn_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile(false);
			}
		});
		
		btn_save_to.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile(true);
			}
		});
		
		btn_load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});
		
		slider_elasticity.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				setElasticity(slider_elasticity.getValue());
			}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		slider_speed.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				setSpeed(slider_speed.getValue());
			}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		slider_compliance.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				setCompliance(slider_compliance.getValue());
			}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
	}
	
	public void drawGui() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		
		// On commence par insérer tous les boutons
		
		// Zone de saisie de la liste des AX12
		gbc.gridx = 1;
		gbc.gridy = 0;
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		JPanel jp = new JPanel(fl);
		jp.add(new JLabel("AX12 list : "));
		jp.add(txt_ax12_list);
		jp.add(btn_set_ax12);
		this.add(jp,  gbc);
		
		// Liste des ax12 chargés
		gbc.gridy++;
		this.add(lib_liste_ax12, gbc);
		
		// Pentes accélération/décélération
		gbc.gridy++;
		jp = new JPanel(fl);
		jp.add(new JLabel("Acceleration des AX12 : "));
		jp.add(slider_elasticity);
		this.add(jp, gbc);
		
		// Vitesse des ax12
		gbc.gridy++;
		jp = new JPanel(fl);
		jp.add(new JLabel("Vitesse des AX12 : "));
		jp.add(slider_speed);
		this.add(jp, gbc);
		
		// Marge d'erreur des AX12
		gbc.gridy++;
		jp = new JPanel(fl);
		jp.add(new JLabel("Marge d'erreur des AX12 : "));
		jp.add(slider_compliance);
		this.add(jp, gbc);
		
		// Désactivation du couple des moteurs
		gbc.gridy++;
		this.add(btn_disable_torque, gbc);
		
		// Bouton enregistrement nouvelle Pool
		gbc.gridy++;
		this.add(btn_record_new_pool, gbc);
		
		// Bouton ré-enregistrement Pool
		gbc.gridy++;
		this.add(btn_record_current_pool, gbc);
		
		// Bouton insertion délais
		jp = new JPanel(fl);
		jp.add(txt_delay);
		jp.add(btn_insert_delay);
		gbc.gridy++;
		this.add(jp, gbc);
		
		// Bouton insertion allumage/extinction pompe
		jp = new JPanel(fl);
		jp.add(btn_insert_pump_action);
		jp.add(cbo_pump_id);
		jp.add(cbo_pump_action);
		gbc.gridy++;
		this.add(jp, gbc);
		
		// Bouton rejeux Pool / rejeux tout
		jp = new JPanel(fl);
		jp.add(btn_replay_pool);
		jp.add(btn_play_all);
		gbc.gridy++;
		this.add(jp, gbc);
		
		// Bouton déplacement vers le haut / suppression / déplcement vers le bas Pool
		jp = new JPanel(fl);
		jp.add(btn_move_pool_up);
		jp.add(btn_remove_pool);
		jp.add(btn_move_pool_down);
		gbc.gridy++;
		this.add(jp, gbc);
		
		// Bouton de sauvegarde
		jp = new JPanel(fl);
		jp.add(btn_save);
		jp.add(btn_save_to);
		jp.add(btn_load);
		gbc.gridy++;
		this.add(jp, gbc);
		
		// Chemin du fichier de sauvegarde
		gbc.gridy++;
		this.add(lib_loaded_file, gbc);
		
		gbc.gridy++;
		this.add(new JPanel(), gbc);
		
		
		// En dernier le tableau desactions
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridheight = gbc.gridy + 1;
		gbc.gridy = 0;
		gbc.gridx = 0;
		this.add(new JScrollPane(tbl_actions), gbc);
	}
	
	protected void recordPool(boolean currentPool) {
		if (this.currentAX12.isEmpty()) {
			return;
		}
		
		int row = tbl_actions.getSelectedRow();
		if (currentPool) {
			if (row < 0) {
				return;	
			} else {
				row = atm.getPoolIndexForRow(row);
			}
		}
		
		ActionPool pool = new ActionPool();
		for (AX12 ax12 : this.currentAX12) {
			try {
				pool.addAction(new AX12PositionAction(ax12, ax12.readServoPosition()));
			} catch (AX12LinkException | AX12Exception e) {
				JOptionPane.showMessageDialog(this, "Erreur de lecture de l'ax12 #" + ax12.getAddress());
				return;
			}
		}
		
		if (currentPool) {
			orchestrator.replacePool(row, pool);
		} else {
			orchestrator.addActionPool(pool);
		}

		fireOrchestratorModified();
	}
	
	protected void loadAX12FromList() {
		this.currentAX12.clear();
		for (String item : txt_ax12_list.getText().trim().split(",")) {
			try {
				int ax12Id = Integer.parseInt(item.trim());
				if (ax12Id <= AX12.AX12_ADDRESS_MAX && ax12Id > 0) {
					this.currentAX12.add(new AX12(ax12Id, this.ax12Link));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		if (this.currentAX12.isEmpty()) {
			lib_liste_ax12.setText("Aucun AX12");
		} else {
			String liste = "";
			for (AX12 ax12 : currentAX12) {
				liste += ax12.getAddress()+", ";
			}
			
			lib_liste_ax12.setText("AX12 : " + liste.substring(0, liste.length()-2));
		}
	}
	
	protected void replaySelectedPool() {
		int poolIdx = atm.getPoolIndexForRow(tbl_actions.getSelectedRow());
		if (poolIdx > -1) {
			orchestrator.playPool(poolIdx);
		}
	}
	
	protected void disableTorque() {
		for (AX12 ax12 : currentAX12) {
			try {
				ax12.disableTorque();
				ax12.setCcwComplianceSlope(250);
				ax12.setCwComplianceSlope(250);
			} catch (AX12LinkException | AX12Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Insert a delay at the end of the action list or after the selected Pool
	 */
	protected void insertDelay() {
		try {
			long time = Long.parseLong(txt_delay.getText().trim());
			ActionPool ap = new ActionPool();
			ap.addAction(new WaitingAction(time));
			this.orchestrator.addActionPool(ap);
			fireOrchestratorModified();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	protected void insertPumpAction() {
		boolean enable = cbo_pump_action.getSelectedItem().equals("ON");
		PUMP id = (PUMP) cbo_pump_id.getSelectedItem();
		ActionPool ap = new ActionPool();
		ap.addAction(new PumpAction(this.ax12Link, id, enable));
		this.orchestrator.addActionPool(ap);
		fireOrchestratorModified();
	}
	
	protected void moveSelectedPool(String direction) {
		int poolIndex = tbl_actions.getSelectedRow();
		if (poolIndex < 0) {
			return;
		}
		
		int poolIndexOffset = 0;
		poolIndex = atm.getPoolIndexForRow(poolIndex);
		if (direction.equals("UP")) {
			orchestrator.movePoolUp(poolIndex);
			poolIndexOffset = -1;
		} else if (direction.equals("DOWN")) {
			orchestrator.movePoolDown(poolIndex);
			poolIndexOffset = 1;
		} else if (direction.equals("REMOVE")) {
			orchestrator.removePool(poolIndex);
		}
		
		fireOrchestratorModified();
		
		if (poolIndexOffset != 0) {
			int[] newPoolIndexes = atm.getRowsForPoolIndex(poolIndex + poolIndexOffset);
			
			if (newPoolIndexes[0] != -1) {
				tbl_actions.setRowSelectionInterval(newPoolIndexes[0], newPoolIndexes[1]);	
			}
		}
	}
	
	protected void setElasticity(int value) {
		for (AX12 ax : this.currentAX12) {
			try {
				ax.setCcwComplianceSlope(value);
				ax.setCwComplianceSlope(value);
			} catch (AX12LinkException | AX12Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void setSpeed(int value) {
		try {
			broadcast.setServoSpeed(value);
		} catch (AX12LinkException | AX12Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void setCompliance(int value) {
		AX12Compliance c = AX12Compliance.fromFriendlyValue(value);
		try {
			broadcast.setCwComplianceMargin(c);
			broadcast.setCcwComplianceMargin(c);
		} catch (AX12LinkException | AX12Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void saveFile(boolean askWhere) {
		if (askWhere || loaded_file == null) {
			jfc_json.setApproveButtonText("Enregistrer");
			jfc_json.setDialogTitle("Enregistrer");
			if (jfc_json.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			loaded_file = jfc_json.getSelectedFile();
			lib_loaded_file .setText(loaded_file.getAbsolutePath());
		}
		
		try {
			ActionOrchestratorHelper.serializeToJson(orchestrator, loaded_file);
			last_changes_saved = true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Une erreur est survenue pendant l'export :" + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	protected void loadFile() {
		if (!last_changes_saved) {
			switch (JOptionPane.showConfirmDialog(this, "Sauvegarder les actions actuelles ?", "Sauvegarder", JOptionPane.YES_NO_CANCEL_OPTION)) {
				case JOptionPane.YES_OPTION:
					saveFile(false);
					break;
				case JOptionPane.CANCEL_OPTION:
					return;
			}
		}
		
		jfc_json.setApproveButtonText("Ouvrir");
		jfc_json.setDialogTitle("Ouvrir");
		if (jfc_json.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			last_changes_saved = true;
			loaded_file = jfc_json.getSelectedFile();
			
			if (loaded_file != null) {
				lib_loaded_file.setText(loaded_file.getAbsolutePath());
				try {
					orchestrator = ActionOrchestratorHelper.unserializeFromJson(ax12Link, loaded_file);
					atm = new ActionsTableModel(orchestrator);
					tbl_actions.setModel(atm);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Une erreur est survenue pendant l'importation :" + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}

		}
		
	}
	
	protected void fireOrchestratorModified() {
		last_changes_saved = false;
		atm.updateRowMapping();
		atm.fireTableDataChanged();
	}
	
}
