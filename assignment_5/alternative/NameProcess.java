package alternative;
import static java.lang.Thread.sleep;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import original.NameCompInterface;
import original.NameGenerator;

public class NameProcess extends UnicastRemoteObject implements NameCompInterface{
    String myName;
    String[] names;
    private ArrayList<Object> common;
    Registry registry = null;
    boolean isDone = false;
    ArrayList<NameCompInterface> remoteProcesses = null;

    protected NameProcess(String name, int size, Registry registry) throws RemoteException {
        super();
        this.myName = name;
        this.names = new String[size];
        this.common = new ArrayList<>();
        generateNames(size);
        if (registry != null){
            this.registry = registry;
        }
    }
    protected Registry getRegistry() {
        return registry;
    }
    private void generateNames(int size) {
        for (int i = 0; i < size; i++) {
            this.names[i] = NameGenerator.generateName();
            while (!uniqueName(this.names[i], i)) {
                this.names[i] = NameGenerator.generateName();
            }
        }
    }
    
    private boolean uniqueName(String name, int index) {
        for (int i = 0; i < index; i++) {
            if (this.names[i].equals(name)) {
                return false;
            }
        }
        return true;
    }
    
    
    /* Needed in original solution */
    @Override
    public String getName(int id) throws RemoteException {
        if (id < 0 || id >= names.length ){
            throw new RemoteException("list completed");
        }
        if (common.contains(names[id])) {
            return null;
        }
        return names[id];
    }


    @Override
    public synchronized boolean checkName(String name) throws RemoteException {
        if (common.contains(name))
            return true;
        
        boolean found = false;
        for (String name1 : names) {
            if (name1.equals(name)) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public synchronized void notifyCommon(String name) throws RemoteException {
        if(!common.contains(name))
            this.common.add(name);
    }

    @Override
    public synchronized void printCommon() throws RemoteException {
        System.out.println("Proc " + myName + " found " + common.size() + " names:");
        System.out.println(common + "\n");
    }

    protected void registerMe() throws RemoteException {
        if(this.registry == null)
            this.registry = LocateRegistry.getRegistry(1099);
        registry.rebind(this.myName, this);
    }

    protected void collectOtherRemotes() throws RemoteException{
        String[] remoteNames = getProcNames();
        this.remoteProcesses = new ArrayList<>();
        for(String remoteName : remoteNames){
            while(true){
                try {
                    NameCompInterface proc = (NameCompInterface) this.registry.lookup(remoteName);
                    remoteProcesses.add(proc);
                    break;
                } catch (NotBoundException e) {
                    System.out.println("All registrys not bound");
                    try {
                        sleep(1000);
                    } catch (InterruptedException e1) {
                        
                    }
                }
            }

        }
        System.out.println("Registrys bound");
    }

    protected void findCommonNames() throws RemoteException {
        registerMe();
        collectOtherRemotes();

        int i = 0;
        if(remoteProcesses == null){
            System.exit(1);
        }
        while(true){
            int found = 0;
            String name;
            try {
                name = this.getName(i++);
                
            } catch (RemoteException e) {
                break;
            }

            if (name == null) {
                continue;
            }
         
            for (NameCompInterface remoteProc : remoteProcesses) {
                if (remoteProc.checkName(name) == false){
                    break;
                }
                found++;
            }
            if (found == remoteProcesses.size()) {
                //System.out.println("Found bool:"+ found + ", total sofar:"+common.size());
                this.notifyCommon(name);
                for (NameCompInterface remoteProc : remoteProcesses){
                    remoteProc.notifyCommon(name);
                }
            }
        }

        this.isDone = true;
    }

    private String[] getProcNames(){
        String[] other = new String[2];

        switch (this.myName) {
            case "F": {
                other[0] = "G";
                other[1] = "H";
                break;
            }
            case "G": {
                other[0] = "H";
                other[1] = "F";
                break;
            }
            case "H": {
                other[0] = "F";
                other[1] = "G";
                break;
            }
            default: System.exit(1);
        }

        return other;
    }


    protected void waitForOthers() throws RemoteException{
        while (true) { 
            int done = 0;
            for (NameCompInterface remoteProc : remoteProcesses) {
                if (remoteProc.isDone() == true){
                    done ++;
                }
            }
            if(done == remoteProcesses.size())
                break;

        }
    }

    @Override
    public synchronized boolean isDone() throws RemoteException {
        return isDone;
    }

    
}
