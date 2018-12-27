package de.sddb.tinker.haus;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;

import de.sddb.tinker.test.IndustrialOut4;
import de.sddb.tinker.util.Debug;
import de.sddb.tinker.util.IOTools;

public class Main implements Runnable {
		private static final String HOST = "localhost";
		private static final int PORT = 4223;

		// Change XYZ to the UID of your Remote Switch Bricklet
		private static final String UID = "u1L";

		private IPConnection ipcon;
		private BrickletIndustrialQuadRelay ido4;

		public static void main(String[] args) throws Exception {

			IndustrialOut4 br = new IndustrialOut4();

			// flash(80);
			Thread th = new Thread(br);
			th.start();
		}

		public Main() throws UnknownHostException, AlreadyConnectedException, IOException, NetworkException {
			Debug.setDebug(true);
			ipcon = new IPConnection(); // Create IP connection
			ipcon.connect(HOST, PORT); // Connect to brickd
			ido4 = new BrickletIndustrialQuadRelay(UID, ipcon); // Create
		}

		public void flash(int w) throws Exception {
			// device
				for (int i = 0; i < 1; i++) {
					ido4.setValue(15);
					Thread.sleep(w);
					ido4.setValue(0);
					Thread.sleep(w);
				}
//			System.out.println(new Date());
		}

		@Override
		public void run() {
			while (true) {
				try {
					if (buttonPressed()) {
						flash(1000);
					} else {
						//flash(300);
					}
				} catch (Exception e) {
					Debug.err(e);
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
