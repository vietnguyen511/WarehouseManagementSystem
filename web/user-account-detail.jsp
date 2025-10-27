
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*, model.User" %>
<c:set var="activePage" value="categories" scope="request" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Category Management - Warehouse Management System</title>

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
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: var(--spacing-md) var(--spacing-lg);
                border-bottom: 1px solid var(--gray-200);
            }
            .card-body {
                padding: var(--spacing-lg);
            }
            .table-wrapper {
                overflow-x: auto;
                margin-top: var(--spacing-md);
            }
            table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                padding: 0.75rem;
                border-bottom: 1px solid var(--gray-200);
                text-align: left;
            }
            th {
                background: var(--gray-50);
                font-weight: 600;
            }
            .action-bar {
                display: flex;
                gap: var(--spacing-sm);
            }
            .btn-danger {
                background-color: var(--danger-600);
                color: white;
            }
            .btn-danger:hover {
                background-color: var(--danger-700);
            }
            .alert {
                padding: 0.75rem 1rem;
                border-radius: var(--radius-md);
                margin-top: var(--spacing-md);
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
            .search-form {
                display: flex;
                align-items: center;
                gap: 8px;
                background: #f9fafb;
                border: 1px solid #d1d5db;
                border-radius: 8px;
                padding: 6px 10px;
                transition: all 0.2s ease-in-out;
            }

            .search-form input[type="text"] {
                flex: 1;
                border: none;
                outline: none;
                background: transparent;
                padding: 6px;
                font-size: 0.95rem;
            }

            .search-form button {
                background: var(--primary-600);
                color: white;
                border: none;
                border-radius: 6px;
                padding: 6px 12px;
                cursor: pointer;
                transition: background 0.2s ease-in-out;
            }

            .search-form button:hover {
                background: var(--primary-700);
            }

            .search-form:focus-within {
                box-shadow: 0 0 0 2px var(--primary-200);
                background: #fff;
            }
            
            
            
            
            
            
            .edit-input {
                  padding: 6px;
                  width: 60%;
                  border: 1px solid #ccc;
                  border-radius: 6px;
                  font-size: 15px;
           }
           
           .confirm-edit {
                  background: #2ed573;
                  color: white;
                  padding: 10px 18px;
                  border: none;
                  border-radius: 6px;
                  cursor: pointer;
                  font-weight: bold;
                  margin-right: 10px;
            }
            .cancel-edit {
                  background: #a4b0be;
                  color: white;
                  padding: 10px 18px;
                  border: none;
                  border-radius: 6px;
                  cursor: pointer;
                  font-weight: bold;
            }
            
            .action-section {
                  display: flex;
                  justify-content: space-between;
                  margin-top: 40px;
                  gap: 20px;
            }
            
            .action-box {
                  flex: 1;
                  text-align: center;
                  background: white;
                  padding: 30px 0;
                  border-radius: 10px;
                  box-shadow: 0 3px 6px rgba(0,0,0,0.1);
                  cursor: pointer;
                  transition: 0.2s;
                  font-size: 18px;
                  font-weight: bold;
            }
            
             .action-box:hover {
               transform: translateY(-5px);
               box-shadow: 0 5px 10px rgba(0,0,0,0.15);
            }
            
            .edit { background: #1e90ff; color: white; }
            
          .confirm-btn { background: #ff4757; color: white; }
           .cancel-btn { background: #a4b0be; color: white; }
        </style>
    </head>
    <body>
        <!-- Top Header -->
        <jsp:include page="/components/top-header.jsp" />
        <!-- Sidebar -->
        <jsp:include page="/components/sidebar-nav.jsp" />

        <div class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <div>
                        <h1 class="card-title">Account detail information</h1>
                        <p class="card-subtitle"></p>
                    </div>
                  
                   <!--Further upgrade -->
                </div>
                
                <div class="card-body">
                    <div class="table-wrapper">
                         <div id="viewMode">
                             <p><b>Username:</b> ${user.userName}</p>
                             <p><b>Password:</b> ${user.password}</p>
                             <p><b>Email:</b> ${user.email}</p>
                         </div>
                    </div>
                     
                         <!--Hidden edit choice-->      
                    <form id="editMode" style="display:none;" action="edit-account-user" method="post">
                        <input type="hidden" name="id" value="${user.id}">
                              <p><b>Username:</b> <input type="text" name="username" value="${user.userName}" class="edit-input"></p>
                              <p><b>Password:</b> <input type="text" name="password" value="${user.password}" class="edit-input"></p>   
                              <p><b>Email:</b> <input type="email" name="email" value="${user.email}" class="edit-input"></p>                                                                             
                        <div class="edit-buttons">
                              <button type="submit" class="confirm-edit">✅ Confirm</button>
                              <button type="button" class="cancel-edit" onclick="cancelEdit()">❌ Cancel</button>
                        </div>
                    </form>
                         
                      <div class="action-section" id="actionSection">
                              <div class="action-box edit" onclick="enableEdit()">✏️ Edit</div>
                     </div>    
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success">${successMessage}</div>
                    </c:if>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>
                </div>
            </div>
        </div>
  <!-- Enable edit -->
    <script>
    function enableEdit() 
     {
      document.getElementById("viewMode").style.display = "none";
      document.getElementById("actionSection").style.display = "none";
      document.getElementById("editMode").style.display = "block";
     }
   function cancelEdit()  
     {
      document.getElementById("editMode").style.display = "none";
      document.getElementById("viewMode").style.display = "block";
      document.getElementById("actionSection").style.display = "flex";
     }
</script>
        <jsp:include page="/components/footer.jsp" />      
    </body>
</html