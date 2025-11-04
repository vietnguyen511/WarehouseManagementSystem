<%@ page contentType="text/html; charset=UTF-8" %>
<%
    request.setAttribute("activePage", "dashboard");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Manager Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/warehouse-style.css">
</head>
<body>
    <%@ include file="../components/top-header.jsp" %>
    <div style="display: flex;">
        <%@ include file="../components/sidebar-nav.jsp" %>
        <main class="main-content-with-sidebar">
            <div class="card">
                <div class="card-header">
                    <span class="card-title">Welcome Manager!</span>
                </div>
                <div class="card-body">
                    This is the Manager Dashboard. Select a report or function from the sidebar to begin.
                </div>
            </div>
        </main>
    </div>
</body>
</html>
