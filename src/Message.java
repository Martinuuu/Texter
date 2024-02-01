
import java.sql.Timestamp;
public class Message {
    int sqlId;
    Timestamp timestamp;
    String username;
    String content;

    public Message(Timestamp timestamp, String username, String content) {
        this.timestamp = timestamp;
        this.username = username;
        this.content = content;
    }

}
