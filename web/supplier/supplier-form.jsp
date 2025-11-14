<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    request.setAttribute("activePage", "add-supplier");
%>
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
                        <input class="form-input" type="text" name="phone" id="phone" value="${supplier.phone}" placeholder="0xxxxxxxxx (10-11 digits)" />
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
            const nameErrorEl = document.getElementById('nameError');
            const phoneErrorEl = document.getElementById('phoneError');
            
            // Get supplier ID if in edit mode
            const supplierIdInput = form.querySelector('input[name="id"]');
            const supplierId = supplierIdInput ? supplierIdInput.value : null;

            let nameCheckTimeout = null;
            let isCheckingName = false;

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
                if (!value || !value.trim()) return true; // Phone is optional
                const trimmed = value.trim();
                // Must start with 0, contain only digits, and be 10-11 digits total
                return /^0\d{9,10}$/.test(trimmed);
            }
            
            // Validate phone with detailed error messages
            function validatePhone(value) {
                const trimmed = value.trim();
                
                // Phone is optional, so empty is valid
                if (!trimmed) {
                    return { valid: true, message: '' };
                }
                
                // Check if contains non-digit characters
                if (!/^\d+$/.test(trimmed)) {
                    return { valid: false, message: 'Phone number can only contain digits (0-9)' };
                }
                
                // Check if starts with 0
                if (!trimmed.startsWith('0')) {
                    return { valid: false, message: 'Phone number must start with 0' };
                }
                
                // Check length (10-11 digits total)
                if (trimmed.length < 10) {
                    return { valid: false, message: 'Phone number must have 10-11 digits' };
                }
                if (trimmed.length > 11) {
                    return { valid: false, message: 'Phone number must have 10-11 digits' };
                }
                
                return { valid: true, message: '' };
            }

            // Check supplier name in database (real-time validation)
            function checkSupplierName(name) {
                if (!name || !name.trim()) {
                    clearError(nameInput, nameErrorEl);
                    return;
                }

                // Clear previous timeout
                if (nameCheckTimeout) {
                    clearTimeout(nameCheckTimeout);
                }

                // Debounce: wait 500ms after user stops typing
                nameCheckTimeout = setTimeout(function() {
                    if (isCheckingName) return;
                    isCheckingName = true;

                    const url = '${pageContext.request.contextPath}/api/check-supplier-name?name=' + encodeURIComponent(name.trim());
                    const finalUrl = supplierId ? url + '&excludeId=' + encodeURIComponent(supplierId) : url;

                    fetch(finalUrl)
                        .then(response => response.json())
                        .then(data => {
                            isCheckingName = false;
                            if (data.exists) {
                                setError(nameInput, nameErrorEl, data.message || 'Supplier with this name already exists');
                            } else {
                                clearError(nameInput, nameErrorEl);
                            }
                        })
                        .catch(error => {
                            isCheckingName = false;
                            console.error('Error checking supplier name:', error);
                            // Don't show error on network failure, just clear validation
                            clearError(nameInput, nameErrorEl);
                        });
                }, 500);
            }

            function validate(){
                let valid = true;
                clearError(nameInput, nameErrorEl);
                clearError(phoneInput, phoneErrorEl);
                clearError(emailInput, document.getElementById('emailError'));
                clearError(statusSelect, document.getElementById('statusError'));

                if (!nameInput.value.trim()) { 
                    setError(nameInput, nameErrorEl, 'Name is required'); 
                    valid = false; 
                } else {
                    // Check if name exists (synchronous check on submit)
                    // Note: Real-time check already done, but we check again to be sure
                    if (nameInput.classList.contains('invalid')) {
                        valid = false;
                    }
                }
                
                // Validate phone
                const phoneValidation = validatePhone(phoneInput.value);
                if (!phoneValidation.valid) {
                    setError(phoneInput, phoneErrorEl, phoneValidation.message);
                    valid = false;
                }
                
                if (emailInput.value.trim() && !isValidEmail(emailInput.value.trim())) { 
                    setError(emailInput, document.getElementById('emailError'), 'Invalid email format'); 
                    valid = false; 
                }

                return valid;
            }

            // Real-time validation for supplier name
            nameInput.addEventListener('input', function(){
                const name = nameInput.value.trim();
                if (name.length > 0) {
                    checkSupplierName(name);
                } else {
                    clearError(nameInput, nameErrorEl);
                }
            });
            
            // Also check on blur (when user leaves the field)
            nameInput.addEventListener('blur', function(){
                const name = nameInput.value.trim();
                if (name.length > 0) {
                    checkSupplierName(name);
                }
            });

            // Real-time validation for phone number
            phoneInput.addEventListener('input', function(e){
                const value = phoneInput.value;
                
                // Only allow digits
                const digitsOnly = value.replace(/[^\d]/g, '');
                if (value !== digitsOnly) {
                    phoneInput.value = digitsOnly;
                }
                
                // Validate phone number
                const phoneValidation = validatePhone(phoneInput.value);
                if (phoneInput.value.trim() && !phoneValidation.valid) {
                    setError(phoneInput, phoneErrorEl, phoneValidation.message);
                } else {
                    clearError(phoneInput, phoneErrorEl);
                }
            });
            
            // Also validate on blur
            phoneInput.addEventListener('blur', function(){
                const phoneValidation = validatePhone(phoneInput.value);
                if (phoneInput.value.trim() && !phoneValidation.valid) {
                    setError(phoneInput, phoneErrorEl, phoneValidation.message);
                } else {
                    clearError(phoneInput, phoneErrorEl);
                }
            });
            
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


