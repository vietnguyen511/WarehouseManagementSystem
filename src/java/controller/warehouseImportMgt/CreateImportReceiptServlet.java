/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.warehouseImportMgt;

import java.io.IOException;
import dal.ActivityLogHelper;
import dal.ImportReceiptDAO;
import dal.SupplierDAO;
import java.io.PrintWriter;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.ImportDetail;
import model.ImportReceipt;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author admin
 */
public class CreateImportReceiptServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CreateImportReceiptServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreateImportReceiptServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Load suppliers for dropdown
        SupplierDAO supplierDAO = new SupplierDAO();
        java.util.List<model.Supplier> suppliers = supplierDAO.getAllSuppliers();
        request.setAttribute("suppliers", suppliers);
        
        // Load categories for dropdown
        dal.CategoryDAO categoryDAO = new dal.CategoryDAO();
        java.util.List<model.Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);
        
        // Provide today's date for default value
        String today = java.time.LocalDate.now().toString();
        request.setAttribute("today", today);
        // Pull success message from session if present
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object msg = session.getAttribute("successMessage");
            if (msg != null) {
                request.setAttribute("successMessage", msg.toString());
                session.removeAttribute("successMessage");
            }
        }

        // Set active page for sidebar navigation
        request.setAttribute("activePage", "add-import-receipt");

        request.getRequestDispatcher("/warehouse-import-mgt/add-import-receipt.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Quick debug: log start
        System.out.println("=== CREATE IMPORT RECEIPT POST START ===");

        try {
            // Basic receipt info
            String supplierIdStr = request.getParameter("supplierId");
            String importDateStr = request.getParameter("importDate");
            String totalAmountStr = request.getParameter("totalAmount");
            String note = request.getParameter("note");

            int supplierId = Integer.parseInt(supplierIdStr);
            // Use current timestamp when creating receipt to include time information
            Date importDate = (importDateStr == null || importDateStr.isEmpty()) ? new Date() : toSqlTimestampFlexible(importDateStr);
            BigDecimal totalAmount = (totalAmountStr == null || totalAmountStr.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(totalAmountStr);

            // Build receipt model
            ImportReceipt receipt = new ImportReceipt();
            receipt.setSupplierId(supplierId);
            // Temporary: no login yet – hardcode staff user id = 3 (Mike Johnson)
            receipt.setUserId(3);
            receipt.setDate(importDate);
            receipt.setTotalAmount(totalAmount);
            receipt.setNote(note);
            
            System.out.println("Receipt created: " + receipt.getSupplierId() + " " + receipt.getDate() + " " + receipt.getTotalAmount());

            // Parse item rows by scanning sequential indices until none found
            List<ImportDetail> details = new ArrayList<>();
            int index = 0;
            while (true) {
                String code = request.getParameter("items[" + index + "].productCode");
                String name = request.getParameter("items[" + index + "].productName");
                String categoryIdStr = request.getParameter("items[" + index + "].categoryId");
                String material = request.getParameter("items[" + index + "].material");
                String unit = request.getParameter("items[" + index + "].unit");
                String size = request.getParameter("items[" + index + "].size");
                String color = request.getParameter("items[" + index + "].color");
                String qtyStr = request.getParameter("items[" + index + "].quantity");
                String priceStr = request.getParameter("items[" + index + "].price");
                if (code == null && name == null && qtyStr == null && priceStr == null) {
                    break;
                }
                if (code == null || qtyStr == null || priceStr == null || code.trim().isEmpty()) {
                    index++;
                    continue;
                }
                ImportDetail d = new ImportDetail();
                d.setProductCode(code.trim());
                d.setProductName(name != null ? name.trim() : null);
                d.setMaterial(material != null ? material.trim() : "");
                d.setUnit(unit != null ? unit.trim() : "");
                d.setSize(size != null ? size.trim() : "");
                d.setColor(color != null ? color.trim() : "");
                
                // Parse category ID if provided
                if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                    try {
                        d.setCategoryId(Integer.parseInt(categoryIdStr.trim()));
                    } catch (NumberFormatException e) {
                        // Ignore invalid category ID
                    }
                }
                
                d.setQuantity(Integer.parseInt(qtyStr));
                BigDecimal price = new BigDecimal(priceStr);
                d.setPrice(price);
                d.setAmount(price.multiply(BigDecimal.valueOf(d.getQuantity())));
                details.add(d);
                index++;
            }

            int totalQty = details.stream().mapToInt(ImportDetail::getQuantity).sum();
            receipt.setTotalQuantity(totalQty);
            receipt.setDetails(details);

            // Persist within transaction and update stock
            System.out.println("Detailed items: " + details.size());
            ImportReceiptDAO dao = new ImportReceiptDAO();
            System.out.println("About to create receipt with details...");
            
            // Create the receipt with details in a single transactional method
            int importId = dao.createReceiptWithDetails(receipt);
            receipt.setImportId(importId);
            
            System.out.println("Receipt created successfully with ID: " + importId);

            // Redirect back to form with success message
            HttpSession session = request.getSession(true);
            session.setAttribute("successMessage", "Import receipt created successfully.");
            
            // Log activity
            SupplierDAO supplierDAO = new SupplierDAO();
            String supplierName = "supplier_" + supplierId;
            try {
                model.Supplier supplier = supplierDAO.findById(supplierId);
                if (supplier != null) {
                    supplierName = supplier.getName();
                }
            } catch (Exception e) {
                // Use default
            }
            
            ActivityLogHelper.logCreate(request.getSession(), "ImportReceipts", importId,
                "Created import receipt for supplier: " + supplierName + ", Total: " + totalAmount + ", Items: " + details.size());
            
            System.out.println("Redirecting to success page...");
            response.sendRedirect(request.getContextPath() + "/createImportReceipt");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to save import receipt: " + e.getMessage());
            request.getRequestDispatcher("/warehouse-import-mgt/add-import-receipt.jsp").forward(request, response);
        }
    }

    private java.sql.Date toSqlDateFlexible(String dateStr) {
        // Accept both yyyy-MM-dd (native date input) and MM/dd/yyyy (some browsers/regional settings)
        String[] patterns = {"yyyy-MM-dd", "MM/dd/yyyy"};
        for (String p : patterns) {
            try {
                LocalDate ld = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(p));
                return java.sql.Date.valueOf(ld);
            } catch (DateTimeParseException ignored) {}
        }
        // Fallback to today if parsing fails
        return new java.sql.Date(System.currentTimeMillis());
    }

    private Date toSqlTimestampFlexible(String dateStr) {
        // Accept both yyyy-MM-dd (native date input) and MM/dd/yyyy (some browsers/regional settings)
        String[] patterns = {"yyyy-MM-dd", "MM/dd/yyyy"};
        for (String p : patterns) {
            try {
                LocalDate ld = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(p));
                // Get current time and apply it to the selected date
                LocalTime currentTime = LocalTime.now();
                LocalDateTime ldt = LocalDateTime.of(ld, currentTime);
                // Convert to Timestamp
                return java.sql.Timestamp.valueOf(ldt);
            } catch (DateTimeParseException ignored) {}
        }
        // Fallback to current timestamp if parsing fails
        return new Date();
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
