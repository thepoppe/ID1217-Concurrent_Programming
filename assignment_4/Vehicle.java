import java.util.Random;

public class Vehicle implements Runnable{
    private final FuelStation station;
    private final int id;
    private final int nitrogenCapacity;
    private final int quantomCapacity;
    private int maxResupply;
    private final Random random = new Random();

    public Vehicle(FuelStation station,int id, int N, int Q, int nofRequests){
        this.station = station;
        this.id = id;
        this.nitrogenCapacity = N;
        this.quantomCapacity = Q;
        this.maxResupply = nofRequests;
    }
    protected int getId(){
        return id;
    }
    protected FuelStation getFuelStation(){
        return this.station;
    }

    protected void sleepLong(){
        try {
            Thread.sleep(1000 + random.nextInt(1000));
        } catch (InterruptedException e) {
            System.err.println("sleepLong failed");
        }
    }
    protected  void sleepShort(){
        try {
            Thread.sleep(100 + random.nextInt(200));
        } catch (InterruptedException e) {
            System.err.println("sleepshort failed");
        }
    }
    protected FuelRequest createFuelRequest(){
        int nitro = 0, quantum = 0;
        while(nitro == 0 && quantum == 0){
            if(random.nextBoolean())
                nitro = random.nextInt(this.nitrogenCapacity);
            if(random.nextBoolean())
                quantum = random.nextInt(this.quantomCapacity);
        }
        
        return new FuelRequest(nitro, quantum);
    }

    protected void leaveDockingStation(){
        station.leaveDockingStation(id, this.getClass().getName());
    }

    @Override
    public void run() {
        while (maxResupply > 0){
            sleepLong();
            FuelRequest request = createFuelRequest();
            try {
                station.requestDockingStation(request, id);
            } catch (Exception e) {
            }
            sleepShort();
            leaveDockingStation();
            maxResupply -= 1;
        }
    }

}
