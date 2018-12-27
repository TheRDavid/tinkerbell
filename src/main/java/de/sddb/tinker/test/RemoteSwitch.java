package de.sddb.tinker.test;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.tinkerforge.BrickletRemoteSwitch;
import com.tinkerforge.IPConnection;

import de.sddb.tinker.util.Debug;

public class RemoteSwitch {
	private static final String HOST = "localhost";
	private static final int PORT = 4223;

	// Change XYZ to the UID of your Remote Switch Bricklet
	private final String UID = "v5i";

	// Note: To make the example code cleaner we do not handle exceptions.
	// Exceptions
	// you might normally want to catch are described in the documentation

	private BrickletRemoteSwitch remote;

	public RemoteSwitch() throws Exception {
		Debug.setDebug(true);
		IPConnection ipcon = new IPConnection(); // Create IP connection

		ipcon.connect(HOST, PORT); // Connect to brickd
		remote = new BrickletRemoteSwitch(UID, ipcon); // Create
		Query q = new Query();
		new Thread(q).start();
		System.out.println("Press key to exit");
		System.in.read();
		ipcon.disconnect();
		Debug.out("Ende");
		System.exit(0);
		;
	}

	class RemoteSwitchStatus {
		String name;
		int houseCode;
		int code=-1;
		int status=-1;
	}

	class Query implements Runnable {
		/**
		 * 
		 * @return on=true
		 * @throws Exception
		 */
		private ArrayList<RemoteSwitchStatus> read() throws Exception {
			ArrayList<RemoteSwitchStatus> res = new ArrayList<RemoteSwitchStatus>();
			URL url = new URL("http://www.sddb.de:8181/ShoppingList/GetItems");
			JsonReader r = Json.createReader(url.openStream());
			JsonArray arr = r.readArray();
			for (int i = 0; i < arr.size(); i++) {
				JsonValue v = arr.get(i);
				JsonReader reader = Json.createReader(new StringReader(v.toString()));

				JsonObject o = reader.readObject();
				String item = o.getString("ITEM");
				if (item != null && item.startsWith(UID)) {
					RemoteSwitchStatus rs = new RemoteSwitchStatus();
					StringTokenizer st=new StringTokenizer(item, "-");
					rs.name=st.nextToken();
					if ( st.hasMoreTokens()){
						try{
						rs.houseCode=Integer.parseInt(st.nextToken());
						}catch (NumberFormatException e){
							Debug.err(e);
						}
					}
					if ( st.hasMoreTokens()){
						try{
						rs.code=Integer.parseInt(st.nextToken());
						}catch (NumberFormatException e){
							Debug.err(e);
						}
					}
					rs.status = o.getInt("STATUS");
					res.add(rs);
				}
			}

			return res;
		}

		@Override
		public void run() {
			while (true) {
				try {
					 ArrayList<RemoteSwitchStatus> res = read();
					for (RemoteSwitchStatus rs:res) {
						if (rs.houseCode>-1 && rs.code>-1) {
							remote.switchSocketA((short) rs.houseCode, (short) rs.code, BrickletRemoteSwitch.SWITCH_TO_ON);
						} else {
							remote.switchSocketA((short) rs.houseCode, (short) rs.code, BrickletRemoteSwitch.SWITCH_TO_OFF);

						}
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public static void main(String args[]) throws Exception {
		new RemoteSwitch();
	}

}