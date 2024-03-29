package ax12;

import java.util.BitSet;

import ax12.value.AX12Compliance;
import ax12.value.AX12Position;

public class AX12 {
	
	public static final int AX12_ADDRESS_MIN = 0;
	public static final int AX12_ADDRESS_MAX = 253;
	public static final int AX12_ADDRESS_BROADCAST = 254;
	private static final byte AX12_ADDRESS_BROADCAST_BYTE = intToUnsignedByte(AX12_ADDRESS_BROADCAST);
	public static final double AX12_MAX_ANGLE_DEGREES = 300;
	public static final double AX12_MIN_ANGLE_DEGREES = 0;
	
	// AX12 instructions
	public enum AX12_Instr {
	  AX12_INSTR_PING(0x01, 0, 0),
	  AX12_INSTR_READ_DATA(0x02, 2, 2),
	  AX12_INSTR_WRITE_DATA(0x03, 2, 100),
	  AX12_INSTR_REG_WRITE(0x04, 2, 100),
	  AX12_INSTR_ACTION(0x05, 0, 0),
	  AX12_INSTR_RESET(0x06, 0, 0),
	  AX12_INSTR_SYNC_WRITE(0x83, 4, 100);
	  
	  public final byte instr;
	  public final int minParamCount; // The register address counts for a parametter
	  public final int maxParamCount;
	  private AX12_Instr(int instr, int minParamsCount, int maxParamsCount) {
		  this.instr = AX12.intToUnsignedByte(instr);
		  this.minParamCount = minParamsCount;
		  this.maxParamCount = maxParamsCount;
	  }
	};
	
	public void reset() throws AX12LinkException, AX12Exception {
		this.sendRequest(AX12_Instr.AX12_INSTR_RESET, new byte[0]);
	}
	
	// AX12 registers
	public enum AX12_Register {
	  AX12_EEPROM_MODEL_NUMBER(0x00, 2, false),
	  AX12_EEPROM_FIRMWARE_VERSION(0x02, 1, false),
	  AX12_EEPROM_ID(0x03, 1, true),
	  AX12_EEPROM_BAUD_RATE(0x04, 1, true),
	  AX12_EEPROM_RETURN_DELAY_TIME(0x05, 1, true),
	  AX12_EEPROM_CW_ANGLE_LIMIT(0x06, 2, true),
	  AX12_EEPROM_CCW_ANGLE_LIMIT(0x08, 2, true),
	  // 0x0A r�serv�
	  AX12_EEPROM_HIGH_TEMP_LIMIT(0x0B, 1, true),
	  AX12_EEPROM_LOW_VOLTAGE_LIMIT(0x0C, 1, true),
	  AX12_EEPROM_HIGH_VOLTAGE_LIMIT(0x0D, 1, true),
	  AX12_EEPROM_MAX_TORQUE(0x0E, 2, true),
	  AX12_EEPROM_STATUS_RETURN_LEVEL(0x10, 2, true),
	  AX12_EEPROM_ALARM_LED(0x11, 1, true),
	  AX12_EEPROM_ALARM_SHUTDOWN(0x12, 1, true),
	  // 0x13 r�serv�
	  AX12_EEPROM_DOWN_CALIBRATION(0x14, 2, false),
	  AX12_EEPROM_UP_CALIBRATION(0x16, 2, false),
	  AX12_RAM_TORQUE_ENABLE(0x18, 1, true),
	  AX12_RAM_LED(0x19, 1, true),
	  AX12_RAM_CW_COMPILANCE_MARGIN(0x1A, 1, true),
	  AX12_RAM_CCW_COMPILANCE_MARGIN(0x1B, 1, true),
	  AX12_RAM_CW_COMPILANCE_SLOPE(0x1C, 1, true),
	  AX12_RAM_CCW_COMPILANCE_SLOPE(0x1D, 1, true),
	  AX12_RAM_GOAL_POSITION(0x1E, 2, true),
	  AX12_RAM_MOVING_SPEED(0x20, 2, true),
	  AX12_RAM_TORQUE_LIMIT(0x22, 2, true),
	  AX12_RAM_PRESENT_POSITION(0x24, 2, false),
	  AX12_RAM_PRESENT_SPEED(0x26, 2, false),
	  AX12_RAM_PRESENT_LOAD(0x28, 2, false),
	  AX12_RAM_PRESENT_VOLTAGE(0x2A, 1, false),
	  AX12_RAM_PRESENT_TEMPERATURE(0x2B, 1, false),
	  AX12_RAM_REGISTERED_INSTRUCTION(0x2C, 1, true),
	  // 0x2C r�sevr�
	  AX12_RAM_MOVING(0x2E, 1, false),
	  AX12_RAM_LOCK(0x2F, 1, true),
	  AX12_RAM_PUNCH(0x30, 2, true);
	  
	  public final byte regi;
	  public final int size;
	  public final boolean writable;
	  private AX12_Register(int regi, int size, boolean writable) {
		  this.regi = AX12.intToUnsignedByte(regi);
		  this.size = size;
		  this.writable = writable;
	  }

	};
	
	// Les erreurs possibles retourn�es par un AX12
	public enum AX12_Error {
		AX12_ERR_INPUT_VOLTAGE,
		AX12_ERR_ANGLE_LIMIT,
		AX12_ERR_OVERHEATING,
		AX12_ERR_RANGE,
		AX12_ERR_CHECKSUM,
		AX12_ERR_OVERLOAD,
		AX12_ERR_INSTRUCTION,
		AX12_ERR_NO_RESPONSE,
		AX12_ERR_INVALID_RESPONSE;
	}
	
	// AX12 register values for UART SPEED
	public enum AX12_UART_SPEEDS {
		SPEED_1000000(1, 1000000),
		SPEED_500000(3, 500000),
		SPEED_4000000(4, 400000),
		SPEED_250000(7, 250000),
		SPEED_200000(9, 200000),
		SPEED_115200(16, 115200),
		SPEED_57600(34, 57600),
		SPEED_19200(103, 19200),
		SPEED_9600(207, 9600);
		
		public final byte byteVal;
		public final int intVal;
		
		private AX12_UART_SPEEDS(int byteVal, int intVal) {
			this.intVal = intVal;
			this.byteVal = AX12.intToUnsignedByte(byteVal);
		}
		
		public static AX12_UART_SPEEDS fromValue(int speed) {
			for (AX12_UART_SPEEDS s : AX12_UART_SPEEDS.values()) {
				if (s.intVal == speed) {
					return s;
				}
			}
			return null;
		}
		
		@Override
		public String toString() {
			return this.intVal+" bps";
		}
	}
	
	
	private AX12Link ac;
	private int baudRate;
	private byte addr;
	private AX12Link alink;

	/**
	 * 
	 * @param addr
	 * @param speed
	 * @param alink
	 * @throws IllegalArgumentException if ax12 address is not valid
	 */
	public AX12(int addr, AX12_UART_SPEEDS speed, AX12Link alink) throws IllegalArgumentException {
		if (speed == null) {
			throw new IllegalArgumentException("La vitesse de l'UART n'est pas valide");
		}
		this.addr = AX12.intToUnsignedByte(addr);
		this.baudRate = speed.intVal;
		this.alink = alink;
	}
	
	/**
	 * 
	 * @param addr
	 * @param alink
	 * @throws IllegalArgumentException if ax12 address is not valid
	 */
	public AX12(int addr, AX12Link alink) throws IllegalArgumentException {
		this(addr, AX12_UART_SPEEDS.fromValue(alink.getBaudRate()), alink);
	}
	
	public AX12Link getAX12Communicator() {
		return this.ac;
	}
	
	/**
	 * Change l'adresse de l'AX12
	 * @param adresse
	 */
	public void setAddress(int adresse) {
		checkAddressRange(adresse);
		this.addr = AX12.intToUnsignedByte(adresse);
	}
	
	public int getAddress() {
		return AX12.unsignedByteToInt(this.addr);
	}

	/**
	 * D�finit la vitesse de l'AX12
	 * En mode position, accepte une valeur entre 0 et 1023. 1 correspond � environ 0.111 rpm et 1023 ~= 114rpm. 0 = max possible.
	 * En mode roue, contr�le la puissance en % envoy�e au moteur : 0~1023 : rotation anti horaire, 1024~2047 roation horaire
	 * @param spd
	 * @throws AX12LinkException 
	 * @throws AX12Exception 
	 */
	public void setServoSpeed(int spd) throws AX12LinkException, AX12Exception {
		this.write(AX12_Register.AX12_RAM_MOVING_SPEED, spd);
	}

	/**
	 * Asservit l'AX12 � un angle de 0 � 300�
	 * N�cessite que l'AX12 soit en mode position
	 * @param pos
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public void setServoPosition(AX12Position ax12Angle) throws AX12LinkException, AX12Exception {
		this.write(AX12_Register.AX12_RAM_GOAL_POSITION, ax12Angle.getRawAngle());
	}
	
	/**
	 * 
	 * 
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public AX12Position readServoPosition() throws AX12LinkException, AX12Exception {
		return AX12Position.buildFromInt(this.read(AX12_Register.AX12_RAM_PRESENT_POSITION));
	}
	
	/*
	 * Allume ou �teint la LED de l'AX12
	 */
	public void setLed(boolean on) throws AX12LinkException, AX12Exception {
		this.write(AX12_Register.AX12_RAM_LED, on ? 1 : 0);
	}
	
	/**
	 * Indique � l'AX12 d'utiliser une nouvelle vitese de communication sur l'UART
	 * @param speed
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public void writeUartSpeed(AX12_UART_SPEEDS speed) throws AX12LinkException, AX12Exception {
		this.write(AX12_Register.AX12_EEPROM_BAUD_RATE, unsignedByteToInt(speed.byteVal));
	}
	
	/**
	 * Met � jour l'ID de l'AX12 sur sa ROM et bascule sur la nouvelle adresse
	 * @param address
	 * @throws AX12LinkException
	 * @throws AX12Exception 
	 */
	public void writeAddress(int address) throws AX12LinkException, AX12Exception {
		checkAddressRange(address);
		this.write(AX12_Register.AX12_EEPROM_ID, address);
		this.addr = AX12.intToUnsignedByte(address);
	}
	
	/**
	 * 
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public boolean ping() throws AX12LinkException {
		try {
			return this.sendRequest(AX12_Instr.AX12_INSTR_PING, new byte[0]).length > 0;
		} catch (AX12Exception e) {
			// Si on re�oit une exception de l'AX12 qui n'est pas une non r�ponse, c'est qu'il existe
			return !e.contains(AX12_Error.AX12_ERR_NO_RESPONSE);
		}
	}
	
	/**
	 * Indique si l'AX12 est en train de bouger ou pas
	 * Pratique pour savoir s'il a atteint son angle de consigne par exemple
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public boolean isMoving() throws AX12LinkException, AX12Exception {
		return this.read(AX12_Register.AX12_RAM_MOVING) > 0;
	}
	
	/**
	 * Retourne la temp�rature interne de l'AX12
	 * @return
	 * @throws AX12Exception 
	 * @throws AX12LinkException 
	 */
	public int getTemperature() throws AX12LinkException, AX12Exception {
		return this.read(AX12_Register.AX12_RAM_PRESENT_TEMPERATURE);
	}
	
	/**
	 * Retourne la tension aux bornes de l'AX12
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public double getVoltage() throws AX12LinkException, AX12Exception {
		return ((double)this.read(AX12_Register.AX12_RAM_PRESENT_VOLTAGE)) / 10.;
	}
	
	/**
	 * D�finit la vitesse de communication � utiliser pour parler � l'AX12 sans l'inscrire dans ce dernier
	 * @param baudRate
	 */
	public void setBaudRateRaw(int baudRate) {
		this.baudRate = baudRate;
	}
	
	/**
	 * R�cup�re la temp�rature maximale de fonctionnement en degr�s
	 * @param temp
	 * @throws AX12Exception 
	 * @throws AX12LinkException 
	 */
	public int getLimitTemp() throws AX12LinkException, AX12Exception {
		return this.read(AX12_Register.AX12_EEPROM_HIGH_TEMP_LIMIT);
	}
	
	/**
	 * R�gle la temp�rature maximale de fonctionnement en degr�s
	 * @param temp
	 * @throws AX12Exception 
	 * @throws AX12LinkException 
	 */
	public void setLimitTemp(int temp) throws AX12LinkException, AX12Exception {
		if (temp < 0 || temp > 255) {
			throw new RuntimeException("La temp�rature doit �tre comprise entre 0 et 255. Re�u "+temp);
		}
		this.write(AX12_Register.AX12_EEPROM_HIGH_TEMP_LIMIT, temp);
	}
	
	/**
	 * De 0 � 254
	 * @param compliance
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public void setCwComplianceMargin(AX12Compliance compliance) throws AX12LinkException, AX12Exception {
		this.write(AX12_Register.AX12_RAM_CW_COMPILANCE_MARGIN, compliance.getRawValue());
	}
	
	/**
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public AX12Compliance getCwComplianceMargin() throws AX12LinkException, AX12Exception {
		try {
			return AX12Compliance.fromRaw(this.read(AX12_Register.AX12_RAM_CW_COMPILANCE_MARGIN));	
		} catch (IllegalArgumentException e) {
			throw new AX12LinkException("Bad compliance value", e);
		}
	}
	
	/**
	 * De 0 � 254
	 * @param value
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public void setCcwComplianceMargin(AX12Compliance compliance) throws AX12LinkException, AX12Exception {
		this.write(AX12_Register.AX12_RAM_CCW_COMPILANCE_MARGIN, compliance.getRawValue());
	}
	
	/**
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public AX12Compliance getCcwComplianceMargin() throws AX12LinkException, AX12Exception {
		try {
			return AX12Compliance.fromRaw(this.read(AX12_Register.AX12_RAM_CCW_COMPILANCE_MARGIN));	
		} catch (IllegalArgumentException e) {
			throw new AX12LinkException("Bad compliance value", e);
		}
	}
	
	/**
	 * De 0 � 254
	 * @param value
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public void setCwComplianceSlope(int value) throws AX12LinkException, AX12Exception {
		if (value < 0 || value > 254) {
			throw new RuntimeException("La valeur doit �tre comprise entre 0 et 254. Re�u "+value);
		}
		this.write(AX12_Register.AX12_RAM_CW_COMPILANCE_SLOPE, value);
	}
	
	/**
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public int getCwComplianceSlope() throws AX12LinkException, AX12Exception {
		return this.read(AX12_Register.AX12_RAM_CW_COMPILANCE_SLOPE);
	}
	
	/**
	 * De 0 � 254
	 * @param value
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public void setCcwComplianceSlope(int value) throws AX12LinkException, AX12Exception {
		if (value < 1 || value > 255) {
			throw new RuntimeException("La valeur doit �tre comprise entre 0 et 255. Re�u "+value);
		}
		this.write(AX12_Register.AX12_RAM_CCW_COMPILANCE_SLOPE, value);
	}
	
	/**
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public int getCcwComplianceSlope() throws AX12LinkException, AX12Exception {
		return this.read(AX12_Register.AX12_RAM_CCW_COMPILANCE_SLOPE);
	}
	
	/**
	 * 
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	public void disableTorque() throws AX12LinkException, AX12Exception {
		this.write(AX12_Register.AX12_RAM_TORQUE_ENABLE, 0);
	}
	
	/**
	 * D�finit le link � utiliser pour contr�ler l'AX12
	 * @param alink
	 */
	public void setAx12Link(AX12Link alink) {
		this.alink = alink;
	}
	
	/**
	 * Ex�cute une routine de reset du baudrate de l'AX12
	 * @param ax12 l'ax12 � reset
	 * @param newSpeed la nouvelle vitesse � lui donner
	 * @param listener un litener qui suit la progression, null si aucun listener ne doit �tre notifi�
	 * @throws AX12LinkException 
	 */
	public void resetAx12BaudRate(AX12 ax12, AX12_UART_SPEEDS newSpeed, AX12BaudrateResetListener listener) throws AX12LinkException {
		int retries = 5;
		try {
			for (int i=0; i<255; i++) {
				int br = (2000000 / (i+1));
				ax12.setBaudRateRaw(br);
				for (int j=0; j<retries;j++) {
					try {
						ax12.writeUartSpeed(newSpeed);
						if (listener != null) {
							listener.ax12BaudRateResetProgression(i*retries + j, 255 * retries);
						}
					} catch (AX12LinkException e ) {
						if (listener != null) {
							listener.ax12BaudRateResetProgression(255 * retries, 255 * retries);
						}
						throw e;
					} catch (AX12Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(150);
				}
			}	
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (listener != null) {
				listener.ax12BaudRateResetProgression(255 * retries, 255 * retries);	
			}
		}
	}
	
	/**
	 * Indique si l'adresse AX12 est configur�e en broadcast
	 * @return
	 */
	public boolean isBroadcasting() {
		return this.addr == AX12_ADDRESS_BROADCAST_BYTE;
	}
	
	/**
	 * Lit un registre de l'AX12
	 * @param register
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	protected int read(AX12_Register register) throws AX12LinkException, AX12Exception {
		byte[] status = this.sendRequest(AX12_Instr.AX12_INSTR_READ_DATA, register.regi, intToUnsignedByte(register.size));
		if (status.length == 0) {
			throw new AX12Exception(AX12_Error.AX12_ERR_NO_RESPONSE);
		}
		
		byte[] payload = extractPayLoad(status);
		if (payload.length != register.size) {
			throw new AX12Exception("La taille de la payload n'est pas celle attendue", AX12_Error.AX12_ERR_INVALID_RESPONSE);
		}
		
		int value = 0;
		
		for (int i=0; i<payload.length; i++) {
			value += unsignedByteToInt(payload[i]) << i*8;
		}
		
		return value;
	}
	
	/**
	 * Ecrit dans un registre de l'AX12
	 * @param register
	 * @param value
	 * @throws AX12LinkException
	 * @throws AX12Exception
	 */
	protected void write(AX12_Register register, int value) throws AX12LinkException, AX12Exception {
		if (!register.writable) {
			throw new AX12LinkException("Le registre "+register.toString()+" n'est pas inscriptible");
		}
		if (value < 0 || value > 65535) {
			throw new AX12LinkException("La valeur doit �tre comprise entre 0 et 65535 : "+value);
		}
		
		byte[] params = new byte[register.size+1];
		params[0] = register.regi;
		for (int i=0; i<register.size; i++) {
			params[i+1] = intToUnsignedByte((value >> i*8) & 0xFF);
		}
		
		byte[] status = this.sendRequest(AX12_Instr.AX12_INSTR_WRITE_DATA, params);
		if (status.length == 0 && !this.isBroadcasting()) {
			throw new AX12Exception(AX12_Error.AX12_ERR_NO_RESPONSE);
		}
	}
	
	/**
	 * Envoie une instruction, un registre et �vetentuellement des param�tres � l'AX12
	 * Si une r�ponse est renvoy�e, son int�grit� est v�rifi�e ainsi que es �ventuelles erreurs remont�es
	 * @param instruction
	 * @param params
	 * @return
	 * @throws AX12LinkException
	 * @throws AX12Exception si une erreur est remont�e par l'AX12 ou que sa r�ponse est erron�e
	 */
	protected byte[] sendRequest(AX12_Instr instruction, byte... params) throws AX12LinkException, AX12Exception {
		if (instruction.minParamCount != -1 && params.length < instruction.minParamCount) {
			throw new RuntimeException(instruction.toString()+" attend au moins "+instruction.minParamCount+" param�tre(s). "+params.length+" re�u(s)");
		}
		if (instruction.maxParamCount != -1 && params.length > instruction.maxParamCount) {
			throw new RuntimeException(instruction.toString()+" attend au plus "+instruction.maxParamCount+" param�tre(s). "+params.length+" re�u(s)");
		}
		
		// Instruction packet : FF FF <ID> <LEN> <INSTR> <PAR0>..<PARN> <CKSUM>
		// <CKSUM> = ( ~(<ID> + <LEN> + <INSTR> + <PAR0> + .. + <PARN>)~) % 0xFF
		byte buffer[] = new byte[params.length + 6];
		int i;
		int pos = 0;
		int checksum = 0;
		buffer[pos++] = AX12.intToUnsignedByte(0xFF);
		buffer[pos++] = AX12.intToUnsignedByte(0xFF);
		checksum += (buffer[pos++] = this.addr);
		checksum += (buffer[pos++] = AX12.intToUnsignedByte(params.length+2));
		checksum += (buffer[pos++] = instruction.instr);
		 for(i=0; i<params.length; i++){
		  buffer[pos++] = params[i];
		  checksum += params[i];
		}
		checksum = (~checksum) & 0xFF;
		buffer[pos++] = AX12.intToUnsignedByte(checksum);
		
		byte[] response = alink.sendCommand(buffer, this.baudRate);
		int address = AX12.unsignedByteToInt(this.addr);
		if (response.length > 0) {
			String validation = validatePacket(response, address); 
			if (validation != null) {
				throw new AX12Exception(validation, AX12_Error.AX12_ERR_INVALID_RESPONSE);
			}
			AX12_Error[] errors = extractErrors(response[4]);
			if (errors.length > 0) {
				throw new AX12Exception("Erreur de l'AX12", errors);
			}
		}
		
		return response;
	}
	
	/**
	 * V�rifie la validit� du packet brut re�u d'un AX12
	 * @param packet
	 * @param ax12Addr l'adresse de l'AX12 solicit�
	 * @return
	 */
	protected static String validatePacket(byte[] packet, int ax12Addr) {
		// Taille minimum
		if (packet.length < 6) {
			return "La taille minimale du packet n'est pas valide ("+packet.length+")";
		}
		
		// Doit commencer par 0xFF deux fois
		if (packet[0] != packet[1] || packet[0] != AX12.intToUnsignedByte(0xFF)) {
			return "Le header du paquet n'est pas valide";
		}
		
		// Doit contenir l'id de l'AX12
		if (AX12.unsignedByteToInt(packet[2]) != ax12Addr) {
			return "Le paquet ne contient pas le bon id de l'ax12";
		}
		
		// La longueur doit �tre > 2 et < taille du packet - 2
		int l = AX12.unsignedByteToInt(packet[3]);
		if (l < 2 || l > packet.length - 2) {
			return "La taille de la charge utile mentionn�e par le paquet ne correspond pas � la taille reelle";
		}
		
		// V�rification du checksum
		l -= 3;
		int cc = packet[2]; // ID
		cc += packet[3];    // Length
		cc += packet[4];    // Error
		while (l >= 0) {
			cc += packet[5 + l--];
		}
		
		if ((~cc & 0xFF) != AX12.unsignedByteToInt(packet[packet.length - 1])) {
			return "Le checksum n'est pas valide";
		}
		
		return null;
	}
	
	/**
	 * Extrait les �ventuelles erreurs contenues dans le registre ad-hoc de l'AX12
	 * @param registerValue
	 * @return
	 */
	protected static AX12_Error[] extractErrors(byte registerValue) {
		int qte = 0;
		for(int i=0; i<7; i++) {
			if ((registerValue >> i & 0x01) == 0x01) {
				qte++;
			}
		}
		
		AX12_Error[] errors = new AX12_Error[qte];
		
		qte = 0;
		for(int i=0; i<7; i++) {
			if ((registerValue >> i & 0x01) == 0x01) {
				errors[qte++] = AX12_Error.values()[i];
			}
		}
		
		return errors;
	}
	
	/**
	 * Extrait les parametres d'une r�ponse suppos�e valide
	 * @param registerValue
	 * @return
	 */
	protected static byte[] extractPayLoad(byte[] packet) {
		if (packet.length == 0) {
			return new byte[0];
		}
		
		int taille = unsignedByteToInt(packet[3]) - 2;
		byte[] params = new byte[taille];
		for(int i=0; i<taille; i++) {
			params[i] = packet[5+i];
		}
		return params;
	}
	
	/**
	 * 
	 * @param val
	 * @return
	 * @throws IllegalArgumentException Si la valeur donn�e n'est pas comprise entre 0 et 255
	 */
	public static byte intToUnsignedByte(int val) throws IllegalArgumentException {
		if (val < 0 || val > 255) {
			throw new IllegalArgumentException("La valeur doit �tre comprise entre 0 et 255 : "+val);
		}
		
		if(val == 0) {
			return 0;
		}
		BitSet bs = new BitSet(8);
		int vals[] = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
		
		for(int i=vals.length-1; i>=0; i--) {
			if (val >= vals[i]) {
				val -= vals[i];
				bs.set(i, true);
			} else {
				bs.set(i, false);
			}
		}
		
		return bs.toByteArray()[0];
	}
	
	/**
	 * 
	 * @param val
	 * @return
	 * @throws IllegalArgumentException Si la valeur donn�e n'est pas comprise entre 0 et 255
	 */
	public static int unsignedByteToInt(byte b) throws IllegalArgumentException {
		BitSet bs = BitSet.valueOf(new byte[]{b});
		int vals[] = new int[]{1, 2, 4, 8, 16, 32, 64, 128};
		int res = 0;
		
		for(int i=vals.length-1; i>=0; i--) {
			if (bs.get(i)) {
				res += vals[i];
				bs.set(i, true);
			}
		}
		
		return res;
	}
	
	/**
	 * Repr�sente un byte sous forme de 0 et de 1, poids fort � gauche
	 * @param b
	 * @return
	 */
	public static String byteToString(byte b) {
		StringBuffer sb = new StringBuffer();
		for (int i=7; i>=0; i--) {
			sb.append((b >> i & 0x01) == 0x01 ? '1' : '0');
		}
		return sb.toString();
	}
	
	/**
	 * L�ve une exception si l'adresse de l'AX12 n'est pas valide
	 * @param address
	 * @throws IllegalArgumentException
	 */
	public static void checkAddressRange(int address) throws IllegalArgumentException {
		if (address == AX12_ADDRESS_BROADCAST) {
			return;
		}
		if (address < AX12_ADDRESS_MIN || address > AX12_ADDRESS_MAX) {
			throw new IllegalArgumentException("L'adresse de l'AX12 doit �tre contenue dans la plage ["+AX12_ADDRESS_MIN+" ~ "+AX12_ADDRESS_MAX+"] Ou correspondre � l'adresse de BroadCast "+AX12_ADDRESS_BROADCAST+". Obtenu : "+address);
		}
	}
}
