import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameCompInterface extends Remote {
    boolean checkName(String name) throws RemoteException;
    String getName(int id) throws RemoteException;
    void notifyCommon(String name) throws RemoteException;
    void printCommon() throws RemoteException;
}
