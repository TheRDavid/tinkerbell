package de.sddb.tinker.haus;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Auftrag {

	private String id; //ID des Auftrages
	private String bricklet; //ID des Bricklets
	private int aktion; // fuer einen switch das relais
	private Date erfasst; // Datum der erfassung des Auftrages
	private Date ausfuehren; // wann soll die aktion ausgefuehrt werden
	private int dauer; //in millisec
	private String status; // erfasst, abgerufen, ausgefuehrt, verpasst
	public static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	public Auftrag(String id,String bricklet, int aktion, Date erfasst, Date ausfuehren,int dauer, String status) {
		super();
		this.setId(id);
		this.bricklet = bricklet;
		this.aktion = aktion;
		this.erfasst = erfasst;
		this.ausfuehren = ausfuehren;
		this.dauer=dauer;
		this.status = status;
	}
	public Auftrag(String bricklet, int aktion,  Date ausfuehren,int dauer) {
		super();
		this.setId(UUID.randomUUID().toString());
		this.bricklet = bricklet;
		this.aktion = aktion;
		this.erfasst = new Date();
		this.ausfuehren = ausfuehren;
		this.dauer=dauer;
		this.status = "erfasst";
	}
	private Auftrag() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Fuer jeden Key aus dem json-string muss ein getter und setter im Java
	 * Objekt existieren, sonst wird der Wert nicht gesetzt
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void simpleSetter(String json, Object o)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if ( json==null)return;
//		JSONObject jo = new JSONObject(json);
//		for (String name : JSONObject.getNames(jo)) {
//			Debug.out(name);
//			Debug.out(jo.get(name));
//			String mName = JavaNameGenerator.parseFirstCharUp(name);
//			Method m = ClassUtil.getMethod("get" + mName, o);
//			if (m != null) {
//				Object value = jo.get(name);
//				Class<?> c = m.getReturnType();
//				Method setter = ClassUtil.setMethod("set" + mName, c, o);
//				if (setter != null) {
//					if (value == null || c == value.getClass()) {
//						setter.invoke(o, value);
//					} else if (c.isAssignableFrom(int.class)) {
//						Integer i = Integer.parseInt(value.toString());
//						setter.invoke(o, i);
//					} else if (c.isAssignableFrom(Date.class)) {
//						 Date d;
//						try {
//							d = Auftrag.simpleDateFormat.parse(value.toString());
//							setter.invoke(o, d);
//						} catch (ParseException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		}
	}

	public static Auftrag fromJson(String s) {
		Auftrag a = new Auftrag();
		try {
			simpleSetter(s, a);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return a;
	}

	public int getDauer() {
		return dauer;
	}
	public void setDauer(int dauer) {
		this.dauer = dauer;
	}
	public String getBricklet() {
		return bricklet;
	}
	public void setBricklet(String bricklet) {
		this.bricklet = bricklet;
	}
	public int getAktion() {
		return aktion;
	}
	public void setAktion(int aktion) {
		this.aktion = aktion;
	}
	public Date getErfasst() {
		return erfasst;
	}
	public void setErfasst(Date erfasst) {
		this.erfasst = erfasst;
	}
	public Date getAusfuehren() {
		return ausfuehren;
	}
	public void setAusfuehren(Date ausfuehren) {
		this.ausfuehren = ausfuehren;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public static String toJson( Object o)
			throws Exception {
		StringBuffer sb = new StringBuffer("{");
//		for (Method m : o.getClass().getMethods()) {
//			String mName=m.getName().substring(3);
//			Method ms = ClassUtil.setMethod("set" + mName,m.getReturnType(), o);
//			if (ms != null) {
//				Class<?> c = m.getReturnType();
//				Object value =m.invoke(o, null);
//				if ( c.isAssignableFrom(Date.class) && value !=null){
//					sb.append("\n\t\""+mName+"\":"+"\t\""+simpleDateFormat.format(value)+"\",");	
//				}else{
//				sb.append("\n\t\""+mName+"\":"+"\t\""+value+"\",");
//				}
//			}
//		}
//		sb.setCharAt(sb.length()-1,'\n');
//		sb.append("}");
//		Debug.out(o);;
		return sb.toString();
	}

	public String toJson() {
		try {
			return toJson(this);
		} catch (Exception e) {
			e.printStackTrace();
//			try {
//				return JSONUtils.toJson(e.getMessage());
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
		}
		return null;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
