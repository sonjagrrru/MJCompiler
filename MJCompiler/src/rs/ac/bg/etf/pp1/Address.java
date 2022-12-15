package rs.ac.bg.etf.pp1;

public class Address {

	int level;
	int adr;

	public Address(int adr, int level) {
		this.level = level;
		this.adr = adr;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getAdr() {
		return adr;
	}

	public void setAdr(int adr) {
		this.adr = adr;
	}

}
