package clubconnect.models;
import java.util.Date;
public class Notification { private int id; private int clubId; private String title; private String message; private Date createdAt; private boolean sent; public Notification(int id,int clubId,String title,String message,Date createdAt,boolean sent){this.id=id;this.clubId=clubId;this.title=title;this.message=message;this.createdAt=createdAt;this.sent=sent;} 

    public Object getTitle() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object getMessage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Object getCreatedAt() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}