<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="baseUrl" value="?page=" />
<c:set var="searchParam" value="${not empty searchTerm ? '&search=' : ''}${searchTerm}" />
<c:set var="dateParam" value="${not empty dateFilter ? '&dateFilter=' : ''}${dateFilter}" />
<c:set var="fullParams" value="${searchParam}${dateParam}" />
<c:set var="activePage" value="import-receipts" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Import Receipts List - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    <style>
        /* Allow page scrolling */
        html, body { height: auto; overflow: auto; }
        .main-content-with-sidebar { min-height: auto; overflow: visible; padding-left: var(--spacing-md); padding-right: var(--spacing-md); padding-bottom: var(--spacing-xl); flex: 1; }
        .main-content-with-sidebar > .card, .main-content-with-sidebar > * { max-width: 100%; }
        .app-footer { position: static; }
        
        /* Table styling */
        .import-receipts-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: var(--spacing-md);
        }
        
        .import-receipts-table th {
            background-color: var(--gray-50);
            color: var(--gray-700);
            font-weight: 600;
            text-align: left;
            padding: var(--spacing-md);
            border-bottom: 2px solid var(--gray-200);
            font-size: 0.875rem;
        }
        
        .import-receipts-table td {
            padding: var(--spacing-md);
            border-bottom: 1px solid var(--gray-200);
            vertical-align: middle;
        }
        
        .import-receipts-table tbody tr:hover {
            background-color: var(--gray-50);
        }
        
        .import-receipts-table tbody tr:nth-child(even) {
            background-color: var(--gray-25);
        }
        
        .import-receipts-table tbody tr:nth-child(even):hover {
            background-color: var(--gray-100);
        }
        
        /* Column widths */
        .import-receipts-table th:nth-child(1), .import-receipts-table td:nth-child(1) { width: 12%; } /* Mã phiếu */
        .import-receipts-table th:nth-child(2), .import-receipts-table td:nth-child(2) { width: 15%; } /* Ngày nhập */
        .import-receipts-table th:nth-child(3), .import-receipts-table td:nth-child(3) { width: 20%; } /* Nhà cung cấp */
        .import-receipts-table th:nth-child(4), .import-receipts-table td:nth-child(4) { width: 12%; text-align: right; } /* Tổng số lượng */
        .import-receipts-table th:nth-child(5), .import-receipts-table td:nth-child(5) { width: 18%; text-align: right; } /* Tổng giá trị */
        .import-receipts-table th:nth-child(6), .import-receipts-table td:nth-child(6) { width: 15%; } /* Người nhập */
        .import-receipts-table th:nth-child(7), .import-receipts-table td:nth-child(7) { width: 8%; text-align: center; } /* Actions */
        
        /* Status badges */
        .status-badge {
            display: inline-block;
            padding: 0.25rem 0.5rem;
            border-radius: var(--radius-sm);
            font-size: 0.75rem;
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 0.025em;
        }
        
        .status-completed {
            background-color: var(--success-100);
            color: var(--success-700);
        }
        
        /* Action buttons */
        .action-buttons {
            display: flex;
            gap: var(--spacing-xs);
            justify-content: center;
        }
        
        .btn-detail {
            background-color: var(--primary-600);
            color: white;
            border: none;
            padding: 0.375rem 0.75rem;
            border-radius: var(--radius-sm);
            font-size: 0.875rem;
            font-weight: 500;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            transition: all 0.2s ease;
        }
        
        .btn-detail:hover {
            background-color: var(--primary-700);
            transform: translateY(-1px);
        }
        
        /* Empty state */
        .empty-state {
            text-align: center;
            padding: var(--spacing-xl);
            color: var(--gray-500);
        }
        
        .empty-state-icon {
            width: 64px;
            height: 64px;
            margin: 0 auto var(--spacing-md);
            opacity: 0.5;
        }
        
        /* Search and filter bar */
        .search-filter-bar {
            display: flex;
            gap: var(--spacing-md);
            align-items: center;
            margin-bottom: var(--spacing-md);
            padding: var(--spacing-md);
            background-color: var(--gray-50);
            border-radius: var(--radius-lg);
            border: 1px solid var(--gray-200);
        }
        
        .search-input {
            flex: 1;
            max-width: 300px;
        }
        
        .filter-select {
            min-width: 150px;
        }
        
        /* Pagination styles */
        .pagination-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: var(--spacing-lg);
            padding: var(--spacing-md);
            background-color: var(--gray-50);
            border-radius: var(--radius-lg);
            border: 1px solid var(--gray-200);
        }
        
        .pagination-info {
            color: var(--gray-600);
            font-size: 0.875rem;
        }
        
        .pagination {
            display: flex;
            gap: var(--spacing-xs);
            align-items: center;
        }
        
        .pagination button,
        .pagination a {
            padding: 0.5rem 0.75rem;
            border: 1px solid var(--gray-300);
            background-color: white;
            color: var(--gray-700);
            border-radius: var(--radius-sm);
            cursor: pointer;
            font-size: 0.875rem;
            transition: all 0.2s ease;
            text-decoration: none;
            display: inline-block;
        }
        
        .pagination button:hover:not(:disabled),
        .pagination a:hover {
            background-color: var(--primary-50);
            border-color: var(--primary-300);
            color: var(--primary-700);
        }
        
        .pagination button.active,
        .pagination .btn-primary {
            background-color: var(--primary-600);
            border-color: var(--primary-600);
            color: white;
        }
        
        .pagination button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        
        .pagination .btn-secondary {
            background-color: white;
            border-color: var(--gray-300);
            color: var(--gray-700);
        }
        
        .pagination .btn-secondary:hover {
            background-color: var(--primary-50);
            border-color: var(--primary-300);
            color: var(--primary-700);
        }
        
        .pagination .page-numbers {
            display: flex;
            gap: var(--spacing-xs);
            align-items: center;
        }
        
        .pagination .ellipsis {
            color: var(--gray-500);
            font-size: 0.875rem;
            padding: 0 0.25rem;
        }
        
        /* Responsive design */
        @media (max-width: 768px) {
            .import-receipts-table {
                font-size: 0.875rem;
            }
            
            .import-receipts-table th,
            .import-receipts-table td {
                padding: var(--spacing-sm);
            }
            
            .search-filter-bar {
                flex-direction: column;
                align-items: stretch;
            }
            
            .search-input,
            .filter-select {
                max-width: none;
                min-width: auto;
            }
            
            .pagination-container {
                flex-direction: column;
                gap: var(--spacing-sm);
                text-align: center;
            }
            
            .pagination {
                justify-content: center;
            }
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
                    <h1 class="card-title">Import Receipts</h1>
                    <p class="card-subtitle">History of all import receipts</p>
                </div>
                <div class="action-bar">
                    <a href="${pageContext.request.contextPath}/warehouse-import-mgt/add-import-receipt.jsp" class="btn btn-success">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"></line>
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                        </svg>
                        Add New Receipt
                    </a>
                </div>
            </div>

            <div class="card-body">
                <!-- Search and Filter Bar -->
                <form method="get" action="" id="searchForm">
                    <div class="search-filter-bar">
                        <div class="search-input">
                            <input type="text" id="searchInput" name="search" class="form-input" 
                                   placeholder="Search by receipt ID, supplier, or user..." 
                                   value="${searchTerm}">
                        </div>
                        <div class="filter-select">
                            <select id="dateFilter" name="dateFilter" class="form-select">
                                <option value="">All Dates</option>
                                <option value="today" ${dateFilter == 'today' ? 'selected' : ''}>Today</option>
                                <option value="week" ${dateFilter == 'week' ? 'selected' : ''}>This Week</option>
                                <option value="month" ${dateFilter == 'month' ? 'selected' : ''}>This Month</option>
                                <option value="quarter" ${dateFilter == 'quarter' ? 'selected' : ''}>This Quarter</option>
                            </select>
                        </div>
                        <button type="button" id="searchBtn" class="btn btn-primary btn-sm">Search</button>
                        <button type="button" id="clearFilters" class="btn btn-secondary btn-sm">Clear</button>
                    </div>
                </form>

                <!-- Import Receipts Table -->
                <c:choose>
                    <c:when test="${not empty importReceipts}">
                        <div class="table-wrapper">
                            <table class="import-receipts-table" id="importReceiptsTable">
                                <thead>
                                    <tr>
                                        <th>Receipt ID</th>
                                        <th>Import Date</th>
                                        <th>Supplier</th>
                                        <th>Quantity</th>
                                        <th>Total Amount</th>
                                        <th>Created By</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="receipt" items="${importReceipts}">
                                        <tr>
                                            <td>
                                                <strong>#${receipt.importId}</strong>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${receipt.date}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td>
                                                <div class="font-medium">${receipt.supplierName}</div>
                                            </td>
                                            <td>
                                                <span class="font-semibold">
                                                    <fmt:formatNumber value="${receipt.totalQuantity}" type="number" />
                                                </span>
                                            </td>
                                            <td>
                                                <span class="font-semibold text-success">
                                                    $<fmt:formatNumber value="${receipt.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                                </span>
                                            </td>
                                            <td>
                                                <div class="font-medium">${receipt.userName}</div>
                                            </td>
                                            <td>
                                                <div class="action-buttons">
                                                    <a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-detail?importId=${receipt.importId}" 
                                                       class="btn-detail" title="View Details">
                                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                                            <circle cx="12" cy="12" r="3"></circle>
                                                        </svg>
                                                        Details
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        
                        <!-- Pagination -->
                        <div class="pagination-container">
                            <div class="pagination-info">
                                Showing <span>${startRecord}</span> to <span>${endRecord}</span> of <span>${totalRecords}</span> records
                            </div>
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <a href="${baseUrl}${currentPage - 1}${fullParams}" class="btn btn-secondary">Previous</a>
                                </c:if>
                                <c:if test="${currentPage <= 1}">
                                    <button class="btn btn-secondary" disabled>Previous</button>
                                </c:if>
                                
                                <div class="page-numbers">
                                    <c:choose>
                                        <c:when test="${totalPages <= 5}">
                                            <!-- Show all pages if total pages <= 5 -->
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <c:choose>
                                                    <c:when test="${i == currentPage}">
                                                        <span class="btn btn-primary">${i}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${baseUrl}${i}${fullParams}" class="btn btn-secondary">${i}</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <!-- Show first page -->
                                            <c:choose>
                                                <c:when test="${currentPage == 1}">
                                                    <span class="btn btn-primary">1</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${baseUrl}1${fullParams}" class="btn btn-secondary">1</a>
                                                </c:otherwise>
                                            </c:choose>
                                            
                                            <!-- Show ellipsis if current page is far from start -->
                                            <c:if test="${currentPage > 3}">
                                                <span class="ellipsis">...</span>
                                            </c:if>
                                            
                                            <!-- Show pages around current page (max 3 pages) -->
                                            <c:choose>
                                                <c:when test="${currentPage <= 3}">
                                                    <!-- Show pages 2, 3 if current page is near start -->
                                                    <c:forEach begin="2" end="${Math.min(3, totalPages - 1)}" var="i">
                                                        <c:choose>
                                                            <c:when test="${i == currentPage}">
                                                                <span class="btn btn-primary">${i}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${baseUrl}${i}${fullParams}" class="btn btn-secondary">${i}</a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>
                                                </c:when>
                                                <c:when test="${currentPage >= totalPages - 2}">
                                                    <!-- Show pages near end if current page is near end -->
                                                    <c:forEach begin="${Math.max(2, totalPages - 2)}" end="${totalPages - 1}" var="i">
                                                        <c:choose>
                                                            <c:when test="${i == currentPage}">
                                                                <span class="btn btn-primary">${i}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${baseUrl}${i}${fullParams}" class="btn btn-secondary">${i}</a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <!-- Show current page and 1 page before/after -->
                                                    <c:forEach begin="${currentPage - 1}" end="${currentPage + 1}" var="i">
                                                        <c:choose>
                                                            <c:when test="${i == currentPage}">
                                                                <span class="btn btn-primary">${i}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${baseUrl}${i}${fullParams}" class="btn btn-secondary">${i}</a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                            
                                            <!-- Show ellipsis if current page is far from end -->
                                            <c:if test="${currentPage < totalPages - 2}">
                                                <span class="ellipsis">...</span>
                                            </c:if>
                                            
                                            <!-- Show last page -->
                                            <c:if test="${totalPages > 1}">
                                                <c:choose>
                                                    <c:when test="${totalPages == currentPage}">
                                                        <span class="btn btn-primary">${totalPages}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="${baseUrl}${totalPages}${fullParams}" class="btn btn-secondary">${totalPages}</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <c:if test="${currentPage < totalPages}">
                                    <a href="${baseUrl}${currentPage + 1}${fullParams}" class="btn btn-secondary">Next</a>
                                </c:if>
                                <c:if test="${currentPage >= totalPages}">
                                    <button class="btn btn-secondary" disabled>Next</button>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                                <polyline points="9 22 9 12 15 12 15 22"></polyline>
                            </svg>
                            <h3>No Import Receipts Found</h3>
                            <p>There are no import receipts in the system yet.</p>
                            <a href="${pageContext.request.contextPath}/warehouse-import-mgt/add-import-receipt.jsp" class="btn btn-success">
                                Create First Receipt
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger" style="margin-top: var(--spacing-md);">
                        ${error}
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const searchForm = document.getElementById('searchForm');
            const searchInput = document.getElementById('searchInput');
            const dateFilter = document.getElementById('dateFilter');
            const searchBtn = document.getElementById('searchBtn');
            const clearFilters = document.getElementById('clearFilters');
            
            // Search functionality - submit form with search parameters
            function performSearch() {
                if (searchForm) {
                    searchForm.submit();
                }
            }
            
            // Event listeners
            if (searchInput) {
                searchInput.addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        performSearch();
                    }
                });
            }
            
            if (searchBtn) {
                searchBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    performSearch();
                });
            }
            
            if (dateFilter) {
                dateFilter.addEventListener('change', function(e) {
                    e.preventDefault();
                    performSearch();
                });
            }
            
            if (clearFilters) {
                clearFilters.addEventListener('click', function(e) {
                    e.preventDefault();
                    // Clear the form inputs
                    if (searchInput) {
                        searchInput.value = '';
                    }
                    if (dateFilter) {
                        dateFilter.value = '';
                    }
                    // Submit empty form to clear all filters
                    if (searchForm) {
                        searchForm.submit();
                    }
                });
            }
        });
    </script>
</body>
</html>
