#include <cstdlib>
#include <iostream>
#include <ctime>
#include <unistd.h>

using namespace std;

class Floor{
    private static Elevator elev;
    
    public static void init(Elevator elevator){
        elev = elevator;
    }
    
    public static void callUp(int currentFloor){
        if (currentFloor == 3)
            return;
        
        RegisterBank.illuminateButton(currentFloor + 4);
        RegisterBank.setFloor(currentFloor);
    }
    
    public static void callDown(int currentFloor){
        if (currentFloor = 0)
            return;
        
        RegisterBank.illuminateButton(currentFloor);
        RegisterBank.setFloor(currentFloor);
    }
    
    public static void callKey(int currentFloor){
        elev.setKey(true);
        RegisterBank.setFloor(currentFloor);
    }
}