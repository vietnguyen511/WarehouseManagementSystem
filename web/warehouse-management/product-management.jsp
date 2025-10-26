<%-- 
    Document   : product-management
    Created on : Oct 17, 2025, 6:54:41 AM
    Author     : DANG
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Product Management - Warehouse Management System</title>

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
            .btn-refresh {
                background: white;
                color: var(--primary-600);
                border: 1px solid var(--primary-300);
                border-radius: 6px;
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
                    text-decoration: none;
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

            <jsp:include page="/components/top-header.jsp" />

            <jsp:include page="/components/sidebar-nav.jsp" />

            <div class="main-content-with-sidebar">
                <div class="card">
                    <div class="card-header">
                        <div>
                            <h1 class="card-title">Product Management</h1>
                            <p class="card-subtitle">Manage products in the warehouse</p>
                        </div>

                        <div class="action-bar">
                            <!-- search form -->
                            <form action="${pageContext.request.contextPath}/product-management" method="get" class="search-form">
                                <input type="text" name="searchValue" value="${searchValue}" placeholder="Search by ID or Name">
                                <button type="submit">Search</button>
                                <button type="button" class="btn-reset" onclick="window.location.href = '${pageContext.request.contextPath}/product-management'">Reset</button>
                            </form>
                            <!-- add button -->
                            <form action="${pageContext.request.contextPath}/add-product" method="get" style="display:inline;">
                                <button type="submit" class="btn btn-success">
                                    + Add Product
                                </button>
                            </form>
                        </div>
                    </div>
                    <!-- table product list-->
                    <div class="card-body">
                        <div class="table-wrapper">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Code</th>
                                        <th>Name</th>
                                        <th>Unit</th>
                                        <th>Quantity</th>
                                        <th>Import Price</th>
                                        <th>Export Price</th>
                                        <th>Status</th>
                                        <th>Category Name</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>

                                    <c:forEach var="product" items="${products}">
                                        <tr>
                                            <td>${product.productId}</td>
                                            <td>${product.code}</td>
                                            <td>${product.name}</td>
                                            <td>${product.unit}</td>
                                            <td>${product.quantity}</td>
                                            <td>${product.importPrice}</td>
                                            <td>${product.exportPrice}</td>
                                            <td>${product.status}</td>
                                            <td>${product.categoryName}</td>
                                            <td>
                                                <form method="get" action="${pageContext.request.contextPath}/view-product-details" style="display:inline;">
                                                    <input type="hidden" name="id" value="${product.productId}">
                                                    <button type="submit" class="btn btn-secondary btn-sm">View Details</button>
                                                </form>
                                                <form method="get" action="${pageContext.request.contextPath}/edit-product" style="display:inline;">
                                                    <input type="hidden" name="id" value="${product.productId}">
                                                    <button type="submit" class="btn btn-secondary btn-sm">Edit</button>
                                                </form>
                                                <form method="post" action="${pageContext.request.contextPath}/delete-product" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this product?');">
                                                    <input type="hidden" name="id" value="${product.productId}">
                                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty products}">
                                        <tr><td colspan="4" style="text-align:center;
                                                color:var(--gray-500);">No product found.</td></tr>
                                        </c:if>
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
            <jsp:include page="/components/footer.jsp"/>
        </body>
    </html>