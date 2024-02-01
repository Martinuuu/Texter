import java.sql.*;
import java.util.ArrayList;

public class Database {

    int nextMessage;

    ArrayList<Message> messages;
    Connection con;
    Statement stmt;
    ResultSet chatRS;

    Database() {
        messages = new ArrayList<Message>();
        nextMessage = 0;
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

    public void getMessages() {
        try {
            int i = messages.size();
            while(chatRS.absolute(i)) {
                messages.add(new Message(chatRS.getTimestamp("time"), chatRS.getString("username"), chatRS.getString("content")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendMessage(Message message) throws SQLException {
        String sql = "INSERT INTO chat VALUES (?, ?, ?)";

        try (
                // Create a PreparedStatement with the SQL query
                PreparedStatement preparedStatement = con.prepareStatement(sql)) {
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

