package controller.userLogging;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import dal.*;
import jakarta.servlet.annotation.WebServlet;
import model.User;

@WebServlet(name ="LoginEmail",urlPatterns = {"/loginemail"})
public class LoginEmail extends HttpServlet 
{
protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
       // processRequest(request, response);
          PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        String code = request.getParameter("code");
        GoogleLogin gg = new GoogleLogin();
        String accessToken = gg.getToken(code);
        //System.out.println (accessToken);
        
        GoogleAccount acc = gg.getUserInfo(accessToken);
        //System.out.println(acc);
        try 
        {
            MyDAO conn = new MyDAO();
            conn.xSql = "SELECT * FROM Users WHERE email=?";
            conn. ps = conn.con.prepareStatement(conn.xSql);
            conn.ps.setString(1,acc.getEmail());
            conn.rs = conn.ps.executeQuery();

            if (conn.rs.next()) 
            {
                String role = conn.rs.getString("role");
                int linkedId = conn. rs.getInt("user_id");
                User user   = new User (conn.rs.getString("avatar"),conn.rs.getString("fullname") , conn.rs.getString("role"));
                
                
                 HttpSession session = request.getSession();
                 session.setAttribute("user", user);
                 session.setAttribute("role", role);
                 session.setAttribute("linked_id", linkedId);
                // Redirect based on role
                if (role.equals("admin")) 
                {
                    response.sendRedirect("admin.jsp");
                } 
                else if (role.equals("staff")) 
                {
                    response.sendRedirect("staff.jsp");
                } 
                else if (role.equals("manager")) 
                {
                    response.sendRedirect("manager.jsp");
                } 
            } else {
                response.getWriter().println("Login failed: Account not found or wrong password.");
            }
        } 
         catch (Exception e) 
        {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
/*
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
        out.println("<p>well that's all bro");      
    }*/ 
}
