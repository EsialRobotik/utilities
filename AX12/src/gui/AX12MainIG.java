package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ax12.AX12;
import ax12.AX12LinkException;
import ax12.AX12LinkSerial;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import main.AX12Main;

public class AX12MainIG extends JFrame{

	private JComboBox<SerialPortWrapper> combo_port_name;
	private JComboBox<AX12.AX12_UART_SPEEDS> cbo_uart_speeds;
	private JButton btn_refresh_combo;
	
	private JButton btn_broadcast;
	private JButton[] btns_Ax12s;
	
	private AX12LinkSerial currentAx12Link;
	private SerialPort serialPort;
	
	public AX12MainIG(String title) {
		super(title);
		
		initIg();
		drawIg();
		connectListeners();
		
		refreshUartList();
	}
	
	public AX12LinkSerial getAx12Link() {
		return this.currentAx12Link;
	}
	
	private void initIg() {
		combo_port_name = new JComboBox<SerialPortWrapper>();
		cbo_uart_speeds = new JComboBox<AX12.AX12_UART_SPEEDS>();
		for (AX12.AX12_UART_SPEEDS speed : AX12.AX12_UART_SPEEDS.values()) {
			cbo_uart_speeds.addItem(speed);
		}
		btn_refresh_combo = new JButton("Recharger");
		btn_broadcast = new JButton("AX12 BroadCast");
		
		btns_Ax12s = new JButton[254];
		for (int i=0; i<btns_Ax12s.length; i++) {
			btns_Ax12s[i] = new JButton(""+(i));	
		}
	}
	
	private void drawIg() {
		this.setLayout(new BorderLayout());
		
		JPanel pnl_nord = new JPanel();
		pnl_nord.add(combo_port_name);
		pnl_nord.add(cbo_uart_speeds);
		pnl_nord.add(btn_refresh_combo);
		pnl_nord.add(btn_broadcast);
		
		this.add(pnl_nord, BorderLayout.NORTH);
		JPanel pnl_center = new JPanel(new GridLayout(16,  16));
		for (JButton btn : btns_Ax12s) {
			pnl_center.add(btn);
		}
		this.add(pnl_center, BorderLayout.CENTER);
		
	}
	
	private void connectListeners() {
		btn_broadcast.addActionListener(new ActionAX12(254));
		
		for (int i=0; i<btns_Ax12s.length; i++) {
			btns_Ax12s[i].addActionListener(new ActionAX12(i));
		}
		
		btn_refresh_combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AX12MainIG.this.refreshUartList();
			}
		});
		
		cbo_uart_speeds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AX12MainIG.this.setUartSpeed((AX12.AX12_UART_SPEEDS)cbo_uart_speeds.getSelectedItem());
			}
		});
	}
	
	private List<SerialPort> getAvailableSerialPortList(){
		@SuppressWarnings(	"unchecked")
		Enumeration<CommPortIdentifier> p = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier cpi;
		CommPort cp;
		List<SerialPort> liste = new ArrayList<>();
		
		// On met le port sur lequel on est d�j�
		if (this.serialPort != null) {
			liste.add(this.serialPort);
		}
		
		while(p.hasMoreElements()){
			try {
				cpi = p.nextElement();
				if(cpi != null && !cpi.isCurrentlyOwned()){
					cp = cpi.open(AX12Main.class.getName(), 500);
					if(cp instanceof SerialPort){
						liste.add((SerialPort)cp);
					}
				}
			} catch (PortInUseException e) {
			}
		}
		return liste;
	}
	
	private void refreshUartList() {
		combo_port_name.removeAll();
		for (SerialPort sp : this.getAvailableSerialPortList()) {
			combo_port_name.addItem(new SerialPortWrapper(sp));
		}
	}
	
	private void setUartSpeed(AX12.AX12_UART_SPEEDS speed) {
		if (AX12MainIG.this.currentAx12Link == null) {
			return;
		}
		try {
			AX12MainIG.this.currentAx12Link.setBaudRate(speed.intVal);
		} catch (AX12LinkException e) {
			e.printStackTrace();
		}
	}
	
	private class ActionAX12 implements ActionListener {
		
		private int address;

		public ActionAX12(int address) {
			this.address = address;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (AX12MainIG.this.currentAx12Link == null) {
				if (AX12MainIG.this.serialPort == null) {
					
					Object item = combo_port_name.getSelectedItem();
					if (item == null) {
						return;
					}
					
					AX12MainIG.this.serialPort = ((SerialPortWrapper)item).sp;
				}
				
				try {
					AX12MainIG.this.currentAx12Link = new AX12LinkSerial(AX12MainIG.this.serialPort, ((AX12.AX12_UART_SPEEDS)cbo_uart_speeds.getSelectedItem()).intVal);
				} catch (AX12LinkException e1) {
					e1.printStackTrace();
					return;
				}
			}
			AX12 ax12 = new AX12(this.address, AX12MainIG.this.currentAx12Link);
			AX12ControlPanel pnl = new AX12ControlPanel(ax12);
			pnl.pack();
			pnl.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			pnl.setVisible(true);
		}	
	}
	
	private class SerialPortWrapper {
		
		private SerialPort sp;
		
		public SerialPortWrapper(SerialPort sp) {
			this.sp = sp;
		}
		
		@Override
		public String toString() {
			return sp.getName();
		}
	}
	
}
