public static void main(){
    Elevator elev = new Elevator();
    RegisterBank.init(elev);
    Floor.init(elev);
    
    elev.controller();
}