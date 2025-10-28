package controller.userManagement;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import dal.*;
import jakarta.servlet.annotation.WebServlet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet(name ="Register",urlPatterns = {"/register"})
public class register extends HttpServlet 
{
    
    @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        // Option 1: Forward request to JSP (keeps URL as /dashboard)
      //  request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);

        // Option 2 (alternative): Redirect to JSP (changes URL to dashboard.jsp)
         response.sendRedirect(request.getContextPath() + "/register.jsp");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {       
        try 
        {
         //Check for email exist in DB
            MyDAO conn = new MyDAO();
            conn.ps = conn.con.prepareStatement("SELECT * FROM Users WHERE email = ?");
            String email =request.getParameter("email");
            conn.ps.setString(1, email);
            conn.rs = conn.ps.executeQuery();         
                //When Email already exists in DB
                if (conn.rs.next())
                {
                    request.setAttribute("error", "This email is already registered.");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                    return;
                }
         //Check for Sync password 
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

        //Check for situation
              if (!password.equals(confirmPassword)) 
               {
                  // Send error back to JSP
                 request.setAttribute("error1", "Passwords do not match!");
                 request.getRequestDispatcher("register.jsp").forward(request, response);
                 return;
               }            
              
            conn.xSql = "INSERT INTO Users(fullname, email ,username, password, phone, birthday, gender, address, role, status) VALUES (?,?,?,?,?,?,?,?,?,1)";
            conn.ps = conn.con.prepareStatement(conn.xSql);
            conn.ps.setString(1, request.getParameter("firstname") + " " + request.getParameter("lastname"));
            conn.ps.setString(2, email);
            conn.ps.setString(3, username);
            conn.ps.setString(4, password);
            conn.ps.setString(5, request.getParameter("phone"));
            conn.ps.setDate(6, java.sql.Date.valueOf(request.getParameter("birthday")));
            conn.ps.setString(7, request.getParameter("gender"));
            conn.ps.setString(8, request.getParameter("address"));
            conn.ps.setString(9, request.getParameter("role"));

            conn.ps.executeUpdate();
            response.sendRedirect("staff-list");
        } 
        catch (Exception e) 
        {
            throw new ServletException(e);
        }      
    }
}
