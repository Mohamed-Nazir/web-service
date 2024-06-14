package src;

import java.util.Vector;

public interface ChatRoom {
    void subscribe(String pseudo) throws Exception;
    void unsubscribe(String pseudo) throws Exception;
    void postMessage(String pseudo, String message) throws Exception;
    Vector<String> getMessages() throws Exception;
}
