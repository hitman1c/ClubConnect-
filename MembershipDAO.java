package clubconnect.dao;

import clubconnect.models.Membership;
import clubconnect.models.User;
import clubconnect.util.Config;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipDAO {

    // ✅ Add a new membership directly using a Membership object
    public boolean addMembership(Membership membership) {
        String sql = "INSERT INTO memberships (user_id, club_id, role, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, membership.getUserId());
            ps.setInt(2, membership.getClubId());
            ps.setString(3, membership.getRole());
            ps.setString(4, membership.getStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create a membership row (pending approval/approved)
    public boolean createMembership(int userId, int clubId, String role, String status) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO memberships(user_id, club_id, role, status) VALUES (?, ?, ?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, clubId);
            ps.setString(3, role);
            ps.setString(4, status);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Helper: request membership with pending status ---
    public boolean requestMembership(int userId, int clubId) {
        return createMembership(userId, clubId, "member", "pending");
    }

    // --- Get membership status for user/club ---
    public String getMembershipStatus(int userId, int clubId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT status FROM memberships WHERE user_id=? AND club_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, clubId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("status");
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // --- Get membership id (primary key/id) for user/club ---
    public int getMembershipId(int userId, int clubId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM memberships WHERE user_id=? AND club_id=?");
            ps.setInt(1, userId);
            ps.setInt(2, clubId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    // --- Get all memberships for a specific club ---
    public List<Membership> getMembershipsForClub(int clubId) {
        List<Membership> list = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM memberships WHERE club_id=?");
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Membership(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("club_id"),
                    rs.getString("role"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Get all memberships for a specific user ---
    public List<Membership> getMembershipsForUser(int userId) {
        List<Membership> list = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM memberships WHERE user_id=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Membership(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("club_id"),
                    rs.getString("role"),
                    rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Approve a membership (by id) ---
    public boolean approveMembership(int membershipId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "UPDATE memberships SET status='approved' WHERE id=?");
            ps.setInt(1, membershipId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Remove a membership (by id) ---
    public boolean deleteMembership(int membershipId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "DELETE FROM memberships WHERE id=?");
            ps.setInt(1, membershipId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Check if user is a member of a club ---
    public boolean isMember(int userId, int clubId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM memberships WHERE user_id=? AND club_id=? AND status='approved'");
            ps.setInt(1, userId);
            ps.setInt(2, clubId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // --- Get user details by userId (for comment display, etc.) ---
    public User getUser(int userId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT id, full_name, email, role FROM users WHERE id=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
