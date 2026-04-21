package clubconnect.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private String email;
    private String studentId;
    private boolean approved;

    // Full constructor (all fields)
    public User(int id, String username, String password, String fullName,
                String role, String email, String studentId, boolean approved) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.studentId = studentId;
        this.approved = approved;
    }

    // Constructor without "approved" (for DB fetches where not present)
    public User(int id, String username, String password, String fullName,
                String role, String email, String studentId) {
        this(id, username, password, fullName, role, email, studentId, false);
    }

    // Minimal constructor (new user creation)
    public User(String username, String password, String fullName,
                String role, String email, String studentId) {
        this(-1, username, password, fullName, role, email, studentId, false);
    }

    // Constructor for MembershipDAO.getUser when fetching only basic info
    public User(int id, String fullName, String email, String role) {
        this(id, null, null, fullName, role, email, null, false);
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getStudentId() { return studentId; }
    public boolean isApproved() { return approved; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setApproved(boolean approved) { this.approved = approved; }
}