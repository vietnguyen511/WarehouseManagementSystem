package controller.userManagement;

import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/delete-user")
public class deleteUser extends HttpServlet 
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try{
            int id = Integer.parseInt(request.getParameter("id"));
            UserDAO dao = new UserDAO();
            dao.deleteUser(id);
            response.sendRedirect("staff-list?success=delete");
        } catch (Exception e)
        {
            e.printStackTrace();
            response.sendRedirect("staff-list?error=delete");
        }
    }
}
