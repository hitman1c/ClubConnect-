package clubconnect.models;

import java.util.Date;

public class Comment {
    private int id;
    private int eventId;
    private int userId;
    private String message;
    private Date createdAt;

    public Comment(int id, int eventId, String message, String toString) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getEventId() { return eventId; }
    public int getUserId() { return userId; }
    public String getMessage() { return message; }
    public Date getCreatedAt() { return createdAt; }

    public Object getContent() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}