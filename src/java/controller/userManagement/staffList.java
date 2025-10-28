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
    
    String sql = "SELECT * FROM Users";
    
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
            u.setBirthday(conn.rs.getDate("birthday"));
            u.setGender(conn.rs.getString("gender"));
            u.setAddress(conn.rs.getString("address"));
            u.setRole(conn.rs.getString("role"));
            u.setStatus(conn.rs.getBoolean("status"));
            users.add(u);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    request.setAttribute("users", users);
    RequestDispatcher dispatcher = request.getRequestDispatcher("staff-list.jsp");
    dispatcher.forward(request, response);
}

}
