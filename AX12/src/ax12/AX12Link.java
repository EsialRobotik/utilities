package ax12;

public interface AX12Link {

	/**
	 * Envoie une commande sur le canal
	 * @param cmd la s�quence de byte de la commande
	 * @param baudRate change le baudrate le temps de la transmition si cette op�ration est support�e
	 */
	public void sendCommandWithoutFeedBack(byte[] cmd, int baudRate) throws AX12LinkException;
	
	/**
	 * Renvoie le bauudRate utilis� sur la liaison actuelle
	 * @return
	 */
	public int getBaudRate();
	
	/**
	 * R�gle le baudrate de communication actuel
	 * @param baudRate
	 * @throws AX12LinkException
	 */
	public void setBaudRate(int baudRate) throws AX12LinkException;
	
}
