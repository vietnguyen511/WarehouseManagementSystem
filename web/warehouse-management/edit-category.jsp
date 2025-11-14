<%-- 
    Document   : edit-category
    Created on : Oct 20, 2025, 11:33:03 AM
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
        <title>Edit Category - Warehouse Management System</title>

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
                max-width: 800px;
                margin: var(--spacing-lg) auto;
                border: 1px solid var(--gray-200);
            }

            .card-header {
                padding: var(--spacing-md) var(--spacing-lg);
                border-bottom: 1px solid var(--gray-200);
            }

            .card-body {
                padding: var(--spacing-lg);
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
            textarea,
            select {
                width: 100%;
                padding: 0.6rem 0.7rem;
                border: 1px solid var(--gray-300);
                border-radius: var(--radius-md);
                font-size: 0.95rem;
                background-color: #fff;
                color: var(--gray-900);
                box-sizing: border-box;
            }

            input:focus, textarea:focus, select:focus {
                outline: 2px solid var(--primary-200);
                border-color: var(--primary-500);
                box-shadow: 0 0 0 2px var(--primary-100);
            }

            .btn-bar {
                display: flex;
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
            }

            .btn-secondary {
                background-color: var(--gray-500);
                color: white;
                border: none;
                border-radius: var(--radius-md);
                padding: 8px 16px;
                cursor: pointer;
                font-weight: 500;
            }

            .alert {
                margin-top: 1rem;
                padding: 0.75rem 1rem;
                border-radius: var(--radius-md);
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
        </style>
    </head>
    <body>
        <jsp:include page="/components/top-header.jsp" />
        <jsp:include page="/components/sidebar-nav.jsp" />

        <!-- Page header bar -->
        <div class="page-header-bar">
            <div class="page-header-left">
                <h2>Edit Category</h2>
                <p>Modify category details and status</p>
            </div>
            <div class="page-header-right">
                <button type="button" class="btn-secondary" onclick="goBack()">← Back</button>
            </div>
        </div>

        <div class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <h1>Edit Category</h1>
                    <p class="card-subtitle">Edit category name, description, and status</p>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/warehouse-management/edit-category" method="post" novalidate>
                        <input type="hidden" name="id" value="${category.categoryId}">

                        <div class="form-group">
                            <label for="code" class="required">Category Code</label>
                            <input type="text" id="code" name="code" value="${category.code}" required minlength="2" maxlength="20">
                        </div>

                        <div class="form-group">
                            <label for="name" class="required">Category Name</label>
                            <input type="text" id="name" name="name" value="${category.name}" required minlength="2" maxlength="100">
                        </div>

                        <div class="form-group">
                            <label for="description">Description</label>
                            <textarea id="description" name="description" rows="3">${category.description}</textarea>
                        </div>

                        <div class="form-group">
                            <label for="status">Status</label>
                            <select id="status" name="status">
                                <option value="1" <c:if test="${category.status}">selected</c:if>>Active</option>
                                <option value="0" <c:if test="${!category.status}">selected</c:if>>Inactive</option>
                                </select>
                            </div>

                            <div class="btn-bar">
                                <button type="submit" class="btn-primary">Save Changes</button>
                                <button type="button" class="btn-secondary" onclick="goBack()">Cancel</button>
                            </div>
                        </form>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success">${successMessage}</div>
                    </c:if>
                </div>
            </div>
        </div>

        <jsp:include page="/components/footer.jsp" />

        <script>
            function goBack() {
                if (confirm("Discard changes and return to category list?")) {
                    window.location.href = "${pageContext.request.contextPath}/warehouse-management/category-management";
                }
            }
        </script>
    </body>
</html>