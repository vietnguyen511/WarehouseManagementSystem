package controller.userManagement;

import dal.*;
import model.User;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/edit-account-user")
public class editAccountUser extends HttpServlet 
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
       try{
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String email = request.getParameter("email");  
            int id   = Integer.parseInt(request.getParameter("id"));
            
             UserDAO dao = new UserDAO();   
             User user = dao.getUserDetailById(id);
             request.setAttribute("user", user);
            
          //Check for wrong password insert
            if (!password.equals(confirmPassword)) 
             {
                request.setAttribute("error", "Passwords do not match!");
                request.setAttribute("editMode", true);            
                request.getRequestDispatcher("user-account-detail.jsp").forward(request, response);
                return;
             } 
            
            MyDAO conn = new MyDAO();
           //Check for existed email before 
            conn.ps = conn.con.prepareStatement("SELECT * FROM Users WHERE email = ? AND user_id <> ?");
            conn.ps.setString(1, email);
            conn.ps.setInt(2, id);
            conn.rs = conn.ps.executeQuery(); 
             if (conn.rs.next())
                {
                    request.setAttribute("error1", "This email is already existed.");
                    request.setAttribute("editMode", true);  
                    request.getRequestDispatcher("user-account-detail.jsp").forward(request, response);
                    return;
                }
            conn.ps = conn.con.prepareStatement("SELECT * FROM Users WHERE username = ? AND user_id <> ?");
            conn.ps.setString(1, username);
            conn.ps.setInt(2, id);
            conn.rs = conn.ps.executeQuery(); 
             if (conn.rs.next())
                {
                    request.setAttribute("error2", "This username is already existed.");
                    request.setAttribute("editMode", true);  
                    request.getRequestDispatcher("user-account-detail.jsp").forward(request, response);
                    return;
                }          
             
           //Updating
            conn.xSql = "UPDATE Users SET username = ?, password = ?, email = ? WHERE user_id = ?";        
            conn.ps = conn.con.prepareStatement(conn.xSql);       
            conn.ps.setString(1, username);
            conn.ps.setString(2, password);
            conn.ps.setString(3, email);
            conn.ps.setInt(4, id);
            conn.ps.executeUpdate();
            response.sendRedirect("account");
        } catch (Exception e) 
        {
            e.printStackTrace();
            response.sendRedirect("account");
        }
    }
}
