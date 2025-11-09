package controller.warehouseManagement;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import dal.ActivityLogHelper;
import dal.ProductDAO;
import dal.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import model.Product;
import model.Category;

/**
 *
 * @author DANG
 */
@MultipartConfig
public class EditProductServlet extends HttpServlet {

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
        String idRaw = request.getParameter("id");
        if (idRaw == null) {
            response.sendRedirect(request.getContextPath() + "/warehouse-management/product-management?msg=invalid");
            return;
        }

        try {
            int id = Integer.parseInt(idRaw.trim());
            Product product = productDAO.getProductById(id);
            List<Category> categories = categoryDAO.getAllCategories();

            if (product == null) {
                response.sendRedirect(request.getContextPath() + "/warehouse-management/product-management?msg=notfound");
                return;
            }

            request.setAttribute("product", product);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/warehouse-management/edit-product.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/warehouse-management/product-management?msg=error");
        }
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

        String idRaw = request.getParameter("id");
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String material = request.getParameter("material");
        String unit = request.getParameter("unit");
        String description = request.getParameter("description");
        String categoryIdStr = request.getParameter("category_id");
        String statusStr = request.getParameter("status");
        String image = request.getParameter("image");

        Part imagePart = request.getPart("imageFile");
        if (imagePart != null && imagePart.getSize() > 0) {
            String fileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
            String uploadDir = getServletContext().getRealPath("/") + "uploaded-images";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File uploadedFile = new File(dir, fileName);
            imagePart.write(uploadedFile.getAbsolutePath());

            image = "uploaded-images/" + fileName;
        }

        String redirectUrl = request.getContextPath() + "/warehouse-management/product-management";

        try {
            int id = Integer.parseInt(idRaw.trim());
            int categoryId = Integer.parseInt(categoryIdStr.trim());
            boolean status = "1".equals(statusStr);

            Product p = new Product();
            p.setProductId(id);
            p.setCode(code);
            p.setName(name);
            p.setCategoryId(categoryId);
            p.setMaterial(material);
            p.setUnit(unit);
            p.setStatus(status);
            p.setDescription(description);
            p.setImage(image);

            boolean success = productDAO.updateProduct(p);

            if (success) {
                ActivityLogHelper.logUpdate(request.getSession(), "Products", id,
                        "Updated product: " + name + " (" + code + ")");
                response.sendRedirect(redirectUrl + "?msg=editSuccess");
            } else {
                response.sendRedirect(redirectUrl + "?msg=editFail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath()
                    + "/warehouse-management/product-management?msg=editError"
            );
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
