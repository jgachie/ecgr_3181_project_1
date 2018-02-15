public class Main{

    public static void main(String[] args){
        Elevator elev = new Elevator();
        Floor.init(elev);
        
        elev.controller();
    }
}