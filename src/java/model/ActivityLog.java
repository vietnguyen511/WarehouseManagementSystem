package model;

import java.util.Date;

/**
 * ActivityLog Model - Represents user activity logs
 * Records all user actions in the system
 * 
 * @author lengo
 */
public class ActivityLog {
    private int logId;
    private int userId;
    private String action;        // CREATE, UPDATE, DELETE, VIEW, etc.
    private String targetTable;    // Products, Categories, ImportReceipts, etc.
    private Integer targetId;       // ID of the affected record
    private String description;     // Detailed description of the action
    private Date createdAt;
    
    // Additional info for display
    private String userName;
    private String userRole;
    private String formattedDate;

    public ActivityLog() {
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    /**
     * Get action badge color based on action type
     */
    public String getActionBadgeClass() {
        if (action == null) return "";
        switch (action.toUpperCase()) {
            case "CREATE":
            case "INSERT":
                return "badge badge-success";
            case "UPDATE":
            case "EDIT":
                return "badge badge-warning";
            case "DELETE":
                return "badge badge-danger";
            case "VIEW":
            case "READ":
                return "badge badge-info";
            default:
                return "badge badge-secondary";
        }
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", action='" + action + '\'' +
                ", targetTable='" + targetTable + '\'' +
                ", targetId=" + targetId +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", userName='" + userName + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}

