<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="export-report" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Export Report - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    
    <style>
        /* Allow page scrolling like other forms */
        html, body { height: auto; overflow: auto; }
        .main-content-with-sidebar { 
            min-height: auto; 
            overflow: visible; 
            padding-left: var(--spacing-md); 
            padding-right: var(--spacing-md); 
            padding-bottom: var(--spacing-xl); 
            flex: 1;
        }
        .main-content-with-sidebar > .card, .main-content-with-sidebar > * { max-width: 100%; }
        .app-footer { position: static; }
        
        /* Smooth scrolling */
        .main-content-with-sidebar::-webkit-scrollbar { width: 8px; }
        .main-content-with-sidebar::-webkit-scrollbar-track { background: #f1f5f9; }
        .main-content-with-sidebar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
        .main-content-with-sidebar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        
        /* Form styling consistent with add-import-receipt */
        .export-form {
            background: white;
            border-radius: var(--radius-lg);
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            padding: var(--spacing-lg);
            margin-bottom: var(--spacing-lg);
        }
        .form-section {
            margin-bottom: var(--spacing-lg);
            padding: var(--spacing-lg);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            background-color: var(--gray-50);
        }
        .section-title {
            color: var(--gray-900);
            font-weight: 600;
            font-size: 1.125rem;
            margin-bottom: var(--spacing-md);
            border-bottom: 2px solid var(--primary-600);
            padding-bottom: var(--spacing-sm);
        }
        .export-buttons {
            text-align: center;
            margin-top: var(--spacing-xl);
        }
        .export-btn {
            margin: 0 var(--spacing-sm);
            padding: var(--spacing-md) var(--spacing-lg);
            font-size: 0.9375rem;
            font-weight: 600;
            border-radius: var(--radius-md);
            transition: all 0.2s ease;
        }
        .export-btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        .quick-actions-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: var(--spacing-md);
            margin-top: var(--spacing-lg);
        }
        .quick-action-btn {
            padding: var(--spacing-lg);
            text-align: center;
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            background: white;
            transition: all 0.2s ease;
            cursor: pointer;
        }
        .quick-action-btn:hover {
            border-color: var(--primary-600);
            box-shadow: 0 2px 8px rgba(37, 99, 235, 0.1);
            transform: translateY(-2px);
        }
        .quick-action-icon {
            font-size: 2rem;
            margin-bottom: var(--spacing-sm);
            color: var(--gray-600);
        }
        .quick-action-icon svg {
            width: 24px;
            height: 24px;
        }
        .loading-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 9999;
        }
        .loading-content {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: var(--spacing-xl);
            border-radius: var(--radius-lg);
            text-align: center;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }
        .spinner {
            border: 4px solid var(--gray-100);
            border-top: 4px solid var(--primary-600);
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
            margin: 0 auto var(--spacing-md);
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* Action bar styling consistent with other pages */
        .action-bar {
            display: flex;
            gap: var(--spacing-sm);
        }

        /* Force correct sidebar highlighting for export-report page */
        .sidebar-sublink[href*="/export-report"] {
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
                    <h1 class="card-title">Export Report</h1>
                    <p class="card-subtitle">Generate and download reports in various formats</p>
                </div>
                <div class="action-bar">
                    <a href="${pageContext.request.contextPath}/current-inventory" class="btn btn-secondary">View Inventory</a>
                    <a href="${pageContext.request.contextPath}/revenue-report" class="btn btn-secondary">Revenue Report</a>
                </div>
            </div>

            <div class="card-body" style="padding-top: 1rem;">
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> ${errorMessage}
                    </div>
                </c:if>

                <!-- Export Form -->
                <div class="export-form">
                    <form id="exportForm" method="post" action="${pageContext.request.contextPath}/export-report">
                        <!-- Report Type Section -->
                        <div class="form-section">
                            <h5 class="section-title">Report Type</h5>
                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: var(--spacing-md);">
                                <div class="form-group">
                                    <label for="reportType" class="form-label">Select Report Type</label>
                                    <select class="form-select" id="reportType" name="reportType" required>
                                        <option value="">Choose report type...</option>
                                        <option value="inventory">Current Inventory Report</option>
                                        <option value="import-export">Import/Export Statistics</option>
                                        <option value="revenue">Revenue Report</option>
                                        <option value="comprehensive">Comprehensive Report</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label class="form-label">Export Format</label>
                                    <div style="display: flex; gap: var(--spacing-md); margin-top: var(--spacing-sm);">
                                        <label class="form-check-label" style="display: flex; align-items: center; gap: var(--spacing-sm);">
                                            <input class="form-check-input" type="radio" name="format" id="formatExcel" value="excel" checked>
                                            Excel (.xlsx)
                                        </label>
                                        <label class="form-check-label" style="display: flex; align-items: center; gap: var(--spacing-sm);">
                                            <input class="form-check-input" type="radio" name="format" id="formatPdf" value="pdf">
                                            PDF
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Date Range Section -->
                        <div class="form-section">
                            <h5 class="section-title">Date Range</h5>
                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: var(--spacing-md);">
                                <div class="form-group">
                                    <label for="startDate" class="form-label">Start Date</label>
                                    <input type="date" class="form-input" id="startDate" name="startDate">
                                    <div style="font-size: 0.875rem; color: var(--gray-500); margin-top: var(--spacing-xs);">Leave empty for last 30 days</div>
                                </div>
                                <div class="form-group">
                                    <label for="endDate" class="form-label">End Date</label>
                                    <input type="date" class="form-input" id="endDate" name="endDate">
                                    <div style="font-size: 0.875rem; color: var(--gray-500); margin-top: var(--spacing-xs);">Leave empty for today</div>
                                </div>
                            </div>
                        </div>

                        <!-- Filters Section -->
                        <div class="form-section">
                            <h5 class="section-title">Optional Filters</h5>
                            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: var(--spacing-md);">
                                <div class="form-group">
                                    <label for="categoryId" class="form-label">Category</label>
                                    <select class="form-select" id="categoryId" name="categoryId">
                                        <option value="">All Categories</option>
                                        <!-- Categories will be loaded dynamically -->
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="productId" class="form-label">Product</label>
                                    <select class="form-select" id="productId" name="productId">
                                        <option value="">All Products</option>
                                        <!-- Products will be loaded dynamically -->
                                    </select>
                                </div>
                            </div>
                        </div>

                        <!-- Export Buttons -->
                        <div class="export-buttons">
                            <button type="button" class="btn btn-secondary export-btn" onclick="resetForm()">
                                Reset Form
                            </button>
                            <button type="submit" class="btn btn-primary export-btn">
                                Generate & Download Report
                            </button>
                        </div>
                        <!-- Success message below action buttons -->
                        <div class="alert alert-success" id="successAlert" style="display: none; margin-top: var(--spacing-md);">
                            <strong>Success:</strong> Report generated successfully.
                        </div>
                    </form>
                </div>

                <!-- Quick Actions -->
                <div class="card" style="margin-top: var(--spacing-lg);">
                    <div class="card-header">
                        <h3 style="font-size: 1.125rem; font-weight: 600; margin: 0;">Quick Export Actions</h3>
                    </div>
                    <div class="card-body">
                        <div class="quick-actions-grid">
                            <div class="quick-action-btn" onclick="quickExport('inventory', 'excel')">
                                <div class="quick-action-icon">
                                    <svg width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                                        <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
                                    </svg>
                                </div>
                                <div style="font-weight: 600; margin-bottom: var(--spacing-xs);">Current Inventory</div>
                                <div style="font-size: 0.875rem; color: var(--gray-500);">Excel Format</div>
                            </div>
                            <div class="quick-action-btn" onclick="quickExport('import-export', 'excel')">
                                <div class="quick-action-icon">
                                    <svg width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                                        <path d="M3 3v18h18M7 12l4-4 4 4 4-4"/>
                                    </svg>
                                </div>
                                <div style="font-weight: 600; margin-bottom: var(--spacing-xs);">Import/Export Stats</div>
                                <div style="font-size: 0.875rem; color: var(--gray-500);">Excel Format</div>
                            </div>
                            <div class="quick-action-btn" onclick="quickExport('revenue', 'pdf')">
                                <div class="quick-action-icon">
                                    <svg width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                                        <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                                    </svg>
                                </div>
                                <div style="font-weight: 600; margin-bottom: var(--spacing-xs);">Revenue Report</div>
                                <div style="font-size: 0.875rem; color: var(--gray-500);">PDF Format</div>
                            </div>
                            <div class="quick-action-btn" onclick="quickExport('comprehensive', 'excel')">
                                <div class="quick-action-icon">
                                    <svg width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                                        <path d="M3 3v18h18M9 9l3 3 3-3M9 15l3-3 3 3"/>
                                    </svg>
                                </div>
                                <div style="font-weight: 600; margin-bottom: var(--spacing-xs);">Comprehensive Report</div>
                                <div style="font-size: 0.875rem; color: var(--gray-500);">Excel Format</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />

    <!-- Loading Overlay -->

    <script>
        // Form validation and submission
        document.getElementById('exportForm').addEventListener('submit', function(e) {
            e.preventDefault();
            performExport();
        });

        function performExport() {
            // Validate form
            const reportType = document.getElementById('reportType').value;
            if (!reportType) {
                alert('Please select a report type.');
                return;
            }

            // Show success message
            showSuccessMessage();

            const form = document.getElementById('exportForm');
            const formData = new FormData(form);
            const params = {};
            for (let [key, value] of formData.entries()) {
                params[key] = value;
            }
            submitHiddenForm(params);
        }

        function submitHiddenForm(params) {
            const tempForm = document.createElement('form');
            tempForm.method = 'POST';
            tempForm.action = '${pageContext.request.contextPath}/export-report';
            tempForm.style.display = 'none';

            Object.keys(params).forEach((key) => {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = key;
                input.value = params[key] ?? '';
                tempForm.appendChild(input);
            });

            document.body.appendChild(tempForm);
            tempForm.submit();
            document.body.removeChild(tempForm);
        }
        
        // Show success message function
        function showSuccessMessage() {
            const successAlert = document.getElementById('successAlert');
            successAlert.style.display = 'block';

            // Auto-hide after 3 seconds
            setTimeout(() => {
                successAlert.style.display = 'none';
            }, 3000);
        }

        // Quick export function
        function quickExport(reportType, format) {
            // Set UI values for consistency
            const reportTypeSelect = document.getElementById('reportType');
            const formatRadio = document.querySelector(`input[name="format"][value="${format}"]`);
            if (reportTypeSelect) reportTypeSelect.value = reportType;
            if (formatRadio) formatRadio.checked = true;

            // Collect params explicitly to avoid relying on form submit context
            const params = {
                reportType: reportType,
                format: format,
                startDate: document.getElementById('startDate') ? document.getElementById('startDate').value : '',
                endDate: document.getElementById('endDate') ? document.getElementById('endDate').value : '',
                productId: document.getElementById('productId') ? document.getElementById('productId').value : '',
                categoryId: document.getElementById('categoryId') ? document.getElementById('categoryId').value : ''
            };

            // Show success message and submit
            showSuccessMessage();
            submitHiddenForm(params);
        }
        
        // Reset form function
        function resetForm() {
            document.getElementById('exportForm').reset();
            document.getElementById('formatExcel').checked = true;
        }
        
        // Set default date range (last 30 days)
        window.addEventListener('load', function() {
            const today = new Date();
            const thirtyDaysAgo = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));
            
            document.getElementById('endDate').value = today.toISOString().split('T')[0];
            document.getElementById('startDate').value = thirtyDaysAgo.toISOString().split('T')[0];
        });
    </script>
</body>
</html>
