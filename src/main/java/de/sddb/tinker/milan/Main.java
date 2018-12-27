package de.sddb.tinker.milan;

import com.tinkerforge.BrickletDistanceIR;
import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.BrickletNFCRFID;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.IPConnection;

public class Main {
	private static final String UID_NFC_RFID = "uw8"; // Change to your UID
	private static final String UID_IR = "tHG";


	public static void main(String[] args) throws Exception {
		String HOST = "localhost";
		int PORT = 4223;

		IPConnection ipcon = new IPConnection(); // Create IP connection
		BrickletDualButton dualButton = new BrickletDualButton("mMK", ipcon);
		BrickletIndustrialQuadRelay quadRelayTail = new BrickletIndustrialQuadRelay(
				"u5f", ipcon);
		BrickletIndustrialQuadRelay quadRelayHeadLeft = null;
		BrickletIndustrialQuadRelay quadRelayHeadRight = null;
		// BrickletIndustrialQuadRelay quadRelayHeadLeft = new
		// BrickletIndustrialQuadRelay("", ipcon);
		// BrickletIndustrialQuadRelay quadRelayHeadRight = new
		// BrickletIndustrialQuadRelay("", ipcon);
		BrickletSegmentDisplay4x7 sd = new BrickletSegmentDisplay4x7("wPP",
				ipcon);
		BrickletTemperature temperatureSensor = new BrickletTemperature("tgh",
				ipcon);
		BrickletNFCRFID nfcrfid = new BrickletNFCRFID(UID_NFC_RFID, ipcon); // Create device
		BrickletDistanceIR dir = new BrickletDistanceIR(UID_IR, ipcon);
		ipcon.connect(HOST, PORT);

		try {
			temperatureSensor.getIdentity();
		} catch (Exception e) {
			temperatureSensor = null;
		}
		MilanController controller = new MilanController();

		controller.setDualButton(dualButton);
		controller.setQuadRelay(new BrickletIndustrialQuadRelay[] {
				quadRelayTail, quadRelayHeadLeft, quadRelayHeadRight });
		controller.setDisplay(sd);
		controller.setTemperatureSensor(temperatureSensor);
		controller.setNfcrfid(nfcrfid);
		controller.setDir(dir);
		controller.start();
		System.out.println("Press key to exit");
		System.in.read();

	}
}
