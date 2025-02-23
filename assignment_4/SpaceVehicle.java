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

    protected int getId(){
        return id;
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


    @Override
    public String toString() {
        return "SpaceVehicle{" +
                "id=" + id +
                ", nitrogenCapacity=" + nitrogenCapacity +
                ", quantomCapacity=" + quantomCapacity +
                ", maxResupply=" + maxResupply +
                '}';
    }

}
