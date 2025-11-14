package controller.imports;

import dal.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import model.Product;

public class ProductLookupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String code = request.getParameter("code");
        try (PrintWriter out = response.getWriter()) {
            if (code == null || code.trim().isEmpty()) {
                out.print("{}");
                return;
            }
            ProductDAO dao = new ProductDAO();
            Product p = dao.getProductByCode(code.trim());
            if (p == null) {
                out.print("{}");
            } else {
                // Minimal JSON output without external libs
                StringBuilder json = new StringBuilder("{");
                json.append("\"productId\":").append(p.getProductId()).append(",");
                json.append("\"code\":\"").append(escape(p.getCode())).append("\",");
                json.append("\"name\":\"").append(escape(p.getName())).append("\"");
                
                // Add category info if available
                if (p.getCategoryId() != null) {
                    json.append(",\"categoryId\":").append(p.getCategoryId());
                }
                if (p.getCategoryName() != null) {
                    json.append(",\"categoryName\":\"").append(escape(p.getCategoryName())).append("\"");
                }
                
                // Add material if available
                if (p.getMaterial() != null && !p.getMaterial().isEmpty()) {
                    json.append(",\"material\":\"").append(escape(p.getMaterial())).append("\"");
                }
                
                // Add unit if available
                if (p.getUnit() != null && !p.getUnit().isEmpty()) {
                    json.append(",\"unit\":\"").append(escape(p.getUnit())).append("\"");
                }
                
                json.append("}");
                out.print(json.toString());
            }
        } catch (Exception e) {
            try (PrintWriter out = response.getWriter()) { out.print("{}"); }
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}


