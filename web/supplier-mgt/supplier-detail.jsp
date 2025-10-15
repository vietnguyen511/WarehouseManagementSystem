<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Supplier Detail - Warehouse Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
</head>
<body>
    <jsp:include page="/components/top-header.jsp" />
    <jsp:include page="/components/sidebar-nav.jsp" />

    <div class="main-content-with-sidebar">
        <div class="card">
            <div class="card-header">
                <div>
                    <h1 class="card-title">Supplier Detail</h1>
                    <p class="card-subtitle">View supplier information</p>
                </div>
            </div>
            <div class="card-body">
                <c:if test="${empty supplier}">
                    <div class="alert alert-warning">Supplier not found.</div>
                </c:if>

                <c:if test="${not empty supplier}">
                    <div class="d-flex gap-3" style="flex-wrap:wrap;">
                        <div style="min-width:260px;">
                            <div class="form-group">
                                <label class="form-label">Name</label>
                                <div class="font-medium">${supplier.name}</div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Phone</label>
                                <div>${supplier.phone}</div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Email</label>
                                <div>${supplier.email}</div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Address</label>
                                <div>${supplier.address}</div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Status</label>
                                <div>
                                    <c:choose>
                                        <c:when test="${supplier.status}"><span class="badge badge-success">Active</span></c:when>
                                        <c:otherwise><span class="badge badge-secondary">Inactive</span></c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="d-flex gap-2 mt-3">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/suppliers/edit?id=${supplier.supplierId}">Edit</a>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/suppliers">Back to list</a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />
</body>
</html>


