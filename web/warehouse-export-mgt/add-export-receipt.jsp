<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Create Export Receipt - Warehouse Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>
    <style>
        /* Allow page scrolling */
        html, body { height: auto; overflow: auto; }
        .main-content-with-sidebar { min-height: auto; overflow: visible; padding-left: var(--spacing-md); padding-right: var(--spacing-md); padding-bottom: var(--spacing-xl); flex: 1; }
        .main-content-with-sidebar > .card, .main-content-with-sidebar > * { max-width: 100%; }
        .app-footer { position: static; }
        /* Placeholder styles to match gray tone */
        .form-input::placeholder { color: var(--gray-400); font-style: normal; font-family: var(--font-sans); font-weight: 400; }
        .form-textarea::placeholder { color: var(--gray-400); font-style: normal; font-family: var(--font-sans); font-weight: 400; }
        /* Ensure Note (textarea) and Export Date use the same font as text inputs */
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
        .field-error-slot { min-height: 18px; font-size: 0.8125rem; font-style: italic; color: var(--danger-600); margin-top: 4px; visibility: hidden; }
        .field-error-slot.show { visibility: visible; }
        .totals-bar { display: flex; justify-content: flex-end; align-items: center; gap: var(--spacing-md); padding: var(--spacing-md) var(--spacing-lg); background: var(--gray-50); border: 1px solid var(--gray-200); border-radius: var(--radius-lg); }
        .action-bar { display: flex; gap: var(--spacing-sm); }
        /* Category field styling */
        .category-name { background-color: var(--gray-50); cursor: not-allowed; }
        
        /* Better spacing for form groups */
        .receipt-grid { 
            display: grid; 
            grid-template-columns: 1fr 1fr; 
            gap: var(--spacing-md); 
            margin-bottom: var(--spacing-lg); 
        }
        .receipt-grid .form-group { 
            margin-bottom: 0; 
            width: 100%;
            min-width: 0;
        }
        .receipt-grid .form-group:last-child { 
            grid-column: 1 / span 2; 
        }
        .receipt-grid input,
        .receipt-grid select {
            width: 100%;
        }
        
        /* Add extra padding for Supplier field */
        #supplierId {
            padding: 0.575rem 1rem;
        }
        
        /* Tooltip styling for better UX */
        .form-input[title], .form-select[title] { 
            position: relative; 
        }
        
        /* Card-based product items layout */
        #productItemsContainer {
            display: flex;
            flex-direction: column;
            gap: var(--spacing-lg);
        }
        
        .product-item-card {
            background: white;
            border: 1px solid var(--gray-200);
            border-radius: var(--radius-lg);
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            transition: box-shadow 0.2s;
        }
        
        .product-item-card:hover {
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.08);
        }
        
        .product-item-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: var(--spacing-md) var(--spacing-lg);
            background: var(--gray-50);
            border-bottom: 1px solid var(--gray-200);
            border-radius: var(--radius-lg) var(--radius-lg) 0 0;
        }
        
        .product-item-number {
            font-size: 0.875rem;
            font-weight: 600;
            color: var(--gray-700);
        }
        
        .product-item-actions {
            display: flex;
            gap: var(--spacing-sm);
        }
        
        .product-item-body {
            padding: var(--spacing-lg);
        }
        
        .product-section {
            margin-bottom: var(--spacing-lg);
        }
        
        .product-section:last-child {
            margin-bottom: 0;
        }
        
        .product-section-title {
            font-size: 0.8125rem;
            font-weight: 600;
            color: var(--gray-700);
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: var(--spacing-md);
            padding-bottom: var(--spacing-xs);
            border-bottom: 2px solid var(--primary-100);
        }
        
        .product-section-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: var(--spacing-md);
        }
        
        .product-section-grid-three {
            grid-template-columns: repeat(3, 1fr);
        }
        
        @media (max-width: 1024px) {
            .product-section-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
        
        @media (max-width: 768px) {
            .product-section-grid {
                grid-template-columns: 1fr;
            }
        }
        
        .subtotal-display {
            font-size: 0.875rem;
            font-weight: 600;
            color: var(--success-600);
            padding: 0.375rem 0.75rem;
            background: var(--success-50);
            border: 1px solid var(--success-200);
            border-radius: var(--radius-md);
            text-align: center;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        /* Field label styling */
        .field-label { 
            font-size: 0.75rem; 
            font-weight: 600; 
            color: var(--gray-700); 
            margin-bottom: 0.25rem; 
            text-transform: uppercase;
            letter-spacing: 0.025em;
            text-align: left;
        }
        .field-label .required::after { content: " *"; color: var(--danger-600); }
        
        /* Form group styling */
        .product-item-card .form-group {
            margin-bottom: 0;
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
                    <h1 class="card-title">Create Export Receipt</h1>
                    <p class="card-subtitle">Enter receipt info</p>
                </div>
                <div class="action-bar">
                    <a href="${pageContext.request.contextPath}/warehouse-export-mgt/export-receipt-list" class="btn btn-secondary">Back to List</a>
                </div>
            </div>

            <form class="card-body" method="post" action="${pageContext.request.contextPath}/createExportReceipt" id="exportForm" novalidate>
                <div class="receipt-grid">
                    <div class="form-group">
                        <label class="form-label" for="exportDate">Export Date</label>
                        <input type="date" id="exportDate" name="exportDate" class="form-input" required placeholder="yyyy-mm-dd" value="${today}">
                    </div>
                    <div class="form-group">
                        <label class="form-label required" for="customerId">Customer</label>
                        <select id="customerId" name="customerId" class="form-select" required>
                            <option value="" disabled selected hidden>Select a customer...</option>
                            <c:forEach var="s" items="${customers}">
                                <option value="${s.customerId}">${s.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group" style="grid-column: 1 / span 2;">
                        <label class="form-label" for="note">Note</label>
                        <textarea id="note" name="note" class="form-textarea" placeholder="Add remarks for this receipt..."></textarea>
                    </div>
                </div>

                <!-- Product Items Section -->
                <div id="productItemsSection">
                    <div style="margin-bottom: var(--spacing-md);">
                        <h3 style="margin: 0; font-size: 1.125rem; font-weight: 600; color: var(--gray-900);">Product Items</h3>
                        <p style="margin: 0.25rem 0 0 0; font-size: 0.875rem; color: var(--gray-600);">Add products to this export receipt. All fields marked with * are required.</p>
                    </div>
                    
                    <div id="productItemsContainer">
                        <!-- Product Item Card -->
                        <div class="product-item-card" data-item-index="0">
                            <div class="product-item-header">
                                <div class="product-item-number">Item #1</div>
                                <div class="product-item-actions">
                                    <button type="button" class="btn btn-secondary btn-sm add-item">Add Item</button>
                                    <button type="button" class="btn btn-danger btn-sm remove-item" disabled>Remove</button>
                                </div>
                            </div>
                            
                            <div class="product-item-body">
                                <!-- Product Information Section -->
                                <div class="product-section">
                                    <h4 class="product-section-title">Product Information</h4>
                                    <div class="product-section-grid">
                                        <div class="form-group">
                                            <label class="form-label required" for="productCode_0">Product Code</label>
                                            <input type="text" id="productCode_0" name="items[0].productCode" class="form-input" placeholder="Enter product code" required>
                                            <div class="field-error-slot"></div>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label required" for="productName_0">Product Name</label>
                                            <input type="text" id="productName_0" name="items[0].productName" class="form-input" placeholder="Enter product name" required>
                                            <div class="field-error-slot"></div>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label required" for="category_0">Category</label>
                                            <input type="text" name="items[0].categoryName" class="form-input category-name" placeholder="Category" readonly style="display:none;">
                                            <select id="category_0" name="items[0].categoryId" class="form-select category-select" required>
                                                <option value="" disabled selected hidden>Select category</option>
                                                <c:forEach var="cat" items="${categories}">
                                                    <option value="${cat.categoryId}">${cat.code} - ${cat.name}</option>
                                                </c:forEach>
                                            </select>
                                            <input type="hidden" name="items[0].categoryId" class="category-id-hidden">
                                            <div class="field-error-slot"></div>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label required" for="material_0">Material</label>
                                            <input type="text" id="material_0" name="items[0].material" class="form-input material" placeholder="Enter material" required>
                                            <div class="field-error-slot"></div>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label required" for="unit_0">Unit</label>
                                            <input type="text" id="unit_0" name="items[0].unit" class="form-input unit" placeholder="Enter unit" required>
                                            <div class="field-error-slot"></div>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Product Variant Section -->
                                <div class="product-section">
                                    <h4 class="product-section-title">Product Variant</h4>
                                    <div class="product-section-grid">
                                        <div class="form-group">
                                            <label class="form-label required" for="size_0">Size</label>
                                            <input type="text" id="size_0" name="items[0].size" class="form-input size" placeholder="Enter size" required>
                                            <div class="field-error-slot"></div>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label required" for="color_0">Color</label>
                                            <input type="text" id="color_0" name="items[0].color" class="form-input color" placeholder="Enter color" required>
                                            <div class="field-error-slot"></div>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Export Details Section -->
                                <div class="product-section">
                                    <h4 class="product-section-title">Export Details</h4>
                                    <div class="product-section-grid product-section-grid-three">
                                        <div class="form-group">
                                            <label class="form-label required" for="quantity_0">Quantity</label>
                                            <input type="number" id="quantity_0" name="items[0].quantity" class="form-input qty" min="1" value="1" required>
                                            <div class="field-error-slot"></div>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label required" for="price_0">Export Price</label>
                                            <input type="number" id="price_0" name="items[0].price" class="form-input price" min="0" step="0.01" value="0" placeholder="0.00" required>
                                            <div class="field-error-slot"></div>
                                        </div>
                                        <div class="form-group">
                                            <label class="form-label">Subtotal</label>
                                            <div class="subtotal-display">$0.00</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
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
                        <a href="${pageContext.request.contextPath}/warehouse-export-mgt/export-receipt-list" class="btn btn-secondary">Cancel</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <jsp:include page="/components/footer.jsp" />

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const container = document.getElementById('productItemsContainer');
            const grandTotalEl = document.getElementById('grandTotal');
            const totalAmountInput = document.getElementById('totalAmount');
            const form = document.getElementById('exportForm');
            
            function format(amount){ 
                return (window.formatCurrency ? window.formatCurrency(amount) : new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD'}).format(amount)); 
            }
            // Default export date to today
            (function setToday(){
                const d = new Date();
                const yyyy = d.getFullYear();
                const mm = String(d.getMonth()+1).padStart(2,'0');
                const dd = String(d.getDate()).padStart(2,'0');
                const today = `${yyyy}-${mm}-${dd}`;
                const dateInput = document.getElementById('exportDate');
                if (dateInput && !dateInput.value) { dateInput.value = today; }
            })();
            function recalcRow(card){ 
                const qty = parseFloat(card.querySelector('.qty')?.value || '0'); 
                const price = parseFloat(card.querySelector('.price')?.value || '0'); 
                const subtotalEl = card.querySelector('.subtotal-display');
                
                if (subtotalEl) {
                    subtotalEl.textContent = format(qty * price);
                }
                recalcTotal(); 
            }
            
            function recalcTotal(){ 
                let total = 0; 
                const cards = Array.from(container.querySelectorAll('.product-item-card'));
                cards.forEach(function(card) {
                    const qty = parseFloat(card.querySelector('.qty')?.value || '0'); 
                    const price = parseFloat(card.querySelector('.price')?.value || '0'); 
                    total += qty * price; 
                });
                grandTotalEl.textContent = format(total); 
                totalAmountInput.value = total.toFixed(2); 
            }
            function bindCardEvents(card){
                const code = card.querySelector('input[name*="productCode"]');
                const name = card.querySelector('input[name*="productName"]');
                const categorySelect = card.querySelector('.category-select');
                const material = card.querySelector('.material');
                const unit = card.querySelector('.unit');
                const size = card.querySelector('.size');
                const color = card.querySelector('.color');
                const qty = card.querySelector('.qty');
                const price = card.querySelector('.price');
                
                if (code) code.addEventListener('blur', function(){ clearError(code); triggerLookup(code, card); });
                if (code) code.addEventListener('change', function(){ clearError(code); triggerLookup(code, card); });
                if (code) code.addEventListener('input', function(){
                    clearError(code);
                    // If code value changes, immediately clear name, material, unit, and category to avoid stale values
                    const current = code.value.trim();
                    if (code.dataset.lastCode !== current) {
                        if (name) { name.value = ''; }
                        const materialInput = card.querySelector('.material');
                        const unitInput = card.querySelector('.unit');
                        if (materialInput) { materialInput.value = ''; }
                        if (unitInput) { unitInput.value = ''; }
                        resetCategoryToDropdown(card);
                        code.dataset.lastCode = current;
                    }
                    debounceLookup(code, card);
                });
                if (name) name.addEventListener('input', function(){ clearError(name); });
                if (categorySelect) categorySelect.addEventListener('change', function(){ clearError(categorySelect); });
                if (material) material.addEventListener('input', function(){ clearError(material); });
                if (unit) unit.addEventListener('input', function(){ clearError(unit); });
                if (size) size.addEventListener('input', function(){ clearError(size); });
                if (color) color.addEventListener('input', function(){ clearError(color); });
                if (qty) qty.addEventListener('input', function(){ clearError(qty); recalcRow(card); });
                if (price) price.addEventListener('input', function(){ clearError(price); recalcRow(card); });
                
                const addBtn = card.querySelector('.add-item');
                const removeBtn = card.querySelector('.remove-item');
                if (addBtn) addBtn.addEventListener('click', addItem);
                if (removeBtn) removeBtn.addEventListener('click', function(){ 
                    if(container.querySelectorAll('.product-item-card').length > 1){ 
                        card.remove();
                        updateItemNumbers();
                        recalcTotal(); 
                        updateRemoveButtons(); 
                    }
                });
            }
            
            function updateRemoveButtons(){ 
                const cards = Array.from(container.querySelectorAll('.product-item-card')); 
                const canRemove = cards.length > 1;
                cards.forEach(function(card){ 
                    const removeBtn = card.querySelector('.remove-item');
                    if (removeBtn) {
                        removeBtn.disabled = !canRemove; 
                    }
                }); 
            }
            
            function updateItemNumbers(){ 
                const cards = Array.from(container.querySelectorAll('.product-item-card'));
                cards.forEach(function(card, idx){ 
                    const itemNumber = card.querySelector('.product-item-number');
                    if (itemNumber) {
                        itemNumber.textContent = 'Item #' + (idx + 1);
                    }
                    card.querySelectorAll('input, select').forEach(function(input){ 
                        if (input.name) {
                            input.name = input.name.replace(/items\[[0-9]+\]/, 'items[' + idx + ']');
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
            
            function addItem(){ 
                const idx = container.querySelectorAll('.product-item-card').length;
                const newCard = document.createElement('div');
                newCard.className = 'product-item-card';
                newCard.setAttribute('data-item-index', idx);
                newCard.innerHTML = '' +
                    '<div class="product-item-header">' +
                        '<div class="product-item-number">Item #' + (idx + 1) + '</div>' +
                        '<div class="product-item-actions">' +
                            '<button type="button" class="btn btn-secondary btn-sm add-item">Add Item</button>' +
                            '<button type="button" class="btn btn-danger btn-sm remove-item">Remove</button>' +
                        '</div>' +
                    '</div>' +
                    '<div class="product-item-body">' +
                        '<div class="product-section">' +
                            '<h4 class="product-section-title">Product Information</h4>' +
                            '<div class="product-section-grid">' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Product Code</label>' +
                                    '<input type="text" name="items[' + idx + '].productCode" class="form-input" placeholder="Enter product code" required>' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Product Name</label>' +
                                    '<input type="text" name="items[' + idx + '].productName" class="form-input" placeholder="Enter product name" required>' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Category</label>' +
                                    '<input type="text" name="items[' + idx + '].categoryName" class="form-input category-name" placeholder="Category" readonly style="display:none;">' +
                                    '<select name="items[' + idx + '].categoryId" class="form-select category-select" required>' + getCategoryOptionsHTML() + '</select>' +
                                    '<input type="hidden" name="items[' + idx + '].categoryId" class="category-id-hidden">' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Material</label>' +
                                    '<input type="text" name="items[' + idx + '].material" class="form-input material" placeholder="Enter material" required>' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Unit</label>' +
                                    '<input type="text" name="items[' + idx + '].unit" class="form-input unit" placeholder="Enter unit" required>' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                        '<div class="product-section">' +
                            '<h4 class="product-section-title">Product Variant</h4>' +
                            '<div class="product-section-grid">' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Size</label>' +
                                    '<input type="text" name="items[' + idx + '].size" class="form-input size" placeholder="Enter size" required>' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Color</label>' +
                                    '<input type="text" name="items[' + idx + '].color" class="form-input color" placeholder="Enter color" required>' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                        '<div class="product-section">' +
                            '<h4 class="product-section-title">Export Details</h4>' +
                            '<div class="product-section-grid product-section-grid-three">' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Quantity</label>' +
                                    '<input type="number" name="items[' + idx + '].quantity" class="form-input qty" min="1" value="1" required>' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                                '<div class="form-group">' +
                                    '<label class="form-label required">Export Price</label>' +
                                    '<input type="number" name="items[' + idx + '].price" class="form-input price" min="0" step="0.01" value="0" placeholder="0.00" required>' +
                                    '<div class="field-error-slot"></div>' +
                                '</div>' +
                                '<div class="form-group">' +
                                    '<label class="form-label">Subtotal</label>' +
                                    '<div class="subtotal-display">$0.00</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>';
                
                container.appendChild(newCard); 
                bindCardEvents(newCard); 
                updateRemoveButtons(); 
                recalcRow(newCard); 
            }
            
            // Initialize first card
            const firstCard = container.querySelector('.product-item-card');
            if (firstCard) {
                bindCardEvents(firstCard);
                updateRemoveButtons(); 
                recalcTotal();
            }
            
            // Helper functions for category field management
            function showCategoryAsReadonly(card, categoryName, categoryId) {
                const categoryNameInput = card.querySelector('.category-name');
                const categorySelect = card.querySelector('.category-select');
                const categoryIdHidden = card.querySelector('.category-id-hidden');
                
                if (categoryNameInput && categorySelect) {
                    categoryNameInput.value = categoryName;
                    categoryNameInput.style.display = 'block';
                    categorySelect.style.display = 'none';
                    categorySelect.removeAttribute('required');
                    if (categoryIdHidden) categoryIdHidden.value = categoryId;
                }
            }
            
            function resetCategoryToDropdown(card) {
                const categoryNameInput = card.querySelector('.category-name');
                const categorySelect = card.querySelector('.category-select');
                const categoryIdHidden = card.querySelector('.category-id-hidden');
                
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
            function debounce(fn, delay){ 
                let t; 
                return function(){ 
                    clearTimeout(t); 
                    const args = arguments; 
                    t = setTimeout(() => fn.apply(this, args), delay); 
                }; 
            }
            
            const triggerLookup = function(codeInput, card){
                const codeVal = codeInput.value.trim();
                if (!codeVal) return;
                fetch('${pageContext.request.contextPath}/api/product-lookup?code=' + encodeURIComponent(codeVal) + '&_=' + Date.now())
                    .then(r => r.json())
                    .then(function(data){
                        const nameInput = card.querySelector('input[name*="productName"]');
                        const materialInput = card.querySelector('.material');
                        const unitInput = card.querySelector('.unit');
                        
                        if (!nameInput) return;
                        if (data && data.name) {
                            // Product exists - auto-fill name and category
                            nameInput.value = data.name;
                            clearError(nameInput);
                            
                            // Auto-fill material if available
                            if (materialInput && data.material) {
                                materialInput.value = data.material;
                                clearError(materialInput);
                            }
                            
                            // Auto-fill unit if available
                            if (unitInput && data.unit) {
                                unitInput.value = data.unit;
                                clearError(unitInput);
                            }
                            
                            // Handle category
                            if (data.categoryName && data.categoryId) {
                                // Product has category - show as readonly
                                showCategoryAsReadonly(card, data.categoryName, data.categoryId);
                            } else {
                                // Product exists but no category - show dropdown
                                resetCategoryToDropdown(card);
                            }
                        } else {
                            // Product not found - clear name and show category dropdown
                            nameInput.value = '';
                            resetCategoryToDropdown(card);
                        }
                    }).catch(function(){ 
                        resetCategoryToDropdown(card); 
                    });
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
                const exportDate = document.getElementById('exportDate');
                const supplier = document.getElementById('customerId');
                [exportDate, supplier].forEach(clearError);

                if (!exportDate.value) { setError(exportDate, 'Export date is required'); valid = false; }
                if (!supplier.value) { setError(supplier, 'Please select a customer'); valid = false; }

                // Card validations
                Array.from(container.querySelectorAll('.product-item-card')).forEach(function(card){
                    const code = card.querySelector('input[name*="productCode"]');
                    const name = card.querySelector('input[name*="productName"]');
                    const categorySelect = card.querySelector('.category-select');
                    const categoryNameInput = card.querySelector('.category-name');
                    const material = card.querySelector('.material');
                    const unit = card.querySelector('.unit');
                    const size = card.querySelector('.size');
                    const color = card.querySelector('.color');
                    const qty = card.querySelector('.qty');
                    const price = card.querySelector('.price');
                    [code, name, material, unit, size, color, qty, price].forEach(clearError);
                    if (categorySelect) clearError(categorySelect);
                    
                    if (!code.value.trim()) { setError(code, 'Product code is required'); valid = false; }
                    if (!name.value.trim()) { setError(name, 'Product name is required'); valid = false; }
                    if (!material.value.trim()) { setError(material, 'Material is required'); valid = false; }
                    if (!unit.value.trim()) { setError(unit, 'Unit is required'); valid = false; }
                    if (!size.value.trim()) { setError(size, 'Size is required'); valid = false; }
                    if (!color.value.trim()) { setError(color, 'Color is required'); valid = false; }
                    
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
            document.getElementById('exportDate').addEventListener('input', function(){ clearError(this); });
            document.getElementById('customerId').addEventListener('change', function(){ clearError(this); });

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



