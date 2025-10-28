
package dal;

import model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDAO extends DBContext 
{
    //check email ton tai
    public User getUserByEmail(String email) {
        String sql = "Select * from [Users] where email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) 
            {                
                return new User(
                        rs.getInt(1),
                        rs.getString(2), 
                        rs.getString(3), 
                        rs.getString(4));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
    public User getUserById(int userId)           //for mail
    {
        String sql = "Select * from Users where user_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) 
            {                
                return new User(
                        rs.getInt(1),
                        rs.getString(2), 
                        rs.getString(3), 
                        rs.getString(4));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
    
    public void updatePassword(String email, String password) {
        String sql = "UPDATE [dbo].[Users]\n"
                + "   SET [password] = ?\n"
                + " WHERE [email] = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, password);
            st.setString(2, email);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
  ////////////////For  user managament
    public void lockUser(int id) throws Exception 
    {
    String sql = "UPDATE Users SET status=0 WHERE user_id=?";
    PreparedStatement ps = connection.prepareStatement(sql);
    ps.setInt(1, id);
    ps.executeUpdate();
    }
    
    public void deleteUser(int id) throws Exception 
    {
    String sql = "DELETE FROM Users WHERE user_id=?";
    PreparedStatement ps = connection.prepareStatement(sql);
    ps.setInt(1, id);
    ps.executeUpdate();
    }
    
    public void updateUser(User user) throws Exception 
    {
    String sql = "UPDATE users SET fullname=?, email=?, phone=?, role=? WHERE user_id=?";
    PreparedStatement ps = connection.prepareStatement(sql);
    ps.setString(1, user.getFullname());
    ps.setString(2, user.getEmail());
    ps.setString(3, user.getPhone());
    ps.setString(4, user.getRole());
    ps.setInt(5, user.getId());
    ps.executeUpdate();
    }
    
    public User getUserDetailById(int userId)
    {  
       try {
          //  String sql ="SELECT user_id,fullname, email, username, password, phone, role, status FROM Users WHERE user_id=?";
            String sql ="SELECT * FROM Users WHERE user_id=?";
            PreparedStatement ps = connection.prepareStatement(sql);  
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) 
            {                
                return new User(
                           rs.getInt("user_id"),
                       rs.getString("fullname"), 
                         rs.getString("phone"), 
                       rs.getString("role"),
                       rs.getString("username"),
                         rs.getString("email"),
                          rs.getString("password"),
                          rs.getString("gender"),
                          rs.getString("address"),
                          rs.getDate("birthday"),
                           rs.getString("avatar"),
                        rs.getBoolean("status"));
            }
        } catch (SQLException e) 
        {
            System.out.println(e);
        }
       return null;
    }
    
}
