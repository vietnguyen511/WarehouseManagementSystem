<%-- 
    Document   : add-category
    Created on : Oct 22, 2025, 7:43:28 AM
    Author     : DANG
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    request.setAttribute("activePage", "categories");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Add Category - Warehouse Management System</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
        <script src="${pageContext.request.contextPath}/js/warehouse-app.js" defer></script>

        <style>
            html, body {
                height: auto;
                overflow: auto;
            }
            .main-content-with-sidebar {
                padding: var(--spacing-lg);
                flex: 1;
            }
            .card {
                background: #fff;
                border-radius: var(--radius-lg);
                box-shadow: var(--shadow-sm);
                overflow: hidden;
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
            }
            .required::after {
                content: " *";
                color: red;
            }
            input, textarea, select {
                width: 100%;
                padding: 0.6rem;
                border: 1px solid var(--gray-300);
                border-radius: var(--radius-md);
                font-size: 0.95rem;
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
            }
            .btn-secondary:hover {
                background-color: var(--gray-600);
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
        <!--top header-->
        <jsp:include page="/components/top-header.jsp" />
        <!--sidebar-->
        <jsp:include page="/components/sidebar-nav.jsp" />

        <div class="card-header">
            <h2 class="card-title">Add New Category</h2>
        </div>

        <div class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <h1>Add New Category</h1>
                    <p class="card-subtitle">Create a new category for warehouse products</p>
                </div>

                <div class="card-body">
                    <form id="addCategoryForm" action="${pageContext.request.contextPath}/warehouse-management/add-category" method="post" novalidate>
                        <div class="form-group">
                            <label for="code" class="required">Category Code</label>
                            <input type="text" id="code" name="code" value="${category.code}" required minlength="2" maxlength="20">
                        </div>

                        <div class="form-group">
                            <label for="name" class="required">Category Name</label>
                            <input type="text" id="name" name="name" value="${category.name}" required minlength="2" maxlength="50">
                        </div>

                        <div class="form-group">
                            <label for="description">Description</label>
                            <textarea id="description" name="description" rows="3" maxlength="255">${category.description}</textarea>
                        </div>

                        <div class="form-group">
                            <label for="status">Status</label>
                            <select id="status" name="status">
                                <option value="active" ${category.status ? "selected" : ""}>Active</option>
                                <option value="inactive" ${!category.status ? "selected" : ""}>Inactive</option>
                            </select>
                        </div>

                        <div class="btn-bar">
                            <button type="submit" class="btn-primary">Save</button>
                            <button type="button" class="btn-secondary" onclick="goBack()">Back</button>
                        </div>
                    </form>

                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success">${successMessage}</div>
                    </c:if>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>
                </div>
            </div>
        </div>

        <div class="message">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>
        </div>

        <jsp:include page="/components/footer.jsp" />

        <script>
            function goBack() {
                if (confirm("Unsaved data will be lost. Go back?")) {
                    window.location.href = "${pageContext.request.contextPath}/warehouse-management/category-management";
                }
            }

            document.getElementById('addCategoryForm').addEventListener('submit', function (e) {
                const code = document.getElementById('code').value.trim();
                const name = document.getElementById('name').value.trim();
                if (code.length < 2 || name.length < 2) {
                    alert("Code and Name must have at least 2 characters.");
                    e.preventDefault();
                }
            });
        </script>
    </body>
</html>
