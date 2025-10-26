package model;

public class Supplier {
    private int supplierId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private boolean status;

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}


