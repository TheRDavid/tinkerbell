package de.sddb.tinker.garage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import de.sddb.tinker.util.Debug;

public class SchalterTest {
	 public static void main(String[] args) throws Exception {

		 Debug.setDebug(true);;
	        String serverAddress = "192.168.1.118";
	        Socket s = new Socket(serverAddress, 4711);
	        Debug.out("connected");
	        s.getOutputStream().write("rechts.".getBytes());
	        BufferedReader input =
	            new BufferedReader(new InputStreamReader(s.getInputStream()));
	        Debug.out("read");
	        String answer = input.readLine();
	        Debug.out(answer);
	        Thread.sleep(3000);
	        s.getOutputStream().write("reset.".getBytes());
	        System.exit(0);
	    }
}
