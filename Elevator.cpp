#include <cstdlib>
#include <iostream>
#include <ctime>
#include <unistd.h>
#include <thread>

using namespace std;

class Elevator{
    private int currentFloor;
    private int timeSecs = 0;
    private int floorSecs = 0;
    private int doorSecs = 0;
    private int irSecs = 0;
    private bool sound = false;
    private bool doors = false; //True when open; false when closed
    private bool ir = false;
    private bool fireFighter = false;
    private bool key = false;
    private bool doorOpen = false;
    private bool doorClose = false;
    private bool upDown = true; //Determines if elevator is going up or down; up if true, down if false
    
    public Elevator(){
        currentFloor = 0;
    }
    
    public void controller(){
        while (true){
            while (key && RegisterBank.getNextFloor() != -1)
                move();
            
            //If the doors are open, close them
            if (doors && !fireFighter){
                do {
                    sleep(2);
                } while (!ir)
                
                closeDoors();
            }
            else if (doors && fireFighter){
                while (!doorClose)
                    sleep(1);
                    
                closeDoors();
            }
            
            //If there is another floor call in memory, go to that floor
            if (RegisterBank.getNextFloor() != -1)
                move();
            else
                selectFloor();
                
            timeSecs++;
        }
    }
    
    public void move(){
        //sleep(5); //Wait for five seconds while the elevator reaches the floor
        
        int nextFloor = RegisterBank.getNextFloor();
        
        if (floorSecs == 5){
            floorSecs = 0;
        
            if (currentFloor < nextFloor()){
                currentFloor++;
                if (currentFloor == nextFloor){
                    RegisterBank.floorReached();
                    sleep(1); //Wait one second while the doors latch
                    if (!fireFighter)
                        openDoors(); //Open the doors
                    else{
                        while (!doorOpen)
                            sleep(1);
                    
                        openDoors();
                    }
                }
            }
            else if (currentFloor > nextFloor){
                currentFloor--;
                if (currentFloor == RegisterBank.getNextFloor()){
                    RegisterBank.floorReached();
                    sleep(1); //Wait one second while the doors latch
                    if (!fireFighter)
                        openDoors(); //Open the doors
                    else{
                        while (!doorOpen)
                            sleep(1);
                    
                        openDoors();
                    }
                }
            }
        }
        else
            floorSecs++;
    }
    
    public void openDoors(){
        sleep(1); //Wait one second
        sound = true; //Ding the door sound
        doors = true; //Open the doors
        sound = false; //Stop dinging
        ir = true; //Turn on the IR signal
    }
    
    public void closeDoors(){
        sleep(1); //Wait one second
        doors = false;
    }
    
    public void selectFloor(){
        int newFloor = -1;
        
        //Create new thread and have it take in input while this thread waits for notification. If 30 seconds pass and this thread isn't notified, kill the input thread and move on, resetting to floor 1
        thread inputThread(getInput());
        
        
        //Have third thread running to count seconds continuously while this thread executes.
        
        
        RegisterBank.setFloor(newFloor);
    }
    
    public int getInput(){
        int floor;
        
        cin >> floor;
        
        return floor;
    }
    
    public void waitFor(Function func, int secs){
        
    }
    
    public bool isSound() {
        return sound
    }
    
    public bool isDoors() {
        return doors;
    }
    
    public bool isKey() {
        return key;
    }
    
    public bool isDoorClose() {
        return doorClose;
    }
    
    public bool isDoorOpen() {
        return doorOpen;
    }
    
    public int getCurrentFloor() {
        return currentFloor;
    }
    
    public int getUpDown() {
        return upDown;
    }
    
    public void setKey(bool key){
        this.key = key;
    }
    
    public void setDoorClose(bool doorClose) {
        this.doorClose = doorClose;
    }
    
    public void setDoorOpen(bool doorOpen) {
        this.doorOpen = doorOpen;
    }
    
    public void setUpDown(bool upDown) {
        this.upDown = upDown;
    }
}