package de.sddb.tinker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;

public class PropertyFile extends RandomAccessFile {

	/**
	 * Erzeugt ein PropertyFile, dessen Modus auf 'rw' gesetzt ist
	 * 
	 * @param file
	 *            Die Datei, auf der das PropertyFile aufsetzen soll
	 * @throws FileNotFoundException
	 */
	public PropertyFile(File file) throws FileNotFoundException {
		super(file, "rwd");
	}

	/**
	 * Erzeugt ein PropertyFile, dessen Modus auf 'rw' gesetzt ist
	 * 
	 * @param fileName
	 *            Der Name der Datei, auf der das PropertyFile aufsetzen soll
	 * @throws FileNotFoundException
	 */
	public PropertyFile(String fileName) throws FileNotFoundException {
		super(fileName, "rwd");
	}

	/**
	 * Erzeugt ein PropertyFile, dessen Modus auf 'rw' gesetzt ist
	 * 
	 * @param fileName
	 *            Der Name der Datei, auf der das PropertyFile aufsetzen soll
	 * @param readOnly
	 *            True, wenn die Datei nur gelesen wird. Gibt dann später
	 *            Exceptions, wenn doch geschrieben wird.
	 * @throws FileNotFoundException
	 */
	public PropertyFile(String fileName, boolean readOnly)
			throws FileNotFoundException {
		super(fileName, readOnly ? "r" : "rwd");
	}

	public String getProperty(String name) throws IOException {
		seek(0);
		String line;
		while ((line = readLineUTF8()) != null) {
			String[] tokens = line.split("=");
			if (tokens[0].trim().equals(name)) {
				String val = line.substring(tokens[0].length() + 1);
				if (val != null && val.length() > 0) {
					val = val.trim();
					if (val.length() > 0) {
						if (val.charAt(0) == '\''
								&& val.charAt(val.length() - 1) == '\'') {
							val = val.substring(1, val.length() - 1);
						} else if (val.charAt(0) == '\"'
								&& val.charAt(val.length() - 1) == '\"') {
							val = val.substring(1, val.length() - 1);
						}
						val = val.trim();
						return val;
					}
				}
			}
		}
		return null;
	}

	public HashMap<String, String> getProperties() throws IOException {
		seek(0);
		String line;
		HashMap<String, String> l = new HashMap<String, String>();
		while ((line = readLineUTF8()) != null) {
			String[] tokens = line.split("=");
			if (tokens != null && tokens.length > 1) {
				String key = tokens[0].trim();
				String value = getProperty(key);
				l.put(key, value);
			}
		}
		return l;
	}

	public boolean putProperty(String name, String value) throws IOException {
		seek(0);
		String line;
		while ((line = readLine()) != null) {
			String[] tokens = line.split("=");
			if (tokens[0].equals(name)) {
				long pos = getFilePointer();
				long oldLength = line.length() - line.lastIndexOf('=') - 1;

				long diff = value.length() - oldLength;
				long lineSeparatorLength = System.getProperty("line.separator")
						.length();
				long startPos = pos - oldLength - lineSeparatorLength;
				long oldFileLength = length();
				if (diff > 0) {
					// Neuer Wert laenger als alter? Dann alles rechts davon
					// nach rechts shiften
					setLength(length() + diff);
					for (long p = oldFileLength - 1; p >= pos; p--) {
						seek(p);
						int i = read();
						seek(p + diff);
						writeByte(i);
					}

				} else if (diff < 0) {
					// Neuer Wert kuerzer als alter? Dann alles rechts davon
					// nach links shiften
					for (long p = pos; p < oldFileLength; p++) {
						seek(p);
						int i = read();
						seek(p + diff);
						writeByte(i);
					}
					setLength(length() + diff);
				}
				seek(startPos);
				writeBytes(value);
				writeBytes(System.getProperty("line.separator"));
				return false;
			}
		}
		writeBytes(name + "=" + value);
		writeBytes(System.getProperty("line.separator"));
		return true;
	}

	public final String readLineUTF8() throws IOException {
		LinkedList<Byte> byteList = new LinkedList<Byte>();
		int c = -1;
		boolean eol = false;

		while (!eol) {
			switch (c = read()) {
			case -1:
			case '\n':
				eol = true;
				break;
			case '\r':
				eol = true;
				long cur = getFilePointer();
				if ((read()) != '\n') {
					seek(cur);
				}
				break;
			default:
				byteList.add((byte)c);
				break;
			}
		}

		if ((c == -1) && (byteList.size() == 0)) {
			return null;
		}
		byte[] rawBytes = new byte[byteList.size()];
		for(int i = 0; i < rawBytes.length; i++)
			rawBytes[i] = byteList.get(i).byteValue();
		return new String(rawBytes,Charset.forName("UTF-8"));
	}
}
