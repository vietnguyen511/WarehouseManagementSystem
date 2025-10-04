package controller.warehouseImportMgt;

import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import model.Category;

public class CategoryLookupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            CategoryDAO dao = new CategoryDAO();
            List<Category> categories = dao.getAllCategories();
            
            // Build JSON array manually
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < categories.size(); i++) {
                Category c = categories.get(i);
                json.append("{");
                json.append("\"categoryId\":").append(c.getCategoryId()).append(",");
                json.append("\"name\":\"").append(escape(c.getName())).append("\"");
                json.append("}");
                if (i < categories.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            
            out.print(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            try (PrintWriter out = response.getWriter()) { out.print("[]"); }
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

