package Personal_management;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import dal.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name ="LoginEmail",urlPatterns = {"/loginemail"})
public class LoginEmail extends HttpServlet 
{
protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        String code = request.getParameter("code");
        GoogleLogin gg = new GoogleLogin();
        String accessToken = gg.getToken(code);
        //System.out.println (accessToken);
        
        GoogleAccount acc = gg.getUserInfo(accessToken);
        //System.out.println(acc);
        out.println( acc.getEmail());
        out.println(acc.getName());
    }
}
