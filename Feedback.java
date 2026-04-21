package clubconnect.models;
import java.util.Date;
public class Feedback { private int id; private int clubId; private int userId; private String message; private Date createdAt; public Feedback(int id,int clubId,int userId,String message,Date createdAt){this.id=id;this.clubId=clubId;this.userId=userId;this.message=message;this.createdAt=createdAt;} }