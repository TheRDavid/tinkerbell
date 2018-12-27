package de.sddb.tinker.milan;

import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.sddb.tinker.milan.MilanController.LEDStatus;
import de.sddb.tinker.util.Debug;

public class MilanLights implements Runnable {

	private static final short LED_OFF = 1;
	private static final short LED_ON = 0;
	private BrickletIndustrialQuadRelay relayTail;
	private MilanController milanController;
	private BrickletDualButton dualButton;

	public MilanLights(MilanController milanController,
			BrickletIndustrialQuadRelay quadRelay[],
			BrickletDualButton dualButton) {
		this.relayTail = quadRelay[0];
		this.milanController = milanController;
		this.dualButton = dualButton;
	}

	@Override
	public void run() {
		while (true) {
//			synchronized (milanController) {
				try {
					dualButton.setLEDState(LED_OFF, LED_OFF);
					
					if (milanController.getStatus() == LEDStatus.WARN) {
						warn();
					} else if (milanController.isTurnLeft()) {
						turnLeft();
					} else if (milanController.isTurnRight()) {
						turnRight();
					} else {
						switch (milanController.getStatus()) {
						case FLASH:
							flash();
							break;
						case ON:
							on();
							break;
						case OFF:
							off();
							break;
						default:
							break;
						}
					}
				} catch (InterruptedException | TimeoutException
						| NotConnectedException e) {
					milanController.setError(e);
				}
			}
		}
//	}
	private int buttonBlink=0;
	private void flash() throws InterruptedException, TimeoutException,
			NotConnectedException {
		Debug.out("flash");
		buttonBlink++;
		if (buttonBlink==3){
			dualButton.setLEDState(LED_ON, LED_ON);	
			buttonBlink=0;	
		}
		relayTail.setSelectedValues(0b1111, 0b1010);
		Thread.sleep(150);
		relayTail.setSelectedValues(0b1111, 0b0000);
		Thread.sleep(150);
		relayTail.setSelectedValues(0b1111, 0b1010);
		Thread.sleep(150);
		relayTail.setSelectedValues(0b1111, 0b0000);
		
		Thread.sleep(500);

	}

	private void on() throws InterruptedException, TimeoutException,
			NotConnectedException {
//		Debug.out("on");
		relayTail.setSelectedValues(0b1111, 0b1010);
		dualButton.setLEDState(LED_ON, LED_ON);
		Thread.sleep(800);
	}

	private void off() throws InterruptedException, TimeoutException,
			NotConnectedException {
//		Debug.out("off");
		relayTail.setSelectedValues(0b1111, 0b0000);
		dualButton.setLEDState(LED_OFF, LED_OFF);
		
		Thread.sleep(800);
	}

	private void warn() throws TimeoutException, NotConnectedException,
			InterruptedException {
		Debug.out("warn");

		int delay = 400;
		if (milanController.getSound()) {
			// Hupen!
			delay = 50;
		}
		relayTail.setSelectedValues(0b1111, 0b1111);
		dualButton.setLEDState(LED_ON, LED_ON);
		
		Thread.sleep(delay);
		relayTail.setSelectedValues(0b1111, 0b0000);
		dualButton.setLEDState(LED_OFF, LED_OFF);
		Thread.sleep(delay);
	}

	private void turnLeft() throws TimeoutException, NotConnectedException,
			InterruptedException {
//		Debug.out("left");

		relayTail.setSelectedValues(0b1111, 0b1100);
		dualButton.setLEDState(LED_ON, LED_OFF);
		Thread.sleep(400);
		relayTail.setSelectedValues(0b1111, 0b0000);
		dualButton.setLEDState(LED_OFF, LED_OFF);
		Thread.sleep(400);
	}

	private void turnRight() throws TimeoutException, NotConnectedException,
			InterruptedException {
//		Debug.out("right");
		relayTail.setSelectedValues(0b1111, 0b0011);
		dualButton.setLEDState(LED_OFF, LED_ON);
		Thread.sleep(400);
		relayTail.setSelectedValues(0b1111, 0b0000);
		dualButton.setLEDState(LED_OFF, LED_OFF);
		Thread.sleep(400);
	}

}
