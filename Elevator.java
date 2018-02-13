
public class Elevator{
    private RegisterBank register;
    private int currentFloor;
    private int timeSecs = 0;
    private int floorSecs = 0;
    private int doorSecs = 0;
    private int irSecs = 0;
    private boolean sound = false;
    private boolean doors = false; //True when open; false when closed
    private boolean ir = false;
    private boolean fireFighter = false;
    private boolean key = false;
    private boolean doorOpen = false;
    private boolean doorClose = false;
    private boolean upDown = true; //Determines if elevator is going up or down; up if true, down if false
    
    public Elevator(){
        currentFloor = 0;
        register = new RegisterBank(this);
    }
    
    public void controller(){
        while (true){
            while (key && register.getNextFloor() != -1)
                move();
            
            //If the doors are open, close them
            if (doors && !fireFighter){
                do {
                    //sleep(2);
                } while (!ir);
                
                closeDoors();
            }
            else if (doors && fireFighter){
                while (!doorClose)
                    //sleep(1);
                    
                closeDoors();
            }
            
            //If there is another floor call in memory, go to that floor
            if (register.getNextFloor() != -1)
                move();
            else
                selectFloor();
                
            timeSecs++;
        }
    }
    
    public void move(){
        //sleep(5); //Wait for five seconds while the elevator reaches the floor
        
        int nextFloor = register.getNextFloor();
        
        if (floorSecs == 5){
            floorSecs = 0;
        
            if (currentFloor < nextFloor){
                currentFloor++;
                if (currentFloor == nextFloor){
                    register.floorReached();
                    //sleep(1); //Wait one second while the doors latch
                    if (!fireFighter)
                        openDoors(); //Open the doors
                    else{
                        while (!doorOpen)
                            //sleep(1);
                    
                        openDoors();
                    }
                }
            }
            else if (currentFloor > nextFloor){
                currentFloor--;
                if (currentFloor == register.getNextFloor()){
                    register.floorReached();
                    //sleep(1); //Wait one second while the doors latch
                    if (!fireFighter)
                        openDoors(); //Open the doors
                    else{
                        while (!doorOpen)
                            //sleep(1);
                    
                        openDoors();
                    }
                }
            }
        }
        else
            floorSecs++;
    }
    
    public void openDoors(){
        //sleep(1); //Wait one second
        sound = true; //Ding the door sound
        doors = true; //Open the doors
        sound = false; //Stop dinging
        ir = true; //Turn on the IR signal
    }
    
    public void closeDoors(){
        //sleep(1); //Wait one second
        doors = false;
    }
    
    public void selectFloor(){
        int newFloor = -1;
        
        //Create new thread and have it take in input while this thread waits for notification. If 30 seconds pass and this thread isn't notified, kill the input thread and move on, resetting to floor 1
        
        
        
        //Have third thread running to count seconds continuously while this thread executes.
        
        
        register.setFloor(newFloor);
    }
    
    public int getInput(){
        int floor = 0;
        
        return floor;
    }
    
    public boolean isSound() {
        return sound;
    }
    
    public boolean isDoors() {
        return doors;
    }
    
    public boolean isKey() {
        return key;
    }
    
    public boolean isDoorClose() {
        return doorClose;
    }
    
    public boolean isDoorOpen() {
        return doorOpen;
    }
    
    public int getCurrentFloor() {
        return currentFloor;
    }
    
    public boolean getUpDown() {
        return upDown;
    }
    
    public RegisterBank getRegisterBank() {
        return register;
    }
    
    public void setKey(boolean key){
        this.key = key;
    }
    
    public void setDoorClose(boolean doorClose) {
        this.doorClose = doorClose;
    }
    
    public void setDoorOpen(boolean doorOpen) {
        this.doorOpen = doorOpen;
    }
    
    public void setUpDown(boolean upDown) {
        this.upDown = upDown;
    }
}