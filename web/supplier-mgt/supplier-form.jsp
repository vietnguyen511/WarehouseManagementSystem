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
    <style>
        .field-error { color: var(--danger-600); font-style: italic; font-size: 0.8125rem; margin-top: 4px; }
        .form-input.invalid, .form-select.invalid { border-color: var(--danger-600); box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.12); }
    </style>
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
                <form method="post" action="${pageContext.request.contextPath}<c:choose><c:when test='${mode == "edit"}'>/suppliers/edit</c:when><c:otherwise>/suppliers/new</c:otherwise></c:choose>" id="supplierForm" novalidate>
                    <c:if test='${mode == "edit"}'>
                        <input type="hidden" name="id" value="${supplier.supplierId}" />
                    </c:if>
                    <div class="form-group">
                        <label class="form-label">Name <span class="text-danger">*</span></label>
                        <input class="form-input" type="text" name="name" id="name" value="${supplier.name}" required />
                        <div class="field-error" id="nameError"></div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Phone</label>
                        <input class="form-input" type="text" name="phone" id="phone" value="${supplier.phone}" />
                        <div class="field-error" id="phoneError"></div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Email</label>
                        <input class="form-input" type="email" name="email" id="email" value="${supplier.email}" />
                        <div class="field-error" id="emailError"></div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Address</label>
                        <input class="form-input" type="text" name="address" id="address" value="${supplier.address}" />
                        <div class="field-error" id="addressError"></div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Status</label>
                        <select class="form-select" name="status" id="status">
                            <c:set var="isActive" value='${supplier.status}' />
                            <option value="1" ${isActive ? 'selected' : ''}>Active</option>
                            <option value="0" ${!isActive ? 'selected' : ''}>Inactive</option>
                        </select>
                        <div class="field-error" id="statusError"></div>
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
    <script>
        (function(){
            const form = document.getElementById('supplierForm');
            if (!form) return;
            const nameInput = document.getElementById('name');
            const phoneInput = document.getElementById('phone');
            const emailInput = document.getElementById('email');
            const statusSelect = document.getElementById('status');

            function clearError(input, errorEl){
                if (input) input.classList.remove('invalid');
                if (errorEl) errorEl.textContent = '';
            }
            function setError(input, errorEl, message){
                if (input) input.classList.add('invalid');
                if (errorEl) errorEl.textContent = message || '';
            }

            function isValidEmail(value){
                return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
            }
            function isValidPhone(value){
                return /^[0-9+\-\s]{7,15}$/.test(value);
            }

            function validate(){
                let valid = true;
                clearError(nameInput, document.getElementById('nameError'));
                clearError(phoneInput, document.getElementById('phoneError'));
                clearError(emailInput, document.getElementById('emailError'));
                clearError(statusSelect, document.getElementById('statusError'));

                if (!nameInput.value.trim()) { setError(nameInput, document.getElementById('nameError'), 'Name is required'); valid = false; }
                if (phoneInput.value.trim() && !isValidPhone(phoneInput.value.trim())) { setError(phoneInput, document.getElementById('phoneError'), 'Phone must be 7-15 digits/symbols'); valid = false; }
                if (emailInput.value.trim() && !isValidEmail(emailInput.value.trim())) { setError(emailInput, document.getElementById('emailError'), 'Invalid email format'); valid = false; }

                return valid;
            }

            nameInput.addEventListener('input', function(){ clearError(nameInput, document.getElementById('nameError')); });
            phoneInput.addEventListener('input', function(){ clearError(phoneInput, document.getElementById('phoneError')); });
            emailInput.addEventListener('input', function(){ clearError(emailInput, document.getElementById('emailError')); });
            statusSelect.addEventListener('change', function(){ clearError(statusSelect, document.getElementById('statusError')); });

            form.addEventListener('submit', function(e){
                if (!validate()) {
                    e.preventDefault();
                    const firstInvalid = form.querySelector('.invalid');
                    if (firstInvalid) firstInvalid.focus();
                }
            });
        })();
    </script>
</body>
</html>


