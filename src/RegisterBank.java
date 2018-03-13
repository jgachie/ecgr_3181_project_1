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
		num = num & ~(0xC0);
		return num;
	}

	public int getCallDir(int num) {
		return (num & 0x80) >> 7;
	}

	public boolean internalDestination(int num) {
		return (num & 0x90) >> 7 == 1;
	}
	
	public boolean stopAtFloor(int floor, int direction) {
		if (Main.floors[floor].fireKey && !Main.floors[floor].fireKeyVisited) {
			return true;
		}

		for (int i = 0; i < x1000.length; i++) {
			if (floor != getFloorNum(x1000[i]) ) {
				continue;
			}
			if (internalDestination(x1000[i]) || direction == getCallDir(x1000[i]) || direction == 0) {
				return true;
			}
		}
		return false;
	}
	
	public void setFloorCalled(int floor, int direction) {
		int dirBits = ((direction == 1)? 0x80 : ((direction == 0)?0xC0:0x00));
		x1000[x20] = floor | dirBits;
		x20 = incrementAddress(x20);
	}

	public int getNextDirection(int location, int currentDirection) {
		if (Main.fireCall() != -1) {
			if (Main.fireCall()*5 < location) {
				return -1;
			}
			else if (Main.fireCall()*5 > location) {
				return 1;
			}
			else {
				return 0;
			}
		}

		if (currentDirection == -1 || currentDirection == 0) {
			if (destinationBelow(location, -1)) {
				return -1;
			}
			else if (destinationAbove(location, -1)) {
				return 1;
			}
		}
		else if (currentDirection == 1) {
			if (destinationAbove(location, 1)) {
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
	
	public void clearFloor(int floor, int direction) {
		Main.floors[floor].fireKeyVisited = true;
		for (int i = 0; i < x1000.length; i++) {
			if (floor == getFloorNum(x1000[i]) && (getCallDir(x1000[i]) == direction || internalDestination(x1000[i]))) {
				x1000[i] = -1;
			}
		}
	}

	public int incrementAddress(int addr) {
		return addr == 7 ? 0 : (addr + 1);
	}
	
	public void setLights(int floor, int direction) {
		if (direction == 0) {
			x19 = 0x11 << floor;
			return;
		}
		int offset = direction == 1 ? 4 : 0;
		x19 = 0x01 << floor + offset;
	}

	public void clearQueue() {
		for (int i = 0; i < x1000.length; i++) {
			x1000[i] = -1;
		}
		x20 = 0;
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
		for (int i = 0; i < x1000.length; i++) {
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
