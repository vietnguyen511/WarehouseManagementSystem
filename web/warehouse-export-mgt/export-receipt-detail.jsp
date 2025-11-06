<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="export-receipts" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Export Receipt Details - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>

    <style>
        html, body { height: auto; overflow: auto; }
        .main-content-with-sidebar { min-height: auto; overflow: visible; padding: 1rem; flex: 1; }
        .app-footer { position: static; }

        .receipt-header {
            background: linear-gradient(135deg, var(--primary-50) 0%, var(--primary-100) 100%);
            border: 1px solid var(--primary-200);
            border-radius: var(--radius-lg);
            padding: var(--spacing-lg);
            margin-bottom: var(--spacing-lg);
        }
        .receipt-title { color: var(--primary-800); margin-bottom: var(--spacing-sm); }
        .receipt-meta { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: var(--spacing-md); margin-top: var(--spacing-md); }
        .meta-item { display: flex; flex-direction: column; gap: var(--spacing-xs); }
        .meta-label { font-size: 0.875rem; font-weight: 500; color: var(--gray-600); text-transform: uppercase; }
        .meta-value { font-size: 1rem; font-weight: 600; color: var(--gray-900); }

        .details-table { width: 100%; border-collapse: collapse; margin-top: var(--spacing-md); }
        .details-table th { background-color: var(--gray-50); color: var(--gray-700); font-weight: 600; text-align: left; padding: var(--spacing-md); border-bottom: 2px solid var(--gray-200); font-size: 0.875rem; }
        .details-table td { padding: var(--spacing-md); border-bottom: 1px solid var(--gray-200); vertical-align: middle; }
        .details-table tbody tr:hover { background-color: var(--gray-50); }
        .details-table tbody tr:nth-child(even) { background-color: var(--gray-25); }
        .details-table tbody tr:nth-child(even):hover { background-color: var(--gray-100); }

        .receipt-summary { background-color: var(--gray-50); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); padding: var(--spacing-lg); margin-top: var(--spacing-lg); }
        .summary-row { display: flex; justify-content: space-between; align-items: center; padding: var(--spacing-sm) 0; border-bottom: 1px solid var(--gray-200); }
        .summary-row:last-child { border-bottom: none; font-weight: 600; font-size: 1.125rem; color: var(--primary-700); }
        .summary-label { color: var(--gray-600); }
        .summary-value { font-weight: 600; color: var(--gray-900); }

        .action-buttons { display: flex; gap: var(--spacing-sm); margin-top: var(--spacing-lg); }

        @media print {
            .action-buttons, .sidebar-nav, .app-header, .app-footer { display: none !important; }
            .main-content-with-sidebar { margin: 0; padding: 0; }
            .card { box-shadow: none; border: 1px solid #000; }
        }
    </style>
</head>
<body>
<jsp:include page="/components/top-header.jsp" />
<jsp:include page="/components/sidebar-nav.jsp" />

<div class="main-content-with-sidebar">

    <c:choose>
        <c:when test="${not empty error}">
            <div class="card">
                <div class="card-body">
                    <div class="alert alert-danger">${error}</div>
                    <div class="action-buttons">
                        <a href="${pageContext.request.contextPath}/warehouse-export-mgt/export-receipt-list" class="btn btn-secondary">
                            ← Back to List
                        </a>
                    </div>
                </div>
            </div>
        </c:when>

        <c:when test="${not empty exportReceipt}">
            <div class="card">
                <div class="card-header">
                    <div>
                        <h1 class="card-title">Export Receipt Details</h1>
                        <p class="card-subtitle">Receipt #${exportReceipt.exportId}</p>
                    </div>
                    <div class="action-buttons">
                        <button type="button" onclick="window.print()" class="btn btn-secondary">🖨 Print</button>
                        <a href="${pageContext.request.contextPath}/warehouse-export-mgt/export-receipt-list" class="btn btn-secondary">← Back to List</a>
                    </div>
                </div>

                <div class="card-body">
                    <!-- Header Info -->
                    <div class="receipt-header">
                        <h2 class="receipt-title">Export Receipt #${exportReceipt.exportId}</h2>
                        <div class="receipt-meta">
                            <div class="meta-item">
                                <span class="meta-label">Export Date</span>
                                <span class="meta-value"><fmt:formatDate value="${exportReceipt.date}" pattern="dd/MM/yyyy HH:mm" /></span>
                            </div>
                            <div class="meta-item">
                                <span class="meta-label">Customer</span>
                                <span class="meta-value">${exportReceipt.customerName}</span>
                            </div>
                            <div class="meta-item">
                                <span class="meta-label">Created By</span>
                                <span class="meta-value">${exportReceipt.userName}</span>
                            </div>
                            <div class="meta-item">
                                <span class="meta-label">Total Quantity</span>
                                <span class="meta-value"><fmt:formatNumber value="${exportReceipt.totalQuantity}" type="number" /></span>
                            </div>
                            <div class="meta-item">
                                <span class="meta-label">Total Amount</span>
                                <span class="meta-value text-success">
                                    $<fmt:formatNumber value="${exportReceipt.totalAmount}" type="number" minFractionDigits="0" maxFractionDigits="0" />
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Details Table -->
                    <c:choose>
                        <c:when test="${not empty exportDetails}">
                            <div class="table-wrapper">
                                <table class="details-table">
                                    <thead>
                                    <tr>
                                        <th>No.</th>
                                        <th>Product Code</th>
                                        <th>Product Name</th>
                                        <th>Size</th>
                                        <th>Color</th>
                                        <th>Category</th>
                                        <th style="text-align:right;">Quantity</th>
                                        <th style="text-align:right;">Export Price</th>
                                        <th style="text-align:right;">Amount</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="detail" items="${exportDetails}" varStatus="status">
                                        <tr>
                                            <td>${status.index + 1}</td>
                                            <td class="font-mono text-primary">${detail.productCode}</td>
                                            <td>
                                                <div class="font-medium">${detail.productName}</div>
                                                <c:if test="${not empty detail.unit}">
                                                    <div class="text-muted text-sm">Unit: ${detail.unit}</div>
                                                </c:if>
                                            </td>
                                            <td>${detail.size}</td>
                                            <td>${detail.color}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty detail.categoryName}">
                                                        ${detail.categoryName}
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="text-align:right;">
                                                <fmt:formatNumber value="${detail.quantity}" type="number" />
                                            </td>
                                            <td style="text-align:right;">
                                                $<fmt:formatNumber value="${detail.price}" type="number" minFractionDigits="0" maxFractionDigits="0" />
                                            </td>
                                            <td style="text-align:right;" class="text-success">
                                                $<fmt:formatNumber value="${detail.amount}" type="number" minFractionDigits="0" maxFractionDigits="0" />
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Summary -->
                            <div class="receipt-summary">
                                <div class="summary-row">
                                    <span class="summary-label">Total Quantity:</span>
                                    <span class="summary-value"><fmt:formatNumber value="${exportReceipt.totalQuantity}" type="number" /></span>
                                </div>
                                <div class="summary-row">
                                    <span class="summary-label">Total Amount:</span>
                                    <span class="summary-value text-success">
                                        $<fmt:formatNumber value="${exportReceipt.totalAmount}" type="number" minFractionDigits="0" maxFractionDigits="0" />
                                    </span>
                                </div>
                            </div>
                        </c:when>

                        <c:otherwise>
                            <div class="empty-state">
                                <h3>No Export Details Found</h3>
                                <p>No products found in this export receipt.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <!-- Note -->
                    <c:if test="${not empty exportReceipt.note}">
                        <div class="card" style="margin-top: var(--spacing-lg);">
                            <div class="card-header"><h3 class="card-title">Note</h3></div>
                            <div class="card-body">
                                <p class="text-muted">${exportReceipt.note}</p>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="card">
                <div class="card-body">
                    <div class="empty-state">
                        <h3>Export Receipt Not Found</h3>
                        <p>The requested export receipt could not be found.</p>
                        <a href="${pageContext.request.contextPath}/warehouse-export-mgt/export-receipt-list" class="btn btn-secondary">Back to List</a>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/components/footer.jsp" />
</body>
</html>
