package de.sddb.tinker.test;

import com.tinkerforge.BrickletGPSV2;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;

import de.sddb.tinker.util.Debug;

public class GPSTest {
	private static final String HOST = "localhost";
	private static final int PORT = 4223;
	private static final String UID = "DPB";

	public static void main(String args[]) throws Exception {
		Debug.setDebug(false);
		IPConnection ipcon = new IPConnection(); // Create IP connection
		BrickletGPSV2 gps = new BrickletGPSV2(UID, ipcon); // Create device object
		ipcon.connect(HOST, PORT); // Connect to brickd
		// Add coordinates listener
		gps.addCoordinatesListener(new BrickletGPSV2.CoordinatesListener() {
			public void coordinates(long latitude, char ns, long longitude, char ew) {
				System.out.println("Latitude: " + latitude / 1000000.0 + " °");
				System.out.println("N/S: " + ns);
				System.out.println("Longitude: " + longitude / 1000000.0 + " °");
				System.out.println("E/W: " + ew);
				System.out.println("");
			}
		});

		// Set period for coordinates callback to 1s (1000ms)
		// Note: The coordinates callback is only called every second
		// if the coordinates has changed since the last call!
		gps.setCoordinatesCallbackPeriod(1000);

		System.out.println("Press key to exit");
		System.in.read();
		ipcon.disconnect();
	}
}
