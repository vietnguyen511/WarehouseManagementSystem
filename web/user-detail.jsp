
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*, model.User" %>
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
            .delete { background: #ff4757; color: white; }
            .lock { background: #ffa502; color: white; }
            
            
            .modal {
                 display: none;
                 position: fixed;
                 top: 0; left: 0; right: 0; bottom: 0;
                 background: rgba(0,0,0,0.5);
                 justify-content: center;
                 align-items: center;
                 z-index: 1000;
            }

    /* Modal content box */
           .modal-content {
                background: white;
                border-radius: 12px;
                width: 500px;
                max-width: 90%;
                padding: 25px;
                box-shadow: 0 4px 10px rgba(0,0,0,0.2);
                animation: popup 0.25s ease-out;
            }
            
           .confirm-btn { background: #ff4757; color: white; }
           .cancel-btn { background: #a4b0be; color: white; }
           .confirm-lock { background: #ffa502; color: white; }
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
                        <h1 class="card-title">User detail</h1>
                        <p class="card-subtitle">User information</p>
                    </div>
                  
                   <!--Further upgrade -->
                </div>

                <div class="card-body">
                    <div class="table-wrapper">
                         <div id="viewMode">
                             <p><b>ID:</b> ${user.id}</p>
                             <p><b>Full Name:</b> ${user.fullname}</p>
                             <p><b>Email:</b> ${user.email}</p>
                             <p><b>Phone:</b> ${user.phone}</p>
                             <p><b>Role:</b> ${user.role}</p>
                             <p><b>Status:</b> ${user.status ? "Active ✅" : "Inactive ❌"}</p>
                         </div>
                    </div>
                    <!--Hidden edit choice-->      
                    <form id="editMode" style="display:none;" action="edit-user" method="post">
                        <input type="hidden" name="id" value="${user.id}">
                              <p><b>Full Name:</b> <input type="text" name="fullname" value="${user.fullname}" class="edit-input"></p>
                              <p><b>Email:</b> <input type="email" name="email" value="${user.email}" class="edit-input"></p>
                              <p><b>Phone:</b> <input type="text" name="phone" value="${user.phone}" class="edit-input"></p>
                              <p><b>Role:</b> <input type="text" name="role" value="${user.role}" class="edit-input"></p>
                              <p><b>Status:</b>
                              <select name="status" class="edit-input">
                                        <option value="true" ${user.status ? "selected" : ""}>Active</option>
                                        <option value="false" ${!user.status ? "selected" : ""}>Inactive</option>
                              </select>
                              </p>
                        <div class="edit-buttons">
                              <button type="submit" class="confirm-edit">✅ Confirm</button>
                              <button type="button" class="cancel-edit" onclick="cancelEdit()">❌ Cancel</button>
                        </div>
                    </form>
                     <!--Choice bellow here -->  
                    <div class="action-section" id="actionSection">
                              <div class="action-box edit" onclick="enableEdit()">✏️ Edit</div>
                              <div class="action-box delete" onclick="openDeleteModal()">🗑️ Delete</div>
                              <div class="action-box lock" onclick="openLockModal()">🔒 Lock</div>
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
<!-- DELETE MODAL -->
<div class="modal" id="deleteModal">
    <div class="modal-content">
        <h3>Are you sure to delete this account?</h3>
        <p>Select reason(s) for deletion:</p>

        <div class="reason"><input type="checkbox" id="r1"> User left organization</div>
        <div class="reason"><input type="checkbox" id="r2"> Duplicate account</div>
        <div class="reason"><input type="checkbox" id="r3"> Misconduct / Policy violation</div>
        <div class="reason"><input type="checkbox" id="r4"> Account inactive for long time</div>
        <div class="reason"><input type="checkbox" id="r5"> <label for="other">Other (optional):</label></div>
        <input type="text" class="other-input" id="other" placeholder="Type other reason (optional)...">

        <div class="modal-buttons">
            <button class="cancel-btn" onclick="closeDeleteModal()">Cancel</button>
            <button class="confirm-btn" onclick="confirmDelete()">Confirm Delete</button>
        </div>
    </div>
</div>

<!-- LOCK MODAL -->
<div class="modal" id="lockModal">
    <div class="modal-content">
        <h3 style="color:#ffa502;">Are you sure to lock this account?</h3>
        <p>Select reason(s) for locking:</p>

        <div class="reason"><input type="checkbox" id="l1"> Suspicious activity</div>
        <div class="reason"><input type="checkbox" id="l2"> Multiple failed logins</div>
        <div class="reason"><input type="checkbox" id="l3"> Admin action</div>
        <div class="reason"><input type="checkbox" id="l4"> Account inactive for long time</div>
        <div class="reason"><input type="checkbox" id="l5"> <label for="otherLock">Other (optional):</label></div>
        <input type="text" class="other-input" id="otherLock" placeholder="Type other reason (optional)...">

        <div class="modal-buttons">
            <button class="cancel-btn" onclick="closeLockModal()">Cancel</button>
            <button class="confirm-lock" onclick="confirmLock()">Confirm Lock</button>
        </div>
    </div>
</div>

<!<!-- Enable edit -->
<script>
function enableEdit() {
    document.getElementById("viewMode").style.display = "none";
    document.getElementById("actionSection").style.display = "none";
    document.getElementById("editMode").style.display = "block";
}

function cancelEdit() {
    document.getElementById("editMode").style.display = "none";
    document.getElementById("viewMode").style.display = "block";
    document.getElementById("actionSection").style.display = "flex";
}
</script>


<script>
/* ===== DELETE MODAL FUNCTIONS ===== */
function openDeleteModal() {
    document.getElementById("deleteModal").style.display = "flex";
}
function closeDeleteModal() {
    document.getElementById("deleteModal").style.display = "none";
}
function confirmDelete() 
{
    let reasons = [];
    if (document.getElementById("r1").checked) reasons.push("User left organization");
    if (document.getElementById("r2").checked) reasons.push("Duplicate account");
    if (document.getElementById("r3").checked) reasons.push("Misconduct / Policy violation");
    if (document.getElementById("r4").checked) reasons.push("Account inactive for long time");
    if (document.getElementById("other").value.trim() !== "") reasons.push("Other: " + document.getElementById("other").value.trim());

    if (reasons.length === 0) {
        alert("Please select at least one reason before deleting.");
        return;
    }

    if (confirm("Confirm deleting this user?\n\nReason(s):\n- " + reasons.join("\n- "))) {
        window.location.href = 'delete-user?id=${user.id}';
    }
}

/* ===== LOCK MODAL FUNCTIONS ===== */
function openLockModal() {
    document.getElementById("lockModal").style.display = "flex";
}
function closeLockModal() {
    document.getElementById("lockModal").style.display = "none";
}
function confirmLock() {
    let reasons = [];
    if (document.getElementById("l1").checked) reasons.push("Suspicious activity");
    if (document.getElementById("l2").checked) reasons.push("Multiple failed logins");
    if (document.getElementById("l3").checked) reasons.push("Admin action");
    if (document.getElementById("l4").checked) reasons.push("Inactive for long time");
    if (document.getElementById("otherLock").value.trim() !== "") reasons.push("Other: " + document.getElementById("otherLock").value.trim());

    if (reasons.length === 0) {
        alert("Please select at least one reason before locking.");
        return;
    }

    if (confirm("Confirm locking this user?\n\nReason(s):\n- " + reasons.join("\n- "))) {
        window.location.href = 'lock-user?id=${user.id}';
    }
}
</script>
        <jsp:include page="/components/footer.jsp" />
    </body>
</html