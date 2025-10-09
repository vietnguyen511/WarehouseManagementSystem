<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="import-receipts" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Import Receipt Details - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    <style>
        /* Allow page scrolling */
        html, body { height: auto; overflow: auto; }
        .main-content-with-sidebar { min-height: auto; overflow: visible; padding-left: var(--spacing-md); padding-right: var(--spacing-md); padding-bottom: var(--spacing-xl); flex: 1; }
        .main-content-with-sidebar > .card, .main-content-with-sidebar > * { max-width: 100%; }
        .app-footer { position: static; }
        
        /* Receipt header styling */
        .receipt-header {
            background: linear-gradient(135deg, var(--primary-50) 0%, var(--primary-100) 100%);
            border: 1px solid var(--primary-200);
            border-radius: var(--radius-lg);
            padding: var(--spacing-lg);
            margin-bottom: var(--spacing-lg);
        }
        
        .receipt-title {
            color: var(--primary-800);
            margin-bottom: var(--spacing-sm);
        }
        
        .receipt-meta {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: var(--spacing-md);
            margin-top: var(--spacing-md);
        }
        
        .meta-item {
            display: flex;
            flex-direction: column;
            gap: var(--spacing-xs);
        }
        
        .meta-label {
            font-size: 0.875rem;
            font-weight: 500;
            color: var(--gray-600);
            text-transform: uppercase;
            letter-spacing: 0.025em;
        }
        
        .meta-value {
            font-size: 1rem;
            font-weight: 600;
            color: var(--gray-900);
        }
        
        /* Details table styling */
        .details-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: var(--spacing-md);
        }
        
        .details-table th {
            background-color: var(--gray-50);
            color: var(--gray-700);
            font-weight: 600;
            text-align: left;
            padding: var(--spacing-md);
            border-bottom: 2px solid var(--gray-200);
            font-size: 0.875rem;
        }
        
        .details-table td {
            padding: var(--spacing-md);
            border-bottom: 1px solid var(--gray-200);
            vertical-align: middle;
        }
        
        .details-table tbody tr:hover {
            background-color: var(--gray-50);
        }
        
        .details-table tbody tr:nth-child(even) {
            background-color: var(--gray-25);
        }
        
        .details-table tbody tr:nth-child(even):hover {
            background-color: var(--gray-100);
        }
        
        /* Column widths */
        .details-table th:nth-child(1), .details-table td:nth-child(1) { width: 8%; } /* STT */
        .details-table th:nth-child(2), .details-table td:nth-child(2) { width: 18%; } /* Mã SP */
        .details-table th:nth-child(3), .details-table td:nth-child(3) { width: 22%; } /* Tên SP */
        .details-table th:nth-child(4), .details-table td:nth-child(4) { width: 12%; } /* Danh mục */
        .details-table th:nth-child(5), .details-table td:nth-child(5) { width: 10%; text-align: right; } /* Số lượng */
        .details-table th:nth-child(6), .details-table td:nth-child(6) { width: 12%; text-align: right; } /* Đơn giá */
        .details-table th:nth-child(7), .details-table td:nth-child(7) { width: 18%; text-align: right; } /* Thành tiền */
        
        /* Summary section */
        .receipt-summary {
            background-color: var(--gray-50);
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            padding: var(--spacing-lg);
            margin-top: var(--spacing-lg);
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: var(--spacing-sm) 0;
            border-bottom: 1px solid var(--gray-200);
        }
        
        .summary-row:last-child {
            border-bottom: none;
            font-weight: 600;
            font-size: 1.125rem;
            color: var(--primary-700);
        }
        
        .summary-label {
            color: var(--gray-600);
        }
        
        .summary-value {
            font-weight: 600;
            color: var(--gray-900);
        }
        
        /* Action buttons */
        .action-buttons {
            display: flex;
            gap: var(--spacing-sm);
            margin-top: var(--spacing-lg);
        }
        
        /* Print styles */
        @media print {
            .action-buttons,
            .sidebar-nav,
            .app-header,
            .app-footer {
                display: none !important;
            }
            
            .main-content-with-sidebar {
                margin: 0;
                padding: 0;
            }
            
            .card {
                box-shadow: none;
                border: 1px solid #000;
            }
        }
        
        /* Responsive design */
        @media (max-width: 768px) {
            .receipt-meta {
                grid-template-columns: 1fr;
            }
            
            .details-table {
                font-size: 0.875rem;
            }
            
            .details-table th,
            .details-table td {
                padding: var(--spacing-sm);
            }
            
            .action-buttons {
                flex-direction: column;
            }
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
                        <div class="alert alert-danger">
                            ${error}
                        </div>
                        <div class="action-buttons">
                            <a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-list" class="btn btn-secondary">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M19 12H5M12 19l-7-7 7-7"></path>
                                </svg>
                                Back to List
                            </a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:when test="${not empty importReceipt}">
                <div class="card">
                    <div class="card-header">
                        <div>
                            <h1 class="card-title">Import Receipt Details</h1>
                            <p class="card-subtitle">Receipt #${importReceipt.importId}</p>
                        </div>
                        <div class="action-buttons">
                            <button type="button" onclick="window.print()" class="btn btn-secondary">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="6 9 6 2 18 2 18 9"></polyline>
                                    <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                                    <rect x="6" y="14" width="12" height="8"></rect>
                                </svg>
                                Print
                            </button>
                            <a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-list" class="btn btn-secondary">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M19 12H5M12 19l-7-7 7-7"></path>
                                </svg>
                                Back to List
                            </a>
                        </div>
                    </div>

                    <div class="card-body">
                        <!-- Receipt Header Information -->
                        <div class="receipt-header">
                            <h2 class="receipt-title">Import Receipt #${importReceipt.importId}</h2>
                            <div class="receipt-meta">
                                <div class="meta-item">
                                    <span class="meta-label">Import Date</span>
                                    <span class="meta-value">
                                        <fmt:formatDate value="${importReceipt.date}" pattern="dd/MM/yyyy HH:mm" />
                                    </span>
                                </div>
                                <div class="meta-item">
                                    <span class="meta-label">Total Quantity</span>
                                    <span class="meta-value">
                                        <fmt:formatNumber value="${importReceipt.totalQuantity}" type="number" />
                                    </span>
                                </div>
                                <div class="meta-item">
                                    <span class="meta-label">Total Amount</span>
                                    <span class="meta-value text-success">
                                        $<fmt:formatNumber value="${importReceipt.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Import Details Table -->
                        <c:choose>
                            <c:when test="${not empty importDetails}">
                                <div class="table-wrapper">
                                    <table class="details-table">
                                        <thead>
                                            <tr>
                                                <th>No.</th>
                                                <th>Product Code</th>
                                                <th>Product Name</th>
                                                <th>Category</th>
                                                <th>Quantity</th>
                                                <th>Unit Price</th>
                                                <th>Amount</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="detail" items="${importDetails}" varStatus="status">
                                                <tr>
                                                    <td>
                                                        <strong>${status.index + 1}</strong>
                                                    </td>
                                                    <td>
                                                        <span class="font-mono text-primary">${detail.productCode}</span>
                                                    </td>
                                                    <td>
                                                        <div class="font-medium">${detail.productName}</div>
                                                        <c:if test="${not empty detail.unit}">
                                                            <div class="text-muted text-sm">Unit: ${detail.unit}</div>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty detail.categoryName}">
                                                                <span class="text-muted">${detail.categoryName}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">-</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <span class="font-semibold">
                                                            <fmt:formatNumber value="${detail.quantity}" type="number" />
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <span class="font-semibold">
                                                            $<fmt:formatNumber value="${detail.price}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <span class="font-semibold text-success">
                                                            $<fmt:formatNumber value="${detail.amount}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <!-- Summary Section -->
                                <div class="receipt-summary">
                                    <div class="summary-row">
                                        <span class="summary-label">Number of Items:</span>
                                        <span class="summary-value">
                                            <fmt:formatNumber value="${importReceipt.totalQuantity}" type="number" />
                                        </span>
                                    </div>
                                    <div class="summary-row">
                                        <span class="summary-label">Total Amount:</span>
                                        <span class="summary-value text-success">
                                            $<fmt:formatNumber value="${importReceipt.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                        </span>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                                        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                                        <polyline points="9 22 9 12 15 12 15 22"></polyline>
                                    </svg>
                                    <h3>No Details Found</h3>
                                    <p>No product details found for this import receipt.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <!-- Note Section -->
                        <c:if test="${not empty importReceipt.note}">
                            <div class="card" style="margin-top: var(--spacing-lg);">
                                <div class="card-header">
                                    <h3 class="card-title">Note</h3>
                                </div>
                                <div class="card-body">
                                    <p class="text-muted">${importReceipt.note}</p>
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
                            <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
                                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                                <polyline points="9 22 9 12 15 12 15 22"></polyline>
                            </svg>
                            <h3>Import Receipt Not Found</h3>
                            <p>The requested import receipt could not be found.</p>
                            <a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-list" class="btn btn-secondary">
                                Back to List
                            </a>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <jsp:include page="/components/footer.jsp" />
</body>
</html>
