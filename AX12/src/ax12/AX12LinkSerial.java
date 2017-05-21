package ax12;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import main.AX12Main;

public class AX12LinkSerial implements AX12Link{
	
	private SerialPort sp;
	private OutputStream os;
	private InputStream is;
	
	
	public AX12LinkSerial(SerialPort sp, int baudRate) throws AX12LinkException {
		this.sp = sp;
		this.setBaudRate(baudRate);
		try {
			this.is = sp.getInputStream();
			this.os = sp.getOutputStream();
		} catch (IOException e) {
			throw new AX12LinkException("Erreur de récupération des flux d'entrées/sorties", e);
		}
	}

	@Override
	public int getBaudRate() {
		return sp.getBaudRate();
	}
	
	public String getUartName() {
		return sp.getName();
	}

	@Override
	public void setBaudRate(int baudRate) throws AX12LinkException {
		try {
			sp.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
			throw new AX12LinkException("Erreur de changement de  baudrate", e);
		}
	}

	@Override
	public void sendCommandWithoutFeedBack(byte[] cmd, int baudRate) throws AX12LinkException {
		int oldBr = -1;
		if (sp.getBaudRate() != baudRate) {
			oldBr = sp.getBaudRate();
			try {
				sp.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				throw new AX12LinkException("Erreur de changement du BaudRate", e);
			}
		}
		
		try {
			os.write(cmd);
			os.flush();
		} catch (IOException e1) {
			throw new AX12LinkException("Erreur de transmition de la commande", e1);
		}
		
		if (oldBr != -1) {
			try {
				sp.setSerialPortParams(oldBr, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				throw new AX12LinkException("Erreur de restauration du BaudRate", e);
			}	
		}
	}
	
	public static String[] getAvailableSerialPorts() {
		ArrayList<String> ports = new ArrayList<>();
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> p = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier cpi;
		CommPort cp;
		
		while(p.hasMoreElements()){
			try {
				cpi = p.nextElement();
				if(cpi != null && !cpi.isCurrentlyOwned()){
					cp = cpi.open(AX12Main.class.getName(), 500);
					if(cp instanceof SerialPort){
						ports.add(cp.getName());
						cp.close();
					}
				}
			} catch (PortInUseException e) {
			}
		}
		
		return ports.toArray(new String[ports.size()]);
	}
	
	/**
	 * Retourne un port série identifié par son nom
	 * @param name si null, le premier port série valide est retourné
	 * @return null si le port série identifiée par son nom n'existe ou n'est pas disponible
	 */
	public static SerialPort getSerialPort(String name) {
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> p = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier cpi;
		CommPort cp;
		
		while(p.hasMoreElements()){
			try {
				cpi = p.nextElement();
				if(cpi != null && !cpi.isCurrentlyOwned() && (name == null || cpi.getName().equals(name))) {
					cp = cpi.open(AX12Main.class.getName(), 500);
					if(cp instanceof SerialPort){
						return (SerialPort) cp;
					}
				}
			} catch (PortInUseException e) {
			}
		}
		
		return null;
	}

}
