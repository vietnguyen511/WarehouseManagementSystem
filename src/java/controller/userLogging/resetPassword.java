/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.userLogging;

import dal.TokenForgetDAO;
import dal.UserDAO;
import model.TokenForgetPassword;
import model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

/**
 *
 * @author HP
 */
@WebServlet(name="resetPassword", urlPatterns={"/resetPassword"})
public class resetPassword extends HttpServlet 
{
   TokenForgetDAO DAOToken = new TokenForgetDAO();
   UserDAO DAOUser = new UserDAO();
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet resetPassword</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet resetPassword at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException 
    {
        String token = request.getParameter("token");
        HttpSession session = request.getSession();
        if(token != null) 
        {
            TokenForgetPassword tokenForgetPassword = DAOToken.getTokenPassword(token);
            resetService service = new resetService();
            if(tokenForgetPassword == null) 
            {
                request.setAttribute("mess", "token invalid");
                request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
                return;
            }
            if(tokenForgetPassword.isIsUsed()) 
            {
                request.setAttribute("mess", "token is used");
                request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
                return;
            }
            if(service.isExpireTime(tokenForgetPassword.getExpiryTime())) 
            {
                request.setAttribute("mess", "token is expiry time");
                request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
                return;
            }
            User user = DAOUser.getUserById(tokenForgetPassword.getUserId());
            request.setAttribute("email", user.getEmail());
            session.setAttribute("token", tokenForgetPassword.getToken());
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        } else 
        {
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
        }
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");
        //validate password...
        if(!password.equals(confirmPassword)) {
            request.setAttribute("mess", "confirm password must same password");
            request.setAttribute("email", email);
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }
        HttpSession session = request.getSession();
        String tokenStr = (String) session.getAttribute("token");
        TokenForgetPassword tokenForgetPassword = DAOToken.getTokenPassword(tokenStr);
        //check token is valid, of time, of used
        resetService service = new resetService();
        if (tokenForgetPassword == null) {
            request.setAttribute("mess", "token invalid");
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }
        if (tokenForgetPassword.isIsUsed()) {
            request.setAttribute("mess", "token is used");
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }
        if (service.isExpireTime(tokenForgetPassword.getExpiryTime())) {
            request.setAttribute("mess", "token is expiry time");
            request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
            return;
        }

        //update is used of token
        tokenForgetPassword.setToken(tokenStr);
        tokenForgetPassword.setIsUsed(true);

        DAOUser.updatePassword(email, password);
        DAOToken.updateStatus(tokenForgetPassword);

        //save user in session and redirect to home
        request.setAttribute("mess", "Good bro");
        request.getRequestDispatcher("requestPassword.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
