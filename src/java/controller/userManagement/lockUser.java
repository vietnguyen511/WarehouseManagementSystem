package controller.userManagement;

import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/lock-user")
public class lockUser extends HttpServlet 
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            UserDAO dao = new UserDAO();
            dao.lockUser(id);
            response.sendRedirect("staff-list?success=lock");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("staff-list?error=lock");
        }
    }
}
