package de.sddb.tinker.garage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;

import de.sddb.tinker.util.Debug;

public class Main {
	private IPConnection ipcon;
	private SchalterZustand sz;
	private BrickletIndustrialQuadRelay ido4;
	public Main() throws UnknownHostException, AlreadyConnectedException, IOException, NetworkException {
		init();
	}

	public void init() throws UnknownHostException, AlreadyConnectedException, IOException, NetworkException {
		Debug.setDebug(true);
		String HOST = "localhost";
		int PORT = 4223;
		String UID = "u1L";

		ipcon = new IPConnection(); // Create IP connection
		ipcon.connect(HOST, PORT); // Connect to brickd
		ido4 = new BrickletIndustrialQuadRelay(UID, ipcon); // Create
		sz= new SchalterZustand(ido4);
		startServer(4711);
		INetTest netTest= new INetTest(3000, sz);
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
							Thread th = new Thread(new Worker(clientSocket));
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

	class Worker implements Runnable {
		private Socket socket;

		public Worker(Socket clientSocket) {
			this.socket = clientSocket;
		}

		@Override
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				StringBuffer sb = new StringBuffer();
				int c = in.read();
				while (c != '.') {
					sb.append((char) c);
					c = in.read();
				}
				Debug.out(new Date().toString()+sb);
				if ("links".equals(sb.toString())) {
					sz.triggerLeft();
				} else if ("rechts".equals(sb.toString())) {
					sz.triggerRight();
				}else if ("reset".equals(sb.toString())) {
					sz.reset();
				}
				out.write(sb.toString());
				out.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Main s = new Main();
		
	}
}