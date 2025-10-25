
package controller.userManagement;

import dal.*;
import model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "userPersonalDetail", urlPatterns = {"/account"})
public class userAccountDetail extends HttpServlet 
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    
    {          
            UserDAO dao = new UserDAO();    
            HttpSession session = request.getSession(false);
            int id=Integer.parseInt (session.getAttribute("linked_id").toString());                 
            User user =  dao.getUserDetailById(id);   
           
         request.setAttribute("user", user);
         RequestDispatcher dispatcher = request.getRequestDispatcher("user-account-detail.jsp");
         dispatcher.forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
       
    }
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
