package controller.userLogging;

import java.io.*;
import model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import dal.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name ="Login",urlPatterns = {"/login"})
public class Login extends HttpServlet 
{
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
                    response.sendRedirect("index.html");             
    } 
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        String username = request.getParameter("identifier");
        String password = request.getParameter("password");
        try 
        {
            MyDAO conn = new MyDAO();
            conn.xSql = "SELECT * FROM Users WHERE username=? AND password=?";
            conn. ps = conn.con.prepareStatement(conn.xSql);
            conn.ps.setString(1, username);
            conn.ps.setString(2, password);
            conn.rs = conn.ps.executeQuery();

            if (conn.rs.next()) 
            {
                String role = conn.rs.getString("role");
                int linkedId = conn. rs.getInt("user_id");
                User user   = new User (conn.rs.getString("avatar"),conn.rs.getString("fullname") , conn.rs.getString("role"));
                
                
                 HttpSession session = request.getSession();
                 session.setAttribute("user", user);
                 session.setAttribute("role", role);
                 session.setAttribute("linked_id", linkedId);
                // Redirect based on role
                if (role.equals("admin")) 
                {
                    response.sendRedirect("adminDashboard");
                } 
                else if (role.equals("staff")) 
                {
                    response.sendRedirect("staff.jsp");
                } 
                else if (role.equals("manager")) 
                {
                    response.sendRedirect("managerDashboard");
                } 
            } else {
                response.getWriter().println("Login failed: Account not found or wrong password.");
            }
        } 
         catch (Exception e) 
        {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
