import javax.swing.*;
import java.awt.*;
import java.awt.event.*; // awt = abstract window toolkit
import java.sql.SQLException;
import java.sql.Timestamp;


public class Hauptfenster extends JFrame implements ActionListener {
    JPanel sendPanel;
    JPanel buttonPanel;
    JLabel anzeige;     // Anzeigeflaeche
    JButton sendButton;
    DefaultListModel<String> model;
    JList messageList;
    JScrollPane scrollPane;
    JTextField text;
    static Database db = new Database();


    public Hauptfenster() {

        db.connect();

        sendPanel = new JPanel();
        sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.X_AXIS));

        this.setSize(854, 480);
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
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


        refreshList();

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setPreferredSize(new Dimension(100, 29));
        sendButton.setMinimumSize(new Dimension(100, 29));
        sendButton.setMaximumSize(sendButton.getPreferredSize());

        buttonPanel.add(sendButton);

        text = new JTextField();

        text.setPreferredSize(new Dimension(700, 30));
        text.setMinimumSize(new Dimension(100, 30));
        text.setMaximumSize(text.getPreferredSize());

        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to be executed every 2 seconds
                refreshList();
            }
        });


        this.add(anzeige);
        //this.add(messageList);
        this.add(scrollPane);
        scrollPane.setViewportView(messageList);
        sendPanel.add(text);
        sendPanel.add(buttonPanel);
        this.add(sendPanel);
        timer.start();

    }


    void refreshList() {
        model.clear();
        db.getMessages("chat");
        for(int i = db.messagesLoaded; i <= db.messages.size() - 1; i++) {
            model.addElement(db.messages.get(i).timestamp + " - " + db.messages.get(i).username + ": " + db.messages.get(i).content);
            db.messagesLoaded++;
        }
        scrollToBottom(scrollPane);
    }


    public static void main(String[] args)  // Hauptmethode, mit der gestartet wird
    {
        Hauptfenster halloTest;
        halloTest = new Hauptfenster();
        halloTest.setVisible(true);
        System.out.println("Fenster Sichtbar");
    }

    public void actionPerformed(ActionEvent ausloeser) {
        if(ausloeser.getSource() == this.sendButton) {
            db.sendMessage(new Message(new Timestamp(System.currentTimeMillis()), "Martinuuu", text.getText()));
            refreshList();
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
