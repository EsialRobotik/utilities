package gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ax12.AX12;
import ax12.AX12.AX12_UART_SPEEDS;
import ax12.AX12LinkException;

public class AX12ControlPanel extends JFrame {

	private JSlider slider_angle;
	private JSlider slider_vitesse;
	
	private JComboBox<AX12_UART_SPEEDS> cbo_baudrate;
	private JComboBox<Integer> cbo_id;
	private JCheckBox chk_update_baud_rate;
	private JCheckBox chk_update_id;
	
	private JButton btn_toggle_led;
	
	private JLabel lib_communicator_busy;
	private JLabel lib_angle;
	
	private AX12 ax12;
	
	public AX12ControlPanel(AX12 ax12) {
		super("AX12 ID "+ax12.getAddress());
		this.ax12 = ax12;
		initGui();
		drawGui();
		connectListeners();
	}
	
	private void initGui() {
		slider_angle = new JSlider(JSlider.VERTICAL, (int)(AX12.AX12_MIN_ANGLE_DEGREES * 10), (int)(AX12.AX12_MAX_ANGLE_DEGREES * 10), 1800);
		btn_toggle_led = new JButton("LED ON");
		cbo_baudrate = new JComboBox<AX12_UART_SPEEDS>(AX12_UART_SPEEDS.values());
		cbo_id = new JComboBox<Integer>();
		for (int i=0; i<255; i++) {
			cbo_id.addItem(i);
		}
		cbo_id.setSelectedItem(ax12.getAddress());
		cbo_id.setSelectedItem(this.ax12.getAddress());
		chk_update_baud_rate = new JCheckBox("Update AX12 Baud Rate too");
		chk_update_id = new JCheckBox("Update AX12 ID too");
		lib_angle = new JLabel(""+(slider_angle.getValue()/10.));
	}
	
	private void drawGui() {
		this.setLayout(new FlowLayout());
		this.add(slider_angle);
		this.add(lib_angle);
		this.add(btn_toggle_led);
		this.add(cbo_id);
		this.add(chk_update_id);
		this.add(cbo_baudrate);
		this.add(chk_update_baud_rate);
	}
	
	private void connectListeners() {
		slider_angle.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					AX12ControlPanel.this.ax12.setServoPositionInDegrees(((double)AX12ControlPanel.this.slider_angle.getValue()) / 10.);
					lib_angle.setText(""+(((double)AX12ControlPanel.this.slider_angle.getValue()) / 10.));
				} catch (AX12LinkException e1) {
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
					} catch (AX12LinkException e1) {
						e1.printStackTrace();
					}
				} else {
					btn_toggle_led.setText("LED ON");
					try {
						ax12.setLed(false);
					} catch (AX12LinkException e1) {
						e1.printStackTrace();
					}
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
						} catch (AX12LinkException e1) {
							// TODO Auto-generated catch block
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
	}
}
