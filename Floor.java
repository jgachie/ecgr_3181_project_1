public class Floor {

	final int floorNum;
	boolean fireKey;
	boolean fireKeyVisited;
	
	public Floor(int floorNum) {
		this.floorNum = floorNum;
		this.fireKey = false;
		this.fireKeyVisited = false;
	}
	
	public void callUp() {
		Main.elev.registers.setFloorCalled(floorNum, 1);
	}

	public void callDown() {
		Main.elev.registers.setFloorCalled(floorNum, -1);
	}
	
	public void insertKey() {
		this.fireKey = true;
		this.fireKeyVisited = false;
		this.callDown();
	}

	public void removeKey() {
		this.fireKey = false;
		this.fireKeyVisited = false;
		Main.elev.registers.reset();
	}

}
