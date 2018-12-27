package de.sddb.tinker.garage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeoutException;

public class INetTest implements Runnable {
	private int millis2Wait;
	private SchalterZustand sz;

	public INetTest(int millis2Wait,SchalterZustand bi) {
		this.millis2Wait = millis2Wait;
		this.sz=bi;
	}

	public void test() throws IOException, TimeoutException {
		int timeout = 2000;
		InetAddress[] addresses = InetAddress.getAllByName("www.sddb.de");
		for (InetAddress address : addresses) {
			if (address.isReachable(timeout)) {
				System.out.printf("%s is reachable%n", address);
				sz.setConnected(true);
			} else {
				System.out.printf("%s could not be contacted%n", address);
				sz.setConnected(false);
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
