import java.sql.*;
import java.util.ArrayList;

public class Database {

    int messagesLoaded;
    ArrayList<Message> messages;
    Connection con;
    Statement stmt;
    ResultSet chatRS;
    Timestamp prevMsg;

    Database() {
        messages = new ArrayList<Message>();
        messagesLoaded = 0;
        prevMsg = new Timestamp(0);
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://avnadmin:AVNS_H0Yv2jFd8tnUP9OYq9I@texter-texter.a.aivencloud.com:16138/texterDB?ssl-mode=REQUIRED", "avnadmin", "AVNS_H0Yv2jFd8tnUP9OYq9I");
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            chatRS = stmt.executeQuery("select * from chat");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void getMessages(String table) {

        System.out.println("refreshed Messages");
        try {
            chatRS = stmt.executeQuery("select * from chat");
            chatRS.next();
            while (!chatRS.isAfterLast()) {
                messages.add(new Message(chatRS.getTimestamp("time"), chatRS.getString("username"), chatRS.getString("content"), false));
                chatRS.next();
            }

        } catch (SQLException e) {

            // Check if the SQLState indicates an empty result set error
            if("S1000".equals(e.getSQLState())) {
                // Handle the specific error here
                messages.add(new Message(new Timestamp(0), "", "No Messages here. Be the first to chat!", false));
            } else {
                // Handle other SQLExceptions
                System.out.println(e);
            }
        }

    }

    boolean checkNewMessage() {
        try {
            chatRS = stmt.executeQuery("SELECT * FROM chat ORDER BY time DESC LIMIT 1;");
            chatRS.next();
            Timestamp lastMsg = chatRS.getTimestamp("time");
            boolean isBefore = lastMsg.after(prevMsg);
            if(isBefore) {
                prevMsg = lastMsg;
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendMessage(Message message) {
        String sql = "INSERT INTO chat VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            // Set values for the parameters in the SQL query
            preparedStatement.setTimestamp(1, message.timestamp);
            preparedStatement.setString(2, message.username);
            preparedStatement.setString(3, message.content);

            // Execute the query
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                System.out.println("Insert successful. Rows affected: " + rowsAffected);
            } else {
                System.out.println("Insert failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        con.close();
    }
}

