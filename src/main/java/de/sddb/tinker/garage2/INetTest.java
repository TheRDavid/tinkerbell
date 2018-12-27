package de.sddb.tinker.garage2;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeoutException;

import de.sddb.tinker.util.Debug;

public class INetTest implements Runnable {
	private int millis2Wait;
	private DoppelSchalter dp;
	
	public INetTest(int millis2Wait, DoppelSchalter dp) {
		this.millis2Wait = millis2Wait;
		this.dp=dp;
	}

	public void test() throws IOException, TimeoutException {
		int timeout = 2000;
		InetAddress[] addresses = InetAddress.getAllByName("www.sddb.de");
		for (InetAddress address : addresses) {
			if (address.isReachable(timeout)) {
				Debug.out("%s is reachable%n", address);
				dp.blink();
			} else {
				Debug.out("%s could not be contacted%n", address);
			}
		}
	}

	@Override
	public void run() {
		while (millis2Wait > 0) {
			try {
				test();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(millis2Wait);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
