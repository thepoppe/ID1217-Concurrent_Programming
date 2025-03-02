package original;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Client <ProcessID: F | G | H>");
            System.exit(1);
        }
        String processId = args[0];
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            NameCompInterface procF = (NameCompInterface) registry.lookup("F");
            NameCompInterface procG = (NameCompInterface) registry.lookup("G");
            NameCompInterface procH = (NameCompInterface) registry.lookup("H");

            NameCompInterface self = null;
            NameCompInterface[] others = new NameCompInterface[2];
            if (processId.equals("F")) {
                self = procF;
                others[0] = procG;
                others[1] = procH;
            } else if (processId.equals("G")) {
                self = procG;
                others[0] = procF;
                others[1] = procH;
            } else if (processId.equals("H")) {
                self = procH;
                others[0] = procF;
                others[1] = procG;
            } else {
                System.out.println("Invalid Process ID. Use F, G, or H.");
                System.exit(1);
            }
            
            System.out.println("Process " + processId + " started.");
            int i = 0;
            while (true){
                try {
                    int found = 0;
                    String name = self.getName(i++);
                    if (name == null) {
                        continue;
                    }
                    for (NameCompInterface remoteProc : others) {
                        if (remoteProc.checkName(name) == false){
                            break;
                        }
                        found++;
                    }
                    if (found == others.length) {
                        self.notifyCommon(name);
                        for (NameCompInterface remoteProc : others){
                            remoteProc.notifyCommon(name);
                        }
                    }
                    
                } catch (RemoteException e) {
                    System.out.println("Iterated over the entire Remote Name List");
                    break;
                }
            }


            verifyCommonNames(self, others);


            System.out.println("Process " + processId + " finished.");
            
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Client failed");
        }
    }   

    private static void verifyCommonNames(NameCompInterface self, NameCompInterface[] others) {
        try {
            self.printCommon();
            for(NameCompInterface proc : others){
                proc.printCommon();
            }
            
        } catch (RemoteException e) {
            System.err.println("Could not print the lists");
        }
    }
}
