package Personal_management;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;
import model.*;
import dal.*;

public class ForgotPassword extends HttpServlet 
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) 
        {
            response.sendRedirect("forgot.html?error=1");
            return;
        }

        // Optionally check the DB to ensure email exists
        boolean emailExists = false;

        try 
        {
           MyDAO conn = new MyDAO();
           conn.ps = conn.con.prepareStatement("SELECT user_id FROM Users WHERE email = ?");
           conn.ps.setString(1, email);
          
           conn.rs = conn.ps.executeQuery();
          
           emailExists = conn.rs.next();             
        } 
        catch (Exception ex) 
        {
             ex.printStackTrace();
            // If DB fails, still proceed or change behavior
        }

        if (!emailExists) 
        {
             //////come back soon
        }

        /// Still in testing, better method come back later
        // Generate a 4-digit code
        String code = "1234"; //String.format("%04d", new Random().nextInt(10000));

        // store code and email into session for the later steps
        HttpSession session = request.getSession(true);
        session.setAttribute("resetEmail", email);
        session.setAttribute("resetCode", code);

        response.sendRedirect("enter_code.jsp");
    }
}