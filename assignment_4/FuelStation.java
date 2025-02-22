
import java.util.Random;


public class FuelStation {
    private int nitrogenCapacity;;
    private int quantomCapacity;;
    private int stationCapacity;
    private int availableNitrogen; //N
    private int availableQuantum; //Q
    private int occupiedStations; //V
    private Random random = new Random();
    
    public FuelStation(int N, int Q, int V){
        this.nitrogenCapacity = N;
        this.quantomCapacity = Q;
        this.stationCapacity = V;

        this.availableNitrogen = this.nitrogenCapacity;
        this.availableQuantum = this.quantomCapacity;
        this.occupiedStations = 0;
        System.out.printf("Fuelstation supplies. nitro:%d quantum:d\n",
            this.availableNitrogen, this.availableQuantum);
    }

    private boolean isFuelAvailable(FuelRequest request){
        return request.getNitroAmount() <= this.availableNitrogen  && request.getQuantumAmount() <= this.availableQuantum;
    }

    
    public synchronized void requestDockingStation(FuelRequest request){
        while (this.occupiedStations == this.stationCapacity || !isFuelAvailable(request)){
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        occupiedStations += 1;
        availableNitrogen -= request.getNitroAmount();
        availableQuantum -= request.getQuantumAmount();
    }


    private boolean isPossibleToDeposit(FuelRequest deposit) {
        return (this.availableNitrogen + deposit.getNitroAmount() <= this.nitrogenCapacity) &&
               (this.availableQuantum + deposit.getQuantumAmount() <= this.quantomCapacity);
    }
    
    public synchronized void requestoRefuel(FuelRequest deposit, FuelRequest request){
        while (this.occupiedStations == this.stationCapacity || !isPossibleToDeposit(request)){
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        occupiedStations += 1;
        System.out.printf("Before deposit - Available: Nitro:%d Quantum:%d\n",
            this.availableNitrogen, this.availableQuantum);
        availableNitrogen += (deposit.getNitroAmount() - request.getNitroAmount());
        availableQuantum += (deposit.getQuantumAmount() - request.getQuantumAmount());
        System.out.printf("After deposit - Available: Nitro:%d Quantum:%d\n",
            this.availableNitrogen, this.availableQuantum);
    }

    public synchronized  void leaveDockingStation() {
        occupiedStations -= 1;
        System.out.printf("Fuelstation supplies. nitro:%d quantum:%d\n", 
            this.availableNitrogen, this.availableQuantum);
        notifyAll();
    }


}
