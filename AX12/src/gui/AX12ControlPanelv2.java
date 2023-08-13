package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ax12.AX12;
import ax12.AX12.AX12_UART_SPEEDS;
import ax12.value.AX12Compliance;
import ax12.value.AX12Position;
import ax12.AX12Exception;
import ax12.AX12LinkException;

public class AX12ControlPanelv2 extends JFrame {

	private static final long serialVersionUID = 1L;
	private JSlider slider_angle;
	private JSlider slider_marge_erreur;
	private JSlider slider_pente_acceleration;
	
	private JComboBox<AX12_UART_SPEEDS> cbo_current_baudrate;
	private JComboBox<AX12_UART_SPEEDS> cbo_new_baudrate;
	private JComboBox<Integer> cbo_current_id;
	private JComboBox<Integer> cbo_new_id;
	
	private JButton btn_toggle_led;
	private JButton btn_ping;
	private JButton btn_temperature;
	private JButton btn_voltage;
	private JButton btn_is_moving;
	private JButton btn_current_angle;
	private JButton btn_reset;
	private JButton btn_write_baudrate_id;
	
	private JLabel lib_angle;
	private JLabel lib_marge_erreur;
	private JLabel lib_pente_acceleration;
	
	private AX12 ax12;
	
	public AX12ControlPanelv2(AX12 ax12) {
		super("AX12 ID "+ax12.getAddress());
		this.ax12 = ax12;
		int initialValue = 1800;
		if (ax12.isBroadcasting()) {
		//	btn_ping.setVisible(false);
		} else {
			try {
				initialValue = (int) (ax12.readServoPosition().getAngleAsDegrees())*10;
			} catch (AX12LinkException | AX12Exception e) {
				e.printStackTrace();
			}
		}
		initGui(initialValue );
		drawGui();
		connectListeners();
		computeWriteButtonAccess();
	}
	
	private void initGui(int initialValue) {
		slider_angle = new JSlider(JSlider.VERTICAL, (int)(AX12.AX12_MIN_ANGLE_DEGREES * 10), (int)(AX12.AX12_MAX_ANGLE_DEGREES * 10), initialValue);
		slider_marge_erreur = new JSlider(JSlider.VERTICAL, 1, 7, 3);
		slider_pente_acceleration = new JSlider(JSlider.VERTICAL, 1, 254, 128);
		btn_toggle_led = new JButton("LED ON");
		btn_ping = new JButton("PING");
		btn_current_angle = new JButton("Current angle");
		btn_reset = new JButton("Factory Reset");
		btn_temperature = new JButton("Temperature °c");
		btn_voltage = new JButton("Voltage");
		btn_is_moving = new JButton("In motion ?");
		btn_write_baudrate_id = new JButton("Write new baudrate and new id");
		cbo_current_baudrate = new JComboBox<AX12_UART_SPEEDS>(AX12_UART_SPEEDS.values());
		cbo_new_baudrate = new JComboBox<AX12_UART_SPEEDS>(AX12_UART_SPEEDS.values());
		cbo_current_id = new JComboBox<Integer>();
		cbo_new_id = new JComboBox<Integer>();
		for (int i=0; i<255; i++) {
			cbo_current_id.addItem(i);
			cbo_new_id.addItem(i);
		}
		cbo_current_id.setSelectedItem(ax12.getAddress());
		cbo_new_id.setSelectedItem(ax12.getAddress());
		lib_angle = new JLabel();
		lib_marge_erreur = new JLabel();
		lib_pente_acceleration = new JLabel();
		
		majLibAngle();
		majLibMargeAngle();
		majLibPenteAcceleration();
	}
	
	private void drawGui() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 8, 1, 0., 0., GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);

		JLabel libBehaviour = new JLabel("Behaviour settings :");
		libBehaviour.setOpaque(true);
		libBehaviour.setBackground(Color.LIGHT_GRAY);
		this.add(libBehaviour, gbc);
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.gridheight = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(lib_angle, gbc);
		
		gbc.gridx++;
		this.add(slider_angle, gbc);
		
		gbc.gridx++;
		this.add(lib_marge_erreur, gbc);
		
		gbc.gridx++;
		this.add(slider_marge_erreur, gbc);
		
		gbc.gridx++;
		this.add(lib_pente_acceleration, gbc);
		
		gbc.gridx++;
		this.add(slider_pente_acceleration, gbc);
		
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridheight = 1;
		gbc.gridx++;
		this.add(btn_toggle_led, gbc);

		gbc.gridx++;
		this.add(btn_ping, gbc);
		
		gbc.gridx--;
		gbc.gridy++;
		this.add(btn_temperature, gbc);
		
		gbc.gridx++;
		this.add(btn_voltage, gbc);

		gbc.gridy++;
		this.add(btn_current_angle, gbc);
		
		gbc.gridx--;
		this.add(btn_is_moving, gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 8;
		JLabel libCommunication = new JLabel("Communication settings :");
		libCommunication.setOpaque(true);
		libCommunication.setBackground(Color.LIGHT_GRAY);
		this.add(libCommunication, gbc);

		gbc.gridy++;
		JPanel pnl1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnl1.add(new JLabel("Current UART speed "));
		pnl1.add(cbo_current_baudrate);
		pnl1.add(new JLabel(" New speed "));
		pnl1.add(cbo_new_baudrate);
		this.add(pnl1, gbc);
		
		gbc.gridy++;
		JPanel pnl2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnl2.add(new JLabel("Current AX12 address "));
		pnl2.add(cbo_current_id);
		pnl2.add(new JLabel(" New address "));
		pnl2.add(cbo_new_id);
		this.add(pnl2, gbc);
		
		gbc.gridy++;
		this.add(btn_write_baudrate_id, gbc);
	}

	private void connectListeners() {
		slider_angle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				majLibAngle();
				try {
					AX12ControlPanelv2.this.ax12.setServoPosition(AX12Position.buildFromDegrees(((double)AX12ControlPanelv2.this.slider_angle.getValue()) / 10.));
				} catch (AX12LinkException | AX12Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		slider_marge_erreur.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				majLibMargeAngle();
				try {
					AX12Compliance c = AX12Compliance.fromFriendlyValue((int)AX12ControlPanelv2.this.slider_marge_erreur.getValue());
					AX12ControlPanelv2.this.ax12.setCwComplianceMargin(c);
					AX12ControlPanelv2.this.ax12.setCcwComplianceMargin(c);
				} catch (AX12LinkException | AX12Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		slider_pente_acceleration.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				majLibPenteAcceleration();
				try {
					AX12ControlPanelv2.this.ax12.setCwComplianceSlope((int)AX12ControlPanelv2.this.slider_pente_acceleration.getValue());
					AX12ControlPanelv2.this.ax12.setCcwComplianceSlope((int)AX12ControlPanelv2.this.slider_pente_acceleration.getValue());
				} catch (AX12LinkException | AX12Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		btn_toggle_led.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btn_toggle_led.getText().equals("LED ON")) {
					btn_toggle_led.setText("LED OFF");
					try {
						ax12.setLed(true);
					} catch (AX12LinkException | AX12Exception e1) {
						e1.printStackTrace();
					}
				} else {
					btn_toggle_led.setText("LED ON");
					try {
						ax12.setLed(false);
					} catch (AX12LinkException | AX12Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		btn_current_angle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, Double.toString(AX12ControlPanelv2.this.ax12.readServoPosition().getAngleAsDegrees()));
				} catch (HeadlessException | AX12LinkException | AX12Exception e1) {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_ping.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, AX12ControlPanelv2.this.ax12.ping() ? "OK" : "KO");
				} catch (HeadlessException | AX12LinkException e1) {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					AX12ControlPanelv2.this.ax12.reset();
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, "reset");
				} catch (HeadlessException | AX12Exception | AX12LinkException e1) {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_temperature.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, AX12ControlPanelv2.this.ax12.getTemperature()+"°c");
				} catch (HeadlessException | AX12LinkException | AX12Exception e1) {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_voltage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, AX12ControlPanelv2.this.ax12.getVoltage()+"v");
				} catch (HeadlessException | AX12LinkException | AX12Exception e1) {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_is_moving.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, AX12ControlPanelv2.this.ax12.isMoving() ? "Oui" : "Non");
				} catch (HeadlessException | AX12LinkException | AX12Exception e1) {
					JOptionPane.showMessageDialog(AX12ControlPanelv2.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		cbo_current_baudrate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AX12_UART_SPEEDS speed = (AX12_UART_SPEEDS)cbo_current_baudrate.getSelectedItem();
				try {
					ax12.setBaudRateRaw(speed.intVal);
					computeWriteButtonAccess();
				} catch (Exception e1) { 
					e1.printStackTrace();
				}
			}
		});
		
		cbo_new_baudrate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computeWriteButtonAccess();
			}
		});
		
		cbo_current_id.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ax12.setAddress((int) cbo_current_id.getSelectedItem());
				computeWriteButtonAccess();
			}
		});
		
		cbo_new_id.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computeWriteButtonAccess();
			}
		});
		
		btn_write_baudrate_id.addActionListener(new  ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				writeNewBaudrateAndAddress();
			}
		});
	}
	
	private void writeNewBaudrateAndAddress() {
		AX12_UART_SPEEDS newSpeed = (AX12_UART_SPEEDS)cbo_new_baudrate.getSelectedItem();
		int newId = (int) cbo_new_id.getSelectedItem();
		
		try {
			ax12.writeAddress(newId);
			ax12.writeUartSpeed(newSpeed);
			ax12.setBaudRateRaw(newSpeed.intVal);
			computeWriteButtonAccess();
		} catch (AX12LinkException | AX12Exception e) {
			e.printStackTrace();
		}
	}

	private void computeWriteButtonAccess() {
		AX12_UART_SPEEDS currentSpeed = (AX12_UART_SPEEDS)cbo_current_baudrate.getSelectedItem();
		AX12_UART_SPEEDS newSpeed = (AX12_UART_SPEEDS)cbo_new_baudrate.getSelectedItem();
		int currentId = (int) cbo_current_id.getSelectedItem();
		int newId = (int) cbo_new_id.getSelectedItem();
		
		btn_write_baudrate_id.setEnabled(currentId != newId || currentSpeed != newSpeed);
	}
	
	private void majLibAngle() {
		this.lib_angle.setText("<html>Angle<br>"+(slider_angle.getValue()/10.)+"°</html>");
	}
	
	private void majLibMargeAngle() {
		this.lib_marge_erreur.setText("<html>Angle<br>margin<br>"+slider_marge_erreur.getValue()+"</html>");
	}
	
	private void majLibPenteAcceleration() {
		this.lib_pente_acceleration.setText("<html>Acceleration<br>slope<br>"+slider_pente_acceleration.getValue()+"</html>");
	}
}
