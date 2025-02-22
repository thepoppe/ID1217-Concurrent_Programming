public class SupplyVehicle extends SpaceVehicle{
    int nitrogenRefill = 400;
    int quantumRefill = 400;
    public SupplyVehicle(FuelStation station, int id, int N, int Q, int S) {
        super(station, id, N, Q, S);
    }

    private FuelRequest createDepositRequest(){
        return new FuelRequest(this.nitrogenRefill, this.quantumRefill);
    }

    @Override
    public void run() {
        while(getMaxResupply() > 0){
            sleepLong();
            getFuelStation().requestoRefuel(createDepositRequest(), createFuelRequest());
            sleepShort();
            System.out.printf("Supply Vehicle resupplied the station\n");
            getFuelStation().leaveDockingStation();
        }
    }
}
