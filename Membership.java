package clubconnect.models;

public class Membership {
    private int id;
    private int userId;
    private int clubId;
    private String role;
    private String status;

    public Membership(int id, int userId, int clubId, String role, String status) {
        this.id = id;
        this.userId = userId;
        this.clubId = clubId;
        this.role = role;
        this.status = status;
    }

    public Membership(int userId, int clubId, String role, String status) {
        this(-1, userId, clubId, role, status);
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}