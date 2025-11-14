<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Add Product - Warehouse Management System</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
        <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>

        <style>
            html, body {
                height: auto;
                overflow: auto;
            }

            body {
                background-color: var(--gray-50);
                font-family: "Segoe UI", Roboto, sans-serif;
                color: var(--gray-900);
                margin: 0;
            }

            .main-content-with-sidebar {
                padding: var(--spacing-lg);
                flex: 1;
            }

            .page-header-bar {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: var(--spacing-md) var(--spacing-lg);
                border-bottom: 1px solid var(--gray-200);
                background-color: #fff;
                box-shadow: var(--shadow-xs);
            }

            .page-header-left h2 {
                font-size: 1.2rem;
                font-weight: 600;
                margin: 0;
                color: var(--gray-900);
            }
            .page-header-left p {
                margin: .25rem 0 0 0;
                color: var(--gray-500);
                font-size: .9rem;
                line-height: 1.4;
            }

            .card {
                background: #fff;
                border-radius: var(--radius-lg);
                box-shadow: var(--shadow-sm);
                overflow: hidden;
                max-width: 900px;
                margin: var(--spacing-lg) auto;
                border: 1px solid var(--gray-200);
            }

            .card-header {
                padding: var(--spacing-md) var(--spacing-lg);
                border-bottom: 1px solid var(--gray-200);
            }

            .card-header h1 {
                font-size: 1.1rem;
                font-weight: 600;
                color: var(--gray-900);
                margin: 0;
            }

            .card-subtitle {
                color: var(--gray-500);
                font-size: 0.9rem;
                margin-top: .25rem;
            }

            .card-body {
                padding: var(--spacing-lg);
            }

            .form-grid-2 {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: var(--spacing-md);
            }

            .form-group {
                margin-bottom: 1rem;
            }

            label {
                font-weight: 500;
                color: var(--gray-800);
                display: inline-block;
                margin-bottom: .4rem;
                font-size: .95rem;
            }

            .required::after {
                content: " *";
                color: red;
                font-weight: 600;
            }

            input[type="text"],
            input[type="number"],
            select,
            textarea,
            input[type="file"] {
                width: 100%;
                padding: 0.6rem 0.7rem;
                border: 1px solid var(--gray-300);
                border-radius: var(--radius-md);
                font-size: 0.95rem;
                background-color: #fff;
                color: var(--gray-900);
                box-sizing: border-box;
            }

            input:focus,
            select:focus,
            textarea:focus {
                outline: 2px solid var(--primary-200);
                border-color: var(--primary-500);
                box-shadow: 0 0 0 2px var(--primary-100);
            }

            textarea {
                resize: vertical;
                min-height: 80px;
            }

            .btn-bar {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
                margin-top: 1.5rem;
            }

            .btn-primary {
                background-color: var(--primary-600);
                color: white;
                border: none;
                border-radius: var(--radius-md);
                padding: 8px 16px;
                cursor: pointer;
                font-weight: 600;
                font-size: .95rem;
                line-height: 1.2rem;
            }

            .btn-primary:hover {
                background-color: var(--primary-700);
            }

            .btn-secondary {
                background-color: var(--gray-500);
                color: white;
                border: none;
                border-radius: var(--radius-md);
                padding: 8px 16px;
                cursor: pointer;
                font-weight: 500;
                font-size: .95rem;
                line-height: 1.2rem;
            }

            .btn-secondary:hover {
                background-color: var(--gray-600);
            }

            .alert {
                margin-top: 1rem;
                margin-bottom: 1rem;
                padding: 0.75rem 1rem;
                border-radius: var(--radius-md);
                font-size: .9rem;
                line-height: 1.4;
            }

            .alert-success {
                background-color: var(--success-50);
                color: var(--success-700);
                border: 1px solid var(--success-200);
            }

            .alert-danger {
                background-color: var(--danger-50);
                color: var(--danger-700);
                border: 1px solid var(--danger-200);
            }

            .section-title {
                font-size: .8rem;
                font-weight: 600;
                color: var(--gray-500);
                text-transform: uppercase;
                letter-spacing: .05em;
                margin: 1.5rem 0 .75rem 0;
                border-top: 1px solid var(--gray-200);
                padding-top: 1rem;
            }

            @media (max-width: 768px) {
                .form-grid-2 {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <!-- Top header -->
        <jsp:include page="/components/top-header.jsp" />
        <!-- Sidebar -->
        <jsp:include page="/components/sidebar-nav.jsp" />

        <!-- Page header bar -->
        <div class="page-header-bar">
            <div class="page-header-left">
                <h2>Add New Product</h2>
                <p>Fill in product info and save it to warehouse database</p>
            </div>
            <div class="page-header-right">
                <button type="button" class="btn-secondary" onclick="goBack()">← Back</button>
            </div>
        </div>

        <!-- Main content -->
        <div class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <h1>Add Product</h1>
                    <p class="card-subtitle">Basic info, pricing, status, and product image</p>
                </div>

                <div class="card-body">

                    <!-- server messages -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="alert alert-success">${success}</div>
                    </c:if>

                    <!-- IMPORTANT: multipart for file upload -->
                    <form id="addProductForm"
                          action="${pageContext.request.contextPath}/warehouse-management/add-product"
                          method="post"
                          enctype="multipart/form-data"
                          novalidate>

                        <!-- SECTION: Basic Info -->
                        <div class="section-title">Basic Information</div>

                        <div class="form-grid-2">
                            <!-- Product Code -->
                            <div class="form-group">
                                <label for="code" class="required">Product Code</label>
                                <input type="text"
                                       id="code"
                                       name="code"
                                       value="${old_code}"
                                       required
                                       minlength="2"
                                       maxlength="20"
                                       placeholder="e.g. SP123">
                            </div>

                            <!-- Product Name -->
                            <div class="form-group">
                                <label for="name" class="required">Product Name</label>
                                <input type="text"
                                       id="name"
                                       name="name"
                                       value="${old_name}"
                                       required
                                       minlength="2"
                                       maxlength="100"
                                       placeholder="e.g. Áo Thun Nam Basic">
                            </div>
                        </div>

                        <!-- Category -->
                        <div class="form-group">
                            <label for="category_id" class="required">Category</label>
                            <select id="category_id" name="category_id" required>
                                <option value="">-- Select Category --</option>
                                <c:forEach var="c" items="${categories}">
                                    <option value="${c.categoryId}"
                                            <c:if test="${old_category_id == c.categoryId}">selected</c:if>>
                                        ${c.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- SECTION: Details -->
                        <div class="section-title">Details</div>

                        <div class="form-grid-2">
                            <div class="form-group">
                                <label for="material">Material</label>
                                <input type="text"
                                       id="material"
                                       name="material"
                                       value="${old_material}"
                                       maxlength="50"
                                       placeholder="Cotton, Denim, etc.">
                            </div>

                            <div class="form-group">
                                <label for="unit">Unit</label>
                                <input type="text"
                                       id="unit"
                                       name="unit"
                                       value="${old_unit}"
                                       maxlength="20"
                                       placeholder="cái, bộ...">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="description">Description</label>
                            <textarea id="description"
                                      name="description"
                                      rows="3"
                                      maxlength="255"
                                      placeholder="Short description of the product...">${old_description}</textarea>
                        </div>

                        <!-- SECTION: Status -->
                        <div class="section-title">Status</div>

                        <div class="form-group">
                            <label for="status">Status</label>
                            <select id="status" name="status">
                                <option value="1" <c:if test="${empty old_status || old_status == '1'}">selected</c:if>>Active</option>
                                <option value="0" <c:if test="${old_status == '0'}">selected</c:if>>Inactive</option>
                                </select>
                            </div>

                            <!-- ACTION BUTTONS -->
                            <div class="btn-bar">
                                <button type="submit" class="btn-primary">Save Product</button>
                                <button type="button" class="btn-secondary" onclick="goBack()">Back</button>
                            </div>
                        </form>

                        <!-- duplicated message zone if you want after form -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success">${successMessage}</div>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- footer -->
        <jsp:include page="/components/footer.jsp" />

        <script>
            function goBack() {
                if (confirm("Unsaved data will be lost. Go back?")) {
                    window.location.href = "${pageContext.request.contextPath}/warehouse-management/product-management";
                }
            }

            // basic front-end check
            const form = document.getElementById('addProductForm');
            form.addEventListener('submit', function (e) {
                const code = document.getElementById('code').value.trim();
                const name = document.getElementById('name').value.trim();
                const cat = document.getElementById('category_id').value.trim();

                if (code.length < 2 || name.length < 2 || cat === "") {
                    alert("Please fill Product Code, Product Name, and Category (min length 2).");
                    e.preventDefault();
                }
            });
        </script>
    </body>
</html>
