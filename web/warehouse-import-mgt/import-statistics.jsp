<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Import Statistics - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
    <style>
        html, body { height: auto; overflow: auto; }
        .main-content-with-sidebar { min-height: auto; overflow: visible; padding-left: var(--spacing-md); padding-right: var(--spacing-md); padding-bottom: var(--spacing-xl); flex: 1; }
        .main-content-with-sidebar > .card, .main-content-with-sidebar > * { max-width: 100%; }
        .app-footer { position: static; }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }
        
        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            border-left: 4px solid var(--primary-600);
        }
        
        .stat-card.success { border-left-color: var(--success-600); }
        .stat-card.info { border-left-color: var(--info-600); }
        .stat-card.warning { border-left-color: var(--warning-600); }
        
        .stat-label {
            color: var(--gray-600);
            font-size: 0.875rem;
            font-weight: 500;
            margin-bottom: 0.5rem;
        }
        
        .stat-value {
            font-size: 1.875rem;
            font-weight: 700;
            color: var(--gray-900);
        }
        
        .chart-container {
            background: white;
            padding: 1.5rem;
            border-radius: var(--radius-lg);
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            margin-bottom: 1.5rem;
        }
        
        .chart-title {
            font-size: 1.125rem;
            font-weight: 600;
            color: var(--gray-900);
            margin-bottom: 1rem;
        }
        
        .chart-wrapper {
            position: relative;
            height: 400px;
        }
        
        .filter-bar {
            display: flex;
            flex-wrap: wrap;
            gap: 1rem;
            align-items: flex-end;
            margin-bottom: 1.5rem;
            background: white;
            padding: 1.5rem;
            border-radius: var(--radius-lg);
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .data-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .data-table th {
            background: var(--gray-50);
            padding: 1rem;
            text-align: left;
            font-weight: 600;
            color: var(--gray-700);
            border-bottom: 2px solid var(--gray-200);
            font-size: 0.875rem;
        }
        
        .data-table td {
            padding: 1rem;
            border-bottom: 1px solid var(--gray-200);
        }
        
        .data-table tbody tr:hover {
            background: var(--gray-50);
        }
        
        .text-success { color: var(--success-600); font-weight: 600; }
        .text-primary { color: var(--primary-600); font-weight: 600; }
    </style>
</head>
<body>
    <jsp:include page="/components/top-header.jsp" />
    <jsp:include page="/components/sidebar-nav.jsp" />

    <div class="main-content-with-sidebar">
        <div class="card">
            <div class="card-header">
                <div>
                    <h1 class="card-title">Import Statistics</h1>
                    <p class="card-subtitle">Analyze import activity and trends over time</p>
                </div>
                <div class="action-bar">
                    <a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-list" class="btn btn-secondary">
                        Back to List
                    </a>
                </div>
            </div>

            <div class="card-body">
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> ${errorMessage}
                    </div>
                </c:if>

                <!-- Filter Form -->
                <form class="filter-bar" method="get" action="${pageContext.request.contextPath}/warehouse-import-mgt/import-statistics">
                    <div class="form-group" style="flex: 1; min-width: 150px;">
                        <label class="form-label">Start Date</label>
                        <input type="date" name="startDate" class="form-input" value="${startDateDisplay}" />
                    </div>

                    <div class="form-group" style="flex: 1; min-width: 150px;">
                        <label class="form-label">End Date</label>
                        <input type="date" name="endDate" class="form-input" value="${endDateDisplay}" />
                    </div>

                    <div class="form-group" style="flex: 0 0 150px;">
                        <label class="form-label">Group By</label>
                        <select name="groupBy" class="form-select">
                            <option value="day" ${groupBy == 'day' ? 'selected' : ''}>By Day</option>
                            <option value="month" ${groupBy == 'month' ? 'selected' : ''}>By Month</option>
                        </select>
                    </div>

                    <div class="form-group" style="flex: 0 0 auto;">
                        <button type="submit" class="btn btn-primary">Apply Filter</button>
                    </div>
                </form>

                <!-- Summary Statistics Cards -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-label">Total Receipts</div>
                        <div class="stat-value">${totalReceipts}</div>
                    </div>
                    <div class="stat-card success">
                        <div class="stat-label">Total Quantity</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${totalQuantity}" type="number" />
                        </div>
                    </div>
                    <div class="stat-card info">
                        <div class="stat-label">Total Amount</div>
                        <div class="stat-value">
                            $<fmt:formatNumber value="${totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                    <div class="stat-card warning">
                        <div class="stat-label">Average per Receipt</div>
                        <div class="stat-value">
                            $<fmt:formatNumber value="${avgAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                    </div>
                </div>

                <!-- Chart -->
                <div class="chart-container">
                    <div class="chart-title">Import Amount Trend</div>
                    <div class="chart-wrapper">
                        <canvas id="importChart"></canvas>
                    </div>
                </div>

                <!-- Data Table -->
                <div class="chart-container">
                    <div class="chart-title">Import Statistics Details</div>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Period</th>
                                <th style="text-align: center;">Receipts</th>
                                <th style="text-align: right;">Quantity</th>
                                <th style="text-align: right;">Amount</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty statistics}">
                                    <c:forEach var="stat" items="${statistics}">
                                        <tr>
                                            <td>${stat.period}</td>
                                            <td style="text-align: center;">
                                                <span class="text-primary">${stat.receiptCount}</span>
                                            </td>
                                            <td style="text-align: right;">
                                                <fmt:formatNumber value="${stat.totalQuantity}" type="number" />
                                            </td>
                                            <td style="text-align: right;">
                                                <span class="text-success">
                                                    $<fmt:formatNumber value="${stat.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                                </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" style="text-align: center; padding: 2rem; color: var(--gray-500);">
                                            No import statistics found for the selected period.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                    
                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 1rem; padding-top: 1rem; border-top: 1px solid var(--gray-200);">
                            <div style="color: var(--gray-600); font-size: 0.875rem;">
                                <c:choose>
                                    <c:when test="${totalRecords > 0}">
                                        Showing ${fromIndex + 1} to ${toIndex} of ${totalRecords} records
                                    </c:when>
                                    <c:otherwise>
                                        No records to display
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div style="display: flex; gap: 0.5rem;">
                                <c:url var="prevUrl" value="/warehouse-import-mgt/import-statistics">
                                    <c:param name="startDate" value="${startDateDisplay}" />
                                    <c:param name="endDate" value="${endDateDisplay}" />
                                    <c:param name="groupBy" value="${groupBy}" />
                                    <c:param name="page" value="${currentPage - 1}" />
                                </c:url>
                                <c:url var="nextUrl" value="/warehouse-import-mgt/import-statistics">
                                    <c:param name="startDate" value="${startDateDisplay}" />
                                    <c:param name="endDate" value="${endDateDisplay}" />
                                    <c:param name="groupBy" value="${groupBy}" />
                                    <c:param name="page" value="${currentPage + 1}" />
                                </c:url>
                                
                                <c:if test="${currentPage > 1}">
                                    <a href="${prevUrl}" class="btn" style="padding: 0.5rem 1rem; background: white; border: 1px solid var(--gray-300); color: var(--gray-700); border-radius: 4px; text-decoration: none;">
                                        Previous
                                    </a>
                                </c:if>
                                <c:if test="${currentPage <= 1}">
                                    <button class="btn" disabled style="padding: 0.5rem 1rem; background: var(--gray-100); border: 1px solid var(--gray-300); color: var(--gray-400); border-radius: 4px; cursor: not-allowed;">
                                        Previous
                                    </button>
                                </c:if>
                                
                                <c:if test="${totalPages > 1}">
                                    <c:forEach var="i" begin="1" end="${totalPages}">
                                        <c:if test="${i == currentPage || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                            <c:url var="pageUrl" value="/warehouse-import-mgt/import-statistics">
                                                <c:param name="startDate" value="${startDateDisplay}" />
                                                <c:param name="endDate" value="${endDateDisplay}" />
                                                <c:param name="groupBy" value="${groupBy}" />
                                                <c:param name="page" value="${i}" />
                                            </c:url>
                                            <c:choose>
                                                <c:when test="${i == currentPage}">
                                                    <span class="btn" style="padding: 0.5rem 1rem; background: var(--primary-600); color: white; border-radius: 4px;">
                                                        ${i}
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${pageUrl}" class="btn" style="padding: 0.5rem 1rem; background: white; border: 1px solid var(--gray-300); color: var(--gray-700); border-radius: 4px; text-decoration: none;">
                                                        ${i}
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                                
                                <c:if test="${currentPage < totalPages}">
                                    <a href="${nextUrl}" class="btn" style="padding: 0.5rem 1rem; background: white; border: 1px solid var(--gray-300); color: var(--gray-700); border-radius: 4px; text-decoration: none;">
                                        Next
                                    </a>
                                </c:if>
                                <c:if test="${currentPage >= totalPages}">
                                    <button class="btn" disabled style="padding: 0.5rem 1rem; background: var(--gray-100); border: 1px solid var(--gray-300); color: var(--gray-400); border-radius: 4px; cursor: not-allowed;">
                                        Next
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />

    <script>
        // Chart.js configuration
        const ctx = document.getElementById('importChart');
        const statistics = [
            <c:forEach var="stat" items="${allStatistics}" varStatus="loop">
            {
                period: '${stat.period}',
                receiptCount: ${stat.receiptCount},
                totalQuantity: ${stat.totalQuantity},
                totalAmount: ${stat.totalAmount}
            }<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        ];
        
        // Format period labels
        function formatPeriodLabel(period) {
            const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
            const parts = period.split('-');
            
            if (parts.length === 3) {
                // Date format: YYYY-MM-DD -> "29 Oct"
                const [year, month, day] = parts;
                return day + ' ' + monthNames[parseInt(month) - 1];
            } else if (parts.length === 2) {
                // Month format: YYYY-MM -> "Oct 2024"
                const [year, month] = parts;
                return monthNames[parseInt(month) - 1] + ' ' + year;
            }
            return period;
        }
        
        const periods = statistics.map(s => s.period);
        const formattedPeriods = periods.map(p => formatPeriodLabel(p));
        const amounts = statistics.map(s => s.totalAmount);
        
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: formattedPeriods,
                datasets: [{
                    label: 'Import Amount ($)',
                    data: amounts,
                    backgroundColor: 'rgba(34, 197, 94, 0.7)',
                    borderColor: 'rgba(34, 197, 94, 1)',
                    borderWidth: 2,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                        callbacks: {
                            label: function(context) {
                                return 'Amount: $' + context.parsed.y.toLocaleString('en-US', {
                                    minimumFractionDigits: 2,
                                    maximumFractionDigits: 2
                                });
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return '$' + value.toLocaleString('en-US');
                            }
                        }
                    },
                    x: {
                        ticks: {
                            maxRotation: 45,
                            minRotation: 45
                        }
                    }
                }
            }
        });
    </script>
</body>
</html>

