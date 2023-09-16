package rpLidar.link;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import rpLidar.utils.LidarHelper;

/**
 * Proxy pour accéder au port de communication du Lidar
 * 
 * @author gryttix
 *
 */
public class RpLidarLink {

	protected SerialPort serialPort;
	protected InputStream in;
	protected OutputStream out;
	
	protected BufferedInputStream bis;
	
	private static final int BAUD_SPEED = 250000;
	private static final int RECEIVE_TIMEOUT_MS = 200;
	
	StringBuffer sb;
	
	public RpLidarLink(SerialPort sp) throws IOException {
		this.serialPort = sp;
		this.in = sp.getInputStream();
		bis = new BufferedInputStream(in);
		this.out = sp.getOutputStream();
		try {
			serialPort.setSerialPortParams(
					BAUD_SPEED,
			        SerialPort.DATABITS_8,
			        SerialPort.STOPBITS_1,
			        SerialPort.PARITY_NONE
	        );
			serialPort.enableReceiveTimeout(RECEIVE_TIMEOUT_MS);
			sb = new StringBuffer();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Identique {@link InputStream#read(byte[])}
	 * 
	 * @param b
	 * @return
	 * @throws IOException
	 */
	public int read(byte[] b) throws IOException {
		return this.bis.read(b);
	}
	
	public int read(byte[] b, int offset, int len) throws IOException {
		return this.bis.read(b, offset, len);
	}
	
	public void write(byte[] b, int offset, int length) throws IOException {
		this.out.write(b, offset, length);
	}
	
	public void write(byte[] b) throws IOException {
		this.out.write(b);
	}
	
	public void flush() throws IOException {
		this.out.flush();
	}
	
	/**
	 * Démarre ou arrête la rotation du Lidar
	 * 
	 * @param enable true = démarrer, falmse = arrêter
	 * @throws IOException
	 */
	public void enableRotation(boolean enable) {
		this.serialPort.setDTR(!enable);
	}
	
	/**
	 * Essaye de lire 'len' bytes en appellant plusieurs fois la méthode read du flux de lecture du lidar
	 * Si un appel à red renvoie -1 l'essai s'achève et la méthode renvoie le nombre de bytes lus
	 * Un appel à read bloque pendant une certaine durée avant de rendre la main et de renvoyer -1 si aucun byte n'est lu 
	 * 
	 * @param buffer
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public int tryToReadNByteWith1Retry(byte[] buffer, int len) throws IOException {
		int qte = 0;
		int currentOffset = 0;
		int retryCount = 1;
		
		while (retryCount >= 0 && currentOffset < len) {
			qte = this.bis.read(buffer, currentOffset, len-currentOffset);
			if (qte == 0) {
				retryCount--;
			} else {
				currentOffset += qte;	
			}
		}
		return currentOffset;
	}
	
	/**
 	 * Essaye de lire une ligne ne provenance du lidar
	 * Null si aucun ligne n'a été lue au bout du timeout
	 * La lecture est bloquante
	 * @param timeoutms
	 * @return
	 * @throws IOException
	 */
	public String tryToReadLine(int timeoutms) throws IOException {
		sb.setLength(0);
		long start = System.currentTimeMillis(); 
		/*
	    // available() est très coûteux en temps d'IO et ralentit considérablement le dépilage du buffer jusqu'à saturation
		while (this.bis.available() == 0) {
			if (System.currentTimeMillis() - start > timeoutms) {
				return null;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//*/
		do {
			char c = (char) this.bis.read();
			if (c == '\n' || c == '\r') {
				break;
			}
			sb.append(c);
		} while (true);
		return sb.length() > 0 ? sb.toString() : null;
	}
	
	/**
	 * Purge le flux d'entrée
	 * @throws IOException 
	 */
	public void cleanInput() throws IOException {
		byte[] n = new byte[16];
		int qte = 0;
		while ((qte = this.tryToReadNByteWith1Retry(n, n.length)) > 0) {
			System.out.println("Clean input, found "+qte+" bytes : "+LidarHelper.unsignedBytesToHex(n));
		}
	}
	
	public int getReadAvailableSize() {
		try {
			return this.bis.available();
		} catch (IOException e) {
			return -1;
		}
	}
	
}
