package de.sddb.tinker.garage2;

import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.BrickletDualButton.StateChangedListener;

import de.sddb.tinker.util.Debug;

import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class DoppelSchalter implements StateChangedListener {

	private static final String UID = "vUY";
	private BrickletDualButton db;
	private UpAnDahlDreher up;
	private short left = BrickletDualButton.LED_STATE_OFF;
	private short right = BrickletDualButton.LED_STATE_OFF;

	public DoppelSchalter(UpAnDahlDreher up, IPConnection ipcon) throws TimeoutException, NotConnectedException {
		this.db = new BrickletDualButton(UID, ipcon); // Create device object
		this.up = up;
		db.addStateChangedListener(this);
		db.setLEDState(BrickletDualButton.LED_STATE_AUTO_TOGGLE_OFF, BrickletDualButton.LED_STATE_AUTO_TOGGLE_OFF);
	}

	@Override
	public void stateChanged(short buttonL, short buttonR, short ledL, short ledR) {
		if (buttonL == BrickletDualButton.BUTTON_STATE_PRESSED) {
			System.out.println("Left button pressed");
			Auftrag a = new Auftrag(Auftrag.LEFT);
			up.add(a);
		}

		if (buttonR == BrickletDualButton.BUTTON_STATE_PRESSED) {
			System.out.println("Right button pressed");
			Auftrag a = new Auftrag(Auftrag.RIGHT);
			up.add(a);
		}

	}

	public void toggleLeft() {
		Debug.out(left);

		if (left == BrickletDualButton.LED_STATE_OFF) {
			left = BrickletDualButton.LED_STATE_ON;
		} else {
			left = BrickletDualButton.LED_STATE_OFF;
		}
		try {
			db.setLEDState(left, right);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}

	}

	public void blink() {
		try {
			for (int i = 0; i < 6; i++) {
				if (left == BrickletDualButton.LED_STATE_OFF) {
					left = BrickletDualButton.LED_STATE_ON;
				} else {
					left = BrickletDualButton.LED_STATE_OFF;
				}
				db.setLEDState(left, right);
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}

	}

	public void toggleRight() {
		Debug.out(right);

		if (right == BrickletDualButton.LED_STATE_OFF) {
			right = BrickletDualButton.LED_STATE_ON;
		} else {
			right = BrickletDualButton.LED_STATE_OFF;
		}
		try {
			db.setLEDState(left, right);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}

	}
}
