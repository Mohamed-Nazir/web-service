import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatUserImpl extends UnicastRemoteObject implements ChatUser {
    private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;
    private ChatRoom chatRoom = null;

    private JFrame window = new JFrame(this.title);
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");

    public ChatUserImpl(ChatRoom chatRoom) throws RemoteException {
        this.chatRoom = chatRoom;
        this.createIHM();
        this.requestPseudo();
        this.chatRoom.subscribe(this, this.pseudo);
    }

    public void createIHM() {
        JPanel panel = (JPanel)this.window.getContentPane();
        JScrollPane sclPane = new JScrollPane(txtOutput);
        panel.add(sclPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(this.btnSend, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                window_windowClosing(e);
            }
        });
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSend_actionPerformed(e);
            }
        });
        txtMessage.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == '\n')
                    btnSend_actionPerformed(null);
            }
        });

        this.txtOutput.setBackground(new Color(220, 220, 220));
        this.txtOutput.setEditable(false);
        this.window.setSize(500, 400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }

    public void requestPseudo() {
        this.pseudo = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title, JOptionPane.OK_OPTION
        );
        if (this.pseudo == null) System.exit(0);
    }

    public void window_windowClosing(WindowEvent e) {
        try {
            chatRoom.unsubscribe(this.pseudo);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        System.exit(-1);
    }

    public void btnSend_actionPerformed(ActionEvent e) {
        try {
            chatRoom.postMessage(this.pseudo, this.txtMessage.getText());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        this.txtMessage.setText("");
        this.txtMessage.requestFocus();
    }

    @Override
    public void retrieveMessage(String message) throws RemoteException {
        this.txtOutput.append(message + "\n");
    }

    public static void main(String[] args) {
        try {
            ChatRoom chatRoom = (ChatRoom) java.rmi.Naming.lookup("rmi://localhost/ChatRoom");
            new ChatUserImpl(chatRoom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
