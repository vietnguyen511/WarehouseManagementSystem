package controller.supplier;

import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Servlet to check if supplier name already exists in database
 * Used for real-time validation in supplier form
 */
@WebServlet(name = "CheckSupplierNameServlet", urlPatterns = {"/api/check-supplier-name"})
public class CheckSupplierNameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        String name = request.getParameter("name");
        String excludeIdParam = request.getParameter("excludeId"); // For edit mode
        
        boolean exists = false;
        String message = "";
        
        if (name != null && !name.trim().isEmpty()) {
            try {
                SupplierDAO dao = new SupplierDAO();
                model.Supplier existing = dao.findByName(name.trim());
                
                if (existing != null) {
                    // If in edit mode, exclude current supplier ID
                    if (excludeIdParam != null && !excludeIdParam.trim().isEmpty()) {
                        try {
                            int excludeId = Integer.parseInt(excludeIdParam);
                            if (existing.getSupplierId() == excludeId) {
                                // It's the same supplier, so name is valid
                                exists = false;
                                message = "";
                            } else {
                                // Different supplier with same name
                                exists = true;
                                message = "Supplier with this name already exists";
                            }
                        } catch (NumberFormatException e) {
                            exists = true;
                            message = "Supplier with this name already exists";
                        }
                    } else {
                        // Create mode - name exists
                        exists = true;
                        message = "Supplier with this name already exists";
                    }
                } else {
                    exists = false;
                    message = "";
                }
            } catch (SQLException e) {
                exists = false;
                message = "Error checking supplier name";
            }
        }
        
        // Build JSON response manually
        String json = String.format("{\"exists\":%s,\"message\":\"%s\"}", 
            exists ? "true" : "false", escapeJson(message));
        
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

