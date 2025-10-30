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
            <!-- Dashboard -->
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/adminDashboard" class="sidebar-link ${activePage == 'dashboard' ? 'active' : ''}">
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
            <%
              if(session==null) 
              {
                    response.sendRedirect("index.html");
                    return;
              }
             String role = (String) session.getAttribute("role");
             if("admin".equals(role))
              {
           %>  
            <li class="sidebar-item sidebar-dropdown ${activePage == 'users' ? 'active' : ''}">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                        <circle cx="9" cy="7" r="4"></circle>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                    <span class="sidebar-text">User</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/staff-list" class="sidebar-sublink">User list</a></li>
                    <li><a href="${pageContext.request.contextPath}/register" class="sidebar-sublink">Add User</a></li>
                </ul>
            </li>
            <li class="sidebar-item sidebar-dropdown ${activePage == 'products' ? 'active' : ''}">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
                    <span class="sidebar-text">Product</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/warehouse-management/product-management" class="sidebar-sublink">Product List</a></li>
                    <li><a href="${pageContext.request.contextPath}/warehouse-management/add-product" class="sidebar-sublink">Add Product</a></li>
                </ul>
            </li>
            <li class="sidebar-item sidebar-dropdown ${activePage == 'customers' ? 'active' : ''}">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <circle cx="12" cy="10" r="3"></circle>
                        <path d="M6 18c0-2 4-3 6-3s6 1 6 3"></path>
                    </svg>
                    <span class="sidebar-text">Customer</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/customers" class="sidebar-sublink">Customer List</a></li>
                    <li><a href="${pageContext.request.contextPath}/customer-add" class="sidebar-sublink">Add Customer</a></li>
                </ul>
            </li>
            <li class="sidebar-item sidebar-dropdown ${activePage == 'categories' ? 'active' : ''}">
                <a href="#" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"></rect></svg>
                    <span class="sidebar-text">Category</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/warehouse-management/category-management" class="sidebar-sublink">Category List</a></li>
                    <li><a href="${pageContext.request.contextPath}/warehouse-management/add-category" class="sidebar-sublink">Add Category</a></li>
                </ul>
            </li>
            <li class="sidebar-item sidebar-dropdown ${(activePage == 'suppliers' || activePage == 'add-supplier') ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/suppliers" class="sidebar-link sidebar-toggle">
                    <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="2" y="7" width="20" height="14" rx="2"></rect>
                        <path d="M16 3v4"></path>
                        <path d="M8 3v4"></path>
                    </svg>
                    <span class="sidebar-text">Supplier</span>
                    <svg class="sidebar-dropdown-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
                </a>
                <ul class="sidebar-submenu">
                    <li><a href="${pageContext.request.contextPath}/suppliers" class="sidebar-sublink ${(activePage == 'suppliers') ? 'active' : ''}">Supplier List</a></li>
                    <li><a href="${pageContext.request.contextPath}/suppliers/new" class="sidebar-sublink ${(activePage == 'add-supplier') ? 'active' : ''}">Add Supplier</a></li>
                </ul>
            </li>           
            <% 
               }
              if("staff".equals(role))
              {
            %>
            <!-- Warehouse Operations -->
            <li class="sidebar-item sidebar-dropdown ${activePage == 'import-receipts' || activePage == 'export-receipts'  ? 'active' : ''}">
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
                    <li><a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-list" class="sidebar-sublink ${activePage == 'import-receipts' ? 'active' : ''}">Import Receipts</a></li>
                    <li><a href="${pageContext.request.contextPath}/export" class="sidebar-sublink ${activePage == 'export-receipts' ? 'active' : ''}">Export Receipts</a></li>
                </ul>
            </li>
            <%
              }
             if("manager".equals(role))
              {
            %>
            <!-- Statistics & Reports -->
            <li class="sidebar-item sidebar-dropdown ${activePage == 'current-inventory' || activePage == 'import-export-stats' || activePage == 'revenue-report' || activePage == 'export-report' || activePage == 'activity-log' ? 'active' : ''}">
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
                    <li><a href="${pageContext.request.contextPath}/current-inventory" class="sidebar-sublink ${activePage == 'current-inventory' ? 'active' : ''}">Current Inventory</a></li>
                    <li><a href="${pageContext.request.contextPath}/import-export-stats" class="sidebar-sublink ${activePage == 'import-export-stats' ? 'active' : ''}">Import/Export Stats</a></li>
                    <li><a href="${pageContext.request.contextPath}/revenue-report" class="sidebar-sublink ${activePage == 'revenue-report' ? 'active' : ''}">Revenue Report</a></li>
                    <li><a href="${pageContext.request.contextPath}/export-report" class="sidebar-sublink ${activePage == 'export-report' ? 'active' : ''}">Export Report</a></li>
                    <li><a href="${pageContext.request.contextPath}/activity-log" class="sidebar-sublink ${activePage == 'activity-log' ? 'active' : ''}">Activity Logs</a></li>
                </ul>
            </li>
            <%
              }
            %>
            <!-- User Management (Admin only) -->
            <c:if test="${sessionScope.user != null && sessionScope.user.role == 'admin'}">
                <li class="sidebar-item">
                    <a href="${pageContext.request.contextPath}/users" class="sidebar-link ${activePage == 'users' ? 'active' : ''}">
                        <svg class="sidebar-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                            <circle cx="9" cy="7" r="4"></circle>i
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

