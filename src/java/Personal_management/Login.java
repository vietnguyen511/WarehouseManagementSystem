package Personal_management;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;
import model.*;
import dal.*;



public class Login extends HttpServlet 
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException 
    {

        // Check if this is "Login using Email" button
        String useEmail = request.getParameter("useEmail");
        if (useEmail != null) 
        {
            // For now, redirect to new html
            response.sendRedirect("email_login.html"); // Come back soon
            return;
        }

        // Normal username login
        String username = request.getParameter("identifier");
        String password = request.getParameter("password");

        MyDAO conn = new MyDAO();
        conn.xSql = "SELECT * FROM Users WHERE username=? AND password=?";

        try 
        {
            conn.ps =conn.con.prepareStatement (conn.xSql);
            conn.ps.setString(1, username);
            conn.ps.setString(2, password);

            conn.rs = conn.ps.executeQuery();

            if (conn.rs.next()) 
            {
                // Login successful
                request.getSession().setAttribute("user", conn.rs.getString("username"));
                response.sendRedirect("home.html"); // Change to your home page, come back soon
            } 
            else 
            {
                // Login failed
                response.sendRedirect("login.html?error=1");
            }

        }            
        catch (Exception e) 
        {
            e.printStackTrace();
            response.sendRedirect("login.html?error=500");
        }
    }
}
