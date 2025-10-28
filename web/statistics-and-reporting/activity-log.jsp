<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="activity-log" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Activity Log - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
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
        
        .main-content-with-sidebar::-webkit-scrollbar { width: 8px; }
        .main-content-with-sidebar::-webkit-scrollbar-track { background: #f1f5f9; }
        .main-content-with-sidebar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
        .main-content-with-sidebar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        
        .log-table { width: 100%; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .log-table th { background: #f8fafc; padding: 0.75rem; text-align: left; font-weight: 600; color: #475569; border-bottom: 2px solid #e2e8f0; }
        .log-table td { padding: 0.75rem; border-bottom: 1px solid #e2e8f0; }
        .log-table tr:hover { background: #f8fafc; }
        
        .action-icon {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 28px;
            height: 28px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.75rem;
        }
        
        .filter-bar-stats { 
            display: flex; 
            flex-wrap: wrap; 
            gap: 1rem; 
            align-items: flex-end; 
            margin-bottom: 1.5rem; 
            margin-top: 0; 
            background: white; 
            padding: 1.5rem; 
            border-radius: 8px; 
            box-shadow: 0 1px 3px rgba(0,0,0,0.1); 
        }
        
        .pagination { display: flex; gap: 0.25rem; list-style: none; padding: 0; margin: 0.75rem 0 0; }
        .pagination a, .pagination span { display: flex; align-items: center; justify-content: center; min-width: 30px; height: 30px; padding: 0; border: 1px solid #e2e8f0; border-radius: 6px; text-decoration: none; color: #334155; font-size: 0.8125rem; }
        .pagination .active { background: #2563eb; color: #fff; border-color: #2563eb; }
        .pagination .disabled { opacity: 0.5; pointer-events: none; }
        
        /* Force correct sidebar highlighting for activity-log page */
        .sidebar-sublink[href*="/activity-log"] {
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
                    <h1 class="card-title">Activity Log</h1>
                    <p class="card-subtitle">View all user activities and system events</p>
                </div>
            </div>

            <div class="card-body" style="padding-top: 1rem;">
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        <strong>Error:</strong> ${errorMessage}
                    </div>
                </c:if>

                <!-- Filter Form -->
                <form class="filter-bar-stats" method="get" action="${pageContext.request.contextPath}/activity-log" style="display: flex; gap: 1rem; align-items: flex-end; width: 100%;">
                    <div class="form-group" style="flex: 1 1 0; min-width: 0; display: flex; flex-direction: column;">
                        <label class="form-label" style="margin-bottom: 0.5rem;">Filter by Action</label>
                        <select name="filterAction" class="form-select" style="width: 100%; padding: 0.625rem 0.875rem; height: 40px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.9375rem;">
                            <option value="">All Actions</option>
                            <option value="CREATE" ${filterAction == 'CREATE' ? 'selected' : ''}>Create</option>
                            <option value="UPDATE" ${filterAction == 'UPDATE' ? 'selected' : ''}>Update</option>
                            <option value="DELETE" ${filterAction == 'DELETE' ? 'selected' : ''}>Delete</option>
                            <option value="VIEW" ${filterAction == 'VIEW' ? 'selected' : ''}>View</option>
                        </select>
                    </div>

                    <div class="form-group" style="flex: 1 1 0; min-width: 0; display: flex; flex-direction: column;">
                        <label class="form-label" style="margin-bottom: 0.5rem;">Search by User</label>
                        <input type="text" name="filterUser" class="form-input" value="${filterUser}" placeholder="Enter username" style="width: 100%; padding: 0.625rem 0.875rem; height: 40px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.9375rem;" />
                    </div>

                    <div style="display: flex; gap: 0.5rem; flex: 0 0 auto; flex-shrink: 0; align-items: flex-end; padding-bottom: 1.75rem;">
                        <button type="submit" class="btn btn-primary" style="white-space: nowrap;">Filter</button>
                        <a href="${pageContext.request.contextPath}/activity-log" class="btn btn-secondary" style="white-space: nowrap;">Reset</a>
                    </div>
                </form>

                <!-- Activity Log Table -->
                <div style="overflow-x: auto;">
                    <table class="log-table">
                        <thead>
                            <tr>
                                <th style="width: 180px;">Time</th>
                                <th style="width: 150px;">User</th>
                                <th style="width: 100px;">Action</th>
                                <th style="width: 140px;">Table</th>
                                <th style="width: 100px;" title="Record ID affected by this action">Record ID</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty logs}">
                                    <c:forEach var="log" items="${logs}">
                                        <tr>
                                            <td style="font-size: 0.8125rem; color: #64748b;">
                                                ${log.formattedDate}
                                            </td>
                                            <td>
                                                <div>
                                                    <strong>${log.userName}</strong>
                                                </div>
                                                <div style="font-size: 0.75rem; color: #94a3b8;">
                                                    ${log.userRole}
                                                </div>
                                            </td>
                                            <td>
                                                <span class="${log.actionBadgeClass}">
                                                    ${log.action}
                                                </span>
                                            </td>
                                            <td style="font-family: monospace; font-size: 0.875rem;">
                                                ${log.targetTable}
                                            </td>
                                            <td style="text-align: center; color: #64748b;">
                                                <c:choose>
                                                    <c:when test="${log.targetId != null}">
                                                        #${log.targetId}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: #94a3b8;">-</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="max-width: 400px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${fn:escapeXml(log.description)}">
                                                ${fn:escapeXml(log.description)}
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" style="text-align: center; padding: 3rem; color: #94a3b8; vertical-align: middle;">
                                            <svg width="48" height="48" fill="currentColor" viewBox="0 0 16 16" style="opacity: 0.3; margin-bottom: 0.5rem;">
                                                <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                                                <path d="M7 4.5a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v3a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5zm.5 5.5a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 0 1h-1a.5.5 0 0 1-.5-.5z"/>
                                            </svg>
                                            <div>No activity logs found.</div>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="card-footer">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                            <div class="text-muted">
                                Showing page ${currentPage} of ${totalPages} • ${totalRecords} record(s)
                            </div>
                        </div>
                        <ul class="pagination">
                            <li>
                                <a class="${currentPage <= 1 ? 'disabled' : ''}" 
                                   href="${pageContext.request.contextPath}/activity-log?page=${currentPage - 1}&filterAction=${fn:escapeXml(param.filterAction)}&filterUser=${fn:escapeXml(param.filterUser)}">
                                   Prev
                                </a>
                            </li>
                            <c:forEach var="i" begin="1" end="${totalPages > 10 ? 10 : totalPages}">
                                <c:choose>
                                    <c:when test="${(i == 1) || (i == totalPages) || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                        <li>
                                            <a class="${i == currentPage ? 'active' : ''}" 
                                               href="${pageContext.request.contextPath}/activity-log?page=${i}&filterAction=${fn:escapeXml(param.filterAction)}&filterUser=${fn:escapeXml(param.filterUser)}">
                                                ${i}
                                            </a>
                                        </li>
                                    </c:when>
                                    <c:when test="${i == currentPage - 3 || i == currentPage + 3}">
                                        <li><span style="color: #94a3b8;">...</span></li>
                                    </c:when>
                                </c:choose>
                            </c:forEach>
                            <li>
                                <a class="${currentPage >= totalPages ? 'disabled' : ''}" 
                                   href="${pageContext.request.contextPath}/activity-log?page=${currentPage + 1}&filterAction=${fn:escapeXml(param.filterAction)}&filterUser=${fn:escapeXml(param.filterUser)}">
                                   Next
                                </a>
                            </li>
                        </ul>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />
</body>
</html>

