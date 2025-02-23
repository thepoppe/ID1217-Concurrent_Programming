
import java.util.Random;
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
    private ConcurrentLinkedQueue<Thread> queue;
    
    public FuelStation(int maxNitrogen, int maxQuantum, int maxStations, boolean useFifo){
        this.nitrogenCapacity = maxNitrogen;
        this.quantumCapacity = maxQuantum;
        this.stationCapacity = maxStations;

        this.availableNitrogen = this.nitrogenCapacity;
        this.availableQuantum = this.quantumCapacity;
        this.occupiedStations = 0;
        this.useFifo = useFifo;
        if (useFifo)
            queue = new ConcurrentLinkedQueue<>();

    }

    public synchronized void setSimulationOver() {
        simulationOver = true;
        notifyAll();
    }
    public synchronized boolean isSimulationOver(){
        return this.simulationOver;
    }

    private boolean isFuelAvailable(FuelRequest request){
        return request.getNitroAmount() <= this.availableNitrogen  && request.getQuantumAmount() <= this.availableQuantum;
    }
    private void adjustFuelSupplies(int nitrogenAmount, int quantumAmount){
        this.availableNitrogen += nitrogenAmount;
        this.availableQuantum += quantumAmount;
        System.out.printf("Available: Nitro:%d Quantum:%d\n",this.availableNitrogen, this.availableQuantum);
    }

    private int toNegative(int amount){
        return -1 * amount;
    }
    
    public synchronized void requestDockingStation(FuelRequest request, int id){
        System.out.printf("Space Vehicle %d requested a docking station\n",id);
        while (this.occupiedStations == this.stationCapacity || !isFuelAvailable(request)){
            try {
                System.out.printf("Space Vehicle %d is asked to wait\n",id);
                wait();
            } catch (InterruptedException e) {
            }
        }
        System.out.printf("Space Vehicle %d can enter the docking station, total number of used docks is %d\n",id, (this.occupiedStations));
        occupiedStations += 1;
        adjustFuelSupplies(toNegative(request.getNitroAmount()),toNegative(request.getQuantumAmount()));
    }

    private boolean isPossibleToDeposit(FuelRequest deposit) {
        return (this.availableNitrogen + deposit.getNitroAmount() <= this.nitrogenCapacity) &&
               (this.availableQuantum + deposit.getQuantumAmount() <= this.quantumCapacity);
    }
    
    public synchronized void requestoRefuel(FuelRequest deposit, FuelRequest request, int id){
        System.out.printf("Supply Vehicle %d requested a docking station\n",id);
        while (this.occupiedStations == this.stationCapacity || !isPossibleToDeposit(deposit)){
            if(simulationOver){
                notifyAll();
                return;
            }
            try {
                System.out.printf("Supply Vehicle %d is asked to wait\n",id);
                wait();
            } catch (InterruptedException e) {
            }
        }
        System.out.printf("Supply Vehicle %d can enter the docking station, total number of used docks is %d\n",id, (this.occupiedStations));
        occupiedStations += 1;
        int nitrogen = deposit.getNitroAmount() - request.getNitroAmount();
        int quantum = deposit.getQuantumAmount() - request.getQuantumAmount();
        adjustFuelSupplies(nitrogen, quantum);
    }

    public synchronized  void leaveDockingStation(int id, String type) {
        occupiedStations -= 1;
        System.out.printf("%s %d left the docking station\n",type, id);
        System.out.printf("Fuelstation supplies. nitro:%d quantum:%d\n", 
            this.availableNitrogen, this.availableQuantum);
        notifyAll();
    }

    @Override
    public String toString() {
        return String.format("FuelStation Status - Nitrogen: %d/%d, Quantum: %d/%d, Occupied Stations: %d/%d, Simulation Over: %b",
                availableNitrogen, nitrogenCapacity, availableQuantum, quantumCapacity, occupiedStations, stationCapacity, simulationOver);
    }

}
