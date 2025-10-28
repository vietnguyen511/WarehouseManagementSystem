package dal;

import jakarta.servlet.http.HttpSession;

/**
 * ActivityLogHelper - Helper class to easily log user activities
 * Provides static methods to log activities from any servlet
 * 
 * @author lengo
 */
public class ActivityLogHelper {
    
    /**
     * Log a user activity from session
     * @param session HttpSession to get user ID from
     * @param action Action type (CREATE, UPDATE, DELETE, etc.)
     * @param targetTable Table name affected
     * @param targetId ID of the affected record (nullable)
     * @param description Detailed description
     */
    public static void logActivity(HttpSession session, String action, String targetTable, 
                                   Integer targetId, String description) {
        try {
            // Get user ID from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                System.out.println("Warning: Cannot log activity - no user ID in session");
                return;
            }
            
            // Create DAO and log activity
            ActivityLogDAO dao = new ActivityLogDAO();
            dao.logActivity(userId, action, targetTable, targetId, description);
            
        } catch (Exception e) {
            System.out.println("Error logging activity: " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception - logging should not break the main flow
        }
    }
    
    /**
     * Log create action
     */
    public static void logCreate(HttpSession session, String targetTable, Integer targetId, String description) {
        logActivity(session, "CREATE", targetTable, targetId, description);
    }
    
    /**
     * Log update action
     */
    public static void logUpdate(HttpSession session, String targetTable, Integer targetId, String description) {
        logActivity(session, "UPDATE", targetTable, targetId, description);
    }
    
    /**
     * Log delete action
     */
    public static void logDelete(HttpSession session, String targetTable, Integer targetId, String description) {
        logActivity(session, "DELETE", targetTable, targetId, description);
    }
    
    /**
     * Log view action (optional - may be too verbose)
     */
    public static void logView(HttpSession session, String targetTable, Integer targetId, String description) {
        // logActivity(session, "VIEW", targetTable, targetId, description);
        // Commented out to avoid logging every page view - too verbose
    }
}

