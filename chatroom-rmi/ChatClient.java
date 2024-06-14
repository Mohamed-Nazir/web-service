public class ChatClient {
    public static void main(String[] args) {
        try {
            // Rechercher le serveur de chat dans le registre RMI
            ChatRoom chatRoom = (ChatRoom) java.rmi.Naming.lookup("rmi://localhost/ChatRoom");
            
            // Initialiser l'interface utilisateur et se connecter au serveur de chat
            new ChatUserImpl(chatRoom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
