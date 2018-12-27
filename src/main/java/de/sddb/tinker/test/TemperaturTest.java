package de.sddb.tinker.test;

import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.sddb.tinker.util.Debug;

public class TemperaturTest {
	private static final byte[] DIGITS = { 0x3f, 0x06, 0x5b, 0x4f, 0x66, 0x6d, 0x7d, 0x07, 0x7f, 0x6f, 0x77, 0x7c, 0x39,
			0x5e, 0x79, 0x71, 0x32, 0b01010000, 0b1100011 }; // 0~9,A,b,C,d,E,F,r,°

	private static final String HOST = "localhost";
	private static final int PORT = 4223;

	// Change XYZ to the UID of your Temperature Bricklet
	private static final String UID = "tgh";

	// Note: To make the example code cleaner we do not handle exceptions.
	// Exceptions
	// you might normally want to catch are described in the documentation
	public static void main(String args[]) throws Exception {
		Debug.setDebug(false);
		IPConnection ipcon = new IPConnection(); // Create IP connection
		BrickletTemperature t = new BrickletTemperature(UID, ipcon); // Create device object
		final BrickletIndustrialQuadRelay relay = new BrickletIndustrialQuadRelay("u1L", ipcon);
		final BrickletSegmentDisplay4x7 sd = new BrickletSegmentDisplay4x7("wPP", ipcon);

		ipcon.connect(HOST, PORT); // Connect to brickd
		// for ( short i=1;i<5;i++){
		// Debug.out(i);
		// byte bb=0b1111111;
		// setSegments(sd,new short[] { bb,0,0,0 }, (short) 1, false);
		// Thread.sleep(1000);
		// }

		setSegments(sd, new short[] { 0, 0, 0, 0 }, (short) 1, true);
		t.addTemperatureListener(new BrickletTemperature.TemperatureListener() {
			public void temperature(short temperature) {
				int a = Math.round(temperature / 1000);
				int b = Math.round((temperature - a * 1000) / 100);
				int c = Math.round((temperature - a * 1000 - b * 100) / 10);
				int d = DIGITS.length - 1;

				System.out.println("Temp: " + temperature / 100.0 + " °C");
				setSegments(sd, new short[] { DIGITS[a], DIGITS[b], (short) (DIGITS[c]), DIGITS[d] }, (short) 1, true);
				try {
					setLED(relay, temperature/100.0, 1);
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		// Set period for temperature callback to 1s (1000ms)
		// Note: The temperature callback is only called every second
		// if the temperature has changed since the last call!
		t.setTemperatureCallbackPeriod(1000);

		System.out.println("Press key to exit");
		System.in.read();
		ipcon.disconnect();
	}

	private static void setLED(BrickletIndustrialQuadRelay relay, double temperature, double step)
			throws TimeoutException, NotConnectedException, InterruptedException {
		Debug.out(temperature);
		double median = 21;
		double[] steps = new double[5];
		for (int i = 0; i < steps.length; i++) {
			steps[i] = median - 2.0 * step + i * step;
			Debug.out(i + ":" + steps[i]);
		}
		final short WHITE = 0b1111;
		final short BLUE = 0b1000;
		final short GREEN = 0b0010;
		final short YELLOW = 0b1001;
		final short RED = 0b0001;
		final short PINK = 0b1010;

//		int d=1500;
//		relay.setSelectedValues(0b1111, WHITE);
//		Thread.sleep(d);
//		relay.setSelectedValues(0b1111, BLUE);
//		Thread.sleep(d);
//		relay.setSelectedValues(0b1111, GREEN);
//		Thread.sleep(d);
//		relay.setSelectedValues(0b1111, YELLOW);
//		Thread.sleep(d);
//		relay.setSelectedValues(0b1111, RED);
//		Thread.sleep(d);
//		relay.setSelectedValues(0b1111, PINK);
//		Thread.sleep(5*d);
//	
		short values = WHITE;
		if (temperature < steps[1]) {
			values = PINK;
			Debug.out("pink");
		} else if (temperature < steps[2]) {
			values = BLUE;
			Debug.out("blue");
		} else if (temperature < steps[3]) {
			values = GREEN;
			Debug.out("green");
		} else if (temperature < steps[4]) {
			values = YELLOW;
			Debug.out("yellow");
		} else {
			values = RED;
			Debug.out("red");
		}
	
		relay.setSelectedValues(0b1111, values);
	}

	private static void setSegments(BrickletSegmentDisplay4x7 display, short[] segments, short s, boolean b) {
		try {
			for (int i = 0; i < 3; i++) {
				if (segments[i] == DIGITS[0]) {
					segments[i] = 0;
				} else {
					break;
				}
			}
			display.setSegments(segments, (short) 1, b);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}

}