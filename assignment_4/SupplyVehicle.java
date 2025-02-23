public class SupplyVehicle extends Vehicle{
    int nitrogenRefill = 500;
    int quantumRefill = 500;
    public SupplyVehicle(FuelStation station, int id, int N, int Q) {
        super(station, id, N, Q, 0);
    }

    private FuelRequest createDepositRequest(){
        return new FuelRequest(this.nitrogenRefill, this.quantumRefill);
    }

    private boolean isSimulationOver(){
        return getFuelStation().isSimulationOver();
    }

    @Override
    public void run() {
        while(true){
            sleepLong();
            try{
                getFuelStation().requestoRefuel(createDepositRequest(), createFuelRequest(), getId());
            }
             catch (Exception e) {
            }
            if (isSimulationOver()){
                System.out.println("Simulation over, terminating Supply Vehicle " + this.getId());
                break;
            }
            sleepShort();
            System.out.printf("Supply Vehicle resupplied the station\n");
            leaveDockingStation();
        }
    }
}
