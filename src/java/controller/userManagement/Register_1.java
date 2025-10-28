package controller.userManagement;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import dal.*;
import jakarta.servlet.annotation.WebServlet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet(name ="Register_1",urlPatterns = {"/registerStep1"})
public class Register_1 extends HttpServlet 
{
    @Override
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
                    request.getRequestDispatcher("register_1.jsp").forward(request, response);
                    return;
                }
                  
            HttpSession session = request.getSession();
            session.setAttribute("firstname", request.getParameter("firstname"));
            session.setAttribute("lastname", request.getParameter("lastname"));
       //     session.setAttribute("birthday", request.getParameter("birthday"));
       //     session.setAttribute("gender", request.getParameter("gender"));
            session.setAttribute("email", request.getParameter("email"));
            session.setAttribute("phone", request.getParameter("phone"));
            session.setAttribute("role", request.getParameter("role"));
            
            response.sendRedirect("register_2.jsp");
        } catch (SQLException ex) 
        {
         //   Logger.getLogger(Register_1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
