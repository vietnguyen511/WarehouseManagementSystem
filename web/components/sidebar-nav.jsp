<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!--
    Warehouse Management System - Sidebar Navigation Component
    Left sidebar navigation occupying 30% of screen width
-->

<aside class="sidebar-nav">
    <nav class="sidebar-nav-content">
        <ul class="sidebar-menu">
            <!-- Dashboard -->
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/dashboard" class="sidebar-link">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="3" width="7" height="7"></rect>
                        <rect x="14" y="3" width="7" height="7"></rect>
                        <rect x="14" y="14" width="7" height="7"></rect>
                        <rect x="3" y="14" width="7" height="7"></rect>
                    </svg>
                    <span class="sidebar-text">Dashboard</span>
                </a>
            </li>

            <!-- Products & Categories -->
            <li class="sidebar-item sidebar-dropdown">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                    </svg>
                    <span class="sidebar-text">Products</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/products" class="sidebar-sublink">Product List</a></li>
                    <li><a href="${pageContext.request.contextPath}/products/add" class="sidebar-sublink">Add Product</a></li>
                    <li><a href="${pageContext.request.contextPath}/categories" class="sidebar-sublink">Categories</a></li>
                </ul>
            </li>

            <!-- Warehouse Operations -->
            <li class="sidebar-item sidebar-dropdown">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                        <polyline points="9 22 9 12 15 12 15 22"></polyline>
                    </svg>
                    <span class="sidebar-text">Warehouse</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/import" class="sidebar-sublink">Import Receipts</a></li>
                    <li><a href="${pageContext.request.contextPath}/export" class="sidebar-sublink">Export Receipts</a></li>
                    <li><a href="${pageContext.request.contextPath}/suppliers" class="sidebar-sublink">Suppliers</a></li>
                    <li><a href="${pageContext.request.contextPath}/customers" class="sidebar-sublink">Customers</a></li>
                </ul>
            </li>

            <!-- Statistics & Reports -->
            <li class="sidebar-item sidebar-dropdown">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="20" x2="12" y2="10"></line>
                        <line x1="18" y1="20" x2="18" y2="4"></line>
                        <line x1="6" y1="20" x2="6" y2="16"></line>
                    </svg>
                    <span class="sidebar-text">Reports</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/current-inventory" class="sidebar-sublink">Current Inventory</a></li>
                    <li><a href="${pageContext.request.contextPath}/import-export-stats" class="sidebar-sublink">Import/Export Stats</a></li>
                    <li><a href="${pageContext.request.contextPath}/revenue-report" class="sidebar-sublink">Revenue Report</a></li>
                    <li><a href="${pageContext.request.contextPath}/activity-logs" class="sidebar-sublink">Activity Logs</a></li>
                </ul>
            </li>

            <!-- User Management (Admin only) -->
            <c:if test="${sessionScope.user != null && sessionScope.user.role == 'admin'}">
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/users" class="sidebar-link">
                        <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                            <circle cx="9" cy="7" r="4"></circle>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                        </svg>
                        <span class="sidebar-text">Users</span>
                    </a>
                </li>
            </c:if>
        </ul>
    </nav>
</aside>

<!-- Mobile Sidebar Toggle -->
<button class="sidebar-toggle-btn" id="sidebarToggle">
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="3" y1="12" x2="21" y2="12"></line>
        <line x1="3" y1="6" x2="21" y2="6"></line>
        <line x1="3" y1="18" x2="21" y2="18"></line>
    </svg>
</button>

