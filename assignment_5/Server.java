import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    
    public static void main(String[] args) {
        int size = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        System.out.println("Server starting with size " + size);
        try {
            NameComp F = new NameComp(size);
            NameComp G = new NameComp(size);
            NameComp H = new NameComp(size);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("F", F);
            registry.rebind("G", G);
            registry.rebind("H", H);
        

            System.out.printf("%d names are common to all three processes.\n", countCorrectNumberOfNames(F.getNames(), G.getNames(), H.getNames()));
            System.out.println("Server started.");
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static int countCorrectNumberOfNames(String[] namesF, String[] namesG, String[] namesH) {
        int count = 0;
        for (String name : namesF) {
            for (String name2 : namesG) {
                if (name.equals(name2)) {
                    for (String name3 : namesH) {
                        if (name.equals(name3)) {
                            count++;
                        }
                    }
                }
            } 
        }
        return count;
    }
}
