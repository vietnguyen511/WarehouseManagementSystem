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

            .card-header-left .card-title {
                font-size: 1.2rem;
                font-weight: 600;
                margin: 0;
                color: var(--gray-900);
            }

            .card-header-left .card-subtitle {
                margin: .25rem 0 0 0;
                color: var(--gray-500);
                font-size: .9rem;
                line-height: 1.4;
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
                min-width: 900px;
            }

            th, td {
                padding: 0.75rem;
                border-bottom: 1px solid var(--gray-200);
                text-align: left;
                font-size: 0.9rem;
                color: var(--gray-800);
            }

            th {
                background: var(--gray-50);
                font-weight: 600;
                color: var(--gray-700);
                white-space: nowrap;
            }

            tbody tr:hover {
                background-color: var(--gray-50);
            }

            .action-bar {
                display: flex;
                flex-wrap: wrap;
                gap: var(--spacing-sm);
                align-items: flex-start;
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
                min-width: 140px;
            }

            .search-form button,
            .btn-reset {
                background: var(--primary-600);
                color: white;
                border: none;
                border-radius: 6px;
                padding: 6px 12px;
                cursor: pointer;
                font-size: .85rem;
                line-height: 1.2rem;
                transition: background 0.2s ease-in-out;
                text-decoration: none;
            }

            .search-form button:hover,
            .btn-reset:hover {
                background: var(--primary-700);
            }

            .search-form:focus-within {
                box-shadow: 0 0 0 2px var(--primary-200);
                background: #fff;
            }

            /* Common button base */
            button, .btn {
                font-size: 0.85rem;
                padding: 6px 12px;
                border: none;
                border-radius: 6px;
                font-weight: 500;
                cursor: pointer;
                text-decoration: none;
            }

            .btn-add {
                background-color: #059669; /* Màu xanh ngọc */
                color: white;
                border: none;
                border-radius: 6px;
                padding: 14px 16px;
                text-decoration: none;
                cursor: pointer;
                transition: none;
            }

            .btn-add:hover {
                background-color: #059669; /* Giữ nguyên màu khi hover */
                color: white;
            }

            /* RESET / SEARCH / PRIMARY */
            .btn-reset,
            .btn-primary {
                background-color: #3b82f6;
                color: #fff;
            }
            .btn-reset:hover,
            .btn-primary:hover {
                background-color: #3b82f6;
                color: #fff;
            }

            /* DELETE / DANGER (Đỏ) */
            .btn-danger {
                background-color: #dc2626;
                color: #fff;
            }
            .btn-danger:hover {
                background-color: #dc2626;
                color: #fff;
            }

            /* SECONDARY (xám, có viền) */
            .btn-secondary {
                border: 1px solid #d1d5db;
                border-radius: 6px;
            }

            .btn-secondary:hover {
                border: 1px solid #d1d5db;
            }

        </style>
    </head>
    <body>

        <!-- top header -->
        <jsp:include page="/components/top-header.jsp" />
        <!-- sidebar -->
        <jsp:include page="/components/sidebar-nav.jsp" />

        <div class="main-content-with-sidebar">
            <div class="card">

                <!-- ========== CARD HEADER ========== -->
                <div class="card-header">
                    <div class="card-header-left">
                        <h1 class="card-title">Product Management</h1>
                        <p class="card-subtitle">Manage products in the warehouse</p>                       
                    </div>

                    <div class="action-bar">

                        <!-- search form -->
                        <form action="${pageContext.request.contextPath}/warehouse-management/product-management" method="get" class="search-form">
                            <input type="text"
                                   name="searchValue"
                                   value="${searchValue}"
                                   placeholder="Search by ID or Name">
                            <button type="submit">Search</button>

                            <a class="btn-reset"
                               href="${pageContext.request.contextPath}/warehouse-management/product-management">
                                Reset
                            </a>
                        </form>

                        <!-- add product button -->
                        <form action="${pageContext.request.contextPath}/warehouse-management/add-product" method="get" style="display:inline;">
                            <button type="submit" class="btn-add">
                                + Add Product
                            </button>
                        </form>
                    </div>
                </div>

                <!-- ========== CARD BODY / TABLE ========== -->
                <div class="card-body">
                    <!-- server-pushed messages (optional if you're also using ?msg=...) -->
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success">${successMessage}</div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>

                    <!-- flash message from redirect (?msg=created / updated / deleted) -->
                    <c:if test="${param.msg == 'created'}">
                        <div class="alert alert-success" style="margin-top:0.75rem;">
                            Product created successfully.
                        </div>
                    </c:if>
                    <c:if test="${param.msg == 'delSuccess'}">
                        <div class="alert alert-success" style="margin-top:0.75rem;">
                            Product deleted successfully.
                        </div>
                    </c:if>
                    <c:if test="${param.msg == 'delBlocked'}">
                        <div class="alert alert-danger" style="margin-top:0.75rem;">
                            Cannot delete this product because it has related data (variants / import / export).
                        </div>
                    </c:if>
                    <c:if test="${param.msg == 'delInvalid'}">
                        <div class="alert alert-danger" style="margin-top:0.75rem;">
                            Invalid product ID.
                        </div>
                    </c:if>
                    <c:if test="${param.msg == 'delError'}">
                        <div class="alert alert-danger" style="margin-top:0.75rem;">
                            An unexpected error occurred while deleting the product.
                        </div>
                    </c:if>
                    <c:if test="${param.msg == 'editSuccess'}">
                        <div class="alert alert-success" style="margin-top:0.75rem;">
                            Product updated successfully.
                        </div>
                    </c:if>

                    <c:if test="${param.msg == 'editError'}">
                        <div class="alert alert-danger" style="margin-top:0.75rem;">
                            Failed to open Edit Product screen.
                        </div>
                    </c:if>
                    <c:if test="${param.msg == 'editFail'}">
                        <div class="alert alert-danger" style="margin-top:0.75rem;">
                            Failed to update product. Please try again.
                        </div>
                    </c:if>
                    <div class="table-wrapper">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Material</th>
                                    <th>Unit</th>
                                    <th>Quantity</th>
                                    <th>Import Price</th>
                                    <th>Export Price</th>
                                    <th>Status</th>
                                    <th>Category</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="product" items="${products}">
                                    <tr>
                                        <td>${product.productId}</td>
                                        <td>${product.code}</td>
                                        <td>${product.name}</td>
                                        <td>${product.material}</td>
                                        <td>${product.unit}</td>
                                        <td>${product.quantity}</td>
                                        <td>${product.importPrice}</td>
                                        <td>${product.exportPrice}</td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${product.status}">
                                                    <span class="status-pill status-active">Active</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-pill status-inactive">Inactive</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>${product.categoryName}</td>

                                        <td>
                                            <form method="get"
                                                  action="${pageContext.request.contextPath}/warehouse-management/view-product-details"
                                                  style="display:inline;">
                                                <input type="hidden" name="id" value="${product.productId}">
                                                <button type="submit" class="btn-secondary">View</button>
                                            </form>

                                            <form method="get"
                                                  action="${pageContext.request.contextPath}/warehouse-management/edit-product"
                                                  style="display:inline;">
                                                <input type="hidden" name="id" value="${product.productId}">
                                                <button type="submit" class="btn-secondary">Edit</button>
                                            </form>

                                            <form method="post" 
                                                  action="${pageContext.request.contextPath}/warehouse-management/delete-product"
                                                  style="display:inline;"
                                                  onsubmit="return confirm('Are you sure you want to delete this product?');">
                                                <input type="hidden" name="id" value="${product.productId}">
                                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <c:if test="${empty products}">
                                    <tr class="no-data-row">
                                        <td colspan="11">No product found.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/components/footer.jsp"/>
    </body>
</html>
