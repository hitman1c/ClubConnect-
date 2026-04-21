package clubconnect.dao;

import clubconnect.models.User;
import clubconnect.util.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD
        );
    }

    // --- Find methods ---
    public User findByUsername(String username) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractUser(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findByEmail(String email) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractUser(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findById(int id) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractUser(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users")) {
            while (rs.next()) list.add(extractUser(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Create user ---
    public boolean create(User u) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO users(username,password,full_name,role,email,student_id,approved) VALUES(?,?,?,?,?,?,?)");
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getStudentId());
            ps.setBoolean(7, u.isApproved());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Create user failed: " + e.getMessage());
            return false;
        }
    }

    // --- Update user ---
    public boolean update(User u) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE users SET username=?, password=?, full_name=?, role=?, email=?, student_id=?, approved=? WHERE id=?");
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getStudentId());
            ps.setBoolean(7, u.isApproved());
            ps.setInt(8, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Update approval ---
    public boolean updateApproval(int id, boolean approved) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("UPDATE users SET approved=? WHERE id=?");
            ps.setBoolean(1, approved);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Update role ---
    public boolean updateRole(int id, String newRole) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("UPDATE users SET role=? WHERE id=?");
            ps.setString(1, newRole);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Delete user ---
    public boolean deleteById(int id) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id=?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Get club members ---
    public List<User> getClubMembers(int clubId) {
        List<User> members = new ArrayList<>();
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT u.* FROM users u JOIN memberships m ON u.id = m.user_id WHERE m.club_id=? AND m.status='approved'");
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(extractUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return members;
    }

    // --- Helper to extract user from ResultSet ---
    private User extractUser(ResultSet rs) throws SQLException {
        User u = new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getString("student_id")
        );
        u.setApproved(rs.getBoolean("approved"));
        return u;
    }
}
