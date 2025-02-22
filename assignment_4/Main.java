/*
Consider a future fuel station in space that supplies nitrogen and quantum fluid. 
Assume that the station can handle V space vehicles in parallel (simultaneously) 
and maximum storage for N liters of nitrogen and Q liters of quantum fluid. 

When a vehicle arrives at the station, it requests the needed amounts of fuel of 
the two different types or only one of the types. If there is not enough fuel of 
either type, the vehicle has to wait without blocking other vehicles. 
Special supply vehicles deliver fuel in fixed quantities that far exceed the fuel 
tank capacity of any vehicle. 

When a supply vehicle arrives at the station, it delays until there is enough space 
to deposit the fuel delivered. To travel back, the supply vehicles also request a 
certain amount of fuel of the two different types or one of the types, just like an 
ordinary vehicle, not necessarily the type it supplies.

Develop and implement a multithreaded application (in Java or C++) that simulates the 
actions of the ordinary space vehicles and supply vehicles represented as concurrent 
threads.  Your simulation program should implement all "real world" concurrency in the
actions of the vehicles as described in the above scenario. 
Represent the fuel space station as a monitor (a synchronized object) containing a set 
of counters that define the amounts of available fuel of different types (at most  N 
liters of nitrogen and most Q liters of quantum fluid) and the number of free docking 
places (at most V places). The monitor should be used to control access to and station use. 
Develop and implement the monitor's methods. The vehicle threads call the monitor methods 
to request and release access to the station to get or/and deposit fuel. 

In your simulation program, assume that each vehicle arrives at the station periodically 
to get/supply fuel. Have the vehicles sleep (pause) for a random amount of time between 
arriving at the station to simulate the time it takes to travel in space, and have the 
vehicles sleep (pause) for a smaller random amount of time to simulate the time it takes 
to get/supply the fuel at the station. Stop the simulation after each vehicle has arrived 
at the station the given number of times. Your program should print a trace of the 
interesting events in the program.

Is your solution fair? Explain when presenting homework.
 */
public class Main{
    public static void main(String[] args){
        FuelStation station = new FuelStation(1000, 1000, 1);
        int nofVehicles = 5;
        Thread[] threads = new Thread[nofVehicles];
        for (int i = 0; i < nofVehicles; i++){
            threads[i] = new Thread(new SpaceVehicle(station, i, 100, 100, 5));
        }

        Thread supplyVehicle = new Thread(new SupplyVehicle(station, 1, 200, 200, 4));
        for (int i = 0; i < nofVehicles; i++) {
            threads[i].start();   
        }

        supplyVehicle.start();
        
        
        try {
            for (int i = 0; i < nofVehicles; i++) {
                threads[i].join();   
            }
            supplyVehicle.interrupt();
            supplyVehicle.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Main thread finished execution.");
    }
}
