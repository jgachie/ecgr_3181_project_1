public class Elevator {
	
	RegisterBank registers;
	int location;               // Where the elevator is in the shaft. 0, 5, 10, 15 are the floors
	int irWaitSecs;           	// Number of seconds the IR sensor has gone unbroken. Resets when irSensor is false
	int idleTime;               // Number of seconds the elevator has done nothing. At 30 decide which default floor to visit
	int nextDirection;              // Direction elevator is moving. -1, 0, 1 are down, still and up respectively

	boolean latched;            // If the elevator has latched on to the floor
	boolean doorsOpen;          // Status of doors. True if they're open, false if they're closed
	boolean doorsOpenedOnFloor; // If the doors have been open on that floor before
	boolean sound;              // If the sound is playing

	boolean irSensor;           // IR beam status. True is unbroken, false is broken
	boolean doorOpenButton;
	boolean doorCloseButton;
	boolean doorsOpenedForFire;


	
	public Elevator(int floor) {
		this.registers = new RegisterBank();

		this.location = floor * 5;
		this.irWaitSecs = 0;
		this.idleTime = 0;

		this.latched = true;
		this.doorsOpen = false;
		this.irSensor = true;
		this.doorsOpenedOnFloor = true;
		this.sound = false;
		this.doorOpenButton = false;
		this.doorCloseButton = false;
		this.doorsOpenedForFire = false;

		this.nextDirection = registers.getNextDirection(this.location, 0);
	}

	public void action() {
		sound = false;
		registers.setLights(this.closestFloor(), nextDirection);
		if (Main.fireCall() != -1) {
			this.doorsOpenedForFire = false;
			registers.clearQueue();
			if (doorsOpen) {
				doorsOpen = false;
				return;
			}
			this.move();
			nextDirection = registers.getNextDirection(this.location, this.nextDirection);
			return;
		}
		if (!doorsOpenedForFire) {
			doorsOpen = true;
			return;
		}
		if(atAFloor() && latched) {
			if (Main.fireMode()) {
				if (doorsOpen && doorCloseButton) {
					doorsOpen = false;
					doorCloseButton = false;
				} else if (!doorsOpen && doorOpenButton) {
					doorsOpen = true;
					doorOpenButton = false;
				}
				return;
			} else {
				if (doorsOpen && irWaitSecs < 2) { // Doors are open but wait time isn't up
					if (!irSensor) {
						irWaitSecs = 0;
					} else {
						irWaitSecs++;
					}
					return;
				} else if (doorsOpen) { // Doors are open an no more wait time
					doorsOpen = false;
					return;
				} else if (!doorsOpenedOnFloor) { // Doors are closed and have never been open on this floor before
					irWaitSecs = 0;
					doorsOpen = true;
					doorsOpenedOnFloor = true;
					return;
				}
			}
		}
		this.move();

		if (idleTime == 30) {
			registers.reset();
		}
		nextDirection = registers.getNextDirection(this.location, this.nextDirection);
	}
	
	public void move() {
		if (this.atAFloor() && registers.stopAtFloor(this.closestFloor(), this.nextDirection)) {	
			if (!latched) {
				registers.clearFloor(this.closestFloor(), this.nextDirection);
				latched = true;
				doorsOpenedOnFloor = false;
				sound = true;
			}	
			return;
		}

		if (nextDirection == 0) {
			return;
		}

		latched = false;

		if (nextDirection == 1) {
			location++;
		}
		else {
			location--;
		}
	}

	public int closestFloor() {
		return (int)Math.round(location / 5.0);
	}

	public boolean atAFloor() {
		return location % 5 == 0;
	}

	public Object[] log() {
		return new Object[]{location, irWaitSecs, idleTime, nextDirection, latched, doorsOpen, doorsOpenedOnFloor, sound, irSensor, doorOpenButton, doorCloseButton};
	}
	
}
