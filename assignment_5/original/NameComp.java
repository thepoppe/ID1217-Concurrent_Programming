package original;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class NameComp extends UnicastRemoteObject implements NameCompInterface {
    String myName;
    String[] names;
    ArrayList<String> common;

    NameComp(String name, int size) throws RemoteException {
        super();
        this.myName = name;
        this.names = new String[size];
        this.common = new ArrayList<>();
        generateNames(size);
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
    protected String[] getNames() {
        return this.names;
    }

    
    @Override
    public synchronized String getName(int id) throws RemoteException {
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
        if (!common.contains(name))
            this.common.add(name);
    }

    @Override
    public synchronized void printCommon() throws RemoteException {
        System.out.println("Proc " + myName + " found:");
        System.out.println(common + "\n");
    }

    /* Used in altenative solution */
    @Override
    public boolean isDone() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
