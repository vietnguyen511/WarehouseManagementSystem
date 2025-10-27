
package model;

import java.sql.Date;

public class User 
{
    private int id;
    private String fullname,phone, role;
    private String userName, email, password;
    private String gender, address;
    private java.sql.Date birthday;
    private String avatar;
    private boolean status;

    public User()  {}
    public User(String avatar, String fullname, String role) 
    {
       this.avatar = avatar;
       this.fullname = fullname;
       this.role = role;
    }
 
    public User(int id, String userName, String email, String password) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
    
    public User(int id,String fullname, String email , String userName, String password, String phone,String role, Boolean status) 
    {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.phone    = phone;
        this.role     = role;
        this.status   = status;
    }

    public User(int id, String fullname, String phone, String role, String userName, String email, String password, String gender, String address, Date birthday, String avatar, boolean status) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.role = role;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.address = address;
        this.birthday = birthday;
        this.avatar = avatar;
        this.status = status;
    }
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    
    
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", userName=" + userName + ", email=" + email + ", password=" + password + '}';
    }
    
}
