package clubconnect.models;

import java.util.ArrayList;
import java.util.List;

public class Club {
    private int id;
    private String name;
    private String category;
    private String description;
    private int createdBy;
    private boolean archived;
    private List<Membership> members; // ✅ add members list

    public Club(int id, String name, String category, String description, int createdBy, boolean archived) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.createdBy = createdBy;
        this.archived = archived;
        this.members = new ArrayList<>(); // ✅ initialize
    }

    public Club(String name, String category, String description, int createdBy) {
        this(-1, name, category, description, createdBy, false);
    }

    // ✅ new getter and setter
    public List<Membership> getMembers() {
        return members;
    }

    public void setMembers(List<Membership> members) {
        this.members = members;
    }

    // ✅ helper method to add members
    public void addMember(Membership member) {
        this.members.add(member);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getCreatedBy() { return createdBy; }
    public boolean isArchived() { return archived; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public void setArchived(boolean archived) { this.archived = archived; }
}