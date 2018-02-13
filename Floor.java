
public class Floor{
    private static Elevator elev;
    
    public static void init(Elevator elevator){
        elev = elevator;
    }
    
    public static void callUp(int currentFloor){
        if (currentFloor == 3)
            return;
        
        elev.getRegisterBank().illuminateButton(currentFloor + 4);
        elev.getRegisterBank().setFloor(currentFloor);
    }
    
    public static void callDown(int currentFloor){
        if (currentFloor == 0)
            return;
        
        elev.getRegisterBank().illuminateButton(currentFloor);
        elev.getRegisterBank().setFloor(currentFloor);
    }
    
    public static void callKey(int currentFloor){
        elev.setKey(true);
        elev.getRegisterBank().setFloor(currentFloor);
    }
}