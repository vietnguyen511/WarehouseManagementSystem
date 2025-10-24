<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register Form - Step 1</title>
    <style>
        body { background: #c6ceff; display: flex; justify-content: center; align-items: center; height: 100vh; }
        .register-box { background: #b9c2ff; padding: 40px; border-radius: 10px; width: 400px; }
        input, select { width: 100%; padding: 10px; margin: 10px 0; }
        .btn { padding: 12px; width: 100%; border: none; background: #5a5af5; color: white; cursor: pointer; font-size: 16px; }
        .gender-group { display: flex; align-items: center; gap: 15px; margin: 10px 0; }
    </style>
</head>
<body>
<div class="register-box">
    <h2>Register form</h2>

    <form action="registerStep1" method="post">
        <input type="text" name="firstname" placeholder="First name" required>
        <input type="text" name="lastname" placeholder="Last name" required>
        <input type="email" name="email" placeholder="Email" required>
        
           <!-- Error message if email fails -->
         <% String error =(String) request.getAttribute("error");
           if (error != null) { %>
            <p class="error"> <%= error %> </p>
        <% } %>
        <input type="text" name="phone" placeholder="Phone number" required>
         
        <input type="date" name="birthday" required>

        <!-- Gender side by side -->
        <div class="gender-group">
            Gender: 
            <label><input type="radio" name="gender" value="Male" required> Male</label>
            <label><input type="radio" name="gender" value="Female"> Female</label>
        </div>        
        <input type="text" name="role" placeholder="Role" required>

        <button type="submit" class="btn">Continue</button>
    </form>
</div>
</body>
</html>
