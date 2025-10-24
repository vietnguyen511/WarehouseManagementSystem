
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*, model.User" %>
<c:set var="activePage" value="categories" scope="request" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Category Management - Warehouse Management System</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
        <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>

        <style>
            html, body {
                height: auto;
                overflow: auto;
            }
            .main-content-with-sidebar {
                padding: var(--spacing-lg);
                flex: 1;
            }
            .card {
                background: #fff;
                border-radius: var(--radius-lg);
                box-shadow: var(--shadow-sm);
                overflow: hidden;
            }
            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: var(--spacing-md) var(--spacing-lg);
                border-bottom: 1px solid var(--gray-200);
            }
            .card-body {
                padding: var(--spacing-lg);
            }
            .table-wrapper {
                overflow-x: auto;
                margin-top: var(--spacing-md);
            }
            table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                padding: 0.75rem;
                border-bottom: 1px solid var(--gray-200);
                text-align: left;
            }
            th {
                background: var(--gray-50);
                font-weight: 600;
            }
            .action-bar {
                display: flex;
                gap: var(--spacing-sm);
            }
            .btn-danger {
                background-color: var(--danger-600);
                color: white;
            }
            .btn-danger:hover {
                background-color: var(--danger-700);
            }
            .alert {
                padding: 0.75rem 1rem;
                border-radius: var(--radius-md);
                margin-top: var(--spacing-md);
            }
            .alert-success {
                background-color: var(--success-50);
                color: var(--success-700);
                border: 1px solid var(--success-200);
            }
            .alert-danger {
                background-color: var(--danger-50);
                color: var(--danger-700);
                border: 1px solid var(--danger-200);
            }
            .search-form {
                display: flex;
                align-items: center;
                gap: 8px;
                background: #f9fafb;
                border: 1px solid #d1d5db;
                border-radius: 8px;
                padding: 6px 10px;
                transition: all 0.2s ease-in-out;
            }

            .search-form input[type="text"] {
                flex: 1;
                border: none;
                outline: none;
                background: transparent;
                padding: 6px;
                font-size: 0.95rem;
            }

            .search-form button {
                background: var(--primary-600);
                color: white;
                border: none;
                border-radius: 6px;
                padding: 6px 12px;
                cursor: pointer;
                transition: background 0.2s ease-in-out;
            }

            .search-form button:hover {
                background: var(--primary-700);
            }

            .search-form:focus-within {
                box-shadow: 0 0 0 2px var(--primary-200);
                background: #fff;
            }
        </style>
    </head>
    <body>
        <!-- Top Header -->
        <jsp:include page="/components/top-header.jsp" />
        <!-- Sidebar -->
        <jsp:include page="/components/sidebar-nav.jsp" />

        <div class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <div>
                        <h1 class="card-title">User list Management</h1>
                        <p class="card-subtitle">Manage all users in the warehouse</p>
                    </div>
                    <select name="sort" onchange="this.form.submit()">
                          <option type="submit" value="fullname">Sort by Name</option>
                          <option type="submit" value="email">Sort by Email</option>
                          <option type="submit" value="role">Sort by Role</option>
                         <option type="submit" value="status">Sort by Status</option>
                    </select>
                   <!--Further upgrade -->
                </div>

                <div class="card-body">
                    <div class="table-wrapper">
                        <table class="table">
                            <thead>
                                <tr>
                                      <th>ID<th>
                                      <th>Full Name </th>
                                      <th>Email</th>
                                      <th>Phone</th>
                                      <th>Role</th>
                                      <th>Status</th>
                                      <th style="text-align:center;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                              <%
                                  List<User> users = (List<User>) request.getAttribute("users");
                                  if (users != null)
                                    {           
                                         for (User u : users) 
                                       {
                              %>
                               <tr>
                                      <td><%= u.getId()%></td>
                                      <td> <a href="userDetail?id=<%= u.getId()%>"> <%= u.getFullname() %></a>   </td>
                                      <td><%= u.getEmail() %></td>
                                      <td><%= u.getPhone() %></td>
                                      <td><%= u.getRole() %></td>   
                                      <td><%= u.getStatus() ? "Active" : "Inactive" %></td>
                               </tr>
                              <%
                                       }
                                  } 
                               %>         
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success">${successMessage}</div>
                    </c:if>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>
                </div>
            </div>
        </div>

        <jsp:include page="/components/footer.jsp" />
    </body>
</html