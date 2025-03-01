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
            String[] commonNames = new String[self.getSize()];
            int nameCount = 0;
            for (int i = 0; i < commonNames.length; i++) {
                int found = 0;
                String name = self.getName(i);
                for (NameCompInterface remoteProc : others) {
                    if (remoteProc.checkName(name) == false){
                        break;
                    };
                    found++;
                }
                if (found == 2) {
                    commonNames[nameCount++] = name;
                }
            }
            System.out.println("Common names:");
            for (int i = 0; i < nameCount; i++) {
                if (i == nameCount - 1) System.out.println(commonNames[i]);
                else System.out.print(commonNames[i]+", ");
            }
            System.out.println("Number of common names: " + nameCount);
            System.out.println("Process " + processId + " finished.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
