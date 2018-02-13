#include <cstdlib>
#include <iostream>
#include <ctime>

using namespace std;

public class RegisterBank{
    private static int[] x19 = new bool[8]; //Up/down indicator for buttons
    private static int[] x20 = new int[3]; //Stores destination floors
    private static Elevator elev;
    
    public static void init(Elevator elevator){
        elev = elevator;
    }
    
    public void getNextFloor(){
        return x20[0];
    }
    
    public void setFloor(int floorNum){
        //If going up
        if (elev.getUpDown && floorNum > currentFloor){
            for (int i = 0; i < x20.length; i++){
                if (x20[i] > floorNum){
                    x20[i + 1] = x20[i];
                    x20[i] = floorNum;
                    break;
                }
            }
        }
        //If going down
        else if (!elev.getUpDown && floorNum < currentFloor){
            for (int i = 3; i >= 0; i--){
                if (x20[i] < floorNum){
                    x20[i - 1] = x20[i];
                    x20[i] = floorNum;
                    break;
                }
            }
        }
    }
    
    public void floorReached(){
        deluminateButton(x20[0]);
        
        for (int i = 0; i < x20.length - 1; i++){
            x20[i] = x20[i + 1]
        }
        
        x20[x20.length] = -1;
        
        if (elev.isKey())
            elev.setKey(false);
    }
    
    public void illuminateButton(int index){
        x19[index] = true;
    }
    
    public void deluminateButton(int index){
        x19[index] = false;
    }
}