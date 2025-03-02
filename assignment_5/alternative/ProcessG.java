package alternative;

import java.rmi.RemoteException;

public class ProcessG extends NameProcess{
        public ProcessG( int size) throws RemoteException {
        super("G", size, null);
    }
    public static void main(String[] args) {
        int size = args.length > 0 ? Integer.parseInt(args[0]) : 10;

        try {
            ProcessG proc = new ProcessG(size);
            proc.findCommonNames();
            proc.waitForOthers();
            proc.printCommon();
            System.out.println("Closing G");
            System.exit(0);


        } catch (RemoteException ex) {
        }
    }
    
}
