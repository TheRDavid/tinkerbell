package de.sddb.tinker.util;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class StringUtil {
	public static String lPad(int value, int length) {
		return lPad(String.valueOf(value), length, ' ');
	}

	public static String lPad(final String value, int length) {
		return lPad(value, length, ' ');
	}

	public static String rPad(final String value, int length) {
		return rPad(value, length, ' ');
	}

	public static String lPad(final String value, int length, char c) {
		StringBuffer sbn = new StringBuffer(value);
		while (sbn.length() < length) {
			sbn.insert(0, c);
		}
		return sbn.toString();
	}

	public static String rPad(final String s, int length, char c) {
		StringBuffer sbn = new StringBuffer(String.valueOf(s));
		while (sbn.length() < length) {
			sbn.append(c);
		}
		return sbn.toString();
	}

	public static String remove(String s, String delimiter) {
		StringBuffer sb = new StringBuffer();
		int idx = s.indexOf(delimiter);
		int alt = 0;
		while (idx >= 0) {
			String sub = s.substring(alt, idx);
			sb.append(sub);
			alt = idx + 1;
			idx = s.indexOf(delimiter, idx + 1);
		}
		if (alt != 0) {
			String sub = s.substring(alt + delimiter.length() - 1, s.length());
			sb.append(sub);
		}
		return sb.toString();
	}

	public static String format(double d, int fractionDigits, boolean grouping) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(fractionDigits);
		nf.setGroupingUsed(grouping);
		String s = nf.format(d);
		return s;
	}

	public static String format(double d, int fractionDigits, int intDigits,
			boolean grouping) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(fractionDigits);
		nf.setMinimumFractionDigits(fractionDigits);
		nf.setMaximumIntegerDigits(intDigits);
		nf.setGroupingUsed(grouping);
		String s = nf.format(d);
		return s;
	}

	public static String format(long d, int intDigits, boolean grouping) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		nf.setMaximumIntegerDigits(intDigits);
		nf.setGroupingUsed(grouping);
		String s = nf.format(d);
		return s;
	}

	public static String format(long d, int intDigits) {
		return format(d, intDigits, false);
	}

	public static String format(double d, int fractionDigits, int intDigits) {
		return format(d, fractionDigits, intDigits, false);
	}

	public static String format(double d, int fractionDigits) {
		return format(d, fractionDigits, false);
	}

	public static String toString(Object o) {
		Method[] getters = o.getClass().getMethods();
		TreeSet<String> cols = new TreeSet<String>();
		for (int i = 0; i < getters.length; i++) {
			if (getters[i].getName().startsWith("get")
					&& !"getClass".equals(getters[i].getName())) {
				if (getters[i].getParameterTypes() == null
						|| getters[i].getParameterTypes().length == 0) {
					try {
						Object result = getters[i].invoke(o, (Object[]) null);
						String s = getters[i].getName().substring(3) + "=\""
								+ (result == null ? "NULL" : result.toString())
								+ "\"";
						cols.add(s);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		StringBuffer sb = new StringBuffer("[class="
				+ o.getClass().getSimpleName());
		for (Iterator<String> is = cols.iterator(); is.hasNext();) {
			String s = (String) is.next();
			sb.append(", " + s);
		}

		sb.append("]");
		return sb.toString();
	}

	/**
	 * Testet, ob Object test in Object[] values enthalten ist.
	 * 
	 * @param test
	 * @param values
	 * @return true, wenn test in values vorhanden ist.
	 */
	public static final boolean contains(final Object test,
			final Object[] values) {
		for (Object o : values) {
			if (test.equals(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * entferne Cahracter < 0x20 aus String. Sonst kann der String nicht ueber
	 * Webservice transporitert werden.
	 * 
	 * @param s
	 * @param replace
	 * @return
	 */
	public static String extractUtf(String s, char replace) {
		if (s == null) {
			return null;
		}
		boolean change = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 0x20) {
				sb.append(c);
			} else {
				sb.append(replace);
				Debug.out(i + ":" + (int) c);
				change = true;
			}
		}
		if (change) {
			Debug.out(s);
			Debug.out(sb);
		}
		return sb.toString();
	}

	public static String nvl(String in, String out) {
		if ( in ==null){
			return out;
		}
		return in;
	}

	public static int rows(String t) {
		int row=0;
		if ( t!=null){
			row++;
			int pos=t.indexOf('\n');
			while (pos>0){
				pos=t.indexOf('\n',pos+1);
				row++;
			}
		}
		return row;
	}

	public static int maxRowLength(String t) {
		int cols=0;
		if ( t!=null){
			StringTokenizer st=new StringTokenizer(t,"\n");
			while (st.hasMoreElements()){
				String s=st.nextToken();
				if ( s.length()>cols){
					cols= s.length();
				}
			}		
		}
		return cols;
	}
}
