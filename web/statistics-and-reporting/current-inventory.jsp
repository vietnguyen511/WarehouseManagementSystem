<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="current-inventory" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Current Inventory - Warehouse Management System</title>

    <!-- Shared Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">

    <!-- Shared JavaScript -->
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    <style>
        html, body { height: 100%; overflow: hidden; }
        .main-content-with-sidebar { 
            height: calc(100vh - 60px); 
            overflow-y: auto; 
            overflow-x: hidden; 
            padding-bottom: 80px; 
            padding-left: var(--spacing-md); 
            padding-right: var(--spacing-md); 
            padding-top: var(--spacing-md);
        }
        .main-content-with-sidebar > .card, .main-content-with-sidebar > * { max-width: 100%; }
        .app-footer { position: fixed; bottom: 0; z-index: 100; }
        .pagination { display: flex; gap: 0.25rem; list-style: none; padding: 0; margin: 0.75rem 0 0; }
        .pagination a, .pagination span { display: flex; align-items: center; justify-content: center; min-width: 30px; height: 30px; padding: 0; border: 1px solid #e2e8f0; border-radius: 6px; text-decoration: none; color: #334155; font-size: 0.8125rem; }
        .pagination .active { background: #2563eb; color: #fff; border-color: #2563eb; }
        .pagination .disabled { opacity: 0.5; pointer-events: none; }
        
        /* Smooth scrolling */
        .main-content-with-sidebar::-webkit-scrollbar { width: 8px; }
        .main-content-with-sidebar::-webkit-scrollbar-track { background: #f1f5f9; }
        .main-content-with-sidebar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
        .main-content-with-sidebar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        
        /* Low stock alert styles */
        .alert-banner { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: white; padding: 1rem 1.5rem; border-radius: 8px; margin-bottom: 1.5rem; display: flex; align-items: center; gap: 1rem; box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3); }
        .alert-banner svg { flex-shrink: 0; }
        .low-stock-row { background-color: #fef3c7 !important; }
        .btn-restock { background: #10b981; color: white; padding: 0.4rem 0.75rem; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8125rem; display: inline-flex; align-items: center; gap: 0.375rem; transition: all 0.2s; text-decoration: none; }
        .btn-restock:hover { background: #059669; transform: translateY(-1px); box-shadow: 0 2px 6px rgba(16, 185, 129, 0.3); }
        .filter-checkbox { display: flex; align-items: center; gap: 0.5rem; padding: 0.75rem; background: #f8fafc; border-radius: 6px; }
        .filter-checkbox input[type="checkbox"] { width: 18px; height: 18px; cursor: pointer; }
        .filter-checkbox label { cursor: pointer; font-weight: 500; margin: 0; }
        .threshold-input { width: 80px; padding: 0.4rem; border: 1px solid #cbd5e1; border-radius: 4px; }
        
        /* Table wrapper scrolling */
        .table-wrapper { overflow-x: auto; margin-top: 1rem; }
        .table-wrapper::-webkit-scrollbar { height: 8px; }
        .table-wrapper::-webkit-scrollbar-track { background: #f1f5f9; }
        .table-wrapper::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
        .table-wrapper::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
    </style>
</head>
<body>
    <!-- Include Top Header Component -->
    <jsp:include page="/components/top-header.jsp" />

    <!-- Include Sidebar Navigation Component -->
    <jsp:include page="/components/sidebar-nav.jsp" />

    <!-- Main Content Area with Sidebar Layout -->
    <div class="main-content-with-sidebar">

        <!-- Low Stock Alert Banner -->
        <c:if test="${!lowStockOnly && lowStockCount > 0}">
            <div class="alert-banner">
                <svg width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                    <line x1="12" y1="9" x2="12" y2="13"></line>
                    <line x1="12" y1="17" x2="12.01" y2="17"></line>
                </svg>
                <div style="flex: 1;">
                    <strong>Low Stock Alert!</strong> You have <strong>${lowStockCount}</strong> product(s) running low on inventory.
                </div>
                <a href="${pageContext.request.contextPath}/current-inventory?lowStockOnly=true&threshold=${threshold}" 
                   class="btn btn-sm" style="background: white; color: #d97706; border: none;">
                    View Low Stock Items
                </a>
            </div>
        </c:if>

        <!-- Page Header -->
        <div class="card">
            <div class="card-header">
                <div>
                    <h1 class="card-title">Current Inventory</h1>
                    <p class="card-subtitle">View and manage your warehouse stock levels</p>
                </div>
                <div class="d-flex gap-2">
                    <c:if test="${not empty inventoryList}">
                        <span class="badge badge-primary">${fn:length(inventoryList)} Products</span>
                    </c:if>
                </div>
            </div>

            <div class="card-body">
                <!-- Error Message -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> ${errorMessage}
                    </div>
                </c:if>

                <!-- Filter Form -->
                <form class="filter-bar" method="get" action="${pageContext.request.contextPath}/current-inventory" style="display: flex; flex-wrap: wrap; gap: 1rem; align-items: flex-end; width: 100%;">
                    <div class="form-group" style="flex: 0 0 auto; min-width: 180px;">
                        <label class="form-label" for="categoryId">Category</label>
                        <select name="categoryId" id="categoryId" class="form-select">
                            <option value="">All Categories</option>
                            <c:forEach var="c" items="${categories}">
                                <option value="${c.categoryId}" <c:if test='${param.categoryId == c.categoryId}'>selected</c:if>>
                                    ${c.code} - ${c.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group" style="flex: 1 1 auto; min-width: 250px;">
                        <label class="form-label" for="searchQuery">Search</label>
                        <input
                            type="text"
                            name="q"
                            id="searchQuery"
                            class="form-input"
                            value="${fn:escapeXml(param.q)}"
                            placeholder="Search by product code or name..."
                            style="width: 100%;"
                        />
                    </div>

                    <div class="form-group" style="flex: 0 0 auto; width: 120px;">
                        <label class="form-label">Alert Threshold</label>
                        <input
                            type="number"
                            name="threshold"
                            class="threshold-input"
                            value="${threshold}"
                            min="1"
                            max="1000"
                            style="width: 100%;"
                        />
                    </div>

                    <div style="display: flex; gap: 0.5rem; flex: 0 0 auto;">
                        <button type="submit" class="btn btn-primary" style="white-space: nowrap;">
                            <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16" style="margin-right: 0.25rem;">
                                <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
                            </svg>
                            Filter
                        </button>
                        <a href="${pageContext.request.contextPath}/current-inventory" class="btn btn-secondary" style="white-space: nowrap;">
                            Reset
                        </a>
                    </div>
                    
                    <!-- Low Stock Only Filter -->
                    <div class="filter-checkbox" style="flex-basis: 100%; margin-top: 0.5rem;">
                        <input 
                            type="checkbox" 
                            name="lowStockOnly" 
                            id="lowStockOnly" 
                            value="true"
                            ${lowStockOnly ? 'checked' : ''}
                            onchange="this.form.submit()"
                        />
                        <label for="lowStockOnly">Show Low Stock Items Only (≤ ${threshold})</label>
                    </div>
                </form>

                <!-- Inventory Table -->
                <div class="table-wrapper">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Product Code</th>
                                <th>Product Name</th>
                                <th>Category</th>
                                <th style="text-align: right;">Quantity</th>
                                <th>Unit</th>
                                <th style="text-align: right;">Unit Price</th>
                                <th style="text-align: right;">Total Value</th>
                                <th style="text-align: center;">Status</th>
                                <th style="text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty inventoryList}">
                                    <c:forEach var="item" items="${inventoryList}">
                                        <tr class="${item.quantityOnHand > 0 && item.quantityOnHand <= threshold ? 'low-stock-row' : ''}">
                                            <td>
                                                <span class="font-medium">${item.productCode}</span>
                                            </td>
                                            <td>
                                                <div class="font-medium">${item.productName}</div>
                                            </td>
                                            <td>
                                                <span class="text-muted">${item.categoryCode} - ${item.categoryName}</span>
                                            </td>
                                            <td style="text-align: center;">
                                                <span class="font-semibold">${item.quantityOnHand}</span>
                                            </td>
                                            <td>
                                                ${item.unitName}
                                            </td>
                                            <td style="text-align: right;">
                                                <fmt:formatNumber value="${item.importPrice}" type="currency" currencySymbol="$" />
                                            </td>
                                            <td style="text-align: right;">
                                                <span class="font-semibold">
                                                    <fmt:formatNumber value="${item.inventoryValue}" type="currency" currencySymbol="$" />
                                                </span>
                                            </td>
                                            <td style="text-align: center;">
                                                <c:choose>
                                                    <c:when test="${item.quantityOnHand == 0}">
                                                        <span class="badge badge-danger">Out of Stock</span>
                                                    </c:when>
                                                    <c:when test="${item.quantityOnHand > 0 && item.quantityOnHand <= threshold}">
                                                        <span class="badge badge-warning">Low Stock</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-success">In Stock</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="text-align: center;">
                                                <c:if test="${item.quantityOnHand <= threshold}">
                                                    <a href="${pageContext.request.contextPath}/createImportReceipt?productId=${item.productId}" 
                                                       class="btn-restock"
                                                       title="Restock this product">
                                                        <svg width="14" height="14" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                                                            <line x1="12" y1="5" x2="12" y2="19"></line>
                                                            <line x1="5" y1="12" x2="19" y2="12"></line>
                                                        </svg>
                                                        Restock
                                                    </a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="9" class="table-empty">
                                            <svg width="48" height="48" fill="currentColor" viewBox="0 0 16 16" style="opacity: 0.3; margin-bottom: 0.5rem;">
                                                <path d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 1 0z"/>
                                            </svg>
                                            <div>No inventory data found.</div>
                                            <div class="text-muted" style="font-size: 0.8125rem; margin-top: 0.25rem;">
                                                Try adjusting your filters or search criteria.
                                            </div>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Summary Footer & Pagination -->
                <c:if test="${not empty inventoryList}">
                    <div class="card-footer">
                        <div class="d-flex justify-content-between align-items-center">
                            <div class="text-muted">
                                Showing page ${page} of ${totalPages} • ${totalItems} product(s)
                            </div>
                            <div class="d-flex gap-3">
                                <c:if test="${not empty totalValue}">
                                    <div>
                                        <span class="text-muted">Total Inventory Value:</span>
                                        <span class="font-semibold">
                                            <fmt:formatNumber value="${totalValue}" type="currency" currencySymbol="$" />
                                        </span>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <ul class="pagination">
                            <li>
                                <a class="${page <= 1 ? 'disabled' : ''}" href="${pageContext.request.contextPath}/current-inventory?categoryId=${param.categoryId}&q=${fn:escapeXml(param.q)}&lowStockOnly=${lowStockOnly}&threshold=${threshold}&page=${page-1}&pageSize=${pageSize}">Prev</a>
                            </li>
                            <c:forEach var="p" begin="1" end="${totalPages}">
                                <li>
                                    <a class="${p == page ? 'active' : ''}" href="${pageContext.request.contextPath}/current-inventory?categoryId=${param.categoryId}&q=${fn:escapeXml(param.q)}&lowStockOnly=${lowStockOnly}&threshold=${threshold}&page=${p}&pageSize=${pageSize}"><c:out value="${p}"/></a>
                                </li>
                            </c:forEach>
                            <li>
                                <a class="${page >= totalPages ? 'disabled' : ''}" href="${pageContext.request.contextPath}/current-inventory?categoryId=${param.categoryId}&q=${fn:escapeXml(param.q)}&lowStockOnly=${lowStockOnly}&threshold=${threshold}&page=${page+1}&pageSize=${pageSize}">Next</a>
                            </li>
                        </ul>
                    </div>
                </c:if>

            </div>
        </div>

    </div>

    <!-- Include Footer Component -->
    <jsp:include page="/components/footer.jsp" />
</body>
</html>


