<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="revenue-report" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Revenue Report - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
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
        
        .main-content-with-sidebar::-webkit-scrollbar { width: 8px; }
        .main-content-with-sidebar::-webkit-scrollbar-track { background: #f1f5f9; }
        .main-content-with-sidebar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
        .main-content-with-sidebar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        
        .stats-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 1rem; margin-bottom: 2rem; }
        .stat-card { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border-left: 4px solid #2563eb; }
        .stat-card.revenue { border-left-color: #10b981; }
        .stat-card.quantity { border-left-color: #3b82f6; }
        .stat-card.receipts { border-left-color: #8b5cf6; }
        .stat-card.average { border-left-color: #f59e0b; }
        .stat-card.top-product { border-left-color: #ef4444; }
        .stat-label { color: #64748b; font-size: 0.875rem; font-weight: 500; margin-bottom: 0.5rem; }
        .stat-value { font-size: 1.875rem; font-weight: 700; color: #1e293b; }
        .stat-sub { color: #94a3b8; font-size: 0.875rem; margin-top: 0.25rem; }
        
        .chart-container { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 1.5rem; }
        .chart-wrapper { position: relative; height: 400px; }
        .chart-title { font-size: 1.125rem; font-weight: 600; color: #1e293b; margin-bottom: 1rem; }
        
        .filter-bar-stats { display: flex; flex-wrap: wrap; gap: 1rem; align-items: flex-end; margin-bottom: 1.5rem; margin-top: 0; background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .date-input { padding: 0.5rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.9375rem; }
        
        .data-table { width: 100%; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .data-table th { background: #f8fafc; padding: 0.75rem; text-align: left; font-weight: 600; color: #475569; border-bottom: 2px solid #e2e8f0; }
        .data-table td { padding: 0.75rem; border-bottom: 1px solid #e2e8f0; }
        .data-table tr:hover { background: #f8fafc; }
        .positive { color: #10b981; font-weight: 600; }
        .negative { color: #ef4444; font-weight: 600; }

        /* Force correct sidebar highlighting for revenue-report page */
        .sidebar-sublink[href*="/revenue-report"] {
            color: var(--primary-600) !important;
            background: var(--gray-100) !important;
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
                    <h1 class="card-title">Revenue Report</h1>
                    <p class="card-subtitle">Analyze revenue trends and performance by date, product, and category</p>
                </div>
            </div>

            <div class="card-body" style="padding-top: 1rem;">
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> ${errorMessage}
                    </div>
                </c:if>

                <!-- Filter Form -->
                <form class="filter-bar-stats" method="get" action="${pageContext.request.contextPath}/revenue-report" style="display: flex; gap: 1rem; align-items: flex-end; width: 100%;">
                    <div class="form-group" style="flex: 1 1 0; min-width: 0;">
                        <label class="form-label">Start Date</label>
                        <input type="date" name="startDate" class="date-input" value="${startDate}" style="width: 100%;" />
                    </div>

                    <div class="form-group" style="flex: 1 1 0; min-width: 0;">
                        <label class="form-label">End Date</label>
                        <input type="date" name="endDate" class="date-input" value="${endDate}" style="width: 100%;" />
                    </div>

                    <div class="form-group" style="flex: 1 1 0; min-width: 0;">
                        <label class="form-label">Group By</label>
                        <select name="groupBy" class="form-select" style="width: 100%;">
                            <option value="day" ${groupBy == 'day' ? 'selected' : ''}>Daily</option>
                            <option value="month" ${groupBy == 'month' ? 'selected' : ''}>Monthly</option>
                            <option value="product" ${groupBy == 'product' ? 'selected' : ''}>By Product</option>
                            <option value="category" ${groupBy == 'category' ? 'selected' : ''}>By Category</option>
                        </select>
                    </div>

                    <div class="form-group" style="flex: 1 1 0; min-width: 0;">
                        <label class="form-label">Product</label>
                        <select name="productId" class="form-select" style="width: 100%;">
                            <option value="">All Products</option>
                            <c:forEach var="product" items="${products}">
                                <option value="${product.productId}" ${selectedProductId == product.productId ? 'selected' : ''}>
                                    ${product.code} - ${product.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group" style="flex: 1 1 0; min-width: 0;">
                        <label class="form-label">Category</label>
                        <select name="categoryId" class="form-select" style="width: 100%;">
                            <option value="">All Categories</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryId}" ${selectedCategoryId == category.categoryId ? 'selected' : ''}>
                                    ${category.code} - ${category.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div style="display: flex; gap: 0.5rem; flex: 0 0 auto; flex-shrink: 0; align-items: center; align-self: center; margin-top: 0.65rem;">
                        <button type="submit" class="btn btn-primary" style="white-space: nowrap;">Apply Filter</button>
                        <a href="${pageContext.request.contextPath}/revenue-report" class="btn btn-secondary" style="white-space: nowrap;">Reset</a>
                    </div>
                </form>

                <!-- Summary Statistics -->
                <div class="stats-grid">
                    <div class="stat-card revenue">
                        <div class="stat-label">Total Revenue</div>
                        <div class="stat-value"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="$" /></div>
                    </div>
                    <div class="stat-card quantity">
                        <div class="stat-label">Total Quantity</div>
                        <div class="stat-value">${totalQuantity}</div>
                        <!-- <div class="stat-sub">items sold</div> -->
                    </div>
                    <div class="stat-card receipts">
                        <div class="stat-label">Total Receipts</div>
                        <div class="stat-value">${totalReceipts}</div>
                        <!-- <div class="stat-sub">export receipts</div> -->
                    </div>
                    <div class="stat-card average">
                        <div class="stat-label">Avg Order Value</div>
                        <div class="stat-value"><fmt:formatNumber value="${averageOrderValue}" type="currency" currencySymbol="$" /></div>
                    </div>
                    <div class="stat-card top-product">
                        <div class="stat-label">Top Product Revenue</div>
                        <div class="stat-value"><fmt:formatNumber value="${topProductRevenue}" type="currency" currencySymbol="$" /></div>
                    </div>
                </div>

                <!-- Charts -->
                <c:if test="${groupBy == 'day' || groupBy == 'month'}">
                    <div class="chart-container">
                        <div class="chart-title">Revenue Trend Over Time</div>
                        <div class="chart-wrapper">
                            <canvas id="revenueTrendChart"></canvas>
                        </div>
                    </div>

                    <div class="chart-container">
                        <div class="chart-title">Quantity Sold Over Time</div>
                        <div class="chart-wrapper">
                            <canvas id="quantityTrendChart"></canvas>
                        </div>
                    </div>
                </c:if>

                <c:if test="${groupBy == 'product' || groupBy == 'category'}">
                    <div class="chart-container">
                        <div class="chart-title">Revenue by ${groupBy == 'product' ? 'Product' : 'Category'}</div>
                        <div class="chart-wrapper">
                            <canvas id="revenueByGroupChart"></canvas>
                        </div>
                    </div>

                    <div class="chart-container">
                        <div class="chart-title">Quantity by ${groupBy == 'product' ? 'Product' : 'Category'}</div>
                        <div class="chart-wrapper">
                            <canvas id="quantityByGroupChart"></canvas>
                        </div>
                    </div>
                </c:if>

                <!-- Data Table -->
                <div style="margin-top: 2rem;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <h3 style="font-size: 1.125rem; font-weight: 600; margin: 0;">Detailed Revenue Data</h3>
                        <div style="color: #64748b; font-size: 0.875rem;">
                            Showing ${(currentPage - 1) * 10 + 1} to ${(currentPage - 1) * 10 + fn:length(paginatedStatistics)} of ${totalRecords} records
                        </div>
                    </div>
                    <div style="overflow-x: auto;">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <c:choose>
                                        <c:when test="${groupBy == 'product'}">
                                            <th>Product Code</th>
                                            <th>Product Name</th>
                                            <th>Category</th>
                                            <th style="text-align: right;">Total Quantity</th>
                                            <th style="text-align: right;">Total Revenue</th>
                                            <th style="text-align: right;">Avg Value</th>
                                            <th style="text-align: right;">Receipts</th>
                                        </c:when>
                                        <c:when test="${groupBy == 'category'}">
                                            <th>Category Code</th>
                                            <th>Category Name</th>
                                            <th style="text-align: right;">Total Quantity</th>
                                            <th style="text-align: right;">Total Revenue</th>
                                            <th style="text-align: right;">Avg Value</th>
                                            <th style="text-align: right;">Receipts</th>
                                        </c:when>
                                        <c:otherwise>
                                            <th>Date</th>
                                            <th style="text-align: right;">Total Quantity</th>
                                            <th style="text-align: right;">Total Revenue</th>
                                            <th style="text-align: right;">Avg Value</th>
                                            <th style="text-align: right;">Receipts</th>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty paginatedStatistics}">
                                        <c:forEach var="stat" items="${paginatedStatistics}">
                                            <tr>
                                                <c:choose>
                                                    <c:when test="${groupBy == 'product'}">
                                                        <td><strong>${stat.productCode}</strong></td>
                                                        <td>${stat.productName}</td>
                                                        <td>${stat.categoryName}</td>
                                                        <td style="text-align: right;" class="positive">${stat.totalQuantity}</td>
                                                        <td style="text-align: right;" class="positive"><fmt:formatNumber value="${stat.totalValue}" type="currency" currencySymbol="$" /></td>
                                                        <td style="text-align: right;"><fmt:formatNumber value="${stat.averageValue}" type="currency" currencySymbol="$" /></td>
                                                        <td style="text-align: right;">${stat.receiptCount}</td>
                                                    </c:when>
                                                    <c:when test="${groupBy == 'category'}">
                                                        <td><strong>${stat.categoryCode}</strong></td>
                                                        <td>${stat.categoryName}</td>
                                                        <td style="text-align: right;" class="positive">${stat.totalQuantity}</td>
                                                        <td style="text-align: right;" class="positive"><fmt:formatNumber value="${stat.totalValue}" type="currency" currencySymbol="$" /></td>
                                                        <td style="text-align: right;"><fmt:formatNumber value="${stat.averageValue}" type="currency" currencySymbol="$" /></td>
                                                        <td style="text-align: right;">${stat.receiptCount}</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td><strong>${stat.dateLabel}</strong></td>
                                                        <td style="text-align: right;" class="positive">${stat.totalQuantity}</td>
                                                        <td style="text-align: right;" class="positive"><fmt:formatNumber value="${stat.totalValue}" type="currency" currencySymbol="$" /></td>
                                                        <td style="text-align: right;"><fmt:formatNumber value="${stat.averageValue}" type="currency" currencySymbol="$" /></td>
                                                        <td style="text-align: right;">${stat.receiptCount}</td>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="7" style="text-align: center; padding: 2rem; color: #94a3b8;">
                                                No revenue data available for the selected period and filters.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div style="display: flex; justify-content: center; align-items: center; gap: 0.5rem; margin-top: 1.5rem;">
                            <c:if test="${currentPage > 1}">
                                <a href="${pageContext.request.contextPath}/revenue-report?startDate=${startDate}&endDate=${endDate}&groupBy=${groupBy}&productId=${selectedProductId}&categoryId=${selectedCategoryId}&page=${currentPage - 1}" 
                                   class="btn btn-secondary" style="padding: 0.5rem 0.75rem;">
                                    Previous
                                </a>
                            </c:if>
                            
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <c:choose>
                                    <c:when test="${i == currentPage}">
                                        <span style="padding: 0.5rem 0.75rem; background: #2563eb; color: white; border-radius: 6px; font-weight: 600;">
                                            ${i}
                                        </span>
                                    </c:when>
                                    <c:when test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                        <a href="${pageContext.request.contextPath}/revenue-report?startDate=${startDate}&endDate=${endDate}&groupBy=${groupBy}&productId=${selectedProductId}&categoryId=${selectedCategoryId}&page=${i}" 
                                           style="padding: 0.5rem 0.75rem; background: white; color: #475569; border: 1px solid #cbd5e1; border-radius: 6px; text-decoration: none;">
                                            ${i}
                                        </a>
                                    </c:when>
                                    <c:when test="${i == currentPage - 3 || i == currentPage + 3}">
                                        <span style="padding: 0.5rem 0.25rem; color: #94a3b8;">...</span>
                                    </c:when>
                                </c:choose>
                            </c:forEach>
                            
                            <c:if test="${currentPage < totalPages}">
                                <a href="${pageContext.request.contextPath}/revenue-report?startDate=${startDate}&endDate=${endDate}&groupBy=${groupBy}&productId=${selectedProductId}&categoryId=${selectedCategoryId}&page=${currentPage + 1}" 
                                   class="btn btn-secondary" style="padding: 0.5rem 0.75rem;">
                                    Next
                                </a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            console.log('DOM loaded, initializing revenue charts...');

            // Check if Chart.js is loaded
            if (typeof Chart === 'undefined') {
                console.error('Chart.js is not loaded!');
                return;
            }
            console.log('Chart.js version:', Chart.version);
            
            // Prepare data for charts
            const labels = [];
            const revenueData = [];
            const quantityData = [];
            const groupLabels = [];
            const groupRevenueData = [];
            const groupQuantityData = [];

            <c:forEach var="stat" items="${statistics}">
                <c:choose>
                    <c:when test="${groupBy == 'day' || groupBy == 'month'}">
                        labels.push('${stat.dateLabel}');
                        revenueData.push(${stat.totalValue});
                        quantityData.push(${stat.totalQuantity});
                    </c:when>
                    <c:when test="${groupBy == 'product'}">
                        groupLabels.push('${stat.productName}');
                        groupRevenueData.push(${stat.totalValue});
                        groupQuantityData.push(${stat.totalQuantity});
                    </c:when>
                    <c:when test="${groupBy == 'category'}">
                        groupLabels.push('${stat.categoryName}');
                        groupRevenueData.push(${stat.totalValue});
                        groupQuantityData.push(${stat.totalQuantity});
                    </c:when>
                </c:choose>
            </c:forEach>

            console.log('Data loaded:', {
                labels: labels,
                groupLabels: groupLabels,
                revenueData: revenueData,
                quantityData: quantityData,
                dataPoints: labels.length || groupLabels.length
            });

            // Determine chart type based on groupBy value
            const groupBy = '${groupBy}';
            const isTimeBased = groupBy === 'day' || groupBy === 'month';
            const isGroupBased = groupBy === 'product' || groupBy === 'category';

            // Time-based charts (day/month grouping)
            if (isTimeBased) {
                // Revenue Trend Chart
                try {
                    const revenueTrendCanvas = document.getElementById('revenueTrendChart');
                    if (revenueTrendCanvas) {
                        const revenueTrendCtx = revenueTrendCanvas.getContext('2d');
                        console.log('Creating revenue trend chart...');
                        new Chart(revenueTrendCtx, {
                            type: 'line',
                            data: {
                                labels: labels,
                                datasets: [{
                                    label: 'Revenue',
                                    data: revenueData,
                                    borderColor: '#10b981',
                                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                                    tension: 0.4,
                                    fill: true
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: { position: 'top' },
                                    tooltip: { 
                                        mode: 'index', 
                                        intersect: false,
                                        callbacks: {
                                            label: function(context) {
                                                return 'Revenue: $' + context.parsed.y.toLocaleString();
                                            }
                                        }
                                    }
                                },
                                scales: {
                                    y: { 
                                        beginAtZero: true,
                                        ticks: {
                                            callback: function(value) {
                                                return '$' + value.toLocaleString();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        console.log('Revenue trend chart created successfully');
                    }
                } catch (error) {
                    console.error('Error creating revenue trend chart:', error);
                }

                // Quantity Trend Chart
                try {
                    const quantityTrendCanvas = document.getElementById('quantityTrendChart');
                    if (quantityTrendCanvas) {
                        const quantityTrendCtx = quantityTrendCanvas.getContext('2d');
                        console.log('Creating quantity trend chart...');
                        new Chart(quantityTrendCtx, {
                            type: 'bar',
                            data: {
                                labels: labels,
                                datasets: [{
                                    label: 'Quantity Sold',
                                    data: quantityData,
                                    backgroundColor: 'rgba(59, 130, 246, 0.7)',
                                    borderColor: '#3b82f6',
                                    borderWidth: 1
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: { position: 'top' }
                                },
                                scales: {
                                    y: { beginAtZero: true }
                                }
                            }
                        });
                        console.log('Quantity trend chart created successfully');
                    }
                } catch (error) {
                    console.error('Error creating quantity trend chart:', error);
                }
            }

            // Group-based charts (product/category grouping)
            if (isGroupBased) {
                // Revenue by Group Chart
                try {
                    const revenueByGroupCanvas = document.getElementById('revenueByGroupChart');
                    if (revenueByGroupCanvas) {
                        const revenueByGroupCtx = revenueByGroupCanvas.getContext('2d');
                        console.log('Creating revenue by group chart...');
                        new Chart(revenueByGroupCtx, {
                            type: 'bar',
                            data: {
                                labels: groupLabels,
                                datasets: [{
                                    label: 'Revenue',
                                    data: groupRevenueData,
                                    backgroundColor: 'rgba(16, 185, 129, 0.7)',
                                    borderColor: '#10b981',
                                    borderWidth: 1
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: { position: 'top' },
                                    tooltip: {
                                        callbacks: {
                                            label: function(context) {
                                                return 'Revenue: $' + context.parsed.y.toLocaleString();
                                            }
                                        }
                                    }
                                },
                                scales: {
                                    y: { 
                                        beginAtZero: true,
                                        ticks: {
                                            callback: function(value) {
                                                return '$' + value.toLocaleString();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        console.log('Revenue by group chart created successfully');
                    }
                } catch (error) {
                    console.error('Error creating revenue by group chart:', error);
                }

                // Quantity by Group Chart
                try {
                    const quantityByGroupCanvas = document.getElementById('quantityByGroupChart');
                    if (quantityByGroupCanvas) {
                        const quantityByGroupCtx = quantityByGroupCanvas.getContext('2d');
                        console.log('Creating quantity by group chart...');
                        new Chart(quantityByGroupCtx, {
                            type: 'doughnut',
                            data: {
                                labels: groupLabels,
                                datasets: [{
                                    label: 'Quantity Sold',
                                    data: groupQuantityData,
                                    backgroundColor: [
                                        'rgba(59, 130, 246, 0.7)',
                                        'rgba(16, 185, 129, 0.7)',
                                        'rgba(139, 92, 246, 0.7)',
                                        'rgba(245, 158, 11, 0.7)',
                                        'rgba(239, 68, 68, 0.7)',
                                        'rgba(236, 72, 153, 0.7)',
                                        'rgba(34, 197, 94, 0.7)',
                                        'rgba(168, 85, 247, 0.7)'
                                    ],
                                    borderWidth: 2,
                                    borderColor: '#ffffff'
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: { position: 'right' }
                                }
                            }
                        });
                        console.log('Quantity by group chart created successfully');
                    }
                } catch (error) {
                    console.error('Error creating quantity by group chart:', error);
                }
            }
            
            console.log('All revenue charts initialized!');
        });
    </script>
</body>
</html>
