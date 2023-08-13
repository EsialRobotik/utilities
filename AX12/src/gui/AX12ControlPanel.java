package gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ax12.AX12;
import ax12.AX12.AX12_UART_SPEEDS;
import ax12.value.AX12Compliance;
import ax12.value.AX12Position;
import ax12.AX12Exception;
import ax12.AX12LinkException;

public class AX12ControlPanel extends JFrame {

	private static final long serialVersionUID = 1L;
	private JSlider slider_angle;
	private JSlider slider_marge_erreur;
	private JSlider slider_pente_acceleration;
	private JSlider slider_punch;
	
	private JComboBox<AX12_UART_SPEEDS> cbo_baudrate;
	private JComboBox<Integer> cbo_id;
	private JCheckBox chk_update_baud_rate;
	private JCheckBox chk_update_id;
	
	private JTextField txt_limit_temp;
	
	private JButton btn_toggle_led;
	private JButton btn_ping;
	private JButton btn_temperature;
	private JButton btn_voltage;
	private JButton btn_is_moving;
	private JButton btn_current_angle;
	private JButton btn_reset;
	
	private JLabel lib_angle;
	private JLabel lib_marge_erreur;
	private JLabel lib_pente_acceleration;
	
	private AX12 ax12;
	
	public AX12ControlPanel(AX12 ax12) {
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
		initGui(initialValue);
		drawGui();
		connectListeners();
	}
	
	private void initGui(int initialValue) {
		slider_angle = new JSlider(JSlider.VERTICAL, (int)(AX12.AX12_MIN_ANGLE_DEGREES * 10), (int)(AX12.AX12_MAX_ANGLE_DEGREES * 10), initialValue);
		slider_marge_erreur = new JSlider(JSlider.VERTICAL, 1, 7, 3);
		slider_pente_acceleration = new JSlider(JSlider.VERTICAL, 1, 254, 128);
		btn_toggle_led = new JButton("LED ON");
		btn_ping = new JButton("PING");
		btn_current_angle = new JButton("Angle actuel");
		btn_reset = new JButton("Factory Reset");
		btn_temperature = new JButton("Température");
		btn_voltage = new JButton("Voltage");
		btn_is_moving = new JButton("En mouvment ?");
		try {
			txt_limit_temp = new JTextField(""+this.ax12.getLimitTemp(), 3);
		} catch (AX12LinkException | AX12Exception e) {
			txt_limit_temp = new JTextField("err", 3);
			e.printStackTrace();
		}
		cbo_baudrate = new JComboBox<AX12_UART_SPEEDS>(AX12_UART_SPEEDS.values());
		cbo_id = new JComboBox<Integer>();
		for (int i=0; i<255; i++) {
			cbo_id.addItem(i);
		}
		cbo_id.setSelectedItem(ax12.getAddress());
		cbo_id.setSelectedItem(this.ax12.getAddress());
		chk_update_baud_rate = new JCheckBox("Update AX12 Baud Rate too");
		chk_update_id = new JCheckBox("Update AX12 ID too");
		lib_angle = new JLabel();
		lib_marge_erreur = new JLabel();
		lib_pente_acceleration = new JLabel();
		
		majLibAngle();
		majLibMargeAngle();
		majLibPenteAcceleration();
	}
	
	private void drawGui() {
		this.setLayout(new FlowLayout());

		this.add(slider_angle);
		this.add(lib_angle);
		this.add(slider_marge_erreur);
		this.add(lib_marge_erreur);
		this.add(slider_pente_acceleration);
		this.add(lib_pente_acceleration);
		this.add(btn_toggle_led);
		this.add(btn_ping);
		this.add(btn_temperature);
		this.add(btn_voltage);
		this.add(btn_current_angle);
		this.add(btn_reset);
		this.add(txt_limit_temp);
		this.add(new JLabel("°c"));
		this.add(btn_is_moving);
		this.add(cbo_id);
		this.add(chk_update_id);
		this.add(cbo_baudrate);
		this.add(chk_update_baud_rate);
	}
	
	private void connectListeners() {
		slider_angle.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				majLibAngle();
				try {
					AX12ControlPanel.this.ax12.setServoPosition(AX12Position.buildFromDegrees(((double)AX12ControlPanel.this.slider_angle.getValue()) / 10.));
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
					AX12Compliance c = AX12Compliance.fromFriendlyValue((int)AX12ControlPanel.this.slider_marge_erreur.getValue());
					AX12ControlPanel.this.ax12.setCwComplianceMargin(c);
					AX12ControlPanel.this.ax12.setCcwComplianceMargin(c);
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
					AX12ControlPanel.this.ax12.setCwComplianceSlope((int)AX12ControlPanel.this.slider_pente_acceleration.getValue());
					AX12ControlPanel.this.ax12.setCcwComplianceSlope((int)AX12ControlPanel.this.slider_pente_acceleration.getValue());
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
					JOptionPane.showMessageDialog(AX12ControlPanel.this, Double.toString(AX12ControlPanel.this.ax12.readServoPosition().getAngleAsDegrees()));
				} catch (HeadlessException | AX12LinkException | AX12Exception e1) {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_ping.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, AX12ControlPanel.this.ax12.ping() ? "OK" : "KO");
				} catch (HeadlessException | AX12LinkException e1) {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					AX12ControlPanel.this.ax12.reset();
					JOptionPane.showMessageDialog(AX12ControlPanel.this, "reset");
				} catch (HeadlessException | AX12Exception | AX12LinkException e1) {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_temperature.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, AX12ControlPanel.this.ax12.getTemperature()+"°c");
				} catch (HeadlessException | AX12LinkException | AX12Exception e1) {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_voltage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, AX12ControlPanel.this.ax12.getVoltage()+"v");
				} catch (HeadlessException | AX12LinkException | AX12Exception e1) {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		btn_is_moving.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, AX12ControlPanel.this.ax12.isMoving() ? "Oui" : "Non");
				} catch (HeadlessException | AX12LinkException | AX12Exception e1) {
					JOptionPane.showMessageDialog(AX12ControlPanel.this, "KO : "+e1.getMessage());
				}
			}
		});
		
		cbo_baudrate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AX12_UART_SPEEDS speed = (AX12_UART_SPEEDS)cbo_baudrate.getSelectedItem();
					try {
						if (chk_update_baud_rate.isSelected()) {
							ax12.writeUartSpeed(speed);
						}
						ax12.setBaudRateRaw(speed.intVal);
					} catch (Exception e1) { 
						e1.printStackTrace();
					}
				}
		});
		
		cbo_id.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int adresse = (int) cbo_id.getSelectedItem();
				if (chk_update_id.isSelected()) {
					if (JOptionPane.showConfirmDialog(AX12ControlPanel.this, "Changer l'adresse ?", "Ecrire la nouvelle adresse ("+adresse+") sur l'AX12 "+ax12.getAddress()+" ? ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						try {
							ax12.writeAddress(adresse);
						} catch (AX12LinkException | AX12Exception e1) {
							e1.printStackTrace();
						}
					} else {
						cbo_id.setSelectedItem(ax12.getAddress());
						return;
					}
				} else {
					ax12.setAddress(adresse);
				}
			}
		});
		
		txt_limit_temp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int i = Integer.parseInt(txt_limit_temp.getText());
					if (i >= 0) {
						try {
							ax12.setLimitTemp(i);
						} catch (AX12LinkException | AX12Exception e1) {
							JOptionPane.showMessageDialog(AX12ControlPanel.this, e1.getMessage());
						}
					}
				} catch (NumberFormatException e1) {
				}
			}
		});
	}
	
	private void majLibAngle() {
		this.lib_angle.setText("<html>Angle :<br>"+(slider_angle.getValue()/10.)+"</html>");
	}
	
	private void majLibMargeAngle() {
		this.lib_marge_erreur.setText("<html>Marge<br>angle :<br>"+slider_marge_erreur.getValue()+"</html>");
	}
	
	private void majLibPenteAcceleration() {
		this.lib_pente_acceleration.setText("<html>Pente<br>accélération :<br>"+slider_pente_acceleration.getValue()+"</html>");
	}
}
