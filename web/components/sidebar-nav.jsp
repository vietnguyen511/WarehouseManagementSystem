<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>

<!--
    Warehouse Management System - Sidebar Navigation Component
    Left sidebar navigation occupying 30% of screen width
-->

<aside class="sidebar-nav">
    <nav class="sidebar-nav-content">
        <ul class="sidebar-menu">
            <!-- Dashboard 
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/dashboard" class="sidebar-link ${activePage == 'dashboard' ? 'active' : ''}">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="3" width="7" height="7"></rect>
                        <rect x="14" y="3" width="7" height="7"></rect>
                        <rect x="14" y="14" width="7" height="7"></rect>
                        <rect x="3" y="14" width="7" height="7"></rect>
                    </svg>
                    <span class="sidebar-text">Dashboard</span>
                </a>
            </li>  -->

            <%
              if (session == null) 
              {
                    response.sendRedirect("index.html");
                    return;
              }
             String role = (String) session.getAttribute("role");
             
             // Admin: User Management and Reports only
             if("admin".equals(role))
              {
           %>  
            <!-- User Management (Admin only) -->
            <li class="sidebar-item sidebar-dropdown ${activePage == 'Userlist' || activePage == 'AddUser' ? 'active' : ''}">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                    <span class="sidebar-text">User Management</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/staff-list" class="sidebar-sublink ${activePage == 'Userlist'? 'active' : ''}">User List</a></li>
                    <li><a href="${pageContext.request.contextPath}/register" class="sidebar-sublink ${activePage == 'AddUser' ? 'active' : ''}">Add User</a></li>
                </ul>
            </li>
            
            <%
               }
             
             // Manager: All features except User Management
             if("manager".equals(role))
              {
            %>
            <!-- Products & Categories (Manager) -->
            <li class="sidebar-item sidebar-dropdown ${activePage == 'products' || activePage == 'add-product' || activePage == 'categories' ? 'active' : ''}">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                    </svg>
                    <span class="sidebar-text">Products and Categories</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/warehouse-management/product-management" class="sidebar-sublink ${activePage == 'products' ? 'active' : ''}">Products Management</a></li>
                    <li><a href="${pageContext.request.contextPath}/warehouse-management/category-management" class="sidebar-sublink ${activePage == 'categories' ? 'active' : ''}">Categories Management</a></li>
                </ul>
            </li>
            
            <!-- Suppliers (Manager) -->
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/suppliers" class="sidebar-link ${activePage == 'suppliers' ? 'active' : ''}">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                    <span class="sidebar-text">Suppliers</span>
                </a>
            </li>
            
            <!-- Customers (Manager) -->
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/customers" class="sidebar-link ${activePage == 'customers' ? 'active' : ''}">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                    <span class="sidebar-text">Customers</span>
                </a>
            </li>
            
            <!-- Statistics & Reports (Manager) -->
            <li class="sidebar-item sidebar-dropdown ${activePage == 'current-inventory' || activePage == 'import-export-stats' || activePage == 'revenue-report' || activePage == 'export-report' ? 'active' : ''}">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="20" x2="12" y2="10"></line>
                    <line x1="18" y1="20" x2="18" y2="4"></line>
                    <line x1="6" y1="20" x2="6" y2="16"></line>
                    </svg>
                    <span class="sidebar-text">Reports and Statistics</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/current-inventory" class="sidebar-sublink ${activePage == 'current-inventory' ? 'active' : ''}">Current Inventory</a></li>
                    <li><a href="${pageContext.request.contextPath}/import-export-stats" class="sidebar-sublink ${activePage == 'import-export-stats' ? 'active' : ''}">Import/Export Stats</a></li>
                    <li><a href="${pageContext.request.contextPath}/revenue-report" class="sidebar-sublink ${activePage == 'revenue-report' ? 'active' : ''}">Revenue Report</a></li>
                    <li><a href="${pageContext.request.contextPath}/export-report" class="sidebar-sublink ${activePage == 'export-report' ? 'active' : ''}">Export Report</a></li>
                </ul>
            </li>
            
            <%
              }
             
             // Staff: Only Import and Export Receipts
             if("staff".equals(role))
              {
            %>
            <!-- Import Receipts (Staff) -->
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-list" class="sidebar-link ${activePage == 'import-receipts' ? 'active' : ''}">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                    <polyline points="7 10 12 15 17 10"></polyline>
                    <line x1="12" y1="15" x2="12" y2="3"></line>
                    </svg>
                    <span class="sidebar-text">Import Receipts</span>
                </a>
            </li>
            
            <!-- Export Receipts (Staff) -->
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/warehouse-export-mgt/export-receipt-list" class="sidebar-link ${activePage == 'export-receipts' ? 'active' : ''}">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                    <polyline points="17 8 12 3 7 8"></polyline>
                    <line x1="12" y1="3" x2="12" y2="15"></line>
                    </svg>
                    <span class="sidebar-text">Export Receipts</span>
                </a>
            </li>
            
            <%
              }
            %>
            <!-- User Management (Admin only)
            <c:if test="${sessionScope.user != null && sessionScope.user.role == 'admin'}">
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/users" class="sidebar-link ${activePage == 'users' ? 'active' : ''}">
                        <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                            <circle cx="9" cy="7" r="4"></circle>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                        </svg>
                        <span class="sidebar-text">Users</span>
                    </a>
                </li>
            </c:if> -->
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

