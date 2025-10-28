package controller.userManagement;

  import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import dal.*;
import jakarta.servlet.annotation.WebServlet;


@WebServlet(name ="Register_2",urlPatterns = {"/registerStep2"})
public class Register_2 extends HttpServlet 
{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {

        HttpSession session = request.getSession();    

        // Get step1 data
        String firstname = (String) session.getAttribute("firstname");
        String lastname =  (String) session.getAttribute("lastname");
   //     String birthday =  (String) session.getAttribute("birthday");
   //     String gender =    (String) session.getAttribute("gender");
        String email =     (String) session.getAttribute("email");
        String phone =     (String) session.getAttribute("phone");
        String role =      (String) session.getAttribute("role");
 
        // Get account/password data
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        //Check for situation
        if (!password.equals(confirmPassword)) 
        {
            // Send error back to JSP
            request.setAttribute("error", "Passwords do not match!");
            request.getRequestDispatcher("register_2.jsp").forward(request, response);
            return;
        }
        
        //Do the job
        try 
        {
            MyDAO conn = new MyDAO();
       //     conn.xSql = "INSERT INTO Users(fullname, email, password, phone, role, status, created_at, updated_at) VALUES (?,?,?,?,?,?,NOW(),NOW())";
            conn.xSql = "INSERT INTO Users(fullname, email,username, password, phone, role, status) VALUES (?,?,?,?,?,?,1)";
            conn.ps = conn.con.prepareStatement(conn.xSql);
            conn.ps.setString(1, firstname + " " + lastname);
            conn.ps.setString(2, email);
            conn.ps.setString(3, username);
            conn.ps.setString(4, password);
            conn.ps.setString(5, phone);
            conn.ps.setString(6, role);

            conn.ps.executeUpdate();
            session.invalidate(); // clear session
            response.sendRedirect("index.html");
        } 
        catch (Exception e) 
        {
            throw new ServletException(e);
        }
    }
}
