package clubconnect.dao;

import clubconnect.models.Comment;
import clubconnect.util.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    // -----------------------------
    // Add a new comment to an event
    // -----------------------------
    public void addComment(Comment comment) {
        String sql = "INSERT INTO comments(event_id, user_id, message, created_at) VALUES (?, ?, ?, NOW())";

        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, comment.getEventId());
            ps.setInt(2, comment.getUserId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error adding comment: " + e.getMessage());
        }
    }

    // -----------------------------------------
    // Get all comments for a specific event
    // -----------------------------------------
    public List<Comment> getCommentsForEvent(int eventId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT id, event_id, user_id, message, created_at " +
                     "FROM comments WHERE event_id = ? ORDER BY created_at DESC";

        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                comments.add(new Comment(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getString("message"),
                        java.time.LocalDate.now().toString()
                ));
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("❌ Error fetching comments for event: " + e.getMessage());
        }

        return comments;
    }

    // -----------------------------
    // Get all comments (all events)
    // -----------------------------
    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT id, event_id, user_id, message, created_at FROM comments ORDER BY created_at DESC";

        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD);
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                comments.add(new Comment(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getString("message"),
                        java.time.LocalDate.now().toString()
                ));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching all comments: " + e.getMessage());
        }

        return comments;
    }

    // -----------------------------
    // Update an existing comment
    // -----------------------------
    public void updateComment(int id, String newMessage) {
        String sql = "UPDATE comments SET message = ? WHERE id = ?";

        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newMessage);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error updating comment: " + e.getMessage());
        }
    }

    // -----------------------------
    // Delete a comment by ID
    // -----------------------------
    public void deleteComment(int id) {
        String sql = "DELETE FROM comments WHERE id = ?";

        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error deleting comment: " + e.getMessage());
        }
    }
}
