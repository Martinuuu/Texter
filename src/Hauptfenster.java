import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.sql.Timestamp;


public class Hauptfenster extends JFrame implements ActionListener {
    static Database db = new Database();

    JButton loginButton;
    JPanel sendPanel;
    JPanel buttonPanel;
    JLabel anzeige;     // Anzeigeflaeche
    JButton sendButton;
    JComboBox cb;
    DefaultListModel<String> model;
    JList messageList;
    JScrollPane scrollPane;
    JTextField text;

    JPanel mainPanel;
    JPanel loginPanel;
    JPanel registerPanel;
    CardLayout cardLayout;
    JButton login;
    JPanel startPanel;
    JTextField usernameField;
    JTextField passwordField;

    JTextField registerusernameField;
    JTextField registerpasswordField;
    JButton registerButton;
    JTabbedPane tabpane;
    User usr;


    public Hauptfenster() {
        db.connect();
        FlatLightLaf.setup();
        createLoginPanel();
        createMainPanel();
        cardLayout.show(mainPanel, "login");

        refreshList();
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to be executed every 1 second
                refreshList();
            }
        });

        timer.start();
    }

    public static void main(String[] args)  // Hauptmethode, mit der gestartet wird
    {
        Hauptfenster halloTest;
        halloTest = new Hauptfenster();
        halloTest.setVisible(true);
        System.out.println("Fenster Sichtbar");
    }


    private void createLoginPanel() {
        startPanel = new JPanel();
        tabpane = new JTabbedPane
                (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );

        startPanel.setLayout(new BorderLayout());

        loginPanel = new JPanel();
        startPanel.setLayout(new FlowLayout());
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel,BoxLayout.Y_AXIS));

        JLabel loginuserLabel = new JLabel("Benutzername: ");
        JLabel loginpasswordLabel = new JLabel("Passwort: ");

        JLabel registeruserLabel = new JLabel("Benutzername: ");
        JLabel registerpasswordLabel = new JLabel("Passwort: ");
        registerusernameField = new JTextField();
        registerpasswordField = new JTextField();

        JPanel registeruserPanel = new JPanel();
        registeruserPanel.setLayout(new BoxLayout(registeruserPanel, BoxLayout.X_AXIS));

        JPanel registerpassPanel = new JPanel();
        registerpassPanel.setLayout(new BoxLayout(registerpassPanel, BoxLayout.X_AXIS));

        registerButton = new JButton("Register");
        registerButton.addActionListener(this);

        registeruserPanel.add(registeruserLabel);
        registeruserPanel.add(registerusernameField);

        registerpassPanel.add(registerpasswordLabel);
        registerpassPanel.add(registerpasswordField);

        registerPanel.add(registeruserPanel);
        registerPanel.add(registerpassPanel);

        usernameField = new JTextField();
        passwordField = new JTextField();
        loginButton = new JButton("Login");
        tabpane.addTab("Login", loginPanel);
        tabpane.addTab("Register", registerPanel);

        startPanel.add(tabpane, BorderLayout.CENTER);


        userPanel.add(loginuserLabel);
        userPanel.add(usernameField);

        passwordPanel.add(loginpasswordLabel);
        passwordPanel.add(passwordField);

        loginPanel.add(userPanel);
        loginPanel.add(passwordPanel);

        loginPanel.add(loginButton);

        loginButton.addActionListener(this);


        JLabel disclamer = new JLabel();
        disclamer.setText("<html><p style=\"width:100px\">"+"Achtung! Passwörter werden im Klartext in einer von Administratoren einsehbaren Datenbank abgespeichert! Bitte benutzt keine Passwörter die schon in benutzung sind oder private Informationen beinhalten."+"</p></html>");
        registerPanel.add(registerButton);
        registerPanel.add(disclamer);
    }



    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel();
        sendPanel = new JPanel();
        sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.X_AXIS));

        this.setSize(854, 480);
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        this.setTitle("Texter");

        anzeige = new JLabel("Texter [all] chat");
        anzeige.setFont(new Font("helvetica neue", Font.BOLD, 14));
        anzeige.setMinimumSize(anzeige.getPreferredSize());
        anzeige.setMaximumSize(anzeige.getPreferredSize());

        model = new DefaultListModel<>();
        messageList = new JList(model);


        scrollPane = new JScrollPane();
        scrollPane.setViewportView(messageList);
        messageList.setLayoutOrientation(JList.VERTICAL);


        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setPreferredSize(new Dimension(100, 29));
        sendButton.setMinimumSize(new Dimension(100, 29));
        sendButton.setMaximumSize(sendButton.getPreferredSize());

        login = new JButton("Login Page");
        login.addActionListener(this);
        login.setPreferredSize(new Dimension(100, 29));
        login.setMinimumSize(new Dimension(100, 29));
        login.setMaximumSize(login.getPreferredSize());


        buttonPanel.add(sendButton);

        text = new JTextField();

        text.setPreferredSize(new Dimension(700, 30));
        text.setMinimumSize(new Dimension(100, 30));
        text.setMaximumSize(new Dimension(999999999, 30));


        chatPanel.add(anzeige);
        //this.add(messageList);
        chatPanel.add(scrollPane);
        scrollPane.setViewportView(messageList);
        sendPanel.add(text);
        sendPanel.add(buttonPanel);
        chatPanel.add(sendPanel);
        chatPanel.add(login);

        return chatPanel;
    }

    private void createMainPanel() {
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        mainPanel.add("chat", createChatPanel());  // "chat" is the identifier for the main chat page
        mainPanel.add("login", startPanel);        // "login" is the identifier for the login page

        add(mainPanel);
    }

    void refreshList() {
        if(db.checkNewMessage()) {
            model.clear();
            db.getMessages("chat");
            for(int i = db.messagesLoaded; i <= db.messages.size() - 1; i++) {
                model.addElement(db.messages.get(i).timestamp + " - " + db.messages.get(i).username + ": " + db.messages.get(i).content);
                db.messagesLoaded++;
            }
            scrollToBottom(scrollPane);
        }
    }

    public void actionPerformed(ActionEvent origin) {
        if(origin.getSource() == this.sendButton) {
            db.sendMessage(new Message(new Timestamp(System.currentTimeMillis()), usr.username, text.getText(), false));
            text.setText("");
            refreshList();
        } else if(origin.getSource() == this.login) {
            cardLayout.show(mainPanel, "login");
        } else if(origin.getSource() == this.loginButton) {
            if(db.login(usernameField.getText(),passwordField.getText())) {
                usr = new User(usernameField.getText());
                usernameField.setText(""); passwordField.setText("");
                cardLayout.show(mainPanel,"chat");
            }
        } else if(origin.getSource() == this.registerButton) {
            if(registerusernameField.getText().isEmpty() || registerpasswordField.getText().isEmpty()) return;
            if(db.register(registerusernameField.getText(),registerpasswordField.getText())) {
                registerusernameField.setText(""); registerpasswordField.setText("");
                tabpane.setSelectedIndex(0);
            }
        }
    }

    private void scrollToBottom(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }


}
