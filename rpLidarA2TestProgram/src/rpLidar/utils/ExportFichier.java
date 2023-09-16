package rpLidar.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportFichier {
	
	protected List<ExportValue> liste;
	protected boolean continueRunning;
	protected Thread runningThread;
	protected File outputDir;
	
	public static final SimpleDateFormat dateFormatLog = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
	public static final SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyy-dd-MM_HH-mm-ss");

	public ExportFichier(File outputDir) {
		liste = new ArrayList<>();
		this.outputDir = outputDir;
	}
	
	public void exportAsync(ExportValue e) {
		synchronized (liste) {
			liste.add(e);
		}
	}
	
	public void startExport() throws Exception {
		if (runningThread != null) {
			return;
		}

		runningThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ExportFichier.this.run();	
				} catch (Exception e) {
					runningThread = null;
					continueRunning = false;
				}
			}
		});
		continueRunning = true;
		runningThread.start();
	}
	
	public void stopExport() {
		this.continueRunning = false;
	}
	
	public boolean exportIsRunngin() {
		return this.continueRunning;
	}
	
	public void run() throws Exception {
		long startTime = System.currentTimeMillis();
		Date d = new Date(startTime);
		File f = new File(outputDir.getAbsoluteFile()+File.separator+"export_"+dateFormatFile.format(d)+".log");
		f.createNewFile();
		System.out.println(f.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		
		long exportCount = 0;
		while (continueRunning) {
			// Export périodique
			if (System.currentTimeMillis() > (startTime + exportCount * 1000)) {
				bw.write("#"+dateFormatLog.format(new Date())+"\n");
				System.out.println("file export #"+exportCount);
				exportCount++;
			}
			
			ExportValue ev = null;
			synchronized (liste) {
				if (liste.size() > 0) {
					ev = liste.remove(0);
				}
			}
			
			if (ev != null) {
				bw.write(ev.x+";"+ev.y+";"+ev.quality+";"+ev.distance+";"+ev.angle+"\n");
			}
		}
		bw.flush();
		bw.close();
	}
}
