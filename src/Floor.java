public class Floor {

	final int floorNum;
	boolean fireKey;
	boolean fireKeyVisited;
	
	public Floor(int floorNum) {
		this.floorNum = floorNum;
		this.fireKey = false;
		this.fireKeyVisited = false;
	}
	
	public void insertKey() {
		this.fireKey = true;
		this.fireKeyVisited = false;
		Main.elev.registers.setFloorCalled(floorNum, -1);
	}

	public void removeKey() {
		this.fireKey = false;
		this.fireKeyVisited = false;
		Main.elev.registers.reset();
	}

	public Object[] log() {
		return new Object[]{fireKey, fireKeyVisited};
	}
}
