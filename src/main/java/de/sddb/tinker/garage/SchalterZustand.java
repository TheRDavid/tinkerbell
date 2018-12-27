package de.sddb.tinker.garage;

import java.io.IOException;

import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class SchalterZustand {
	private final static int WAIT = 500;
	private final static int SCHALTE_LINKES_TOR = 0b1000;
	private final static int SCHALTE_RECHTES_TOR = 0b0001;
	private final static int LINKES_TOR_OFFEN = 0b0100;
	private final static int RECHTES_TOR_OFFEN = 0b0010;
	private int mask = 0b000;

	private BrickletIndustrialQuadRelay ido4;

	public SchalterZustand( BrickletIndustrialQuadRelay ido4) {
		this.ido4 = ido4;
		trigger(0, mask);
	}

	public void triggerLeft() {
		mask = mask | SCHALTE_LINKES_TOR;
		mask = mask ^ LINKES_TOR_OFFEN;
		trigger(WAIT, mask);
		mask = mask ^ SCHALTE_LINKES_TOR;
		trigger(0, mask);
	}

	public void triggerRight() {
		mask = mask | SCHALTE_RECHTES_TOR;
		mask = mask ^ RECHTES_TOR_OFFEN;
		trigger(WAIT, mask);
		mask = mask ^ SCHALTE_RECHTES_TOR;
		trigger(0, mask);
	}

	public void setConnected(boolean connected) {
		if (connected) {
			for (int i = 0; i < 5; i++) {
				trigger(200, 0b0110);
				trigger(200, 0b0000);
			}		
		} else {
			for (int i = 0; i < 5; i++) {
				trigger(200, 0b0100);
				trigger(200, 0b0010);
			}
		}
		trigger(0, mask);
	}

	private synchronized void trigger(int wait, int mask) {
		try {
			setValue(mask);
			if (wait > 0) {
				Thread.sleep(wait);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setValue(int mask) throws IOException, TimeoutException, NotConnectedException {
		ido4.setValue(mask);
	}
	public void reset() throws IOException {
		mask=0b0000;
		trigger(0,mask);
	}
}
