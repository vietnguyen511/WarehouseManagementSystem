package controller.userLogging; // change this to your actual package

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogOut extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        
        // Get the current session, if it exists
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Destroy the session — removes all user data
            session.invalidate();
        }

        // Redirect user to the login page
        response.sendRedirect(request.getContextPath() + "/index.html");
    }
}
