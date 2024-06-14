// src/ChatUserImpl.java
package src;

import org.apache.xmlrpc.XmlRpcClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class ChatUserImpl {
    private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;
    private XmlRpcClient server = null;

    private JFrame window = new JFrame(this.title);
    private JTextPane txtOutput = new JTextPane();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    private JButton btnQuit = new JButton("Quitter");

    public ChatUserImpl(XmlRpcClient server) {
        this.server = server;
        this.createUI();
        this.requestPseudo();
        this.subscribe();
        new Thread(this::refreshMessages).start();
    }

    private void createUI() {
        JPanel panel = (JPanel) this.window.getContentPane();
        panel.setLayout(new BorderLayout());

        
        JScrollPane scrollPane = new JScrollPane(this.txtOutput);
        this.txtOutput.setEditable(false);
        this.txtOutput.setContentType("text/html"); 
        panel.add(scrollPane, BorderLayout.CENTER);

        // Zone de saisie des messages
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        inputPanel.add(this.txtMessage, BorderLayout.CENTER);
        inputPanel.add(this.btnSend, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        // Bouton quitter
        panel.add(this.btnQuit, BorderLayout.NORTH);

        // Paramètres de la fenêtre
        this.window.setSize(600, 400);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setVisible(true);

        // Gestionnaires d'événements
        this.btnSend.addActionListener(e -> sendMessage());
        this.txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        this.btnQuit.addActionListener(e -> quitChat());
    }

    private void sendMessage() {
        try {
            Vector<String> params = new Vector<>();
            params.add(this.pseudo);
            params.add(this.txtMessage.getText());
            server.execute("ChatRoom.postMessage", params);
            this.txtMessage.setText(""); // Effacer le champ de texte après envoi
            this.txtMessage.requestFocus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void quitChat() {
        try {
            Vector<String> params = new Vector<>();
            params.add(this.pseudo);
            server.execute("ChatRoom.unsubscribe", params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    private void requestPseudo() {
        this.pseudo = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title, JOptionPane.PLAIN_MESSAGE
        );
        if (this.pseudo == null || this.pseudo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this.window, "Pseudo invalide. Fermeture du programme.");
            System.exit(0);
        }
    }

    private void subscribe() {
        try {
            Vector<String> params = new Vector<>();
            params.add(this.pseudo);
            server.execute("ChatRoom.subscribe", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshMessages() {
        while (true) {
            try {
                Vector<String> params = new Vector<>();
                Vector<String> messages = (Vector<String>) server.execute("ChatRoom.getMessages", params);
                SwingUtilities.invokeLater(() -> displayMessages(messages));
                Thread.sleep(1000); // Rafraîchir les messages toutes les secondes
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayMessages(Vector<String> messages) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        for (String message : messages) {
            html.append(message).append("<br>");
        }
        html.append("</body></html>");
        this.txtOutput.setText(html.toString());
    }

    public static void main(String[] args) {
        try {
            XmlRpcClient server = new XmlRpcClient("http://localhost:8080/RPC2");
            new ChatUserImpl(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
