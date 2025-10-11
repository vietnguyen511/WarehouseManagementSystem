<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activePage" value="import-receipts" scope="request" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Create Import Receipt - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    <style>
        /* Allow page scrolling */
        html, body { height: auto; overflow: auto; }
        .main-content-with-sidebar { min-height: auto; overflow: visible; padding-left: var(--spacing-md); padding-right: var(--spacing-md); padding-bottom: var(--spacing-xl); flex: 1; }
        .main-content-with-sidebar > .card, .main-content-with-sidebar > * { max-width: 100%; }
        .app-footer { position: static; }
        .receipt-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--spacing-md); }
        /* Placeholder styles to match gray tone */
        .form-input::placeholder { color: var(--gray-400); font-style: normal; font-family: var(--font-sans); font-weight: 400; }
        .form-textarea::placeholder { color: var(--gray-400); font-style: normal; font-family: var(--font-sans); font-weight: 400; }
        /* Ensure Note (textarea) and Import Date use the same font as text inputs */
        .form-textarea,
        input[type="date"].form-input {
            font-family: var(--font-sans);
            font-size: 0.875rem;
            font-weight: 400;
            color: var(--gray-900);
            line-height: 1.5;
        }
        /* Normalize native date control look to match text input */
        input[type="date"].form-input { -webkit-appearance: none; appearance: none; }
        /* Show gray placeholder color for selects with empty value */
        .form-select:required:invalid { color: var(--gray-400); }
        .form-select option { color: var(--gray-900); }
        .form-select option[hidden] { color: var(--gray-400); }
        /* Date input placeholder-like styling */
        input[type="date"]:required:invalid { color: var(--gray-400); }
        input[type="date"]:required:invalid::-webkit-datetime-edit { color: var(--gray-400); }
        input[type="date"]:focus::-webkit-datetime-edit { color: var(--gray-900); }
        /* Red asterisk for required field labels */
        .required::after { content: " *"; color: var(--danger-600); }
        /* Field error styling */
        .field-error { color: var(--danger-600); font-style: italic; font-size: 0.8125rem; margin-top: 4px; }
        .form-input.invalid, .form-select.invalid { border-color: var(--danger-600); box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.12); }
        /* Reserve space below inputs inside table cells so rows stay aligned */
        .field-cell { display: flex; flex-direction: column; gap: 0; }
        .field-error-slot { min-height: 18px; font-size: 0.8125rem; font-style: italic; color: var(--danger-600); margin-top: 4px; visibility: hidden; }
        .field-error-slot.show { visibility: visible; }
        .products-table thead th:nth-child(1) { width: 11%; }
        .products-table thead th:nth-child(2) { width: 17%; }
        .products-table thead th:nth-child(3) { width: 18%; }
        .products-table thead th:nth-child(4) { width: 9%; text-align: center; }
        .products-table thead th:nth-child(5) { width: 12%; text-align: right; }
        .products-table thead th:nth-child(6) { width: 11%; text-align: right; }
        .products-table thead th:nth-child(7) { width: 12%; text-align: center; }
        .products-table thead th { padding: 0.625rem 0.4rem; }
        .products-table tbody td { vertical-align: top; padding: 0.625rem 0.4rem; }
        .products-table tbody td:nth-child(4) { text-align: center; }
        .products-table tbody td:nth-child(5), .products-table tbody td:nth-child(6) { text-align: right; padding-right: 0.75rem; }
        .totals-bar { display: flex; justify-content: flex-end; align-items: center; gap: var(--spacing-md); padding: var(--spacing-md) var(--spacing-lg); background: var(--gray-50); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); }
        .action-bar { display: flex; gap: var(--spacing-sm); }
        /* Category field styling */
        .category-name { background-color: var(--gray-50); cursor: not-allowed; }
    </style>
</head>
<body>
    <jsp:include page="/components/top-header.jsp" />
    <jsp:include page="/components/sidebar-nav.jsp" />

    <div class="main-content-with-sidebar">
        <div class="card">
            <div class="card-header">
                <div>
                    <h1 class="card-title">Create Import Receipt</h1>
                    <p class="card-subtitle">Enter receipt info and product items</p>
                </div>
                <div class="action-bar">
                    <a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-list" class="btn btn-secondary">Back to List</a>
                </div>
            </div>

            <form class="card-body" method="post" action="${pageContext.request.contextPath}/createImportReceipt" id="importForm" novalidate>
                <div class="receipt-grid">
                    <div class="form-group">
                        <label class="form-label required" for="receiptCode">Receipt Code</label>
                        <input type="text" id="receiptCode" name="receiptCode" class="form-input" placeholder="e.g., IR-2025-0001" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="importDate">Import Date</label>
                        <input type="date" id="importDate" name="importDate" class="form-input" required placeholder="yyyy-mm-dd" value="${today}">
                    </div>
                    <div class="form-group" style="grid-column: 1 / span 2;">
                        <label class="form-label required" for="supplierId">Supplier</label>
                        <select id="supplierId" name="supplierId" class="form-select" required>
                            <option value="" disabled selected hidden>Select a supplier...</option>
                            <c:forEach var="s" items="${suppliers}">
                                <option value="${s.supplierId}">${s.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group" style="grid-column: 1 / span 2;">
                        <label class="form-label" for="note">Note</label>
                        <textarea id="note" name="note" class="form-textarea" placeholder="Add remarks for this receipt..."></textarea>
                    </div>
                </div>

                <div class="table-wrapper" style="margin-top: var(--spacing-lg);">
                    <table class="table products-table" id="productsTable">
                        <thead>
                            <tr>
                                <th><span class="required">Product Code</span></th>
                                <th><span class="required">Product Name</span></th>
                                <th><span class="required">Category</span></th>
                                <th><span class="required">Quantity</span></th>
                                <th><span class="required">Import Price</span></th>
                                <th>Subtotal</th>
                                <th style="text-align:center;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>
                                    <div class="field-cell">
                                        <input type="text" name="items[0].productCode" class="form-input" placeholder="Code" required>
                                        <div class="field-error-slot"></div>
                                    </div>
                                </td>
                                <td>
                                    <div class="field-cell">
                                        <input type="text" name="items[0].productName" class="form-input" placeholder="Name" required>
                                        <div class="field-error-slot"></div>
                                    </div>
                                </td>
                                <td>
                                    <div class="field-cell">
                                        <input type="text" name="items[0].categoryName" class="form-input category-name" placeholder="Category" readonly style="display:none;">
                                        <select name="items[0].categoryId" class="form-select category-select" required>
                                            <option value="" disabled selected hidden>Select category</option>
                                            <c:forEach var="cat" items="${categories}">
                                                <option value="${cat.categoryId}">${cat.code} - ${cat.name}</option>
                                            </c:forEach>
                                        </select>
                                        <input type="hidden" name="items[0].categoryId" class="category-id-hidden">
                                        <div class="field-error-slot"></div>
                                    </div>
                                </td>
                                <td>
                                    <div class="field-cell">
                                        <input type="number" name="items[0].quantity" class="form-input qty" min="1" value="1" required>
                                        <div class="field-error-slot"></div>
                                    </div>
                                </td>
                                <td>
                                    <div class="field-cell">
                                        <input type="number" name="items[0].price" class="form-input price" min="0" step="0.01" value="0" required>
                                        <div class="field-error-slot"></div>
                                    </div>
                                </td>
                                <td class="subtotal">$0.00</td>
                                <td style="text-align:center;">
                                    <button type="button" class="btn btn-secondary btn-sm add-row">Add</button>
                                    <button type="button" class="btn btn-danger btn-sm remove-row" disabled>Remove</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="d-flex justify-content-between align-items-center" style="margin-top: var(--spacing-lg);">
                    <div></div>
                    <div class="totals-bar">
                        <span class="text-muted">Total Amount:</span>
                        <strong id="grandTotal" class="font-semibold">$0.00</strong>
                        <input type="hidden" name="totalAmount" id="totalAmount" value="0">
                    </div>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger" style="margin: var(--spacing-md) 0;">
                        ${errorMessage}
                    </div>
                </c:if>
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success" style="margin: var(--spacing-md) 0;">
                        ${successMessage}
                    </div>
                </c:if>

                <div class="card-footer" style="margin-top: var(--spacing-lg);">
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-success">Save</button>
                        <a href="${pageContext.request.contextPath}/warehouse-import-mgt/import-receipt-list" class="btn btn-secondary">Cancel</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const table = document.getElementById('productsTable');
            const tbody = table.querySelector('tbody');
            const grandTotalEl = document.getElementById('grandTotal');
            const totalAmountInput = document.getElementById('totalAmount');
            const form = document.getElementById('importForm');
            function format(amount){ return (window.formatCurrency?window.formatCurrency(amount):new Intl.NumberFormat('en-US',{style:'currency',currency:'USD'}).format(amount)); }
            // Default import date to today
            (function setToday(){
                const d = new Date();
                const yyyy = d.getFullYear();
                const mm = String(d.getMonth()+1).padStart(2,'0');
                const dd = String(d.getDate()).padStart(2,'0');
                const today = `${yyyy}-${mm}-${dd}`;
                const dateInput = document.getElementById('importDate');
                if (dateInput && !dateInput.value) { dateInput.value = today; }
            })();
            function recalcRow(tr){ const qty=parseFloat(tr.querySelector('.qty').value||'0'); const price=parseFloat(tr.querySelector('.price').value||'0'); tr.querySelector('.subtotal').textContent=format(qty*price); recalcTotal(); }
            function recalcTotal(){ let total=0; tbody.querySelectorAll('tr').forEach(function(tr){ const qty=parseFloat(tr.querySelector('.qty').value||'0'); const price=parseFloat(tr.querySelector('.price').value||'0'); total+=qty*price; }); grandTotalEl.textContent=format(total); totalAmountInput.value=total.toFixed(2); }
            function bindRowEvents(tr){
                const code = tr.querySelector('input[name*="productCode"]');
                const name = tr.querySelector('input[name*="productName"]');
                const categorySelect = tr.querySelector('.category-select');
                const qty = tr.querySelector('.qty');
                const price = tr.querySelector('.price');
                if (code) code.addEventListener('blur', function(){ clearError(code); triggerLookup(code, tr); });
                if (code) code.addEventListener('change', function(){ clearError(code); triggerLookup(code, tr); });
                if (code) code.addEventListener('input', function(){
                    clearError(code);
                    // If code value changes, immediately clear name and category to avoid stale values
                    const current = code.value.trim();
                    if (code.dataset.lastCode !== current) {
                        const nameInput = tr.querySelector('input[name*="productName"]');
                        if (nameInput) { nameInput.value = ''; }
                        // Reset category to dropdown mode
                        resetCategoryToDropdown(tr);
                        code.dataset.lastCode = current;
                    }
                    debounceLookup(code, tr);
                });
                if (name) name.addEventListener('input', function(){ clearError(name); });
                if (categorySelect) categorySelect.addEventListener('change', function(){ clearError(categorySelect); });
                if (qty) qty.addEventListener('input', function(){ clearError(qty); recalcRow(tr); });
                if (price) price.addEventListener('input', function(){ clearError(price); recalcRow(tr); });
                tr.querySelector('.add-row').addEventListener('click',addRow);
                tr.querySelector('.remove-row').addEventListener('click',function(){ if(tbody.rows.length>1){ tr.remove(); updateRowNames(); recalcTotal(); updateRemoveButtons(); }});
            }
            function updateRemoveButtons(){ const rows=Array.from(tbody.querySelectorAll('tr')); const canRemove=rows.length>1; rows.forEach(function(r){ r.querySelector('.remove-row').disabled=!canRemove; }); }
            function updateRowNames(){ 
                Array.from(tbody.querySelectorAll('tr')).forEach(function(tr,idx){ 
                    tr.querySelectorAll('input, select').forEach(function(input){ 
                        if (input.name) {
                            input.name = input.name.replace(/items\[[0-9]+\]/,'items['+idx+']'); 
                        }
                    }); 
                }); 
            }
            
            // Build category dropdown options HTML
            function getCategoryOptionsHTML() {
                let html = '<option value="" disabled selected hidden>Select category...</option>';
                <c:forEach var="cat" items="${categories}">
                html += '<option value="${cat.categoryId}">${cat.code} - ${cat.name}</option>';
                </c:forEach>
                return html;
            }
            
            function addRow(){ 
                const idx=tbody.rows.length; 
                const tr=document.createElement('tr'); 
                tr.innerHTML=''+
                '<td><div class="field-cell"><input type="text" name="items['+idx+'].productCode" class="form-input" placeholder="Code" required><div class="field-error-slot"></div></div></td>'+
                '<td><div class="field-cell"><input type="text" name="items['+idx+'].productName" class="form-input" placeholder="Name" required><div class="field-error-slot"></div></div></td>'+
                '<td><div class="field-cell">'+
                    '<input type="text" name="items['+idx+'].categoryName" class="form-input category-name" placeholder="Category" readonly style="display:none;">'+
                    '<select name="items['+idx+'].categoryId" class="form-select category-select" required>'+getCategoryOptionsHTML()+'</select>'+
                    '<input type="hidden" name="items['+idx+'].categoryId" class="category-id-hidden">'+
                    '<div class="field-error-slot"></div>'+
                '</div></td>'+
                '<td><div class="field-cell"><input type="number" name="items['+idx+'].quantity" class="form-input qty" min="1" value="1" required><div class="field-error-slot"></div></div></td>'+
                '<td><div class="field-cell"><input type="number" name="items['+idx+'].price" class="form-input price" min="0" step="0.01" value="0" required><div class="field-error-slot"></div></div></td>'+
                '<td class="subtotal">$0.00</td>'+
                '<td style="text-align:center;"><button type="button" class="btn btn-secondary btn-sm add-row">Add</button> <button type="button" class="btn btn-danger btn-sm remove-row">Remove</button></td>';
                tbody.appendChild(tr); bindRowEvents(tr); updateRemoveButtons(); recalcRow(tr); 
            }
            bindRowEvents(tbody.querySelector('tr')); updateRemoveButtons(); recalcTotal();
            
            // Helper functions for category field management
            function showCategoryAsReadonly(tr, categoryName, categoryId) {
                const categoryNameInput = tr.querySelector('.category-name');
                const categorySelect = tr.querySelector('.category-select');
                const categoryIdHidden = tr.querySelector('.category-id-hidden');
                
                if (categoryNameInput && categorySelect) {
                    categoryNameInput.value = categoryName;
                    categoryNameInput.style.display = 'block';
                    categorySelect.style.display = 'none';
                    categorySelect.removeAttribute('required');
                    if (categoryIdHidden) categoryIdHidden.value = categoryId;
                }
            }
            
            function resetCategoryToDropdown(tr) {
                const categoryNameInput = tr.querySelector('.category-name');
                const categorySelect = tr.querySelector('.category-select');
                const categoryIdHidden = tr.querySelector('.category-id-hidden');
                
                if (categoryNameInput && categorySelect) {
                    categoryNameInput.value = '';
                    categoryNameInput.style.display = 'none';
                    categorySelect.style.display = 'block';
                    categorySelect.setAttribute('required', 'required');
                    categorySelect.value = '';
                    if (categoryIdHidden) categoryIdHidden.value = '';
                }
            }
            
            // Debounced product lookup by code
            function debounce(fn, delay){ let t; return function(){ clearTimeout(t); const args=arguments; t=setTimeout(()=>fn.apply(this,args), delay); }; }
            const triggerLookup = function(codeInput, tr){
                const codeVal = codeInput.value.trim();
                if (!codeVal) return;
                fetch('${pageContext.request.contextPath}/api/product-lookup?code=' + encodeURIComponent(codeVal) + '&_=' + Date.now())
                    .then(r=>r.json())
                    .then(function(data){
                        const nameInput = tr.querySelector('input[name*="productName"]');
                        if (!nameInput) return;
                        if (data && data.name) {
                            // Product exists - auto-fill name and category
                            nameInput.value = data.name;
                            clearError(nameInput);
                            
                            // Handle category
                            if (data.categoryName && data.categoryId) {
                                // Product has category - show as readonly
                                showCategoryAsReadonly(tr, data.categoryName, data.categoryId);
                            } else {
                                // Product exists but no category - show dropdown
                                resetCategoryToDropdown(tr);
                            }
                        } else {
                            // Product not found - clear name and show category dropdown
                            nameInput.value = '';
                            resetCategoryToDropdown(tr);
                        }
                    }).catch(function(){ resetCategoryToDropdown(tr); });
            };
            const debounceLookup = debounce(triggerLookup, 300);

            // -------- Validation helpers --------
            function clearError(input){
                input.classList.remove('invalid');
                const slot = input.parentElement.querySelector('.field-error-slot');
                if (slot) { slot.textContent = ''; slot.classList.remove('show'); }
            }
            function setError(input, message){
                input.classList.add('invalid');
                let slot = input.parentElement.querySelector('.field-error-slot');
                if (!slot) {
                    slot = document.createElement('div');
                    slot.className = 'field-error-slot';
                    input.parentElement.appendChild(slot);
                }
                slot.textContent = message;
                slot.classList.add('show');
            }

            function validateForm(){
                let valid = true;
                // Header fields
                const receiptCode = document.getElementById('receiptCode');
                const importDate = document.getElementById('importDate');
                const supplier = document.getElementById('supplierId');
                [receiptCode, importDate, supplier].forEach(clearError);

                if (!receiptCode.value.trim()) { setError(receiptCode, 'Receipt code is required'); valid = false; }
                if (!importDate.value) { setError(importDate, 'Import date is required'); valid = false; }
                if (!supplier.value) { setError(supplier, 'Please select a supplier'); valid = false; }

                // Row validations
                Array.from(tbody.querySelectorAll('tr')).forEach(function(tr){
                    const code = tr.querySelector('input[name*="productCode"]');
                    const name = tr.querySelector('input[name*="productName"]');
                    const categorySelect = tr.querySelector('.category-select');
                    const categoryNameInput = tr.querySelector('.category-name');
                    const qty = tr.querySelector('.qty');
                    const price = tr.querySelector('.price');
                    [code, name, qty, price].forEach(clearError);
                    if (categorySelect) clearError(categorySelect);
                    
                    if (!code.value.trim()) { setError(code, 'Product code is required'); valid = false; }
                    if (!name.value.trim()) { setError(name, 'Product name is required'); valid = false; }
                    
                    // Validate category - check if dropdown is visible and required
                    if (categorySelect && categorySelect.style.display !== 'none' && categorySelect.hasAttribute('required')) {
                        if (!categorySelect.value) { 
                            setError(categorySelect, 'Please select a category'); 
                            valid = false; 
                        }
                    }
                    
                    if (!qty.value || parseInt(qty.value, 10) < 1) { setError(qty, 'Quantity must be at least 1'); valid = false; }
                    if (price.value === '' || parseFloat(price.value) < 0) { setError(price, 'Price must be 0 or greater'); valid = false; }
                });

                return valid;
            }

            // Clear error for header fields on user input/change
            document.getElementById('receiptCode').addEventListener('input', function(){ clearError(this); });
            document.getElementById('importDate').addEventListener('input', function(){ clearError(this); });
            document.getElementById('supplierId').addEventListener('change', function(){ clearError(this); });

            form.addEventListener('submit', function(e){
                if (!validateForm()) {
                    e.preventDefault();
                    const firstError = form.querySelector('.invalid');
                    if (firstError) { firstError.focus({ preventScroll: false }); firstError.scrollIntoView({ behavior: 'smooth', block: 'center' }); }
                }
            });
        });
    </script>
</body>
</html>


