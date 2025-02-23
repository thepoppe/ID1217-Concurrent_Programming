import java.util.concurrent.*;

public class FuelStation {
    private int nitrogenCapacity;;
    private int quantumCapacity;;
    private int stationCapacity;
    private int availableNitrogen; //N
    private int availableQuantum; //Q
    private int occupiedStations; //V
    private boolean simulationOver = false;
    private boolean useFifo;
    private ConcurrentLinkedQueue<Thread> vehicleQueue;
    private ConcurrentLinkedQueue<Thread> supplyQueue;
    
    public FuelStation(int maxFuelQuantity, int maxStations, boolean useFifo){
        this.nitrogenCapacity = maxFuelQuantity;
        this.quantumCapacity = maxFuelQuantity;
        this.stationCapacity = maxStations;

        this.availableNitrogen = this.nitrogenCapacity;
        this.availableQuantum = this.quantumCapacity;
        this.occupiedStations = 0;
        this.useFifo = useFifo;
        if (useFifo)
            vehicleQueue = new ConcurrentLinkedQueue<>();
            supplyQueue = new ConcurrentLinkedQueue<>();

    }

    public synchronized void setSimulationOver() {
        simulationOver = true;
        notifyAll();
    }
    public synchronized boolean isSimulationOver(){
        return this.simulationOver;
    }

    private int availableDocks(){
        return this.stationCapacity - this.occupiedStations;
    }

    private boolean isFuelAvailable(FuelRequest request){
        return request.getNitroAmount() <= this.availableNitrogen  && request.getQuantumAmount() <= this.availableQuantum;
    }
    private void adjustFuelSupplies(int nitrogenAmount, int quantumAmount){
        this.availableNitrogen += nitrogenAmount;
        this.availableQuantum += quantumAmount;
        //System.out.printf("Available: Nitro:%d Quantum:%d\n",this.availableNitrogen, this.availableQuantum);
    }

    private int toNegative(int amount){
        return -1 * amount;
    }
    
    public synchronized void requestDockingStation(FuelRequest request, int id){
        System.out.printf("Vehicle %d requested a dock. Available docks:%d\n",id, availableDocks());
        
        // fifo logic separated for presentation 
        if(useFifo){
            vehicleQueue.add(Thread.currentThread());
            while(true){
                while(vehicleQueue.peek() != Thread.currentThread() || this.occupiedStations == this.stationCapacity){
                    try {
                        wait();
                        //System.out.printf("Vehicle %d is asked to wait\n",id);
                    } catch (InterruptedException e) {
                    }
                }
                vehicleQueue.poll();
                if (isFuelAvailable(request))
                    break;
                else
                    vehicleQueue.add(Thread.currentThread());
            }
        }
        else{
            while (this.occupiedStations == this.stationCapacity || !isFuelAvailable(request)){
                try {
                    //System.out.printf("Vehicle %d is asked to wait\n",id);
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        occupiedStations += 1;
        System.out.printf("Vehicle %d enters. Available docks:%d. Nitrogen:%d Quantum:%d\n",id, availableDocks(),this.availableNitrogen, this.availableQuantum);
        adjustFuelSupplies(toNegative(request.getNitroAmount()),toNegative(request.getQuantumAmount()));
    }

    private boolean isPossibleToDeposit(FuelRequest deposit) {
        return (this.availableNitrogen + deposit.getNitroAmount() <= this.nitrogenCapacity) &&
               (this.availableQuantum + deposit.getQuantumAmount() <= this.quantumCapacity);
    }
    
    public synchronized void requestoRefuel(FuelRequest deposit, FuelRequest request, int id){
        System.out.printf("Supply Vehicle %d requested a dock. Available docks:%d\n",id, availableDocks());
        if (useFifo)
            supplyQueue.add(Thread.currentThread());
        while (this.occupiedStations == this.stationCapacity ||
             !isPossibleToDeposit(deposit)||
            (useFifo && supplyQueue.peek() != Thread.currentThread())){
            if(simulationOver){
                notifyAll();
                return;
            }
            try {
                //System.out.printf("Supply Vehicle %d is asked to wait\n",id);
                wait();
            } catch (InterruptedException e) {
            }
        }
        if (useFifo)
            supplyQueue.poll();
        occupiedStations += 1;
        System.out.printf("Supply Vehicle %d enters. Available docks:%d. Nitrogen:%d Quantum:%d\n",id, availableDocks(),this.availableNitrogen, this.availableQuantum);
        int nitrogen = deposit.getNitroAmount() - request.getNitroAmount();
        int quantum = deposit.getQuantumAmount() - request.getQuantumAmount();
        adjustFuelSupplies(nitrogen, quantum);
    }

    public synchronized  void leaveDockingStation(int id, String type) {
        occupiedStations -= 1;
        System.out.printf("%s %d left. Available docks:%d. Nitrogen:%d Quantum:%d\n",type, id, availableDocks(),this.availableNitrogen, this.availableQuantum);
        //System.out.printf("Fuelstation supplies. nitro:%d quantum:%d\n", this.availableNitrogen, this.availableQuantum);
        notifyAll();
    }

    @Override
    public String toString() {
        return String.format("FuelStation Status - Nitrogen: %d/%d, Quantum: %d/%d, Occupied Stations: %d/%d, useFifo: %s, Simulation Over: %b",
                availableNitrogen, nitrogenCapacity, availableQuantum, quantumCapacity, occupiedStations, stationCapacity, useFifo, simulationOver);
    }

}
