package de.sddb.tinker.util;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class IOTools {

	public static final FileFilter EXCEL_FILE_FILTER = new FileFilter() {

		@Override
		public String getDescription() {
			return "Excel-Dateien (97-2003)";
		}

		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toUpperCase().endsWith(".XLS");
		}
	};

	public static String replaceInvalidFileNameChars(String fileName, char replaceChar) {
		String invalid = ":;\\/";
		for (int i = 0; i < invalid.length(); i++) {
			fileName = fileName.replace(invalid.charAt(i), replaceChar);
		}
		return fileName;
	}

	public static String readTextFile(String fileName) throws IOException {
		File f = new File(fileName);
		return readTextFile(f);
	}

	public static String readTextFile(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuffer sb = new StringBuffer();
		String line = br.readLine();
		while (line != null) {
			sb.append(line);
			sb.append("\n");
			line = br.readLine();
		}
		br.close();
		return sb.toString();
	}

	public static String readStream(InputStream in) throws IOException {
		StringBuffer sb = new StringBuffer();
		int c = (int) in.read();
		while (c != -1) {
			sb.append((char) c);
			c = in.read();
		}
		in.close();
		return sb.toString();
	}

	public static String readTextResource(URL url) throws Exception {
		InputStream is = getInputStream(url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = reader.readLine();
		while (line != null) {
			sb.append(line + "\n");
			line = reader.readLine();
		}
		is.close();
		return sb.toString();
	}

	public static InputStream getInputStream(URL url) throws Exception {
		// System.setProperty("javax.net.debug", "all");
		//SSLSocketFactory factory = new SSLSocketFactory();
		URLConnection con = url.openConnection();
		InputStream input = null;
//		if (con instanceof HttpsURLConnection) {
//			HttpsURLConnection connection = (HttpsURLConnection)con;
//			connection.setSSLSocketFactory(factory);
//			connection.setRequestProperty("charset", "utf-8");
//			input = connection.getInputStream();
//		}else{
			input=con.getInputStream();
//		}

		return input;
	}

	public static void writeTextFile(String fileName, String text) throws IOException {
		File f = new File(fileName);
		writeTextFile(f, text);
	}

	public static void writeTextFile(File f, String text) throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter(f));
		wr.write(text);
		wr.close();
	}

	/**
	 * Ein Filefilter, der nut Textdateien und Verzeichnisse akzeptiert
	 */
	public static class TextFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

		/**
		 * Konstruktor
		 */
		public TextFileFilter() {
		}

		/**
		 * Ermittelt, ob eine Datei akzeptiert wird.
		 * 
		 * @param file
		 *            die zu pr&uuml;fende Datei
		 * @return true, wenn die Datei auf .txt endet oder ein Verzeichnis ist
		 */
		public boolean accept(File file) {
			if (file.getName().endsWith(".txt") || file.isDirectory()) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Liefert die Beschreibung der akzeptierten Dateien
		 * 
		 * @return "Textdateien"
		 */
		public String getDescription() {
			return "Textdateien";
		}
	}

	/**
	 * Ein Filefilter, der nut Bilddateien und Verzeichnisse akzeptiert
	 */
	public static class ImageFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

		/**
		 * Konstruktor
		 */
		public ImageFileFilter() {
		}

		/**
		 * Ermittelt, ob eine Datei akzeptiert wird.
		 * 
		 * @param file
		 *            die zu pr&uuml;fende Datei
		 * @return true, wenn die Datei auf .png, .jpg, .gif endet oder ein
		 *         Verzeichnis ist
		 */
		public boolean accept(File file) {
			if (file.getName().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png")
					|| file.getName().toLowerCase().endsWith(".gif") || file.getName().toLowerCase().endsWith(".tif")
					|| file.getName().toLowerCase().endsWith(".tiff") || file.isDirectory()) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Liefert die Beschreibung der akzeptierten Dateien
		 * 
		 * @return "Textdateien"
		 */
		public String getDescription() {
			return "Grafiken (.jpg, .png, .gif)";
		}
	}

	public class SimpleFileNameExtensionFilter implements FilenameFilter {
		String suffix = ".xxx";

		public SimpleFileNameExtensionFilter(String suffix) {
			this.suffix = suffix;
		}

		public boolean accept(File dir, String name) {
			if (name != null && name.endsWith(suffix)) {
				return true;
			}
			return false;
		}

	}

	public class SimpleFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
		ArrayList<String> suffix = new ArrayList<String>();

		/**
		 * Konstruktor
		 */
		public SimpleFileFilter() {

		}

		public SimpleFileFilter(String suffix) {
			this.suffix.add(suffix);
		}

		public SimpleFileFilter(String... sf) {
			for (String s : sf) {
				this.suffix.add(s);
			}
		}

		public void setSuffix(String suffix) {
			this.suffix.clear();
			this.suffix.add(suffix);
		}

		/**
		 * Ermittelt, ob eine Datei akzeptiert wird.
		 * 
		 * @param file
		 *            die zu pr&uuml;fende Datei
		 * @return true, wenn die Datei auf .txt endet oder ein Verzeichnis ist
		 */
		public boolean accept(File file) {
			for (String s : suffix) {
				if (file.getName().toUpperCase().endsWith(s.toUpperCase()) || file.isDirectory()) {
					return true;
				}
			}
			return false;
		}

		public String getDescription() {
			return "Datei " + suffix;
		}
	}

	public static FileFilter getSimpleFileFilter(String... s) {
		return IOTOOL.new SimpleFileFilter(s);
	}

	public static SimpleFileFilter getSimpleFileFilter(String s) {
		// SIMPLE_FILE_FILTER.setSuffix(s);

		return IOTOOL.new SimpleFileFilter(s);
	}

	public static SimpleFileNameExtensionFilter getSimpleFileNameExtensionFilter(String s) {
		return IOTOOL.new SimpleFileNameExtensionFilter(s);
	}

	static IOTools IOTOOL = new IOTools();

	public static ImageFileFilter IMAGE_FILE_FILTER = new ImageFileFilter();

	private static PropertyFile pf;

	// public static SimpleFileFilter SIMPLE_FILE_FILTER = new
	// SimpleFileFilter();

	public IOTools() {
	}

	public static void fileNames2Lower(String dirName, String suffix) {
		File dir = new File(dirName);
		IOTools.SimpleFileFilter sf = IOTools.getSimpleFileFilter(suffix);
		File[] f = dir.listFiles(sf);
		System.out.println(dir);
		for (int i = 0; f != null && i < f.length; i++) {
			String name = f[i].getParent() + System.getProperty("file.separator") + f[i].getName().toLowerCase();
			File fn = new File(name);
			System.out.println(fn);
			f[i].renameTo(fn);
		}
	}

	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[512];
		int count = in.read(buffer);
		while (count > 0) {
			out.write(buffer, 0, count);
			count = in.read(buffer);
		}
		if (count > 0) {
			out.write(buffer, 0, count);
		}
		out.flush();
	}

	public static String getTempDirectory() {
		return System.getProperty("java.io.tmpdir");
	}

	public static String getHomeDirectory() {
		return System.getProperty("user.home");
	}

	public static String getFileSeparator() {
		String sep = "\\";
		try {
			sep = System.getProperty("file.separator");
		} catch (Throwable t) {
			System.err.println(t);
		}
		return sep;
	}

	public static void deleteRecursive(File f) {
		File[] children = f.listFiles();
		for (int file = 0; children != null && file < children.length; file++) {
			deleteRecursive(children[file]);
		}
		f.delete();
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static Vector<String[]> readCSV(InputStream in, String delimiter, boolean removeQuotes) throws IOException {
		Vector<String[]> result = new Vector<String[]>();
		BufferedReader bin = new BufferedReader(new InputStreamReader(in));
		String readString;
		while ((readString = bin.readLine()) != null) {
			String[] line = readString.split(delimiter);
			for (int i = 0; i < line.length; i++) {
				String s = line[i];
				if (removeQuotes && s.startsWith("\"") && s.endsWith("\"")) {
					line[i] = s.substring(1, s.length() - 1);
				}
				Debug.out(i + ":" + line[i]);
			}
			result.add(line);
		}
		in.close();
		return result;
	}

	public static void writeCSV(OutputStream out, Vector<Object[]> values, String delimiter, boolean quote)
			throws IOException {
		BufferedWriter bin = new BufferedWriter(new OutputStreamWriter(out));
		for (Object[] line : values) {
			writeLineCSV(bin, line, delimiter, quote);
		}
		bin.close();
	}

	public static void writeLineCSV(BufferedWriter out, Object[] line, String delimiter, boolean quote)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < line.length; i++) {
			Object o = line[i];
			if (o == null) {
				o = "";
			}
			String s = o.toString();
			if (quote) {
				s = "\"" + s + "\"";
			}
			sb.append(s + delimiter);
		}
		sb.setLength(sb.length() - delimiter.length());
		out.write(sb.toString() + "\n");
	}

	public static void main(String[] args) throws Exception {
		Debug.setDebug(true);
		String name = "/tmp/hoehen.csv";
		Vector<Object[]> values = new Vector<Object[]>();
		values.add(new Object[] { "1", "2" });
		values.add(new Object[] { "a", "b" });
		values.add(new Object[] { "c", "d" });
		writeCSV(new FileOutputStream(name), values, ";", false);
		FileInputStream in = new FileInputStream(name);
		readCSV(in, ";", false);
	}

	public static File getFileWithExtension(File choice, String suffix) {
		String name = choice.getAbsolutePath();
		if (!name.endsWith("." + suffix)) {
			name += "." + suffix;
		}
		File f = new File(name);
		return f;
	}

	public static File replaceSuffix(File file, String suffix) {
		String name = file.getAbsolutePath();
		int idx = name.lastIndexOf('.');
		if (idx > 0) {
			name = name.substring(0, idx + 1) + suffix;
		}
		return new File(name);
	}

	public static String getNameWithoutExtension(File lastFile) {
		int idx = lastFile.getName().lastIndexOf('.');
		String name = lastFile.getName().substring(0, (idx < 1 ? lastFile.getName().length() : idx));
		return name;
	}

	public static void systemRedirect(String fName, boolean split) {
		if (split) {
			String outName = fName + ".out";
			try {
				System.setOut(new PrintStream(new FileOutputStream(outName)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String errName = fName + ".err";
			try {
				System.setErr(new PrintStream(new FileOutputStream(errName)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				PrintStream ps = new PrintStream(new FileOutputStream(fName));
				System.setOut(ps);
				System.setErr(ps);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void systemRedirect(String fName) {
		systemRedirect(fName, false);
	}

	public static byte[] readBytes(File f) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(f);
		IOTools.copyStream(in, out);
		in.close();
		return out.toByteArray();
	}

	public static byte[] readBytes(String fileName) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		File f = new File(fileName);
		FileInputStream in = new FileInputStream(f);
		IOTools.copyStream(in, out);
		in.close();
		return out.toByteArray();
	}

	public static File chooseFile(Component frm, String key, String suffix, boolean saveDialog) {
		File[] files = chooseFiles(frm, key, suffix, false, saveDialog);
		if (files != null && files.length > 0) {
			return files[0];
		}
		return null;
	}

	public static File[] chooseFiles(Component frm, String key, String suffix, boolean multiple, boolean saveDialog) {
		try {
			String fName = getHomeDirectory() + getFileSeparator() + key + suffix;
			String props = getHomeDirectory() + getFileSeparator() + "chooseFile.properties";
			pf = new PropertyFile(props, false);
			for (String p : pf.getProperties().keySet()) {
				if (key != null && key.equals(p)) {
					fName = pf.getProperty(key);
				}
				;
			}

			JFileChooser chooser = new JFileChooser(fName);
			chooser.setFileFilter(getSimpleFileFilter(suffix));
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setMultiSelectionEnabled(multiple);
			chooser.setSelectedFile(new File(fName));
			int choice = 0;
			if (saveDialog) {
				choice = chooser.showSaveDialog(frm);
			} else {
				choice = chooser.showOpenDialog(frm);
			}
			if (choice == JFileChooser.APPROVE_OPTION) {
				File[] newFile = chooser.getSelectedFiles();
				if (newFile != null && newFile.length > 0) {
					fName = newFile[0].getAbsolutePath();
					pf.putProperty(key, fName);
				} else {
					File f = chooser.getSelectedFile();
					if (f != null) {
						fName = f.getAbsolutePath();
						if (!fName.endsWith(suffix)) {
							fName += suffix;
							f = new File(fName);
						}
						pf.putProperty(key, fName);
						newFile = new File[] { f };
					}
				}
				return newFile;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}
}
