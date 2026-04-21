package clubconnect.dao;

import clubconnect.models.Club;
import clubconnect.models.Event;
import clubconnect.util.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {

    // List all clubs
    public List<Club> listAll() {
        List<Club> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(
                "SELECT id, name, category, description, created_by, archived FROM clubs");

            while (rs.next()) {
                out.add(new Club(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getInt("created_by"),
                        rs.getInt("archived") == 1
                ));
            }
            rs.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    // Create a new club
    public boolean create(Club club) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO clubs(name, category, description, created_by, archived) VALUES(?,?,?,?,?)");
            ps.setString(1, club.getName());
            ps.setString(2, club.getCategory());
            ps.setString(3, club.getDescription());
            ps.setInt(4, club.getCreatedBy());
            ps.setBoolean(5, club.isArchived());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing club
    public boolean update(Club club) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            PreparedStatement ps = c.prepareStatement(
                    "UPDATE clubs SET name=?, category=?, description=?, archived=? WHERE id=?");
            ps.setString(1, club.getName());
            ps.setString(2, club.getCategory());
            ps.setString(3, club.getDescription());
            ps.setBoolean(4, club.isArchived());
            ps.setInt(5, club.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a club by ID
    public boolean delete(int id) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            PreparedStatement ps = c.prepareStatement("DELETE FROM clubs WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get club by leader ID
    public Club getClubForLeader(int leaderId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM clubs WHERE created_by=? LIMIT 1");
            ps.setInt(1, leaderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Club(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getInt("created_by"),
                        rs.getInt("archived") == 1
                );
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // NEW: Find a club by name (needed for UI)
    public Club findByName(String name) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            PreparedStatement ps = c.prepareStatement(
                    "SELECT id, name, category, description, created_by, archived FROM clubs WHERE name=? LIMIT 1");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Club(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getInt("created_by"),
                        rs.getInt("archived") == 1
                );
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get events for a club
    public List<Event> getEventsForClub(int clubId) {
        List<Event> events = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM events WHERE club_id=? ORDER BY date_time ASC");
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getInt("club_id"),
                        rs.getString("title"),
                        rs.getTimestamp("date_time"),
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
        return events;
    }

    // Get all clubs a member belongs to
    public List<Club> getClubsForMember(int userId) {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.category, c.description, c.created_by, c.archived " +
                     "FROM clubs c " +
                     "JOIN memberships m ON c.id = m.club_id " +
                     "WHERE m.user_id = ? AND m.status = 'approved'";
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                clubs.add(new Club(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getInt("created_by"),
                        rs.getInt("archived") == 1
                ));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clubs;
    }

    // OPTIONAL: Stub for files/resources
    public List<String> getFilesForClub(int clubId) {
        List<String> files = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {

            PreparedStatement ps = c.prepareStatement(
                    "SELECT name FROM resources"); // Replace with actual mapping if exists
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                files.add(rs.getString("name"));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
}
