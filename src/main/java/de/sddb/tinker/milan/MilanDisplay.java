package de.sddb.tinker.milan;

import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class MilanDisplay implements Runnable {
	private static final byte[] DIGITS = { 0x3f, 0x06, 0x5b, 0x4f, 0x66, 0x6d,
			0x7d, 0x07, 0x7f, 0x6f, 0x77, 0x7c, 0x39, 0x5e, 0x79, 0x71, 0x32,
			0b01010000,0b1100011 }; // 0~9,A,b,C,d,E,F,r,°

	private MilanController controller;
	private BrickletSegmentDisplay4x7 display;
	private long start = System.currentTimeMillis();

	public MilanDisplay(MilanController milanController,
			BrickletSegmentDisplay4x7 display) {
		this.controller = milanController;
		this.display = display;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String msg = controller.getMessage();
				short a = (short) (DIGITS.length - 1);
				short b = (short) (DIGITS.length - 1);
				short c = (short) (DIGITS.length - 1);
				short d = (short) (DIGITS.length - 1);

				if (msg != null) {

					if (controller.getError() != null) {
						if (controller.getError() instanceof TimeoutException) {
							a = 1;
						} else if (controller.getError() instanceof NotConnectedException) {
							a = 2;
						} else if (controller.getError() instanceof InterruptedException) {
							a = 3;
						}
						b = DIGITS[14];
						c = DIGITS[16];
						b = DIGITS[16];
						setSegments(new short[] { DIGITS[a], DIGITS[b],
								DIGITS[c], DIGITS[d] }, (short) 7, false);
						Thread.sleep(10 * 1000);
					}
				}
				int temperature = controller.getTemperature();
				showTemperature(temperature);
				Thread.sleep(3000);

				showTime();
				Thread.sleep(3000);
			} catch (Exception e) {
			}
		}
	}

	private void showTemperature(int temperature) {
		int a = Math.round(temperature / 1000);
		int b = Math.round((temperature - a * 1000) / 100);
		int c = Math.round((temperature - a * 1000 - b * 100) / 10);
		int d = DIGITS.length - 1;
		short[] segments = { DIGITS[a], DIGITS[b], DIGITS[c], DIGITS[d] };
		setSegments(segments, (short) 1, true);
	}

	private void setSegments(short[] segments, short s, boolean b) {
		try {
			for ( int i=0;i<3;i++){
				if (segments[i]==DIGITS[0]){
					segments[i]=0;
				}else{
					break;
				}
			}
			display.setSegments(segments, (short) 1, b);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}

	private void showTime() {
		long runTime = System.currentTimeMillis() - start;
		int hour = (int) (runTime / 1000 / 60 / 60);
		int a = Math.round(hour / 10);
		int b = Math.round(hour % 10);
		int min = (int) ((runTime - hour * 1000 * 60 * 60) / 1000 / 60);
		int c = Math.round(min / 10);
		int d = Math.round(min % 10);
		short[] segments = { DIGITS[a], DIGITS[b], DIGITS[c], DIGITS[d] };
		setSegments(segments, (short) 1, true);
	}

}
