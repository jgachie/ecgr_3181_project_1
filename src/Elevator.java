public class Elevator {
	
	RegisterBank registers;
	int location;               // Where the elevator is in the shaft. 0, 5, 10, 15 are the floors
	int irWaitSecs;           	// Number of seconds the IR sensor has gone unbroken. Resets when irSensor is false
	int idleTime;               // Number of seconds the elevator has done nothing. At 30 decide which default floor to visit
	int direction;              // Direction elevator is moving. -1, 0, 1 are down, still and up respectively

	boolean latched;            // If the elevator has latched on to the floor
	boolean doorsOpen;          // Status of doors. True if they're open, false if they're closed
	boolean doorsOpenedOnFloor; // If the doors have been open on that floor before
	boolean sound;              // If the sound is playing

	boolean irSensor;           // IR beam status. True is unbroken, false is broken
	boolean doorOpenButton;
	boolean doorCloseButton;


	
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

		this.direction = registers.getNextDirection(this.location, 0);
	}

	public void action() {
		sound = false;
		if (Main.fireCall() != -1) {
			registers.clearQueue();
			if (doorsOpen) {
				doorsOpen = false;
				return;
			}
			this.move();
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
				if (doorsOpen && irWaitSecs < 2) {
					if (!irSensor) {
						irWaitSecs = 0;
					} else {
						irWaitSecs++;
					}
					return;
				} else if (doorsOpen) {
					doorsOpen = false;
					return;
				} else if (!doorsOpen && !doorsOpenedOnFloor) {
					irWaitSecs = 0;
					doorsOpen = true;
					doorsOpenedOnFloor = true;
					return;
				}
			}
		}
		this.move();

		if (idleTime == 30) {
			if (Main.time < 50400) {
				registers.setFloorCalled(1, 0);
			} else {
				registers.setFloorCalled(2, 0);
			}
		}
		direction = registers.getNextDirection(this.location, direction);
	}
	
	public boolean move() {
		registers.setLights(this.closestFloor(), direction);
		if (this.atAFloor() && registers.stopAtFloor(this.closestFloor())) {
			if (!latched) {
				latched = true;
				doorsOpenedOnFloor = false;
				sound = true;
			}
			registers.clearFloor(this.closestFloor());
			return true;
		}

		if (direction == 0) {
			return false;
		}

		latched = false;

		if (direction == 1) {
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

	public Object[] log() {
		return new Object[]{location, irWaitSecs, idleTime, direction, latched, doorsOpen, doorsOpenedOnFloor, sound, irSensor, doorOpenButton, doorCloseButton};
	}
	
}
