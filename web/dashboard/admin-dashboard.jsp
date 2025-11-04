<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
    <style>
        .dashboard-container {
            display: flex;
            min-height: 100vh;
        }
        .main-content {
            flex: 1;
            background: #f5f6fa;
            padding: 32px;
            min-height: 600px;
        }
    </style>
</head>
<body>
    <%@ include file="../components/top-header.jsp" %>
    <%
        request.setAttribute("activePage", "dashboard");
    %>
    <div style="display: flex;">
        <%@ include file="../components/sidebar-nav.jsp" %>
        <main class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <span class="card-title">Welcome Admin!</span>
                </div>
                <div class="card-body">
                    Select a function from the sidebar to get started.
                </div>
            </div>
        </main>
    </div>
</body>
</html>
