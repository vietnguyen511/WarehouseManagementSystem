package controller.userManagement;

import dal.MyDAO;
import  model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;


@WebServlet("/staff-list")
public class staffList extends HttpServlet 
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException 
    {
    
    String sort = request.getParameter("sort");
    
    String sql = "SELECT user_id,fullname, email, phone, role, status FROM Users";
    
    if (sort != null) 
    {
        switch (sort) 
        {
            case "fullname": sql += " ORDER BY fullname"; break;
            case "email":    sql += " ORDER BY email"; break;
            case "role":     sql += " ORDER BY role"; break;
            case "status":   sql += " ORDER BY status"; break;
        }
    }
    
    List<User> users = new ArrayList<>();
    try 
    {
        MyDAO conn = new MyDAO();
        conn.ps = conn.con.prepareStatement(sql);
        conn.rs = conn.ps.executeQuery();
        while (conn.rs.next()) 
        {
            User u = new User();
            u.setId(conn.rs.getInt("user_id"));
            u.setFullname(conn.rs.getString("fullname"));
            u.setEmail(conn.rs.getString("email"));
            u.setPhone(conn.rs.getString("phone"));
            u.setRole(conn.rs.getString("role"));
            u.setStatus(conn.rs.getBoolean("status"));
            users.add(u);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    request.setAttribute("users", users);
     request.setAttribute("testing", sql);
    RequestDispatcher dispatcher = request.getRequestDispatcher("staff-list.jsp");
    dispatcher.forward(request, response);
}

}
