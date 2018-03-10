public class Elevator {
	
	RegisterBank registers;
	                            // Movements are equivalent to seconds
	int currentFloor;
	int location;               // Where the elevator is in the shaft. 0, 5, 10, 15 are the floors
	int irWaitSecs;           	// Number of seconds the IR sensor has gone unbroken. Resets when irSensor is false
	int idleTime;               // Number of seconds the elevator has done nothing. At 30 decide which default floor to visit
	int direction;              // Direction elevator is moving. -1, 0, 1 are down, still and up respectively

	boolean latched;            // If the elevator has latched on to the floor
	boolean doorsOpen;          // Status of doors. True if they're open, false if they're closed
	boolean irSensor;           // IR beam status. True is unbroken, false is broken
	boolean doorsOpenedOnFloor; // If the doors have been open on that floor before
	boolean sound;              // If the sound is playing

	boolean doorOpenButton;
	boolean doorCloseButton;


	
	public Elevator() {
		this.registers = new RegisterBank();

		this.currentFloor = 0;
		this.location = 0;
		this.irWaitSecs = 0;
		this.idleTime = 0;
		this.direction = 0 ;

		this.latched = false;
		this.doorsOpen = false;
		this.irSensor = false;
		this.doorsOpenedOnFloor = false;
		this.sound = false;

		this.doorOpenButton = false;
		this.doorCloseButton = false;
	}

	public void action() {

		if (Main.fireCall() != -1) {
			idleTime = 0;
			this.move();
			return;
		}

		if(Main.fireMode()) {
			idleTime = 0;
			if (doorsOpen && doorCloseButton) {
				doorsOpen = false;	
			}
			else if (!doorsOpen && doorOpenButton) {
				doorsOpen = true;
			}
			return;
		}
		else {
			if (doorsOpen && irWaitSecs < 2) {
				idleTime = 0;
				if(!irSensor) {
					irWaitSecs = 0;
				}
				else {
					irWaitSecs++;
				}
				return;
			}
			else if (doorsOpen) {
				idleTime = 0;
				doorsOpen = false;
				return;
			}
			else if (!doorsOpen) {
				if (!doorsOpenedOnFloor) {
					idleTime = 0;
					doorsOpen = true;
					return;
				}
			}
		}
		if (!this.move()) {
			idleTime++;
		}
	}
	
	public boolean move() {
		doorsOpenedOnFloor = false;
		registers.setLights(this.closestFloor(), direction);
		if (this.atAFloor() && registers.stopAtFloor(this.closestFloor())) {
			if (!latched) {
				latched = true;
				sound = true;
				return true;
			}

			registers.clearFloor(this.closestFloor());
			return true;
		}

		if (registers.getNextDirection(this.closestFloor(), direction) == 0) {
			return false;
		}

		latched = false;

		if (registers.getNextDirection(this.closestFloor(), direction) == 1) {
			location++;
			return true;
		}
		else {
			location--;
			return true;
		}
	}

	public int closestFloor() {
		return (int)Math.round(location / 5.0);
	}

	public boolean atAFloor() {
		return location % 5 == 0;
	}
}
