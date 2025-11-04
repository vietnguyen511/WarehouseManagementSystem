/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.warehouseManagement;

import dal.ProductDAO;
import dal.CategoryDAO;
import model.Product;
import model.Category;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.MultipartConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author DANG
 */
@MultipartConfig
public class AddProductServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/warehouse-management/add-product.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // ====== LẤY FORM FIELD TEXT ======
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String categoryIdStr = request.getParameter("category_id");
        String material = request.getParameter("material");
        String unit = request.getParameter("unit");
        String importPriceStr = request.getParameter("import_price");
        String exportPriceStr = request.getParameter("export_price");
        String description = request.getParameter("description");
        String manualImagePath = request.getParameter("image"); // optional fallback
        String statusStr = request.getParameter("status");

        // ====== LẤY FILE ẢNH UPLOAD (neu co) ======
        jakarta.servlet.http.Part imagePart = request.getPart("imageFile");
        String uploadedPath = null;
        if (imagePart != null && imagePart.getSize() > 0) {
            String submittedFileName = imagePart.getSubmittedFileName();

            // nơi lưu file trên server
            String uploadFolder = getServletContext().getRealPath("/uploads/products");
            java.io.File dir = new java.io.File(uploadFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            java.io.File savedFile = new java.io.File(dir, submittedFileName);
            imagePart.write(savedFile.getAbsolutePath());

            // path tương đối để lưu vào DB
            uploadedPath = "uploads/products/" + submittedFileName;
        }

        // ảnh cuối cùng dùng để insert DB
        String image = (uploadedPath != null && !uploadedPath.isEmpty())
                ? uploadedPath
                : manualImagePath;

        // ====== VALIDATE TỐI THIỂU ======
        if (code == null || code.trim().isEmpty()
                || name == null || name.trim().isEmpty()
                || categoryIdStr == null || categoryIdStr.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Please fill all required fields (Code, Name, Category).");

            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

            // giữ lại input
            request.setAttribute("old_code", code);
            request.setAttribute("old_name", name);
            request.setAttribute("old_category_id", categoryIdStr);
            request.setAttribute("old_material", material);
            request.setAttribute("old_unit", unit);
            request.setAttribute("old_import_price", importPriceStr);
            request.setAttribute("old_export_price", exportPriceStr);
            request.setAttribute("old_description", description);
            request.setAttribute("old_image", image);
            request.setAttribute("old_status", statusStr);
            request.setAttribute("old_image_preview_url", image);

            request.getRequestDispatcher("/warehouse-management/add-product.jsp").forward(request, response);
            return;
        }

        try {
            int categoryId = Integer.parseInt(categoryIdStr.trim());

            BigDecimal importPrice = null;
            if (importPriceStr != null && !importPriceStr.trim().isEmpty()) {
                importPrice = new BigDecimal(importPriceStr.trim());
            }

            BigDecimal exportPrice = null;
            if (exportPriceStr != null && !exportPriceStr.trim().isEmpty()) {
                exportPrice = new BigDecimal(exportPriceStr.trim());
            }

            boolean status = "1".equals(statusStr);

            // Map vào model Product (model Product không có description/image)
            Product p = new Product();
            p.setCode(code.trim());
            p.setName(name.trim());
            p.setCategoryId(categoryId);
            p.setMaterial(material != null ? material.trim() : null);
            p.setUnit(unit != null ? unit.trim() : null);
            p.setQuantity(0); // new product starts with 0 stock
            p.setImportPrice(importPrice);
            p.setExportPrice(exportPrice);
            p.setStatus(status);

            boolean ok = productDAO.insertProduct(p);

            if (ok) {
                response.sendRedirect(
                        request.getContextPath() + "/warehouse-management/product-management?msg=created"
                );
                return;
            } else {
                request.setAttribute("errorMessage", "Failed to create product. Product code may already exist.");

                List<Category> categories = categoryDAO.getAllCategories();
                request.setAttribute("categories", categories);

                request.setAttribute("old_code", code);
                request.setAttribute("old_name", name);
                request.setAttribute("old_category_id", categoryIdStr);
                request.setAttribute("old_material", material);
                request.setAttribute("old_unit", unit);
                request.setAttribute("old_import_price", importPriceStr);
                request.setAttribute("old_export_price", exportPriceStr);
                request.setAttribute("old_description", description);
                request.setAttribute("old_image", image);
                request.setAttribute("old_status", statusStr);
                request.setAttribute("old_image_preview_url", image);

                request.getRequestDispatcher("/warehouse-management/add-product.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("errorMessage", "An error occurred when retrieving data: " + e.getMessage());

            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

            request.setAttribute("old_code", code);
            request.setAttribute("old_name", name);
            request.setAttribute("old_category_id", categoryIdStr);
            request.setAttribute("old_material", material);
            request.setAttribute("old_unit", unit);
            request.setAttribute("old_import_price", importPriceStr);
            request.setAttribute("old_export_price", exportPriceStr);
            request.setAttribute("old_description", description);
            request.setAttribute("old_image", image);
            request.setAttribute("old_status", statusStr);
            request.setAttribute("old_image_preview_url", image);

            request.getRequestDispatcher("/warehouse-management/add-product.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
