import java.util.Arrays;

public class RegisterBank {

	private int x19 = 0;                                   // Lights register. the left-most bit is 7
	private int[] x1000 = new int[Main.floors.length * 2]; // Destination floors, ordered by selection time
	private int x20 = 0;                                   // Current address, 0 being 0x1000
	
	public RegisterBank() {
		for (int i = 0; i < x1000.length; i++) {
			x1000[i] = -1;
		}
	}

	public int getFloorNum(int num) {
		num = num & ~(0x80);
		return num;
	}

	public int getCallDir(int num) {
		return (num & 0x80) >> 7;
	}
	
	public boolean stopAtFloor(int floor) {
		if (Main.floors[floor].fireKey && !Main.floors[floor].fireKeyVisited) {
			return true;
		}

		for (int i = 0; i < x1000.length; i++) {
			if (floor == getFloorNum(x1000[i]) && (Main.elev.direction == getCallDir(x1000[i]) || Main.elev.direction == 0)) {
				return true;
			}
		}
		return false;
	}
	
	public void setFloorCalled(int floor, int direction) {
		if (direction == 0) {
			setFloorCalled(floor, -1);
			setFloorCalled(floor, 1);
			return;
		}
		x1000[x20] = floor | ((direction == 1)? 0x80 : 0x00);
		x20 = incrementAddress(x20);
	}

	public int getNextDirection(int location, int currentDirection) {
		if (Main.fireCall() != -1) {
			return Main.fireCall()*5 < location ? -1 : 1;
		}

		if (currentDirection == -1 || currentDirection == 0) {
			if (destinationBelow(location, -1)) {
				return -1;
			}
			else if (destinationAbove(location, -1)) {
				return 1;
			}
		}
		else if (currentDirection == 1 || currentDirection == 0) {
			if(destinationAbove(location, 1)) {
				return 1;
			}
			else if (destinationBelow(location, 1)) {
				return -1;
			}
		}
		return 0; // No floors in the queue
	}

	public boolean destinationAbove(int location, int currentDirection) {
		if (location == 20) {
			return false;
		}
		for (int i = 0; i < x1000.length; i++) {
			if (x1000[i] != -1 && location < getFloorNum(x1000[i]) * 5) {
				return true;
			}
		}
		return false;
	}

	public boolean destinationBelow(int location, int currentDirection) {
		if (location == 0) {
			return false;
		}
		for (int i = 0; i < x1000.length; i++) {
			if (x1000[i] != -1 && location > getFloorNum(x1000[i]) * 5) {
				return true;
			}
		}
		return false;
	}
	
	public void clearFloor(int floor) {
		Main.floors[floor].fireKeyVisited = true;
		for (int i = 0; i < x1000.length; i++) {
			if (floor == getFloorNum(x1000[i])) {
				x1000[i] = -1;
				break;
			}
		}
	}

	public int incrementAddress(int addr) {
		return addr == 7 ? 0 : (addr + 1);
	}
	
	public void setLights(int floor, int direction) {
		if (direction == 0) {
			return;
		}
		int offset = direction == 1 ? 4 : 0;
		x19 = 0x01 << floor + offset;
	}

	public void reset() {
		x19 = 0;
		for (int i = 0; i < x1000.length; i++) {
			x1000[i] = -1;
		}
		x20 = 0;

		// Go to default floor for current time
		if (Main.time < 50400) {
			setFloorCalled(1, 0);
		}
		else {
			setFloorCalled(2, 0);
		}
		
	}

	public boolean isRequest() {
		for(int i = 0; i < x1000.length; i++) {
			if (x1000[i] != -1) {
				return true;
			}
		}
		return false;
	}

	public Object[] log() {
		return new Object[]{x19, x20, Arrays.copyOf(x1000, x1000.length)};
	}

	public int[] logArray() {
		return Arrays.copyOf(x1000, x1000.length);
	}

}
