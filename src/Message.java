
import java.sql.Timestamp;
public class Message {
    boolean isUserHighlighted;
    Timestamp timestamp;
    String username;
    String content;

    public Message(Timestamp timestamp, String username, String content, boolean userHighlighted) {
        this.timestamp = timestamp;
        this.username = username;
        this.content = content;
        this.isUserHighlighted = userHighlighted;
    }

}
