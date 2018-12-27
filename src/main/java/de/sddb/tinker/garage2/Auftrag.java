package de.sddb.tinker.garage2;

public class Auftrag {
	private long created = System.currentTimeMillis();
	private String what;
	public static final String LEFT = "l";
	public static final String RIGHT = "r";

	Auftrag(String what) {
		this.what = what;
	}

	public String getWhat() {
		return what;
	}

	public long getCreated() {
		return created;
	}

	public void setWhat(String what) {
	this.what=what;	
	}

	@Override
	public String toString() {
		return "Auftrag [created=" + created + ", what=" + what + "]";
	}

}
