<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title><c:choose><c:when test='${mode == "edit"}'>Edit Supplier</c:when><c:otherwise>Create Supplier</c:otherwise></c:choose> - Warehouse Management System</title>
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
                    <h1 class="card-title"><c:choose><c:when test='${mode == "edit"}'>Edit Supplier</c:when><c:otherwise>Create Supplier</c:otherwise></c:choose></h1>
                    <p class="card-subtitle"><c:choose><c:when test='${mode == "edit"}'>Update supplier information</c:when><c:otherwise>Add a new supplier</c:otherwise></c:choose></p>
                </div>
            </div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <form method="post" action="${pageContext.request.contextPath}<c:choose><c:when test='${mode == "edit"}'>/suppliers/edit</c:when><c:otherwise>/suppliers/new</c:otherwise></c:choose>">
                    <c:if test='${mode == "edit"}'>
                        <input type="hidden" name="id" value="${supplier.supplierId}" />
                    </c:if>
                    <div class="form-group">
                        <label class="form-label">Name <span class="text-danger">*</span></label>
                        <input class="form-input" type="text" name="name" value="${supplier.name}" required />
                    </div>
                    <div class="form-group">
                        <label class="form-label">Phone</label>
                        <input class="form-input" type="text" name="phone" value="${supplier.phone}" />
                    </div>
                    <div class="form-group">
                        <label class="form-label">Email</label>
                        <input class="form-input" type="email" name="email" value="${supplier.email}" />
                    </div>
                    <div class="form-group">
                        <label class="form-label">Address</label>
                        <input class="form-input" type="text" name="address" value="${supplier.address}" />
                    </div>
                    <div class="form-group">
                        <label class="form-label">Status</label>
                        <select class="form-select" name="status">
                            <c:set var="isActive" value='${supplier.status}' />
                            <option value="1" ${isActive ? 'selected' : ''}>Active</option>
                            <option value="0" ${!isActive ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary"><c:choose><c:when test='${mode == "edit"}'>Save</c:when><c:otherwise>Create</c:otherwise></c:choose></button>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/suppliers">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />
</body>
</html>


