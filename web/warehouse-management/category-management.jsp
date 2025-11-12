<%-- 
    Document   : category-management
    Created on : Oct 13, 2025, 10:27:11 AM
    Author     : DANG
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    request.setAttribute("activePage", "categories");
%>

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
                background-color: var(--gray-50);
                font-family: "Segoe UI", Roboto, sans-serif;
                color: var(--gray-900);
                margin: 0;
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
                border: 1px solid var(--gray-200);
            }
            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                gap: var(--spacing-md);
                flex-wrap: wrap;
                padding: var(--spacing-md) var(--spacing-lg);
                border-bottom: 1px solid var(--gray-200);
            }
            .card-body {
                padding: var(--spacing-lg);
            }
            .table-wrapper {
                overflow-x: auto;
                margin-top: var(--spacing-md);
                border: 1px solid var(--gray-200);
                border-radius: var(--radius-md);
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
            .btn-refresh {
                display: inline-block;
                background-color: var(--primary-600);
                color: white;
                border: none;
                border-radius: 6px;
                padding: 6px 12px;
                font-size: 0.95rem;
                text-decoration: none;
                line-height: normal;
                cursor: pointer;
                transition: background 0.2s ease-in-out;
            }

            .btn-refresh:hover {
                background-color: var(--primary-700);
                color: white;
                text-decoration: none;
            }
        </style>
    </head>
    <body>
        <!--top header-->
        <jsp:include page="/components/top-header.jsp" />
        <!--sidebar-->
        <jsp:include page="/components/sidebar-nav.jsp" />

        <div class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <div>
                        <h1 class="card-title">Category Management</h1>
                        <p class="card-subtitle">Manage product categories in the warehouse</p>
                    </div>
                    <div class="action-bar">
                        <!-- search form -->
                        <form action="${pageContext.request.contextPath}/warehouse-management/category-management" method="get" class="search-form">
                            <input type="text" name="searchValue" value="${searchValue}" placeholder="Search by ID or Name">
                            <button type="submit">Search</button>
                            <button type="button" class="btn-reset" onclick="window.location.href = '${pageContext.request.contextPath}/warehouse-management/category-management'">Reset</button>
                        </form>
                        <!-- add button -->
                        <form action="${pageContext.request.contextPath}/warehouse-management/add-category" method="get" style="display:inline;">
                            <button type="submit" class="btn btn-success">
                                + Add Category
                            </button>
                        </form>
                    </div>
                </div>

                <div class="card-body">
                    <!-- Add category message -->
                    <c:if test="${param.msg == 'added'}">
                        <div class="alert alert-success">
                            Category added successfully.
                        </div>
                    </c:if>

                    <!-- Edit category message -->
                    <c:if test="${param.msg == 'updated'}">
                        <div class="alert alert-success">
                            Category updated successfully.
                        </div>
                    </c:if>

                    <c:if test="${param.msg == 'updateFail'}">
                        <div class="alert alert-success">
                            Failed to update category. Please try again.
                        </div>
                    </c:if>

                    <c:if test="${param.msg == 'invalid'}">
                        <div class="alert alert-danger">
                            Invalid category ID.
                        </div>
                    </c:if>

                    <c:if test="${param.msg == 'notFound'}">
                        <div class="alert alert-danger">
                            Category not found.
                        </div>
                    </c:if>

                    <c:if test="${param.msg == 'error'}">
                        <div class="alert alert-danger">
                            An unexpected error occurred.
                        </div>
                    </c:if>
                    
                    <div class="table-wrapper">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Code</th>
                                    <th>Category Name</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th>Created At</th>
                                    <th>Updated At</th>
                                    <th style="text-align:center;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="category" items="${categoryList}">
                                    <tr>
                                        <td>${category.categoryId}</td>
                                        <td>${category.code}</td>
                                        <td>${category.name}</td>
                                        <td>${category.description}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${category.status}">
                                                    <span class="status-pill status-active">Active</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-pill status-inactive">Inactive</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${category.createdAt}</td>
                                        <td>${category.updatedAt}</td>

                                        <td style="text-align:center;">
                                            <form method="get" action="${pageContext.request.contextPath}/warehouse-management/edit-category" style="display:inline;">
                                                <input type="hidden" name="id" value="${category.categoryId}">
                                                <button type="submit" class="btn btn-secondary btn-sm">Edit</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty categoryList}">
                                    <tr><td colspan="4" style="text-align:center; color:var(--gray-500);">No category found.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/components/footer.jsp" />
    </body>
</html>