<%-- 
    Document   : view-product-details
    Created on : Oct 30, 2025, 9:25:58 PM
    Author     : DANG
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Product Details - Warehouse Management System</title>

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
                display: flex;
                flex-wrap: wrap;
                gap: .5rem;
                align-items: center;
            }

            .stock-badge {
                background: var(--gray-100);
                border: 1px solid var(--gray-300);
                color: var(--gray-700);
                font-size: .7rem;
                font-weight: 500;
                padding: 2px 6px;
                border-radius: var(--radius-md);
                line-height: 1rem;
            }

            .status-pill {
                display: inline-block;
                font-size: .75rem;
                line-height: 1rem;
                font-weight: 600;
                padding: 3px 8px;
                border-radius: 999px;
            }
            .status-active {
                background-color: var(--success-50);
                border: 1px solid var(--success-200);
                color: var(--success-700);
            }
            .status-inactive {
                background-color: var(--gray-100);
                border: 1px solid var(--gray-300);
                color: var(--gray-600);
            }

            .card-subtitle {
                color: var(--gray-500);
                font-size: 0.9rem;
                margin-top: .25rem;
            }

            .card-body {
                padding: var(--spacing-lg);
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

            input[type="text"],
            input[type="number"],
            select,
            textarea {
                width: 100%;
                padding: 0.6rem 0.7rem;
                border: 1px solid var(--gray-300);
                border-radius: var(--radius-md);
                font-size: 0.95rem;
                background-color: var(--gray-100);
                color: var(--gray-900);
                box-sizing: border-box;
            }

            textarea {
                resize: vertical;
                min-height: 80px;
            }

            input[disabled],
            select[disabled],
            textarea[disabled] {
                background-color: var(--gray-100);
                color: var(--gray-700);
                cursor: not-allowed;
            }

            .image-preview-wrapper {
                display: flex;
                align-items: flex-start;
                gap: 1rem;
            }

            .image-preview-box {
                width: 80px;
                height: 80px;
                border: 1px solid var(--gray-300);
                border-radius: var(--radius-md);
                background-color: var(--gray-100);
                background-size: cover;
                background-position: center;
                background-repeat: no-repeat;
                font-size: .7rem;
                color: var(--gray-500);
                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;
            }

            .btn-bar {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
                margin-top: 2rem;
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
                text-decoration: none;
                display: inline-block;
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
                text-decoration: none;
                display: inline-block;
            }

            .btn-secondary:hover {
                background-color: var(--gray-600);
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
                <h2>Product Details</h2>
                <p>View full product information</p>
            </div>
            <div class="page-header-right">
                <button type="button" class="btn-secondary" onclick="goBack()">← Back</button>
            </div>
        </div>

        <!-- Main content -->
        <div class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <h1>
                        <span><c:out value="${product.name}"/></span>
                        <c:choose>
                            <c:when test="${product.status}">
                                <span class="status-pill status-active">Active</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-pill status-inactive">Inactive</span>
                            </c:otherwise>
                        </c:choose>
                        <span class="stock-badge">Stock: <c:out value="${product.quantity}"/></span>
                    </h1>

                    <p class="card-subtitle">
                        ID #<c:out value="${product.productId}"/> · Code <c:out value="${product.code}"/>
                    </p>
                </div>

                <div class="card-body">

                    <!-- SECTION: Basic Info -->
                    <div class="section-title">Basic Information</div>

                    <div class="form-grid-2">
                        <div class="form-group">
                            <label>Product Code</label>
                            <input type="text"
                                   value="<c:out value='${product.code}'/>"
                                   disabled>
                        </div>

                        <div class="form-group">
                            <label>Product Name</label>
                            <input type="text"
                                   value="<c:out value='${product.name}'/>"
                                   disabled>
                        </div>
                    </div>

                    <!-- Category -->
                    <div class="form-group">
                        <label>Category</label>
                        <input type="text"
                               value="<c:out value='${product.categoryName}'/>"
                               disabled>
                    </div>

                    <!-- SECTION: Details -->
                    <div class="section-title">Details</div>

                    <div class="form-grid-2">
                        <div class="form-group">
                            <label>Material</label>
                            <input type="text"
                                   value="<c:out value='${product.material}'/>"
                                   disabled>
                        </div>

                        <div class="form-group">
                            <label>Unit</label>
                            <input type="text"
                                   value="<c:out value='${product.unit}'/>"
                                   disabled>
                        </div>
                    </div>

                    <div class="form-grid-2">
                        <div class="form-group">
                            <label>Import Price</label>
                            <input type="number"
                                   value="<c:out value='${product.importPrice}'/>"
                                   disabled>
                        </div>

                        <div class="form-group">
                            <label>Export Price</label>
                            <input type="number"
                                   value="<c:out value='${product.exportPrice}'/>"
                                   disabled>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Description</label>
                        <textarea rows="3" disabled><c:out value='${product.description}'/></textarea>
                    </div>

                    <!-- SECTION: Image -->
                    <div class="section-title">Product Image</div>

                    <div class="form-group">
                        <label>Main Image</label>
                        <div class="image-preview-wrapper">
                            <c:choose>
                                <c:when test="${not empty product.image}">
                                    <div class="image-preview-box"
                                         style="background-image:url('${pageContext.request.contextPath}/${previewUrl}')";color:transparent;">
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="image-preview-box">No Image</div>
                                </c:otherwise>
                            </c:choose>

                            <div style="flex:1;">
                                <label style="font-size:.8rem; color:var(--gray-500); display:block;">Image Path / URL</label>
                                <input type="text"
                                       value="<c:out value='${product.image}'/>"
                                       disabled>
                            </div>
                        </div>
                    </div>

                    <!-- SECTION: Status -->
                    <div class="section-title">Status</div>

                    <div class="form-grid-2">
                        <div class="form-group">
                            <label>Status</label>
                            <input type="text"
                                   value="<c:out value='${product.status ? "Active" : "Inactive"}'/>"
                                   disabled>
                        </div>

                        <div class="form-group">
                            <label>Quantity in Stock</label>
                            <input type="number"
                                   value="<c:out value='${product.quantity}'/>"
                                   disabled>
                        </div>
                    </div>

                    <!-- ACTION BUTTONS -->
                    <div class="btn-bar">
                        <a class="btn-primary"
                           href="${pageContext.request.contextPath}/warehouse-management/edit-product?id=${product.productId}">
                            Edit Product
                        </a>

                        <button type="button"
                                class="btn-secondary"
                                onclick="goBack()">
                            Back
                        </button>
                    </div>

                </div>
            </div>
        </div>

        <!-- footer -->
        <jsp:include page="/components/footer.jsp" />

        <script>
            function goBack() {
                window.location.href =
                    "${pageContext.request.contextPath}/warehouse-management/product-management";
            }
        </script>
    </body>
</html>

