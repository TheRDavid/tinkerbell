package de.sddb.tinker.garage2;

import java.util.HashSet;

import com.tinkerforge.BrickletNFC;
import com.tinkerforge.BrickletNFC.ReaderStateChangedListener;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.sddb.tinker.util.Debug;

public class NFC implements ReaderStateChangedListener {
	private static final String UID = "Eea";
	private IPConnection ipcon;
	private BrickletNFC nfc;
	private UpAnDahlDreher up;
	private HashSet<String> leftKeys;
	private HashSet<String> rightKeys;

	public NFC(UpAnDahlDreher up, IPConnection ipcon) throws TimeoutException, NotConnectedException {
		this.up = up;
		this.ipcon = ipcon;
		init();
		leftKeys = new HashSet<String>();
		leftKeys.add("4 6 F2 FA FD 4A 81");
		leftKeys.add("4 F4 F2 FA FD 4A 80");
		rightKeys = new HashSet<String>();
		rightKeys.add("4 42 2F CA 82 49 81");
		rightKeys.add("4 78 28 9A A5 48 80");
		rightKeys.add("4 43 37 CA 82 49 81");
		rightKeys.add("4 B1 29 9A A5 48 80");
	}

	private void init() throws TimeoutException, NotConnectedException {
		this.nfc = new BrickletNFC(UID, ipcon); // Create device object

		// Add reader state changed listener
		nfc.addReaderStateChangedListener(this);

		// Enable reader mode
		nfc.setMode(BrickletNFC.MODE_READER);
	}

	@Override
	public void readerStateChanged(int state, boolean idle) {
		// Debug.out(state + "-" + idle);
		if (state == BrickletNFC.READER_STATE_IDLE) {
			try {
				nfc.readerRequestTagID();
			} catch (Exception e) {
				return;
			}
		} else if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_READY) {
			try {
				int i = 0;
				StringBuilder tag = new StringBuilder();
				BrickletNFC.ReaderGetTagID ret = nfc.readerGetTagID();

				for (int v : ret.tagID) {
					if (i < ret.tagID.length - 1) {
						tag.append(String.format("%X ", v));
					} else {
						tag.append(String.format("%X", v));
					}

					i++;
				}

				if (leftKeys.contains(tag.toString())) {
					Debug.out("Found LEFT tag" + tag);
					Auftrag a = new Auftrag(Auftrag.LEFT);
					up.add(a);
				} else if (rightKeys.contains(tag.toString())) {
					Debug.out("Found RIGHT tag" + tag);
					Auftrag a = new Auftrag(Auftrag.RIGHT);
					up.add(a);
				} else {
					Debug.err("Found INVALID tag" + tag);
				}
				Thread.sleep(3000);
			} catch (Exception e) {
				return;
			}
		} else if (state == BrickletNFC.READER_STATE_REQUEST_TAG_ID_ERROR) {
			System.out.println("Request tag ID error");
		}

		// Enable reader mode
		try {
			nfc.setMode(BrickletNFC.MODE_READER);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		Debug.err("NFC beendet");
		super.finalize();
	}
}