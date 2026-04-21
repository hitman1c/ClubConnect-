package clubconnect.models;

import java.util.Date;

public class Event {
    private int id;
    private int clubId;
    private String title;
    private Date dateTime;
    private String venue;
    private int capacity;
    private String details;
    private boolean budgetRequested;
    private String budgetStatus;

    public Event(int id, int clubId, String title, Date dateTime, String venue,
                 int capacity, String details, boolean budgetRequested, String budgetStatus) {
        this.id = id;
        this.clubId = clubId;
        this.title = title;
        this.dateTime = dateTime;
        this.venue = venue;
        this.capacity = capacity;
        this.details = details;
        this.budgetRequested = budgetRequested;
        this.budgetStatus = budgetStatus;
    }

    public Event(int clubId, String title, Date dateTime, String venue,
                 int capacity, String details, boolean budgetRequested, String budgetStatus) {
        this(-1, clubId, title, dateTime, venue, capacity, details, budgetRequested, budgetStatus);
    }

    // Getters
    public int getId() { return id; }
    public int getClubId() { return clubId; }
    public String getTitle() { return title; }
    public Date getDateTime() { return dateTime; }
    public String getVenue() { return venue; }
    public int getCapacity() { return capacity; }
    public String getDetails() { return details; }
    public boolean isBudgetRequested() { return budgetRequested; }
    public String getBudgetStatus() { return budgetStatus; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setClubId(int clubId) { this.clubId = clubId; }
    public void setTitle(String title) { this.title = title; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setDetails(String details) { this.details = details; }
    public void setBudgetRequested(boolean budgetRequested) { this.budgetRequested = budgetRequested; }
    public void setBudgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; }
}
