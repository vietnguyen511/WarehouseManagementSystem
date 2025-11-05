/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.warehouseExportMgt;

import java.io.IOException;
import dal.ActivityLogHelper;
import dal.ExportReceiptDAO;
import dal.CustomerDAO;
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
import model.ExportDetail;
import model.ExportReceipt;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author admin
 */
public class CreateExportReceiptServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.Loads customers and categories for the form and forwards to JSP.
     * @param request
     * @param response
     * @throws jakarta.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Load customers for dropdown
        CustomerDAO customerDAO = new CustomerDAO();
        java.util.List<model.Customer> customers = customerDAO.getAllCustomers();
        request.setAttribute("customers", customers);

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
        request.setAttribute("activePage", "add-export-receipt");

        request.getRequestDispatcher("/warehouse-export-mgt/add-export-receipt.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.Parses form, builds ExportReceipt + ExportDetail list, persists and logs activity.
     * @param request
     * @param response
     * @throws jakarta.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Debug start
        System.out.println("=== CREATE EXPORT RECEIPT POST START ===");

        try {
            // Basic receipt info
            String customerIdStr = request.getParameter("customerId");
            String exportDateStr = request.getParameter("exportDate");
            String totalAmountStr = request.getParameter("totalAmount");
            String note = request.getParameter("note");

            int customerId = Integer.parseInt(customerIdStr);
            // Use current timestamp if date not provided or parse flexible
            Date exportDate = (exportDateStr == null || exportDateStr.isEmpty()) ? new Date() : toSqlTimestampFlexible(exportDateStr);
            BigDecimal totalAmount = (totalAmountStr == null || totalAmountStr.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(totalAmountStr);

            // Build receipt model
            ExportReceipt receipt = new ExportReceipt();
            receipt.setCustomerId(customerId);
            // Temporary user id; replace with real logged-in user when available
            receipt.setUserId(3);
            receipt.setDate(exportDate);
            receipt.setTotalAmount(totalAmount);
            receipt.setNote(note);

            System.out.println("ExportReceipt created (temp): customer=" + receipt.getCustomerId() + " date=" + receipt.getDate() + " total=" + receipt.getTotalAmount());

            // Parse item rows by scanning sequential indices until none found
            List<ExportDetail> details = new ArrayList<>();
            int index = 0;
            while (true) {
                String code = request.getParameter("items[" + index + "].productCode");
                String name = request.getParameter("items[" + index + "].productName");
                String categoryIdStr = request.getParameter("items[" + index + "].categoryId");
                String unit = request.getParameter("items[" + index + "].unit");
                String size = request.getParameter("items[" + index + "].size");
                String color = request.getParameter("items[" + index + "].color");
                String qtyStr = request.getParameter("items[" + index + "].quantity");
                String priceStr = request.getParameter("items[" + index + "].price");

                // End loop condition: if none of the main item fields present
                if (code == null && name == null && qtyStr == null && priceStr == null) {
                    break;
                }

                // Skip incomplete rows where code or qty/price missing
                if (code == null || qtyStr == null || priceStr == null || code.trim().isEmpty()) {
                    index++;
                    continue;
                }

                ExportDetail d = new ExportDetail();
                d.setProductCode(code.trim());
                d.setProductName(name != null ? name.trim() : null);
                d.setUnit(unit != null ? unit.trim() : "");
                d.setSize(size != null ? size.trim() : "");
                d.setColor(color != null ? color.trim() : "");

                // Parse category ID if provided
                if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                    try {
                        d.setCategoryId(Integer.parseInt(categoryIdStr.trim()));
                    } catch (NumberFormatException e) {
                        // ignore invalid category id
                    }
                }

                d.setQuantity(Integer.parseInt(qtyStr));
                BigDecimal price = new BigDecimal(priceStr);
                d.setPrice(price);
                d.setAmount(price.multiply(BigDecimal.valueOf(d.getQuantity())));
                details.add(d);

                index++;
            }

            int totalQty = details.stream().mapToInt(ExportDetail::getQuantity).sum();
            receipt.setTotalQuantity(totalQty);
            receipt.setDetails(details);

            // Persist within transaction and update stock via DAO
            System.out.println("Export details parsed: count=" + details.size());
            ExportReceiptDAO dao = new ExportReceiptDAO();
            System.out.println("Attempting to create export receipt with details...");

            // Create the export receipt with details in one transactional method (implement in DAO)
            int exportId = dao.createReceiptWithDetails(receipt);
            receipt.setExportId(exportId);

            System.out.println("Export receipt created successfully with ID: " + exportId);

            // Redirect back to form with success message
            HttpSession session = request.getSession(true);
            session.setAttribute("successMessage", "Export receipt created successfully.");

            // Log activity with customer name lookup
            CustomerDAO customerDAO = new CustomerDAO();
            String customerName = "customer_" + customerId;
            try {
                model.Customer customer = customerDAO.findById(customerId);
                if (customer != null) {
                    customerName = customer.getName();
                }
            } catch (Exception e) {
                // ignore and use fallback name
            }

            ActivityLogHelper.logCreate(request.getSession(), "ExportReceipts", exportId,
                "Created export receipt for customer: " + customerName + ", Total: " + totalAmount + ", Items: " + details.size());

            System.out.println("Redirecting to create export receipt page...");
            response.sendRedirect(request.getContextPath() + "/createExportReceipt");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to save export receipt: " + e.getMessage());
            request.getRequestDispatcher("/warehouse-export-mgt/add-export-receipt.jsp").forward(request, response);
        }
    }

    /**
     * Accepts flexible date strings (yyyy-MM-dd or MM/dd/yyyy) and returns java.sql.Date
     */
    private java.sql.Date toSqlDateFlexible(String dateStr) {
        String[] patterns = {"yyyy-MM-dd", "MM/dd/yyyy"};
        for (String p : patterns) {
            try {
                LocalDate ld = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(p));
                return java.sql.Date.valueOf(ld);
            } catch (DateTimeParseException ignored) {}
        }
        // fallback to today
        return new java.sql.Date(System.currentTimeMillis());
    }

    /**
     * Accepts flexible date strings and returns java.util.Date with current time applied.
     */
    private Date toSqlTimestampFlexible(String dateStr) {
        String[] patterns = {"yyyy-MM-dd", "MM/dd/yyyy"};
        for (String p : patterns) {
            try {
                LocalDate ld = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(p));
                LocalTime currentTime = LocalTime.now();
                LocalDateTime ldt = LocalDateTime.of(ld, currentTime);
                return java.sql.Timestamp.valueOf(ldt);
            } catch (DateTimeParseException ignored) {}
        }
        // fallback to now
        return new Date();
    }

    @Override
    public String getServletInfo() {
        return "CreateExportReceiptServlet - handles creation of export receipts and details";
    }// </editor-fold>

}
