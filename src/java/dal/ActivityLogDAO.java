package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ActivityLog;

/**
 * ActivityLogDAO - Data Access Object for Activity Logs
 * Handles logging and retrieval of user activities
 * 
 * @author lengo
 */
public class ActivityLogDAO extends DBContext {

    public ActivityLogDAO() {
        super();
    }

    public ActivityLogDAO(Connection connection) {
        super(connection);
    }

    /**
     * Log a user activity
     * @param userId User performing the action
     * @param action Action type (CREATE, UPDATE, DELETE, etc.)
     * @param targetTable Table name affected
     * @param targetId ID of the affected record (nullable)
     * @param description Detailed description
     */
    public void logActivity(int userId, String action, String targetTable, 
                           Integer targetId, String description) {
        String sql = "INSERT INTO ActivityLogs (user_id, action, target_table, target_id, description, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, GETDATE())";
        
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            st.setString(2, action);
            st.setString(3, targetTable);
            if (targetId != null) {
                st.setInt(4, targetId);
            } else {
                st.setNull(4, java.sql.Types.INTEGER);
            }
            st.setString(5, description);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error logging activity: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing statement: " + e.getMessage());
            }
        }
    }

    /**
     * Get all activity logs with pagination
     * @param page Page number (1-based)
     * @param pageSize Number of records per page
     * @return List of activity logs
     */
    public List<ActivityLog> getAllActivityLogs(int page, int pageSize) {
        List<ActivityLog> logs = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    al.log_id, " +
                     "    al.user_id, " +
                     "    al.action, " +
                     "    al.target_table, " +
                     "    al.target_id, " +
                     "    al.description, " +
                     "    al.created_at, " +
                     "    u.username AS user_name, " +
                     "    u.role AS user_role " +
                     "FROM ActivityLogs al " +
                     "INNER JOIN Users u ON al.user_id = u.user_id " +
                     "ORDER BY al.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, (page - 1) * pageSize);
            st.setInt(2, pageSize);
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setTargetTable(rs.getString("target_table"));
                
                Integer targetId = (Integer) rs.getObject("target_id");
                log.setTargetId(targetId);
                
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                log.setUserName(rs.getString("user_name"));
                log.setUserRole(rs.getString("user_role"));
                
                logs.add(log);
            }
        } catch (SQLException e) {
            System.out.println("Error getting activity logs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return logs;
    }

    /**
     * Get activity logs by username (search)
     */
    public List<ActivityLog> getActivityLogsByUsername(String username, int page, int pageSize) {
        List<ActivityLog> logs = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    al.log_id, " +
                     "    al.user_id, " +
                     "    al.action, " +
                     "    al.target_table, " +
                     "    al.target_id, " +
                     "    al.description, " +
                     "    al.created_at, " +
                     "    u.username AS user_name, " +
                     "    u.role AS user_role " +
                     "FROM ActivityLogs al " +
                     "INNER JOIN Users u ON al.user_id = u.user_id " +
                     "WHERE u.username LIKE ? " +
                     "ORDER BY al.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + username + "%");
            st.setInt(2, (page - 1) * pageSize);
            st.setInt(3, pageSize);
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setTargetTable(rs.getString("target_table"));
                
                Integer targetId = (Integer) rs.getObject("target_id");
                log.setTargetId(targetId);
                
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                log.setUserName(rs.getString("user_name"));
                log.setUserRole(rs.getString("user_role"));
                
                logs.add(log);
            }
        } catch (SQLException e) {
            System.out.println("Error getting activity logs by username: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return logs;
    }

    /**
     * Get activity logs by user ID (legacy)
     */
    public List<ActivityLog> getActivityLogsByUser(int userId, int page, int pageSize) {
        List<ActivityLog> logs = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    al.log_id, " +
                     "    al.user_id, " +
                     "    al.action, " +
                     "    al.target_table, " +
                     "    al.target_id, " +
                     "    al.description, " +
                     "    al.created_at, " +
                     "    u.username AS user_name, " +
                     "    u.role AS user_role " +
                     "FROM ActivityLogs al " +
                     "INNER JOIN Users u ON al.user_id = u.user_id " +
                     "WHERE al.user_id = ? " +
                     "ORDER BY al.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            st.setInt(2, (page - 1) * pageSize);
            st.setInt(3, pageSize);
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setTargetTable(rs.getString("target_table"));
                
                Integer targetId = (Integer) rs.getObject("target_id");
                log.setTargetId(targetId);
                
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                log.setUserName(rs.getString("user_name"));
                log.setUserRole(rs.getString("user_role"));
                
                logs.add(log);
            }
        } catch (SQLException e) {
            System.out.println("Error getting activity logs by user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return logs;
    }

    /**
     * Get activity logs by action type
     */
    public List<ActivityLog> getActivityLogsByAction(String action, int page, int pageSize) {
        List<ActivityLog> logs = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    al.log_id, " +
                     "    al.user_id, " +
                     "    al.action, " +
                     "    al.target_table, " +
                     "    al.target_id, " +
                     "    al.description, " +
                     "    al.created_at, " +
                     "    u.username AS user_name, " +
                     "    u.role AS user_role " +
                     "FROM ActivityLogs al " +
                     "INNER JOIN Users u ON al.user_id = u.user_id " +
                     "WHERE al.action = ? " +
                     "ORDER BY al.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, action);
            st.setInt(2, (page - 1) * pageSize);
            st.setInt(3, pageSize);
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setTargetTable(rs.getString("target_table"));
                
                Integer targetId = (Integer) rs.getObject("target_id");
                log.setTargetId(targetId);
                
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                log.setUserName(rs.getString("user_name"));
                log.setUserRole(rs.getString("user_role"));
                
                logs.add(log);
            }
        } catch (SQLException e) {
            System.out.println("Error getting activity logs by action: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return logs;
    }

    /**
     * Count activity logs by username
     */
    public int countActivityLogsByUsername(String username) {
        String sql = "SELECT COUNT(*) AS total " +
                     "FROM ActivityLogs al " +
                     "INNER JOIN Users u ON al.user_id = u.user_id " +
                     "WHERE u.username LIKE ?";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setString(1, "%" + username + "%");
            rs = st.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting activity logs by username: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return 0;
    }
    
    /**
     * Count total activity logs
     */
    public int countActivityLogs() {
        String sql = "SELECT COUNT(*) AS total FROM ActivityLogs";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting activity logs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return 0;
    }

    /**
     * Get recent activity logs (last N records)
     */
    public List<ActivityLog> getRecentActivityLogs(int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        
        String sql = "SELECT TOP (?) " +
                     "    al.log_id, " +
                     "    al.user_id, " +
                     "    al.action, " +
                     "    al.target_table, " +
                     "    al.target_id, " +
                     "    al.description, " +
                     "    al.created_at, " +
                     "    u.username AS user_name, " +
                     "    u.role AS user_role " +
                     "FROM ActivityLogs al " +
                     "INNER JOIN Users u ON al.user_id = u.user_id " +
                     "ORDER BY al.created_at DESC";
        
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(sql);
            st.setInt(1, limit);
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setTargetTable(rs.getString("target_table"));
                
                Integer targetId = (Integer) rs.getObject("target_id");
                log.setTargetId(targetId);
                
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                log.setUserName(rs.getString("user_name"));
                log.setUserRole(rs.getString("user_role"));
                
                logs.add(log);
            }
        } catch (SQLException e) {
            System.out.println("Error getting recent activity logs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return logs;
    }
}

