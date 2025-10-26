package controller.statisticsAndReporting;

import dal.InventoryDAO;
import model.ProductVariantInventoryItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.Gson;

/**
 * ProductVariantsServlet - API endpoint to get product variants
 * Returns JSON data for AJAX requests from current inventory page
 */
public class ProductVariantsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String productIdParam = request.getParameter("productId");
            
            if (productIdParam == null || productIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"Product ID is required\"}");
                return;
            }
            
            int productId = Integer.parseInt(productIdParam);
            
            InventoryDAO dao = new InventoryDAO();
            List<ProductVariantInventoryItem> variants = dao.getProductVariants(productId);
            
            // Convert to JSON
            Gson gson = new Gson();
            String json = gson.toJson(variants);
            
            PrintWriter out = response.getWriter();
            out.print("{\"variants\":" + json + "}");
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Invalid product ID\"}");
        } catch (Exception e) {
            System.out.println("Error in ProductVariantsServlet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

