package controller.userManagement;  // change this to your actual package

import dal.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

@WebServlet(name = "editPersonalUser", urlPatterns = {"/edit-personal-user"})
public class editPersonalUser extends HttpServlet 
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException  
    {          
            try {
            
                
            int id = Integer.parseInt(request.getParameter("id"));
            String fullname = request.getParameter("fullname");
            String gender = request.getParameter("gender");
            String birthdayStr = request.getParameter("birthday");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");

            // 2️⃣ Convert birthday to SQL Date
            Date birthday = null;
            if (birthdayStr != null && !birthdayStr.isEmpty()) {
                birthday = Date.valueOf(birthdayStr); // yyyy-mm-dd
            }

            // 3️⃣ Validate some required fields (optional but safe)
            if (fullname == null || fullname.trim().isEmpty()) 
            {
                request.setAttribute("error", "Full name cannot be empty!");
                request.setAttribute("editMode", true);
                UserDAO dao = new UserDAO();
                request.setAttribute("user", dao.getUserDetailById(id));
                request.getRequestDispatcher("user-personal-detail.jsp").forward(request, response);
                return;
            }

            // 4️⃣ Update SQL
            MyDAO conn = new MyDAO();
            String sql = "UPDATE Users SET fullname = ?, gender = ?, birthday = ?, phone = ?, address = ? WHERE user_id = ?";
            conn.ps = conn.con.prepareStatement(sql);
            conn.ps.setString(1, fullname);
            conn.ps.setString(2, gender);
            conn.ps.setDate(3, birthday);
            conn.ps.setString(4, phone);
            conn.ps.setString(5, address);
            conn.ps.setInt(6, id);

            conn.ps.executeUpdate();
           //Session update related
            HttpSession session = request.getSession(false);
            User user =(User) session.getAttribute("user");
            user.setFullname(fullname);
            session.setAttribute("user", user);
            // 5️⃣ Redirect or reload
            response.sendRedirect("profile"); 

        } catch (Exception e) 
        {
            e.printStackTrace();
            System.out.println("❌ Error in edit-personal-user: " + e.getMessage());
            response.sendRedirect("account");
        }
       
    }
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
