package alternative;

import java.rmi.RemoteException;

public class ProcessH extends NameProcess{
        public ProcessH( int size) throws RemoteException {
        super("H", size, null);
    }
    public static void main(String[] args) {
        int size = args.length > 0 ? Integer.parseInt(args[0]) : 10;

        try {
            ProcessH proc = new ProcessH(size);
            proc.findCommonNames();
            proc.waitForOthers();
            proc.printCommon();
            System.out.println("Closing H");
            System.exit(0);

        } catch (RemoteException ex) {
        }
    }
    
}
