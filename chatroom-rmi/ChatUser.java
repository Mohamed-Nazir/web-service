import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatUser extends Remote {
    void retrieveMessage(String message) throws RemoteException;
}
