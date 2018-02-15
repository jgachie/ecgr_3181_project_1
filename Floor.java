public class Floor {

	final int floorNum;
	boolean fireKey;
	boolean fireKeyVisited;
	
	public Floor(int floorNum) {
		this.floorNum = floorNum;
		fireKey = false;
		fireKeyVisited = false;
	}
	
	public void callUp() {
		Main.elev.registers.setFloorCalled(floorNum, 1);
	}

	public void callDown() {
		Main.elev.registers.setFloorCalled(floorNum, -1);
	}
	
	public void insertKey() {
		fireKey = true;
		fireKeyVisited = false;
		this.callDown();
	}

	public void removeKey() {
		fireKey = false;
		fireKeyVisited = false;
		Main.elev.registers.reset();
	}

}
