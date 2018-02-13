#include <cstdlib>
#include <iostream>
#include <ctime>
#include <unistd.h>

using namespace std;

int main(){
    Elevator elev = new Elevator();
    RegisterBank.init(elev);
    Floor.init(elev);
    
    elev.controller();
    
    return 0;
}