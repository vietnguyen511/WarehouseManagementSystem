<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setAttribute("activePage", "suppliers");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Suppliers - Warehouse Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    <style>
        .btn-action { background: transparent; border-color: var(--gray-300); color: var(--gray-700); padding: 0.25rem 0.5rem; }
        .btn-action:hover { background: var(--gray-50); }
        .btn-action svg { color: currentColor; }
        .btn-view { color: var(--warning-700); border-color: var(--warning-600); }
        .btn-view:hover { background: rgba(245, 158, 11, 0.35); }
        .btn-edit { color: var(--primary-700); border-color: var(--primary-600); }
        .btn-edit:hover { background: rgba(2, 132, 199, 0.35); }
        .btn-delete { color: var(--danger-700); border-color: var(--danger-600); }
        .btn-delete:hover { background: rgba(220, 38, 38, 0.35); }
        .btn-activate { color: var(--success-700); border-color: var(--success-600); }
        .btn-activate:hover { background: rgba(34, 197, 94, 0.35); }
        /* Modal styles */
        .modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: none; align-items: center; justify-content: center; z-index: 2000; }
        .modal { background: #fff; border-radius: var(--radius-lg); box-shadow: var(--shadow-lg); width: 420px; max-width: calc(100% - 2rem); border: 1px solid var(--gray-200); }
        .modal-header { padding: var(--spacing-md) var(--spacing-lg); border-bottom: 1px solid var(--gray-200); }
        .modal-title { margin: 0; font-size: 1.125rem; color: var(--gray-800); }
        .modal-body { padding: var(--spacing-lg); color: var(--gray-700); }
        .modal-footer { padding: var(--spacing-md) var(--spacing-lg); border-top: 1px solid var(--gray-200); display: flex; justify-content: flex-end; gap: var(--spacing-sm); }
        .btn-outline { background: transparent; border: 1px solid var(--gray-300); color: var(--gray-700); }
        .btn-danger-solid { background: var(--danger-600); border-color: var(--danger-600); color: #fff; }
        .btn-outline:hover { background: var(--gray-50); }
        .btn-danger-solid:hover { background: var(--danger-700); border-color: var(--danger-700); }
    </style>
</head>
<body>
    <jsp:include page="/components/top-header.jsp" />
    <jsp:include page="/components/sidebar-nav.jsp" />

    <div class="main-content-with-sidebar">
        <div class="card">
            <div class="card-header">
                <div>
                    <h1 class="card-title">Suppliers</h1>
                    <p class="card-subtitle">Manage supplier list</p>
                </div>
                <div class="action-bar">
                    <a href="${pageContext.request.contextPath}/suppliers/new" class="btn btn-success">Add Supplier</a>
                </div>
            </div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <c:if test="${param.created == '1'}">
                    <div class="alert alert-success">Supplier created successfully.</div>
                </c:if>
                <c:if test="${param.updated == '1'}">
                    <div class="alert alert-success">Supplier updated successfully.</div>
                </c:if>
                <c:if test="${param.deactivated == '1'}">
                    <div class="alert alert-success">Supplier deactivated successfully.</div>
                </c:if>
                <c:if test="${param.activated == '1'}">
                    <div class="alert alert-success">Supplier activated successfully.</div>
                </c:if>
                <c:if test="${param.deleted == '1'}">
                    <div class="alert alert-success">Supplier deleted successfully.</div>
                </c:if>

                <form method="get" action="${pageContext.request.contextPath}/suppliers">
                    <div class="filter-bar" style="grid-template-columns: 2fr minmax(140px, 12%) auto;">
                        <div class="form-group" style="flex:2;">
                            <label class="form-label">Search</label>
                            <input class="form-input" type="text" name="search" value="${fn:escapeXml(search)}" placeholder="Search name, phone, email, address" />
                        </div>
                        <div class="form-group" style="flex:0 0 160px;">
                            <label class="form-label">Status</label>
                            <select class="form-select" name="status">
                                <option value="" ${statusFilter == '' ? 'selected' : ''}>All Status</option>
                                <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Active</option>
                                <option value="inactive" ${statusFilter == 'inactive' ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>
                        <div class="filter-actions">
                            <button type="submit" class="btn btn-primary">Apply</button>
                            <a href="${pageContext.request.contextPath}/suppliers" class="btn btn-secondary">Reset</a>
                        </div>
                    </div>
                </form>

                <div class="table-wrapper">
                    <table class="table">
                        <thead>
                            <tr>
                                <th style="width:18%;">Name</th>
                                <th style="width:12%;">Phone</th>
                                <th style="width:20%;">Email</th>
                                <th style="width:32%;">Address</th>
                                <th style="text-align:center; width:8%;">Status</th>
                                <th style="text-align:center; width:10%;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty suppliers}">
                                <c:forEach var="s" items="${suppliers}">
                                    <tr>
                                        <td><span class="font-medium">${s.name}</span></td>
                                        <td>${s.phone}</td>
                                        <td>${s.email}</td>
                                        <td>${s.address}</td>
                                        <td style="text-align:center;">
                                            <c:choose>
                                                <c:when test="${s.status}"><span class="badge badge-success">Active</span></c:when>
                                                <c:otherwise><span class="badge badge-secondary">Inactive</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="text-align:center;">
                                            <div class="d-flex gap-2" style="justify-content:center;">
                                                <a class="btn btn-sm btn-action btn-view" href="${pageContext.request.contextPath}/suppliers/view?id=${s.supplierId}">
                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px;">
                                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                                        <circle cx="12" cy="12" r="3"></circle>
                                                    </svg>
                                                    View
                                                </a>
                                                <a class="btn btn-sm btn-action btn-edit" href="${pageContext.request.contextPath}/suppliers/edit?id=${s.supplierId}">
                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px;">
                                                        <path d="M12 20h9"></path>
                                                        <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"></path>
                                                    </svg>
                                                    Edit
                                                </a>
                                                <c:choose>
                                                    <c:when test="${s.status}">
                                                        <!-- Show Deactivate button for active suppliers -->
                                                        <form method="post" action="${pageContext.request.contextPath}/suppliers/delete" class="deactivate-form" style="display:inline;">
                                                            <input type="hidden" name="id" value="${s.supplierId}" />
                                                            <button type="button" class="btn btn-sm btn-action btn-delete btn-open-deactivate-modal" data-supplier-id="${s.supplierId}" data-supplier-name="${fn:escapeXml(s.name)}">
                                                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px;">
                                                                    <circle cx="12" cy="12" r="10"></circle>
                                                                    <line x1="15" y1="9" x2="9" y2="15"></line>
                                                                    <line x1="9" y1="9" x2="15" y2="15"></line>
                                                                </svg>
                                                                Deactivate
                                                            </button>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <!-- Show Activate button for inactive suppliers -->
                                                        <form method="post" action="${pageContext.request.contextPath}/suppliers/activate" class="activate-form" style="display:inline;">
                                                            <input type="hidden" name="id" value="${s.supplierId}" />
                                                            <button type="button" class="btn btn-sm btn-action btn-activate btn-open-activate-modal" data-supplier-id="${s.supplierId}" data-supplier-name="${fn:escapeXml(s.name)}">
                                                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:4px;">
                                                                    <circle cx="12" cy="12" r="10"></circle>
                                                                    <polyline points="9 12 11 14 15 10"></polyline>
                                                                </svg>
                                                                Activate
                                                            </button>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="table-empty">No suppliers found. Try adjusting filters.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>

                <div class="d-flex justify-content-between align-items-center" style="margin-top: 1rem; flex-wrap: nowrap; gap: 1rem;">
                    <div>
                        <form method="get" action="${pageContext.request.contextPath}/suppliers" class="d-flex gap-2" style="align-items: center; white-space: nowrap;">
                            <input type="hidden" name="search" value="${fn:escapeXml(search)}" />
                            <input type="hidden" name="status" value="${statusFilter}" />
                            <input type="hidden" name="page" value="${page}" />
                            <label class="form-label" style="margin: 0; align-self: center;">Page Size</label>
                            <select class="form-select" name="size" onchange="this.form.submit()" style="width: 80px; padding: 0.25rem 0.5rem;">
                                <option value="5" ${size == 5 ? 'selected' : ''}>5</option>
                                <option value="10" ${size == 10 ? 'selected' : ''}>10</option>
                                <option value="20" ${size == 20 ? 'selected' : ''}>20</option>
                            </select>
                        </form>
                    </div>
                    <div>
                        <c:if test="${totalPages > 1}">
                            <ul class="pagination" style="margin:0;">
                                <li>
                                    <a class="${page <= 1 ? 'disabled' : ''}" href="${pageContext.request.contextPath}/suppliers?page=${page-1}&size=${size}&search=${fn:escapeXml(search)}&status=${statusFilter}">
                                        <span style="display:inline-flex; align-items:center; gap:4px;">
                                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                <polyline points="15 18 9 12 15 6"></polyline>
                                            </svg>
                                            Prev
                                        </span>
                                    </a>
                                </li>
                                <c:forEach var="p" begin="1" end="${totalPages}">
                                    <li>
                                        <a class="${p == page ? 'active' : ''}" href="${pageContext.request.contextPath}/suppliers?page=${p}&size=${size}&search=${fn:escapeXml(search)}&status=${statusFilter}">${p}</a>
                                    </li>
                                </c:forEach>
                                <li>
                                    <a class="${page >= totalPages ? 'disabled' : ''}" href="${pageContext.request.contextPath}/suppliers?page=${page+1}&size=${size}&search=${fn:escapeXml(search)}&status=${statusFilter}">
                                        <span style="display:inline-flex; align-items:center; gap:4px;">
                                            Next
                                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                <polyline points="9 18 15 12 9 6"></polyline>
                                            </svg>
                                        </span>
                                    </a>
                                </li>
                            </ul>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />
    
    <!-- Deactivate Confirmation Modal -->
    <div class="modal-backdrop" id="deactivateModal">
        <div class="modal" role="dialog" aria-modal="true" aria-labelledby="deactivateModalTitle">
            <div class="modal-header">
                <h2 class="modal-title" id="deactivateModalTitle">Confirm Deactivate</h2>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to deactivate supplier <strong id="deactivateSupplierName"></strong>?</p>
                <p style="color: var(--gray-600); font-size: 0.875rem; margin-top: 0.5rem;">The supplier will be marked as inactive but can be reactivated later.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-outline" id="cancelDeactivateBtn">Cancel</button>
                <button type="button" class="btn btn-sm btn-danger-solid" id="confirmDeactivateBtn">Deactivate</button>
            </div>
        </div>
    </div>
    
    <!-- Activate Confirmation Modal -->
    <div class="modal-backdrop" id="activateModal">
        <div class="modal" role="dialog" aria-modal="true" aria-labelledby="activateModalTitle">
            <div class="modal-header">
                <h2 class="modal-title" id="activateModalTitle">Confirm Activate</h2>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to activate supplier <strong id="activateSupplierName"></strong>?</p>
                <p style="color: var(--gray-600); font-size: 0.875rem; margin-top: 0.5rem;">The supplier will be marked as active and available for use.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-outline" id="cancelActivateBtn">Cancel</button>
                <button type="button" class="btn btn-sm" id="confirmActivateBtn" style="background: var(--success-600); border-color: var(--success-600); color: #fff;">Activate</button>
            </div>
        </div>
    </div>

    <script>
        (function(){
            // Deactivate Modal
            const deactivateModal = document.getElementById('deactivateModal');
            const deactivateSupplierNameEl = document.getElementById('deactivateSupplierName');
            const cancelDeactivateBtn = document.getElementById('cancelDeactivateBtn');
            const confirmDeactivateBtn = document.getElementById('confirmDeactivateBtn');
            let pendingDeactivateForm = null;

            function openDeactivateModal(form, name) {
                pendingDeactivateForm = form;
                deactivateSupplierNameEl.textContent = name || '';
                deactivateModal.style.display = 'flex';
            }

            function closeDeactivateModal() {
                deactivateModal.style.display = 'none';
                pendingDeactivateForm = null;
            }

            document.querySelectorAll('.btn-open-deactivate-modal').forEach(btn => {
                btn.addEventListener('click', function(e){
                    e.preventDefault();
                    const form = this.closest('form');
                    const name = this.getAttribute('data-supplier-name');
                    openDeactivateModal(form, name);
                });
            });

            cancelDeactivateBtn.addEventListener('click', function(){ closeDeactivateModal(); });
            deactivateModal.addEventListener('click', function(e){ if (e.target === deactivateModal) closeDeactivateModal(); });
            confirmDeactivateBtn.addEventListener('click', function(){ if (pendingDeactivateForm) pendingDeactivateForm.submit(); });

            // Activate Modal
            const activateModal = document.getElementById('activateModal');
            const activateSupplierNameEl = document.getElementById('activateSupplierName');
            const cancelActivateBtn = document.getElementById('cancelActivateBtn');
            const confirmActivateBtn = document.getElementById('confirmActivateBtn');
            let pendingActivateForm = null;

            function openActivateModal(form, name) {
                pendingActivateForm = form;
                activateSupplierNameEl.textContent = name || '';
                activateModal.style.display = 'flex';
            }

            function closeActivateModal() {
                activateModal.style.display = 'none';
                pendingActivateForm = null;
            }

            document.querySelectorAll('.btn-open-activate-modal').forEach(btn => {
                btn.addEventListener('click', function(e){
                    e.preventDefault();
                    const form = this.closest('form');
                    const name = this.getAttribute('data-supplier-name');
                    openActivateModal(form, name);
                });
            });

            cancelActivateBtn.addEventListener('click', function(){ closeActivateModal(); });
            activateModal.addEventListener('click', function(e){ if (e.target === activateModal) closeActivateModal(); });
            confirmActivateBtn.addEventListener('click', function(){ if (pendingActivateForm) pendingActivateForm.submit(); });

            // Close modals on Escape key
            document.addEventListener('keydown', function(e){ 
                if (e.key === 'Escape') {
                    closeDeactivateModal();
                    closeActivateModal();
                }
            });

            // Auto-hide success alerts after 3 seconds
            window.addEventListener('load', function(){
                const successAlerts = document.querySelectorAll('.alert.alert-success');
                if (successAlerts && successAlerts.length) {
                    setTimeout(function(){
                        successAlerts.forEach(function(el){ el.style.display = 'none'; });
                    }, 3000);
                }
            });
        })();
    </script>
</body>
</html>



