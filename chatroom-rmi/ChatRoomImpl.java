import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoom {
    private Map<String, ChatUser> users;

    protected ChatRoomImpl() throws RemoteException {
        users = new HashMap<>();
    }

    @Override
    public synchronized void subscribe(ChatUser user, String pseudo) throws RemoteException {
        users.put(pseudo, user);
        postMessage("System", pseudo + " has joined the chat.");
    }

    @Override
    public synchronized void unsubscribe(String pseudo) throws RemoteException {
        users.remove(pseudo);
        postMessage("System", pseudo + " has left the chat.");
    }

    @Override
    public synchronized void postMessage(String pseudo, String message) throws RemoteException {
        String fullMessage = pseudo + ": " + message;
        for (ChatUser user : users.values()) {
            user.retrieveMessage(fullMessage);
        }
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            ChatRoomImpl server = new ChatRoomImpl();
            java.rmi.Naming.rebind("ChatRoom", server);
            System.out.println("Chat Room Server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
