public class Main {

	public static Elevator elev;
	public static Floor[] floors;

	public static void main(String[] args){
		floors = new Floor[4];
		for (int i = 0; i < floors.length; i++) {
			floors[i] = new Floor(i);
		}
		elev = new Elevator();
		elev.start();
	}

	public static int fireCall() {
		for (int i = 0; i < floors.length; i++) {
			if (floors[i].fireKey && !floors[i].fireKeyVisited) {
				return i;
			}
		}
		return -1;
	}

	public static boolean fireMode() {
		for (Floor floor : Main.floors) {
			if (floor.fireKey) {
				return true;
			}
		}
		return false;
	}
	
}
