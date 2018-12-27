package de.sddb.tinker.garage2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import de.sddb.tinker.util.Debug;

public class Worker implements Runnable {

	private Socket socket;
	UpAnDahlDreher ud;

	public Worker(UpAnDahlDreher ud, Socket clientSocket) {
		this.socket = clientSocket;
		this.ud = ud;
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
			Debug.out(new Date().toString() + sb);
			if ("links".equals(sb.toString())) {
				Auftrag a = new Auftrag(Auftrag.LEFT);

				ud.add(a);
			} else if ("rechts".equals(sb.toString())) {
				Auftrag a = new Auftrag(Auftrag.RIGHT);

				ud.add(a);
			}
			out.write(sb.toString());
			out.close();
			in.close();
		} catch (

		Exception e) {
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
