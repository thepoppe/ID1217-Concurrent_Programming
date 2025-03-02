package alternative;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class ProcessF extends NameProcess {
    
    public ProcessF(int size, Registry registry) throws RemoteException {
        super("F", size, registry);
    }
    public static void main(String[] args) {
        int size = args.length > 0 ? Integer.parseInt(args[0]) : 10;

        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            ProcessF f = new ProcessF(size, registry);
            f.findCommonNames();
            f.waitForOthers();
            f.printCommon();
            System.out.println("Closing F");
            System.exit(0);

        } catch (RemoteException ex) {
        }
    }

    
}
