<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!--
    Warehouse Management System - Header Component
    Reusable header for all pages in the application
-->

<header class="app-header">
    <div class="header-container">
        <!-- Logo and Brand -->
        <div class="header-brand">
            <a href="${pageContext.request.contextPath}/" class="brand-link">
                <svg class="brand-icon" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                    <polyline points="9 22 9 12 15 12 15 22"></polyline>
                </svg>
                <div class="brand-text">
                    <span class="brand-name">Warehouse MS</span>
                    <span class="brand-subtitle">Management System</span>
                </div>
            </a>
        </div>

        <!-- Main Navigation -->
        <nav class="header-nav">
            <ul class="nav-menu">
                <!-- Dashboard -->
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="3" width="7" height="7"></rect>
                            <rect x="14" y="3" width="7" height="7"></rect>
                            <rect x="14" y="14" width="7" height="7"></rect>
                            <rect x="3" y="14" width="7" height="7"></rect>
                        </svg>
                        <span>Dashboard</span>
                    </a>
                </li>

                <!-- Products & Categories -->
                <li class="nav-item dropdown">
                    <a href="#" class="nav-link">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                        </svg>
                        <span>Products</span>
                        <svg class="dropdown-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6 9 12 15 18 9"></polyline>
                        </svg>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="${pageContext.request.contextPath}/products">Product List</a></li>
                        <li><a href="${pageContext.request.contextPath}/products/add">Add Product</a></li>
                        <li><a href="${pageContext.request.contextPath}/categories">Categories</a></li>
                    </ul>
                </li>

                <!-- Warehouse Operations -->
                <li class="nav-item dropdown">
                    <a href="#" class="nav-link">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                            <polyline points="9 22 9 12 15 12 15 22"></polyline>
                        </svg>
                        <span>Warehouse</span>
                        <svg class="dropdown-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6 9 12 15 18 9"></polyline>
                        </svg>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="${pageContext.request.contextPath}/import">Import Receipts</a></li>
                        <li><a href="${pageContext.request.contextPath}/export">Export Receipts</a></li>
                        <li><a href="${pageContext.request.contextPath}/suppliers">Suppliers</a></li>
                        <li><a href="${pageContext.request.contextPath}/customers">Customers</a></li>
                    </ul>
                </li>

                <!-- Statistics & Reports -->
                <li class="nav-item dropdown">
                    <a href="#" class="nav-link">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="20" x2="12" y2="10"></line>
                            <line x1="18" y1="20" x2="18" y2="4"></line>
                            <line x1="6" y1="20" x2="6" y2="16"></line>
                        </svg>
                        <span>Reports</span>
                        <svg class="dropdown-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6 9 12 15 18 9"></polyline>
                        </svg>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="${pageContext.request.contextPath}/current-inventory">Current Inventory</a></li>
                        <li><a href="${pageContext.request.contextPath}/inventory-alerts">Inventory Alerts</a></li>
                        <li><a href="${pageContext.request.contextPath}/import-export-stats">Import/Export Stats</a></li>
                        <li><a href="${pageContext.request.contextPath}/revenue-report">Revenue Report</a></li>
                        <li><a href="${pageContext.request.contextPath}/activity-logs">Activity Logs</a></li>
                    </ul>
                </li>

                <!-- User Management (Admin only) -->
                <c:if test="${sessionScope.user != null && sessionScope.user.role == 'admin'}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/users" class="nav-link">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                <circle cx="9" cy="7" r="4"></circle>
                                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                            </svg>
                            <span>Users</span>
                        </a>
                    </li>
                </c:if>
            </ul>
        </nav>

        <!-- User Profile & Actions -->
        <div class="header-actions">
            <!-- Notifications -->
            <div class="header-action-item">
                <button class="action-btn" title="Notifications">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                    </svg>
                    <span class="notification-badge">3</span>
                </button>
            </div>

            <!-- User Menu -->
            <div class="header-action-item user-menu">
                <c:choose>
                    <c:when test="${sessionScope.user != null}">
                        <button class="user-btn">
                            <div class="user-avatar">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.user.avatar}">
                                        <img src="${pageContext.request.contextPath}/${sessionScope.user.avatar}" alt="${sessionScope.user.fullname}">
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="firstLetter" value="${fn:substring(sessionScope.user.fullname, 0, 1)}" />
                                        <span class="avatar-placeholder">${firstLetter}</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="user-info">
                                <span class="user-name">${sessionScope.user.fullname}</span>
                                <span class="user-role">${sessionScope.user.role}</span>
                            </div>
                            <svg class="dropdown-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6 9 12 15 18 9"></polyline>
                            </svg>
                        </button>
                        <ul class="dropdown-menu user-dropdown">
                            <li><a href="${pageContext.request.contextPath}/profile">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                My Profile
                            </a></li>
                            <li><a href="${pageContext.request.contextPath}/account">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="12" cy="12" r="3"></circle>
                                    <path d="M12 1v6m0 6v6m5.2-13.2l-4.2 4.2m0 6l4.2 4.2M23 12h-6m-6 0H1m18.2 5.2l-4.2-4.2m0-6l4.2-4.2"></path>
                                </svg>
                                Account Settings
                            </a></li>
                            <li class="divider"></li>
                            <li><a href="${pageContext.request.contextPath}/logout" class="logout-link">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                    <polyline points="16 17 21 12 16 7"></polyline>
                                    <line x1="21" y1="12" x2="9" y2="12"></line>
                                </svg>
                                Logout
                            </a></li>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">Login</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</header>

<!-- Mobile Menu Toggle (for responsive design) -->
<button class="mobile-menu-toggle" id="mobileMenuToggle">
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="3" y1="12" x2="21" y2="12"></line>
        <line x1="3" y1="6" x2="21" y2="6"></line>
        <line x1="3" y1="18" x2="21" y2="18"></line>
    </svg>
</button>

