package controller.userManagement;  // change this to your actual package

import dal.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

@WebServlet("/edit-user")
public class editUser extends HttpServlet
{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
      try 
        {
        // 1️⃣ Get parameters from form
        int id = Integer.parseInt(request.getParameter("id"));
        String fullname = request.getParameter("fullname");
        String gender = request.getParameter("gender");
        String birthdayStr = request.getParameter("birthday");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String role = request.getParameter("role");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        
        UserDAO dao = new UserDAO();
        request.setAttribute("user", dao.getUserDetailById(id));
        // 2️⃣ Convert birthday string → SQL Date
        Date birthday = null;
        if (birthdayStr != null && !birthdayStr.isEmpty()) {
            birthday = Date.valueOf(birthdayStr); // yyyy-mm-dd
        }
        
                    // 3️⃣ Validate some required fields (optional but safe)
        if (fullname == null || fullname.trim().isEmpty()) 
            {
                request.setAttribute("error", "Full name cannot be empty!");
                request.setAttribute("editMode", true);              
                request.getRequestDispatcher("user-detail.jsp").forward(request, response);
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
                    request.getRequestDispatcher("user-detail.jsp").forward(request, response);
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
                    request.getRequestDispatcher("user-detail.jsp").forward(request, response);
                    return;
                }         
        // 3️⃣ SQL Update query
        String sql = "UPDATE Users SET fullname = ?, gender = ?, birthday = ?, email = ?, "
                   + "phone = ?, address = ?, role = ?, username = ?, password = ? "
                   + " WHERE user_id = ?";

        // 4️⃣ Execute SQL safely 
           conn.ps= conn.con.prepareStatement(sql);    
           conn.ps.setString(1, fullname);
           conn.ps.setString(2, gender);
           conn.ps.setDate(3, birthday);
           conn.ps.setString(4, email);
           conn.ps.setString(5, phone);
           conn.ps.setString(6, address);
           conn.ps.setString(7, role);
           conn.ps.setString(8, username);
           conn.ps.setString(9, password);
           conn.ps.setInt(10, id);
          
           conn.ps.executeUpdate();
           response.sendRedirect("staff-list");   
      
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
}
