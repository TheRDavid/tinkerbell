package de.sddb.tinker.test;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.BrickletSoundIntensity;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;

import de.sddb.tinker.util.Debug;
import de.sddb.tinker.util.IOTools;

public class IndustrialOut4 implements Runnable {
	private static final String HOST = "localhost";
	private static final int PORT = 4223;

	// Change XYZ to the UID of your Remote Switch Bricklet
	private static final String UID = "u1L";
	private static final String SOUNDUID = "voB";

	private IPConnection ipcon;
	private BrickletIndustrialQuadRelay ido4;
	private BrickletSoundIntensity soundBrick;

	private static final int level1 = 120, level2 = 280, level3 = 550;

	private static final int recentLevels[] = new int[200];
	private static int currentIndex = 0;
	
	public static void main(String[] args) throws Exception {

		IndustrialOut4 br = new IndustrialOut4();

		// flash(80);
		Thread th = new Thread(br);
		th.start();
	}

	public IndustrialOut4() throws UnknownHostException, AlreadyConnectedException, IOException, NetworkException {
		Debug.setDebug(true);
		ipcon = new IPConnection(); // Create IP connection
		ipcon.connect(HOST, PORT); // Connect to brickd
		ido4 = new BrickletIndustrialQuadRelay(UID, ipcon); // Create
		soundBrick = new BrickletSoundIntensity(SOUNDUID, ipcon);
	}

	public void flash(int w) throws Exception {
		// device
		for (int i = 0; i < 1; i++) {
			ido4.setValue(15);
			Thread.sleep(w);
			ido4.setValue(0);
			Thread.sleep(w);
		}
		// System.out.println(new Date());
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(20);
				System.out.println();
				int avg = 0;
				for (int i : recentLevels) {
					System.out.print(i + " ");
					avg += i;
				}
				System.out.println();
				avg /= recentLevels.length;
				System.out.println(avg);
				recentLevels[currentIndex] = soundBrick.getIntensity();
				currentIndex = currentIndex < recentLevels.length - 1 ? currentIndex + 1 : 0;
				ido4.setValue(0);
				if (avg > level3) {
					ido4.setValue(0b111);
				} else if (avg > level2) {
					ido4.setValue(0b110);
				} else if (avg > level1) {
					ido4.setValue(0b010);
				}
				/*
				 * if (buttonPressed()) { flash(1000); } else { flash(300); }
				 */
			} catch (Exception e) {
				Debug.err(e);
			}
			/*
			 * try { Thread.sleep(3000); } catch (InterruptedException e) {
			 * e.printStackTrace(); }
			 */
		}
	}

	private boolean buttonPressed() {
		boolean pressed = false;
		try {
			URL url = new URL("http://sddb.de/switch/ButtonPressed");
			String ret = IOTools.readTextResource(url);
			if (ret != null) {
				System.out.println(ret);
				StringTokenizer st = new StringTokenizer(ret);
				if (st.hasMoreTokens()) {
					pressed = "Y".equalsIgnoreCase(st.nextToken());
				}
			}
		} catch (Exception e) {
			Debug.err(e.getMessage());
		}
		return pressed;
	}
}
