import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NameComp extends UnicastRemoteObject implements NameCompInterface {
    String[] names;
    String[] commonNames;
    NameComp(int size) throws RemoteException {
        super();
        this.names = new String[size];
        this.commonNames = new String[size];
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
    public String getName(int id) throws RemoteException {
       return names[id];
    }


    @Override
    public boolean checkName(String name) throws RemoteException {
        boolean found = false;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public int getSize() throws RemoteException {
        return names.length;
    }


}
