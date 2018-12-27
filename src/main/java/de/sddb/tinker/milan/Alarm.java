package de.sddb.tinker.milan;

import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDistanceIR.DistanceListener;
import com.tinkerforge.BrickletNFCRFID;
import com.tinkerforge.BrickletNFCRFID.StateChangedListener;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.sddb.tinker.util.Debug;

public class Alarm implements StateChangedListener, DistanceListener {
	private static final short[] KEY = new short[] { 0x04, 0xB1, 0x29, 0x9A,
			0xA5, 0x48, 0x80 };
	
	private BrickletNFCRFID nfcrfid;
	private BrickletDistanceIR dir;
	private short currentTagType;
	private boolean ready = false;
	private boolean alarm = false;
	private int closedDistance;
	private MilanController control;

	public boolean isReady() {
		return ready;
	}

	Alarm(MilanController control) {
		this.control = control;
	}
	public void setNfcrfid(BrickletNFCRFID nfcrfid) {
		this.nfcrfid = nfcrfid;
		// object
		nfcrfid.addStateChangedListener(this);
		// Start scan loop
		try {
			nfcrfid.requestTagID(BrickletNFCRFID.TAG_TYPE_TYPE2);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
	}

	public void setDir(BrickletDistanceIR dir) {
		this.dir = dir;
		try {
			dir.setDistanceCallbackPeriod(1000);
		} catch (TimeoutException | NotConnectedException e) {
			e.printStackTrace();
		}
		dir.addDistanceListener(this);
	}

	public void stateChanged(short state, boolean idle) {
		try {
			if (idle) {
				currentTagType = (short) ((currentTagType + 1) % 3);
				nfcrfid.requestTagID(currentTagType);
			}

			if (state == BrickletNFCRFID.STATE_REQUEST_TAG_ID_READY) {
				BrickletNFCRFID.TagID tagID = nfcrfid.getTagID();
				String s = "Found tag of type " + tagID.tagType + " with ID ["
						+ Integer.toHexString(tagID.tid[0]);
				boolean accept = true;
				for (int i = 1; i < tagID.tidLength; i++) {
					s += " " + Integer.toHexString(tagID.tid[i]);
					if (tagID.tid[i] != KEY[i]) {
						accept = false;
					}
				}
				Debug.out("Schlüssel gültig:" + accept);
				if (accept) {
					setAlarm(false);
					ready = !ready;
					if (ready) {
						control.setStatus(MilanController.LEDStatus.ON);
						Thread.sleep(5 * 1000);
						closedDistance = dir.getDistance();
						control.setStatus(MilanController.LEDStatus.OFF);
					} else {
						// ir.removeDistanceListener(this);
//						control.setStatus(MilanController.LEDStatus.ON);
						Thread.sleep(5*1000);
						control.setStatus(MilanController.LEDStatus.FLASH);

					}
					// Thread.sleep(3*1000);
				}
				Debug.out("Alarm:" + ready);

				s += "]";
				System.out.println(s);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}



	public void distance(int distance) {
		Debug.out("Distance:" + distance);
		if (closedDistance == 0) {// noch kein Wert
			closedDistance = distance;
		} else if (ready) {
			Debug.out("closed:" + (closedDistance));
			Debug.out("current:" + (distance));

			if (Math.abs(closedDistance - distance) > 100) {
				setAlarm(true);
				Debug.out("ALARM:" + (closedDistance - distance));
				if (control != null) {
					control.setStatus(MilanController.LEDStatus.WARN);
				}
				// ir.removeDistanceListener(this);
			}
		}
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

}
