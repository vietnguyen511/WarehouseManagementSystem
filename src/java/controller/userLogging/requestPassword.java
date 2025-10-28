
package controller.userLogging;

import dal.TokenForgetDAO;
import dal.UserDAO;
import model.User;
import model.TokenForgetPassword;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;


@WebServlet(name="requestPassword", urlPatterns={"/requestPassword"})
public class requestPassword extends HttpServlet 
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
    } 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        PrintWriter out = response.getWriter(); ///testing
        UserDAO daoUser = new UserDAO();
        String email = request.getParameter("email");
        //email co ton tai trong db
        User user = daoUser.getUserByEmail(email);
        if(user == null) 
        {
            request.setAttribute("mess", "email not existed");
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }
        resetService service = new resetService();
        String token = service.generateToken();
        
        String linkReset = "http://localhost:8080/WarehouseManagementSystem/resetPassword?token="+token;
        
        TokenForgetPassword newTokenForget = new TokenForgetPassword(
                user.getId(), false, token, service.expireDateTime());
        
        //send link to this email
        TokenForgetDAO daoToken = new TokenForgetDAO();
        boolean isInsert = daoToken.insertTokenForget(newTokenForget);
        if(!isInsert) 
        {
            request.setAttribute("mess", "have error in serve|");
            
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }
        boolean isSend = service.sendEmail(email, linkReset, user.getUserName());
        if(!isSend) 
        {
            request.setAttribute("mess", "can not send request");
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }
        request.setAttribute("mess", "send request success");
        request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
