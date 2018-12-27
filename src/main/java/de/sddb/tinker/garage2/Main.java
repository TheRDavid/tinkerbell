package de.sddb.tinker.garage2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.IPConnection;

import de.sddb.tinker.util.Debug;

public class Main {
	private IPConnection ipcon;
	private BrickletIndustrialQuadRelay ido4;
	private UpAnDahlDreher up;

	public Main() throws Exception {
		init();
	}

	public void init() throws Exception {
		Debug.setDebug(true);
		String HOST = "localhost";
		int PORT = 4223;
		String UID = "u1L";

		ipcon = new IPConnection(); // Create IP connection
		ipcon.connect(HOST, PORT); // Connect to brickd
		this.ido4 = new BrickletIndustrialQuadRelay(UID, ipcon); // Create
		up = new UpAnDahlDreher(ido4);
		// Thread th=new Thread(up);
		// th.start();
		DoppelSchalter dp = new DoppelSchalter(up, ipcon);
		INetTest it=new INetTest(3000,dp);
		new Thread(it).start();
		NFC nfc= new NFC(up,ipcon);
		up.setDs(dp);
		startServer(4711);
		up.run();
		// while (true) {
		// try {
		// Thread.sleep(50000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	public static void main(String[] args) throws Exception, AlreadyConnectedException, IOException {
		new Main();
	}

	public void startServer(final int port) {
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {

				ServerSocket serverSocket = null;
				try {
					serverSocket = new ServerSocket(port);
					Debug.out("started");

					while (true) {
						try {
							Socket clientSocket = serverSocket.accept();
							Thread th = new Thread(new Worker(up, clientSocket));
							th.start();

						} catch (IOException e) {
							System.err.println("Accept failed ");
							Debug.err(e);
						}
					}

				} catch (IOException e) {
					System.out.println("Could not listen on port: " + port);
					System.exit(-1);
				} finally {
					if (serverSocket != null)
						try {
							serverSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		});
		th.start();
	}

}
