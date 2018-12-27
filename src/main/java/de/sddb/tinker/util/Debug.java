package de.sddb.tinker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;


public class Debug {
	private static boolean debug = System.getProperty("DEBUG") != null;
	private static PrintStream out = System.out;
	private static PrintStream err = System.err;
	private static SimpleDateFormat fmt = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss.SSSS");
	static {
		String sOut = System.getProperty("DEBUG_OUT");
		if (sOut != null) {
			try {
				setOut(new PrintStream(new File(sOut)));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		String sErr = System.getProperty("DEBUG_ERR");
		if (sErr != null) {
			try {
				setErr(new PrintStream(new File(sErr)));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		String sDEBUG = System.getProperty("DEBUG");
		if(sDEBUG != null&& sDEBUG.length()>0){
			IOTools.systemRedirect(sDEBUG,false);
		}
	}

	public static void out(Object o) {
		if (debug) {
			String s = getString(o);
			getOut().println(s);
			getOut().flush();
			if (getOut() != System.out) {
				System.out.println(s);
			}
		}
	}

	public static void out(Object... o) {
		if (debug) {
			getOut().println(getString(encode(o)));
			getOut().flush();
		}
	}

	public static void err(Object[] o) {
		String s = getString(encode(o));
		getErr().println(s);
		getErr().flush();
		if (getErr() != System.err) {
			System.err.println(s);
		}
	}

	public static void out(double o) {
		if (debug) {
			getOut().println(getString(o));
			if (getOut() != System.out) {
				System.out.println(getString(o));
			}
			getOut().flush();
		}
	}

	public static void err(Object o) {
		String s = getString(o);
		getErr().println(s);
		getErr().flush();
		if (getErr() != System.err) {
			System.err.println(s);
		}
	}

	public static void err(Exception o) {
		err(getString(o));
		if ( err !=null){
			o.printStackTrace(err);
		}else{
			o.printStackTrace();
		}
		getErr().flush();
	}

	public static void err(double o) {
		getErr().println(getString(o));
		getErr().flush();
	}

	public static String getString(Object o) {
		Exception e = new Exception();
		StackTraceElement[] st = e.getStackTrace();
		// String s
		// =st[1].getMethodName()+"("+st[1].getFileName()+":"+st[1].getLineNumber()+")";
		String s = null;
		for (int i = 0; i < st.length; i++) {
			s = st[i].toString();
			if (s.indexOf("Debug") == -1) {
				break;
			}
		}
		Date d = new Date(System.currentTimeMillis());
		s += "==>" + o;

		return (fmt.format(d) + " " + s);
	}

	// public static String getString(double o) {
	// Exception e = new Exception();
	// StackTraceElement[] st = e.getStackTrace();
	// // String s
	// //
	// =st[1].getMethodName()+"("+st[1].getFileName()+":"+st[1].getLineNumber()+")";
	//
	// String s = st[2].toString();
	// s += "==>" + o;
	// return ("DEBUG:" + s);
	// }

	public static void stack() {
		Exception e = new Exception();
		StackTraceElement[] st = e.getStackTrace();

		getOut().println("STACKTRACE:");
		for (int i = 0; i < st.length; i++) {
			getOut().println("\t" + st[i]);
		}
	}

	private static long logTime = System.currentTimeMillis();

	public static long getLogTime() {
		return logTime;
	}

	public static void logTime(Object o) {
		long now = System.currentTimeMillis();
		long duration = now - logTime;
		logTime = now;
		String s = "[" + StringUtil.lPad(String.valueOf(duration), 6) + "ms"
				+ "]";
		Debug.out(o + s);
	}

	public static void logTime(double d) {
		logTime(new Double(d));
	}

	public static void resetTime() {
		logTime = System.currentTimeMillis();
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Debug.debug = debug;
	}

	public static void printTree(final DefaultMutableTreeNode tn) {
		if (!debug) {
			return;
		}
		printTreeNode(tn, "");
	}

	public static void printTreeNode(final DefaultMutableTreeNode tn,
			final String linePref) {
		String newLinePref = linePref + " ";
		getOut().println(tn.getUserObject());
		@SuppressWarnings("unchecked")
		Enumeration<TreeNode> children = tn.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode ntn = (DefaultMutableTreeNode) children
					.nextElement();
			printTreeNode(ntn, newLinePref);
		}
	}

	public static void heap(String title) {
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long mb = 1024 * 1024;
		Debug.out(title + "\tMax:" + max / mb + " total:" + total / mb
				+ " free:" + free / mb);
	}

	/**
	 * @param out
	 *            the out to set
	 */
	public static void setOut(PrintStream out) {
		Debug.out = out;
	}

	/**
	 * @param err
	 *            the err to set
	 */
	public static void setOut(String fileName) {
		try {
			Debug.out = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			out = System.out;
		}
	}

	/**
	 * @return the out
	 */
	public static PrintStream getOut() {
		return out;
	}

	/**
	 * @param err
	 *            the err to set
	 */
	public static void setErr(PrintStream err) {
		Debug.err = err;
	}

	/**
	 * @param err
	 *            the err to set
	 */
	public static void setErr(String fileName) {
		try {
			Debug.err = new PrintStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			err = System.err;
		}
	}

	/**
	 * @return the err
	 */
	public static PrintStream getErr() {
		return err;
	}

	private static String encode(Object[] os) {
		StringBuffer sb = new StringBuffer();
		if (os != null) {
			for (Object o : os) {
				String value = (o == null ? "NULL" : o.toString());
				value = value.replace('\n', (char) 7);
				sb.append("'" + value + "',");
			}
		}
		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);
		// sb.append("\n");
		return sb.toString();
	}

	public static void object(Object o) {
		if (debug) {
			if (o == null) {
				Debug.out("NULL");
			} else {
				String s = o.toString();
				if (s.length() > 80) {
					s = s.substring(0, 80) + "<<<";
				}
				Debug.out(o.getClass().getSimpleName() + ":" + s);
				if (o instanceof Collection<?>) {
					Collection<?> list = (Collection<?>) o;
					for (Object child : list) {
						Debug.object(child);
					}
				}
				if (o instanceof Map<?, ?>) {
					Map<?, ?> list = (Map<?, ?>) o;
					for (Object key : list.keySet()) {
						Debug.object(key);
						Debug.object(list.get(key));
					}
				}
			}
		}
	}

	public static void properties(Object o) {
		try {
			Debug.out(getProperties(o));
		} catch (Exception e) {
			Debug.out(e);
		}
	}

	public static String getProperties(Object o)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method[] ms = o.getClass().getMethods();
		TreeMap<String, Object> properties = new TreeMap<String, Object>();
		for (Method m : ms) {
			if (m.getName().startsWith("get")
					&& m.getParameterTypes().length == 0) {
				Object val = m.invoke(o, (Object[])null);
				if (val instanceof Number && val != null) {
					val = val.toString();
				} else {
					val = "\"" + (val == null ? "NULL" : val.toString()) + "\"";
				}
				String key = m.getName().substring(3);
				properties.put(key, val);
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\n\t" + o.getClass().getName() + "[");
		for (Entry<String, Object> k : properties.entrySet()) {
			sb.append("\n\t\t");
			sb.append(k.getKey() + " = ");
			sb.append(k.getValue().toString());
		}
		sb.append("]");
		return sb.toString();
	}

	private static HashSet<String>propertyRequests=new HashSet<String>();
	public static String getProperty(String key) {
		String value = System.getProperty(key);
		Debug.out("PROPERTY:" + key + "=" + "'" + value + "'");
		propertyRequests.add(key);
		return value;
	}
	
	public static Collection<String> getPropertyRequests(){
		return propertyRequests;
	}
	public static String getEnvironment(String key) {
		Map<String, String> env = System.getenv();
		return env.get(key);
	}
}
