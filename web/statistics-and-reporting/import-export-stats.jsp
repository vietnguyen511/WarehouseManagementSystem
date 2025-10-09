<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="import-export-stats" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Import/Export Statistics - Warehouse Management System</title>

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
        
         .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 2rem; }
        .stat-card { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border-left: 4px solid #2563eb; }
        .stat-card.import { border-left-color: #10b981; }
        .stat-card.export { border-left-color: #ef4444; }
        .stat-card.profit { border-left-color: #f59e0b; }
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

        /* Force correct sidebar highlighting for import-export-stats page */
        .sidebar-sublink[href*="/import"]:not([href*="/import-export-stats"]) {
            color: inherit !important;
            background: transparent !important;
        }
        .sidebar-sublink[href*="/import-export-stats"] {
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
                    <h1 class="card-title">Import/Export Statistics</h1>
                    <p class="card-subtitle">Analyze warehouse activity and trends over time</p>
                </div>
            </div>

            <div class="card-body" style="padding-top: 1rem;">
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> ${errorMessage}
                    </div>
                </c:if>

                <!-- Filter Form -->
                <form class="filter-bar-stats" method="get" action="${pageContext.request.contextPath}/import-export-stats" style="display: flex; gap: 1rem; align-items: flex-end; width: 100%;">
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
                        </select>
                    </div>

                    <div style="display: flex; gap: 0.5rem; flex: 0 0 auto; flex-shrink: 0; align-items: center; align-self: center; margin-top: 0.65rem;">
                        <button type="submit" class="btn btn-primary" style="white-space: nowrap;">Apply Filter</button>
                        <a href="${pageContext.request.contextPath}/import-export-stats" class="btn btn-secondary" style="white-space: nowrap;">Reset</a>
                    </div>
                </form>

                <!-- Summary Statistics -->
                <div class="stats-grid">
                    <div class="stat-card import">
                        <div class="stat-label">Total Imports</div>
                        <div class="stat-value">${totalImports}</div>
                        <div class="stat-sub">items</div>
                    </div>
                    <div class="stat-card export">
                        <div class="stat-label">Total Exports</div>
                        <div class="stat-value">${totalExports}</div>
                        <div class="stat-sub">items</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Import Value</div>
                        <div class="stat-value"><fmt:formatNumber value="${totalImportValue}" type="currency" currencySymbol="$" /></div>
                    </div>
                    <div class="stat-card profit">
                        <div class="stat-label">Net Profit</div>
                        <div class="stat-value"><fmt:formatNumber value="${netProfit}" type="currency" currencySymbol="$" /></div>
                    </div>
                </div>

                <!-- Charts -->
                <div class="chart-container">
                    <div class="chart-title">Import vs Export Quantity</div>
                    <div class="chart-wrapper">
                        <canvas id="quantityChart"></canvas>
                    </div>
                </div>

                <div class="chart-container">
                    <div class="chart-title">Stock Difference (Import - Export)</div>
                    <div class="chart-wrapper">
                        <canvas id="differenceChart"></canvas>
                    </div>
                </div>

                <div class="chart-container">
                    <div class="chart-title">Value Comparison</div>
                    <div class="chart-wrapper">
                        <canvas id="valueChart"></canvas>
                    </div>
                </div>

                <!-- Data Table -->
                <div style="margin-top: 2rem;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                        <h3 style="font-size: 1.125rem; font-weight: 600; margin: 0;">Detailed Statistics</h3>
                        <div style="color: #64748b; font-size: 0.875rem;">
                            Showing ${(currentPage - 1) * 10 + 1} to ${(currentPage - 1) * 10 + fn:length(paginatedStatistics)} of ${totalRecords} records
                        </div>
                    </div>
                    <div style="overflow-x: auto;">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th style="text-align: right;">Import Qty</th>
                                    <th style="text-align: right;">Export Qty</th>
                                    <th style="text-align: right;">Difference</th>
                                    <th style="text-align: right;">Import Value</th>
                                    <th style="text-align: right;">Export Value</th>
                                    <th style="text-align: right;">Value Difference</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty paginatedStatistics}">
                                        <c:forEach var="stat" items="${paginatedStatistics}">
                                            <tr>
                                                <td><strong>${stat.dateLabel}</strong></td>
                                                <td style="text-align: right;" class="positive">${stat.importQuantity}</td>
                                                <td style="text-align: right;" class="negative">${stat.exportQuantity}</td>
                                                <td style="text-align: right;" class="${stat.stockDifference >= 0 ? 'positive' : 'negative'}">
                                                    ${stat.stockDifference >= 0 ? '+' : ''}${stat.stockDifference}
                                                </td>
                                                <td style="text-align: right;"><fmt:formatNumber value="${stat.importValue}" type="currency" currencySymbol="$" /></td>
                                                <td style="text-align: right;"><fmt:formatNumber value="${stat.exportValue}" type="currency" currencySymbol="$" /></td>
                                                <td style="text-align: right;" class="${stat.importValue >= stat.exportValue ? 'positive' : 'negative'}">
                                                    <fmt:formatNumber value="${stat.valueDifference}" type="currency" currencySymbol="$" />
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="7" style="text-align: center; padding: 2rem; color: #94a3b8;">
                                                No statistics data available for the selected period.
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
                                <a href="${pageContext.request.contextPath}/import-export-stats?startDate=${startDate}&endDate=${endDate}&groupBy=${groupBy}&page=${currentPage - 1}" 
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
                                        <a href="${pageContext.request.contextPath}/import-export-stats?startDate=${startDate}&endDate=${endDate}&groupBy=${groupBy}&page=${i}" 
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
                                <a href="${pageContext.request.contextPath}/import-export-stats?startDate=${startDate}&endDate=${endDate}&groupBy=${groupBy}&page=${currentPage + 1}" 
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
            console.log('DOM loaded, initializing charts...');


            
            // Check if Chart.js is loaded
            if (typeof Chart === 'undefined') {
                console.error('Chart.js is not loaded!');
                return;
            }
            console.log('Chart.js version:', Chart.version);
            
            // Prepare data for charts
            const labels = [];
            const importData = [];
            const exportData = [];
            const differenceData = [];
            const importValueData = [];
            const exportValueData = [];

            <c:forEach var="stat" items="${statistics}">
                labels.push('${stat.dateLabel}');
                importData.push(${stat.importQuantity});
                exportData.push(${stat.exportQuantity});
                differenceData.push(${stat.stockDifference});
                importValueData.push(${stat.importValue});
                exportValueData.push(${stat.exportValue});
            </c:forEach>

            console.log('Data loaded:', {
                labels: labels,
                importData: importData,
                exportData: exportData,
                dataPoints: labels.length
            });

            // Check if canvas elements exist
            const quantityCanvas = document.getElementById('quantityChart');
            const differenceCanvas = document.getElementById('differenceChart');
            const valueCanvas = document.getElementById('valueChart');
            
            if (!quantityCanvas || !differenceCanvas || !valueCanvas) {
                console.error('Canvas elements not found!');
                return;
            }
            console.log('All canvas elements found');

            // Quantity Chart
            try {
                const quantityCtx = quantityCanvas.getContext('2d');
                console.log('Creating quantity chart...');
                new Chart(quantityCtx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Import',
                            data: importData,
                            borderColor: '#10b981',
                            backgroundColor: 'rgba(16, 185, 129, 0.1)',
                            tension: 0.4,
                            fill: true
                        }, {
                            label: 'Export',
                            data: exportData,
                            borderColor: '#ef4444',
                            backgroundColor: 'rgba(239, 68, 68, 0.1)',
                            tension: 0.4,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { position: 'top' },
                            tooltip: { mode: 'index', intersect: false }
                        },
                        scales: {
                            y: { beginAtZero: true }
                        }
                    }
                });
                console.log('Quantity chart created successfully');
            } catch (error) {
                console.error('Error creating quantity chart:', error);
            }

            // Difference Chart (Bar)
            try {
                const differenceCtx = differenceCanvas.getContext('2d');
                console.log('Creating difference chart...');
                new Chart(differenceCtx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Stock Difference',
                            data: differenceData,
                            backgroundColor: differenceData.map(v => v >= 0 ? 'rgba(16, 185, 129, 0.8)' : 'rgba(239, 68, 68, 0.8)'),
                            borderColor: differenceData.map(v => v >= 0 ? '#10b981' : '#ef4444'),
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
                console.log('Difference chart created successfully');
            } catch (error) {
                console.error('Error creating difference chart:', error);
            }

            // Value Chart
            try {
                const valueCtx = valueCanvas.getContext('2d');
                console.log('Creating value chart...');
                new Chart(valueCtx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Import Value',
                            data: importValueData,
                            backgroundColor: 'rgba(16, 185, 129, 0.7)',
                            borderColor: '#10b981',
                            borderWidth: 1
                        }, {
                            label: 'Export Value',
                            data: exportValueData,
                            backgroundColor: 'rgba(239, 68, 68, 0.7)',
                            borderColor: '#ef4444',
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
                console.log('Value chart created successfully');
            } catch (error) {
                console.error('Error creating value chart:', error);
            }
            
            console.log('All charts initialized!');
        });
    </script>
</body>
</html>

