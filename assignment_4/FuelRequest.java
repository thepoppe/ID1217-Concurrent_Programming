public class FuelRequest {
    private final int nitroAmount;
    private final int quantumAmount;

    public FuelRequest(int nitrogenAmount, int quantumAmount){
        this.nitroAmount = nitrogenAmount;
        this.quantumAmount = quantumAmount;
    }

    public int getNitroAmount() {
        return nitroAmount;
    }

    public int getQuantumAmount() {
        return quantumAmount;
    }
}
