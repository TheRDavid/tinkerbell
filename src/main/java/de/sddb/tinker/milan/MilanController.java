package de.sddb.tinker.milan;

import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.BrickletDualButton.StateChangedListener;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.BrickletNFCRFID;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletTemperature.TemperatureListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.sddb.tinker.util.Debug;

public class MilanController implements StateChangedListener,
		TemperatureListener{
	public enum LEDStatus {
		OFF, ON, FLASH, WARN
	}

	private static final long DOUBLE_PRESS = 1000;
	// Constants to indikate Display
	private boolean turnLeft = false;
	private boolean turnRight = false;
	private LEDStatus status = LEDStatus.FLASH;
	private long leftButtonPressed = 0; // last time the button was
										// pressed
	private long rightButtonPressed = 0; // last time the button
											// was pressed

	private BrickletDualButton dualButton;
	private BrickletIndustrialQuadRelay quadRelay[];
	private BrickletSegmentDisplay4x7 display;
	private BrickletTemperature temperatureSensor;
	
	private Exception error = new InterruptedException();
	private int temperature = 0;// falls keine gelesen wurde, wird nur die
								// Zeit auf dem Display angezeigt
	private String message;
	private boolean sound;
	private Alarm alarm;
	public MilanController(){
		alarm=new Alarm(this);
	}
	// MilanController(BrickletDualButton dualButton,
	// BrickletIndustrialQuadRelay[] brickletIndustrialQuadRelays,
	// BrickletSegmentDisplay4x7 display, BrickletTemperature temperatureSensor,
	// BrickletDistanceIR distance,
	// BrickletNFCRFID nfcrfid) {
	// this.dualButton = dualButton;
	// this.quadRelay = brickletIndustrialQuadRelays;
	// this.display = display;
	// this.temperatureSensor = temperatureSensor;
	// this.distanceSensor = distance;
	// this.nfcrfid = nfcrfid;
	// }

	public void setDualButton(BrickletDualButton dualButton) {
		this.dualButton = dualButton;
	}

	public void setQuadRelay(BrickletIndustrialQuadRelay[] quadRelay) {
		this.quadRelay = quadRelay;
	}

	public void setTemperatureSensor(BrickletTemperature temperatureSensor) {
		this.temperatureSensor = temperatureSensor;
	}

	public void setNfcrfid(BrickletNFCRFID nfcrfid) {
		alarm.setNfcrfid( nfcrfid);
	}

	public void start() {
		
		if (dualButton != null) {
			dualButton.addStateChangedListener(this);
		}
		if (quadRelay != null) {
			Thread th = new Thread(new MilanLights(this, quadRelay, dualButton));
			th.start();
		}
		if (display != null) {
			Thread thDisplay = new Thread(new MilanDisplay(this, display));
			thDisplay.start();
		}
		if (temperatureSensor != null) {
			try {
				temperature = temperatureSensor.getTemperature();
				temperatureSensor.addTemperatureListener(this);
				temperatureSensor.setTemperatureCallbackPeriod(1000);

			} catch (TimeoutException | NotConnectedException e) {
				e.printStackTrace();
				error = e;
			}
		}
		
	}

	@Override
	public void stateChanged(short buttonL, short buttonR, short ledL,
			short ledR) {
		Debug.out("ButtonL:" + buttonL + " buttonR:" + buttonR);
		long now = System.currentTimeMillis();
		// if (leftButtonPressed==Long.MAX_VALUE){//first time
		// leftButtonPressed=now;
		// leftButtonPressed=now;
		// }
		// synchronized (this) {
		if (buttonL == BrickletDualButton.BUTTON_STATE_PRESSED
				&& buttonR == BrickletDualButton.BUTTON_STATE_PRESSED) {
			try {
				// dann schaltet distance relay scharf
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setStatus(LEDStatus.WARN);
		} else if (buttonL == BrickletDualButton.BUTTON_STATE_PRESSED) {
			if (now - leftButtonPressed < DOUBLE_PRESS) {
				long test = leftButtonPressed - now;
				Debug.out(test);
				if (getStatus() == LEDStatus.OFF) {
					setStatus(LEDStatus.FLASH);
				} else {
					setStatus(LEDStatus.OFF);
				}
				setTurnLeft(setTurnRight(false));
			} else {
				setTurnRight(false);
				setTurnLeft(!isTurnLeft());
			}
			leftButtonPressed = now;
		} else if (buttonR == BrickletDualButton.BUTTON_STATE_PRESSED) {
			if (now - rightButtonPressed < DOUBLE_PRESS) {
				if (getStatus() == LEDStatus.ON) {
					setStatus(LEDStatus.FLASH);
				} else {
					setStatus(LEDStatus.ON);
				}
				setTurnLeft(setTurnRight(false));
			} else {
				setTurnLeft(false);
				setTurnRight(!isTurnRight());
			}
			rightButtonPressed = now;
		}
		// }
	}

	public LEDStatus getStatus() {
		return status;
	}

	public boolean isTurnLeft() {
		return turnLeft;
	}

	private void setTurnLeft(boolean turnLeft) {
		this.turnLeft = turnLeft;
	}

	public boolean isTurnRight() {
		return turnRight;
	}

	private boolean setTurnRight(boolean turnRight) {
		this.turnRight = turnRight;
		return turnRight;
	}

	public void setError(Exception e) {
		this.error = e;
	}

	public String getMessage() {
		return message;
	}

	public int getTemperature() {
		return temperature;
	}

	public Exception getError() {
		return error;
	}

	@Override
	public void temperature(short temperature) {
		this.temperature = temperature;
	}

	public boolean getSound() {
		return sound;
	}

	public void setDisplay(BrickletSegmentDisplay4x7 sd) {
		display = sd;
	}

	public void setStatus(LEDStatus status) {
		this.status = status;
	}

	public void setDir(BrickletDistanceIR dir) {
		alarm.setDir(dir);
	}
}
