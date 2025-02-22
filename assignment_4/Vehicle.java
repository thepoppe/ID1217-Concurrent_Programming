import java.util.Random;

public class Vehicle implements Runnable{
    private FuelStation station;
    private int id;
    private int nitrogenCapacity;
    private int quantomCapacity;
    private int maxResupply;
    private Random random = new Random();
    
    public Vehicle(FuelStation station,int id, int N, int Q, int S){
        this.station = station;
        this.id = id;
        this.nitrogenCapacity = N;
        this.quantomCapacity = Q;
        this.maxResupply = S;
    }
    public int getMaxResupply() {
        return maxResupply;
    }

    protected void decreaseMaxResupply() {
        maxResupply--;
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
    
    protected void sleep(int min, int max){
        try {
            Thread.sleep(min + random.nextInt(max));
        } catch (InterruptedException e) {
            System.err.println("sleepLong failed");
        }
    }
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
