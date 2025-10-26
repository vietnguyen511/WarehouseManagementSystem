<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>
<%
    String code = (String) session.getAttribute("resetCode");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Enter verification code</title>
    <style>
      body { display:flex; justify-content:center; align-items:center; height:100vh; background:#f6f9ff; }
      .box { background:#c6ceff; padding:30px; width:420px; }
      input { width:100%; padding:10px; margin:10px 0; }
      .btn { padding:10px 16px; background:#5a5af5; color:white; border:none; cursor:pointer; }
    </style>
</head>
<body>
  <div class="box">
    <a href="login.html">← Back to login</a>
    <h2>Enter verification code</h2>

    <p>We sent a code to your email. (For testing the code is shown here)</p>
    <p>Code (for testing): <%= (code == null ? "(no code)" : code) %></strong></p>

    <% if ("1".equals(error)) { %>
      <p style="color:red;">Invalid code, try again.</p>
    <% } %>

    <form action="verify" method="post">
        <label>Enter code</label>
        <input type="text" name="code" placeholder="e.g. 3721" required />
        <button type="submit" class="btn">Verify</button>
    </form>
  </div>
</body>
</html>
