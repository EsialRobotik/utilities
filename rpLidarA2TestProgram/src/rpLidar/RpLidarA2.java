package rpLidar;

import rpLidar.command.LidarCommand;
import rpLidar.command.LidarCommandException;
import rpLidar.command.LidarCommandExpressScan;
import rpLidar.command.LidarCommandGetHealth;
import rpLidar.command.LidarCommandGetInfo;
import rpLidar.command.LidarCommandGetSampleRate;
import rpLidar.command.LidarCommandReset;
import rpLidar.command.LidarCommandScan;
import rpLidar.command.LidarCommandStop;
import rpLidar.command.exception.LidarCommandAlreadyRunningException;
import rpLidar.command.response.LidarResponseHealth;
import rpLidar.command.response.LidarResponseInfo;
import rpLidar.command.response.LidarResponseSampleRate;
import rpLidar.link.RpLidarLink;

public class RpLidarA2 implements RpLidarInterface {
	
	protected LidarCommand runningCommand;
	protected Boolean commandIsRunning;
	protected RpLidarLink link;
	
	public RpLidarA2(RpLidarLink link) {
		this.commandIsRunning = false;
		this.link = link;
		link.enableRotation(false);
	}
	
	@Override
	public boolean scanIsRunning() {
		synchronized (this) {
			return this.commandIsRunning && (this.runningCommand instanceof LidarCommandScan || this.runningCommand instanceof LidarCommandExpressScan);
		}
	}

	@Override
	public LidarResponseHealth getHealth() throws LidarCommandException {
		LidarCommandGetHealth command = new LidarCommandGetHealth(link); 
		this.launchCommand(command, false, true);
		return command.getResult();
	}
	
	@Override
	public LidarResponseSampleRate getSampleRate() throws LidarCommandException {
		LidarCommandGetSampleRate command = new LidarCommandGetSampleRate(link);
		this.launchCommand(command, false, true);
		return command.getResult();
	}
	
	@Override
	public LidarResponseInfo getInfos() throws LidarException {
		LidarCommandGetInfo command = new LidarCommandGetInfo(link);
		this.launchCommand(command, false, true);
		return command.getResult();
	}

	@Override
	public void startScan(RpLidarScanHandlerInterface scanHandler) throws LidarException {
		this.launchCommand(new LidarCommandScan(link, scanHandler), false, false);
	}
	
	@Override
	public void startScanExpress(RpLidarScanHandlerInterface scanHandler) throws LidarException {
		this.launchCommand(new LidarCommandExpressScan(link, scanHandler), false, false);
	}
	
	public void waitScanToTerminate() {
		LidarCommand command = null;
		synchronized (this) {
			if (this.commandIsRunning && this.runningCommand != null && (this.runningCommand instanceof LidarCommandExpressScan || this.runningCommand instanceof LidarCommandScan)) {
				command = this.runningCommand;
			}
		}
		if (command != null) {
			try {
				command.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stopScan() throws LidarException {
		synchronized (this) {
			if (this.commandIsRunning && (this.runningCommand instanceof LidarCommandScan)) {
				this.runningCommand.terminate();
			} else {
				LidarCommandStop command = new LidarCommandStop(this.link);
				this.launchCommand(command, true, true);
			}
		}
	}

	@Override
	public void reset() throws LidarException {
		LidarCommandReset command = new LidarCommandReset(this.link);
		this.launchCommand(command, true, true);
	}
	
	protected void waitCommandToFinish(LidarCommand command) {
		try {
			command.join();
			this.commandIsRunning = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Lance une nouvelle commande
	 * 
	 * @param command la commande � lancer
	 * @param forceLaunch force le stop
	 * @param waitTermination si true, bloque jusqu'� ce que la command se termine
	 * @throws LidarCommandAlreadyRunningException
	 */
	protected void launchCommand(LidarCommand command, boolean forceLaunch, boolean waitTermination) throws LidarCommandAlreadyRunningException {
		// On s'assure de ne faire qu'un seule lancement de commande � la fois
		synchronized(this.commandIsRunning) {
			// Si le flag de commande en train de tourner est � true 
			if (this.commandIsRunning) {
				
				// Si une commande est encore en train de tourner
				if (this.runningCommand != null && this.runningCommand.getCommandState() != LidarCommand.COMMAND_STATE.FINISHED) {
					// Si on force le lancement de la nouvelle commande => arr�t de celle en cours
					if (forceLaunch) {
						this.runningCommand.terminate();
						try {
							this.runningCommand.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						throw new LidarCommandAlreadyRunningException("Une commande est d�j� en cours d'ex�cution : "+runningCommand.getName());
					}
				} else {
					
				}
			}
			
			this.commandIsRunning = true;
			this.runningCommand = command;
			
			if (waitTermination) {	
				command.start();
				this.waitCommandToFinish(command);
			} else {
				new TerminationThread(command).start();;	
			}
		}
	}
	
	private class TerminationThread extends Thread {
		
		protected LidarCommand c;
		
		public TerminationThread(LidarCommand c) {
			this.c = c;
		}
		
		@Override
		public void run() {
			try {
				c.start();
				c.join();
				synchronized (RpLidarA2.this) {
					RpLidarA2.this.commandIsRunning = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}
