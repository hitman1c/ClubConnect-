package clubconnect.dao;

import clubconnect.models.Event;
import clubconnect.util.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    // Fetch upcoming events for a specific club
    public List<Event> upcomingForClub(int clubId) {
        List<Event> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            String sql = "SELECT id, club_id, title, date_time, venue, capacity, details, budget_requested, budget_status " +
                         "FROM events WHERE club_id=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                java.util.Date dt = rs.getTimestamp("date_time");
                out.add(new Event(
                        rs.getInt("id"),
                        rs.getInt("club_id"),
                        rs.getString("title"),
                        dt,
                        rs.getString("venue"),
                        rs.getInt("capacity"),
                        rs.getString("details"),
                        rs.getInt("budget_requested") == 1,
                        rs.getString("budget_status")
                ));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    // Alias for consistency with UI
    public List<Event> getEventsForClub(int clubId) {
        return upcomingForClub(clubId);
    }

    // Find event by title
    public Event findByTitle(String title) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            String sql = "SELECT id, club_id, title, date_time, venue, capacity, details, budget_requested, budget_status " +
                         "FROM events WHERE title=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                java.util.Date dt = rs.getTimestamp("date_time");
                return new Event(
                        rs.getInt("id"),
                        rs.getInt("club_id"),
                        rs.getString("title"),
                        dt,
                        rs.getString("venue"),
                        rs.getInt("capacity"),
                        rs.getString("details"),
                        rs.getInt("budget_requested") == 1,
                        rs.getString("budget_status")
                );
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Find event by ID
    public Event getEventById(int eventId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            String sql = "SELECT id, club_id, title, date_time, venue, capacity, details, budget_requested, budget_status " +
                         "FROM events WHERE id=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                java.util.Date dt = rs.getTimestamp("date_time");
                return new Event(
                        rs.getInt("id"),
                        rs.getInt("club_id"),
                        rs.getString("title"),
                        dt,
                        rs.getString("venue"),
                        rs.getInt("capacity"),
                        rs.getString("details"),
                        rs.getInt("budget_requested") == 1,
                        rs.getString("budget_status")
                );
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Alias for UI code to find event by ID
    public Event findEventById(int eventId) {
        return getEventById(eventId);
    }

    // New: Find event by title AND clubId (needed in your UI for comments)
    public Event findEventByTitleAndClub(String title, int clubId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            String sql = "SELECT id, club_id, title, date_time, venue, capacity, details, budget_requested, budget_status " +
                         "FROM events WHERE title=? AND club_id=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, title);
            ps.setInt(2, clubId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                java.util.Date dt = rs.getTimestamp("date_time");
                return new Event(
                        rs.getInt("id"),
                        rs.getInt("club_id"),
                        rs.getString("title"),
                        dt,
                        rs.getString("venue"),
                        rs.getInt("capacity"),
                        rs.getString("details"),
                        rs.getInt("budget_requested") == 1,
                        rs.getString("budget_status")
                );
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createEvent(Event e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<Event> findAllEvents() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
