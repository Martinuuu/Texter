import javax.swing.*;
import java.awt.*;
import java.awt.event.*; // awt = abstract window toolkit
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Hauptfenster extends JFrame implements ActionListener {
    JLabel anzeige;     // Anzeigeflaeche
    JButton sendButton;
    DefaultListModel<String> model;
    JList messageList;
    JScrollPane scrollPane;
    JTextField text;
    static Database db = new Database();


    public Hauptfenster() {

        db.connect();

        this.setSize(854, 480);
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        this.setTitle("Meine erste GUI");

        anzeige = new JLabel("#HalloWelt");
        anzeige.setBounds(15, 5, 200, 50);

        model = new DefaultListModel<>();
        messageList = new JList(model);
        messageList.setBounds(15, 50, 800, 330);

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(messageList);
        messageList.setLayoutOrientation(JList.VERTICAL);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        refreshList();

        sendButton = new JButton("Send");
        sendButton.setBounds(720, 390, 100, 35);
        sendButton.addActionListener(this);

        text = new JTextField();
        text.setBounds(10, 390, 700, 35);

        this.add(sendButton);
        this.add(anzeige);
        //this.add(messageList);
        this.add(text);
        this.add(scrollPane);
        scrollPane.setViewportView(messageList);
        //ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // Schedule the task to run every half second
        //scheduler.scheduleAtFixedRate(task, 0, 1000, TimeUnit.MILLISECONDS);
    }



    void refreshList() {
        model.clear();
        db.getMessages();
        for(int i = db.messagesLoaded; i <= db.messages.size() - 1; i++) {
            model.addElement(db.messages.get(i).timestamp + " - " + db.messages.get(i).username + ": " + db.messages.get(i).content);
            db.messagesLoaded++;
        }
    }

    // Call your function here
    Runnable task = this::refreshList;

    public static void main(String[] args)  // Hauptmethode, mit der gestartet wird
    {
        Hauptfenster halloTest;
        halloTest = new Hauptfenster();
        halloTest.setVisible(true);
        System.out.println("Fenster Sichtbar");



    }

    public void actionPerformed(ActionEvent ausloeser) {
        if(ausloeser.getSource() == this.sendButton) {
            try {
                db.sendMessage(new Message(new Timestamp(System.currentTimeMillis()), "Martinuuu", text.getText()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            refreshList();
        }
    }




}
