import java.util.Random;

public class SpaceVehicle implements Runnable{
    private final FuelStation station;
    private final int id;
    private final int nitrogenCapacity;
    private final int quantomCapacity;
    private int maxResupply;
    private final Random random = new Random();

    public SpaceVehicle(FuelStation station,int id, int N, int Q, int S){
        this.station = station;
        this.id = id;
        this.nitrogenCapacity = N;
        this.quantomCapacity = Q;
        this.maxResupply = S;
    }

    protected FuelStation getFuelStation(){
        return this.station;
    }
    protected Random getRandom(){
        return this.random;
    }
    protected int getMaxResupply(){
        return this.maxResupply;
    }
    protected void decrementMaxResupply(){
        this.maxResupply -= 1;
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

    @Override
    public void run() {
        while (maxResupply > 0){
            sleepLong();
            FuelRequest request = createFuelRequest();
            try {
                station.requestDockingStation(request);
                sleepShort();
                System.out.printf("Vehicle %d refuel %d l nitrogen and %d l quantum\n",id, request.getNitroAmount(), request.getQuantumAmount());
                station.leaveDockingStation();
            } catch (Exception e) {
            }
            maxResupply -= 1;
        }
    }


}
