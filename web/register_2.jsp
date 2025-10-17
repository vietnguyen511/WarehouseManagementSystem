<!DOCTYPE html>
<html>
<head>
    <title>Register Form - Step 2</title>
    <style>
        body { background: #c6ceff; display: flex; justify-content: center; align-items: center; height: 100vh; }
        .register-box { background: #b9c2ff; padding: 40px; border-radius: 10px; width: 400px; }
        input { width: 100%; padding: 10px; margin: 10px 0; }
        .btn { padding: 12px; width: 100%; border: none; background: #5a5af5; color: white; cursor: pointer; font-size: 16px; }
        .error { color: red; font-size: 14px; }
    </style>
</head>
<body>
<div class="register-box">
    <h2>Register form</h2>

    <form action="registerStep2" method="post">
        <input type="text" name="username" placeholder="Username" required>
        <input type="password" name="password" placeholder="Password" required>
        <input type="password" name="confirmPassword" placeholder="Confirm password" required>

        <!-- Error message if confirm password fails -->
        <% String error = request.getParameter("error");
           if (error != null) { %>
            <p class="error"> <%= error %> </p>
        <% } %>

        <button type="submit" class="btn">Confirm</button>
    </form>
</div>
</body>
</html>
