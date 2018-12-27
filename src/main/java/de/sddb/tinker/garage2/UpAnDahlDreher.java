package de.sddb.tinker.garage2;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.sddb.tinker.util.Debug;

public class UpAnDahlDreher implements Runnable {
	private BrickletIndustrialQuadRelay ido4;
	Queue<Auftrag> q;
	private final long ZU_ALT = 1000 ;
	private DoppelSchalter ds;

	public UpAnDahlDreher(BrickletIndustrialQuadRelay ido4) {
		this.ido4 = ido4;
		q = new ConcurrentLinkedQueue<>();
	}

	// 0 = left door
	// 1 = success
	// 2 = error
	// 3 = right
	public synchronized void exec() throws TimeoutException, NotConnectedException {
		Auftrag a = q.poll();
		if (a == null)
			return;
		Debug.out(a);
		
		Auftrag b = q.poll(); // Doppel Befehl
		if (b != null && b.getWhat().equals(a.getWhat()) && b.getCreated() - a.getCreated() < ZU_ALT) {
			if (Auftrag.LEFT.equals(a.getWhat())) { // umdrehen
				a.setWhat(Auftrag.RIGHT);
			} else {
				a.setWhat(Auftrag.LEFT);
			}
			Debug.out(a);
		}
		q.clear();
		if (ds != null) {
			if (Auftrag.LEFT.equals(a.getWhat())) {
				ds.toggleLeft();
				schalten(0b0001);
			} else {
				ds.toggleRight();
				schalten(0b1000);
					}
		}
	}

	private void schalten(int b) throws TimeoutException, NotConnectedException {
		ido4.setValue(b);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ido4.setValue(0b0000);
	
	}

	public synchronized void add(Auftrag a) {
		q.add(a);
	}

	@Override
	public void run() {
		while (true) {
			try {
				exec();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				//Debug.out("sleep");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public DoppelSchalter getDs() {
		return ds;
	}

	public void setDs(DoppelSchalter ds) {
		this.ds = ds;
	}
}
