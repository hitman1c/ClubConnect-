package clubconnect.models;
import java.util.Date;
public class Discussion { private int id; private int clubId; private int userId; private String content; private Date createdAt; public Discussion(int id,int clubId,int userId,String content,Date createdAt){this.id=id;this.clubId=clubId;this.userId=userId;this.content=content;this.createdAt=createdAt;} }