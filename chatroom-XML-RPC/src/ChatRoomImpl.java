package src;


import org.apache.xmlrpc.WebServer;
import java.util.Vector;

public class ChatRoomImpl implements ChatRoom {
    private Vector<String> messages = new Vector<>();
    private Vector<String> users = new Vector<>();

    @Override
    public synchronized void subscribe(String pseudo) {
        users.add(pseudo);
        postMessage("System", pseudo + " a rejoint le chat.");
    }

    @Override
    public synchronized void unsubscribe(String pseudo) {
        users.remove(pseudo);
        postMessage("System", pseudo + " a quitté le chat.");
    }

    @Override
    public synchronized void postMessage(String pseudo, String message) {
        messages.add(pseudo + ": " + message);
    }

    @Override
    public synchronized Vector<String> getMessages() {
        return new Vector<>(messages);
    }

    public static void main(String[] args) {
        try {
            WebServer server = new WebServer(8080);
            server.addHandler("ChatRoom", new ChatRoomImpl());
            server.start();
            System.out.println("Le serveur du Chat Room tourne sur le port 8080.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
