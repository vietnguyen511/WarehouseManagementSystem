package controller.imports;

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
import java.util.LinkedHashMap;
import java.util.Map;

public class CreateImportReceiptServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CreateImportReceiptServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreateImportReceiptServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        System.out.println("=== CREATE IMPORT RECEIPT POST START ===");

        try {

            String supplierIdStr = request.getParameter("supplierId");
            String importDateStr = request.getParameter("importDate");
            String totalAmountStr = request.getParameter("totalAmount");
            String note = request.getParameter("note");

            // Sanitize note
            note = sanitizeHtml(note);

            int supplierId = Integer.parseInt(supplierIdStr);

            // Dùng timestamp (ngày + giờ hiện tại) cho phiếu nhập
            Date importDate = (importDateStr == null || importDateStr.isEmpty())
                    ? new Date()
                    : toSqlTimestampFlexible(importDateStr);

            // Validate Input day <= Current day
            if (importDateStr != null && !importDateStr.isEmpty()) {
                LocalDate selectedDate = LocalDate.parse(importDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate today = LocalDate.now();
                if (selectedDate.isAfter(today)) {
                    request.setAttribute("errorMessage", "Import date cannot be in the future.");

                    // Load  suppliers, categories, today form
                    SupplierDAO supplierDAO = new SupplierDAO();
                    java.util.List<model.Supplier> suppliers = supplierDAO.getAllSuppliers();
                    request.setAttribute("suppliers", suppliers);

                    dal.CategoryDAO categoryDAO = new dal.CategoryDAO();
                    java.util.List<model.Category> categories = categoryDAO.getAllCategories();
                    request.setAttribute("categories", categories);

                    String todayStr = java.time.LocalDate.now().toString();
                    request.setAttribute("today", todayStr);
                    request.setAttribute("activePage", "add-import-receipt");

                    request.getRequestDispatcher("/warehouse-import-mgt/add-import-receipt.jsp")
                            .forward(request, response);
                    return;
                }
            }

            BigDecimal totalAmount = (totalAmountStr == null || totalAmountStr.isEmpty())
                    ? BigDecimal.ZERO
                    : new BigDecimal(totalAmountStr);

            // Build ImportReceipt
            ImportReceipt receipt = new ImportReceipt();
            receipt.setSupplierId(supplierId);
            // TODO: sau này lấy userId từ session
            receipt.setUserId(3);
            receipt.setDate(importDate);
            receipt.setTotalAmount(totalAmount);
            receipt.setNote(note);

            System.out.println("Receipt created (header): supplierId=" + supplierId
                    + ", date=" + importDate + ", totalAmount=" + totalAmount);

            // Read list item from form
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

                // Sanitize text
                code = sanitizeHtml(code);
                name = sanitizeHtml(name);
                material = sanitizeHtml(material);
                unit = sanitizeHtml(unit);
                size = sanitizeHtml(size);
                color = sanitizeHtml(color);

                // Validate size
                if (size != null && !size.trim().isEmpty()) {
                    String sizeTrimmed = size.trim();
                    if (!isValidSize(sizeTrimmed)) {
                        request.setAttribute("errorMessage",
                                "Invalid size for item " + (index + 1)
                                + ": Size must be XXS, XS, S, M, L, XL, XXL, XXXL (case insensitive) or a number between 20-50.");

                        // Re-load suppliers, categories, today for form
                        SupplierDAO supplierDAO = new SupplierDAO();
                        java.util.List<model.Supplier> suppliers = supplierDAO.getAllSuppliers();
                        request.setAttribute("suppliers", suppliers);

                        dal.CategoryDAO categoryDAO = new dal.CategoryDAO();
                        java.util.List<model.Category> categories = categoryDAO.getAllCategories();
                        request.setAttribute("categories", categories);

                        String todayStr = java.time.LocalDate.now().toString();
                        request.setAttribute("today", todayStr);
                        request.setAttribute("activePage", "add-import-receipt");

                        request.getRequestDispatcher("/warehouse-import-mgt/add-import-receipt.jsp")
                                .forward(request, response);
                        return;
                    }
                }

                ImportDetail d = new ImportDetail();
                d.setProductCode(code != null ? code.trim() : "");
                d.setProductName(name != null ? name.trim() : null);
                d.setMaterial(material != null ? material.trim() : "");
                d.setUnit(unit != null ? unit.trim() : "");
                d.setSize(size != null ? size.trim() : "");
                d.setColor(color != null ? color.trim() : "");

                // Parse categoryId if have
                if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                    try {
                        d.setCategoryId(Integer.parseInt(categoryIdStr.trim()));
                    } catch (NumberFormatException e) {
                        // remove categoryId error
                    }
                }

                d.setQuantity(Integer.parseInt(qtyStr));
                BigDecimal price = new BigDecimal(priceStr);
                d.setPrice(price);
                d.setAmount(price.multiply(BigDecimal.valueOf(d.getQuantity())));

                details.add(d);
                index++;
            }

            // If item Empty
            if (details.isEmpty()) {
                request.setAttribute("errorMessage", "Please add at least one product item.");

                SupplierDAO supplierDAO = new SupplierDAO();
                java.util.List<model.Supplier> suppliers = supplierDAO.getAllSuppliers();
                request.setAttribute("suppliers", suppliers);

                dal.CategoryDAO categoryDAO = new dal.CategoryDAO();
                java.util.List<model.Category> categories = categoryDAO.getAllCategories();
                request.setAttribute("categories", categories);

                String todayStr = java.time.LocalDate.now().toString();
                request.setAttribute("today", todayStr);
                request.setAttribute("activePage", "add-import-receipt");

                request.getRequestDispatcher("/warehouse-import-mgt/add-import-receipt.jsp")
                        .forward(request, response);
                return;
            }

            System.out.println("Raw item count (before merge): " + details.size());

            // ----- Merge (productCode + size + color + price) -----
            Map<String, ImportDetail> grouped = new LinkedHashMap<>();

            for (ImportDetail d : details) {
                String codeKey = d.getProductCode() == null ? "" : d.getProductCode().trim().toUpperCase();
                String sizeKey = d.getSize() == null ? "" : d.getSize().trim().toUpperCase();
                String colorKey = d.getColor() == null ? "" : d.getColor().trim().toUpperCase();
                String priceKey = (d.getPrice() == null ? "0" : d.getPrice().toPlainString());

                String key = codeKey + "|" + sizeKey + "|" + colorKey + "|" + priceKey;

                ImportDetail existing = grouped.get(key);
                if (existing == null) {
                    grouped.put(key, d);
                } else {
                    // quantity ++
                    int mergedQty = existing.getQuantity() + d.getQuantity();
                    existing.setQuantity(mergedQty);

                    // amount = price * total quantity
                    BigDecimal price = existing.getPrice() != null ? existing.getPrice() : BigDecimal.ZERO;
                    existing.setAmount(price.multiply(BigDecimal.valueOf(mergedQty)));
                }
            }

            List<ImportDetail> mergedDetails = new ArrayList<>(grouped.values());

            System.out.println("Merged item count: " + mergedDetails.size());

            // ----- 4. Calculate quantity and set to Receipt -----
            int totalQty = mergedDetails.stream().mapToInt(ImportDetail::getQuantity).sum();
            receipt.setTotalQuantity(totalQty);
            receipt.setDetails(mergedDetails);

            // ----- 5. DAO Save import receipts + details in transaction -----
            ImportReceiptDAO dao = new ImportReceiptDAO();
            System.out.println("About to create receipt with merged details...");

            int importId = dao.createReceiptWithDetails(receipt);
            receipt.setImportId(importId);

            System.out.println("Receipt created successfully with ID: " + importId);

            // ----- 6. Set message sucess + log activity -----
            HttpSession session = request.getSession(true);
            session.setAttribute("successMessage", "Import receipt created successfully.");

            SupplierDAO supplierDAO = new SupplierDAO();
            String supplierName = "supplier_" + supplierId;
            try {
                model.Supplier supplier = supplierDAO.findById(supplierId);
                if (supplier != null) {
                    supplierName = supplier.getName();
                }
            } catch (Exception e) {
            }

            ActivityLogHelper.logCreate(request.getSession(), "ImportReceipts", importId,
                    "Created import receipt for supplier: " + supplierName
                    + ", Total: " + totalAmount
                    + ", Items: " + mergedDetails.size());

            System.out.println("Redirecting to success page...");
            response.sendRedirect(request.getContextPath() + "/createImportReceipt");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to save import receipt: " + e.getMessage());

            // Re-Load  suppliers, categories, today cho form when have error
            SupplierDAO supplierDAO = new SupplierDAO();
            java.util.List<model.Supplier> suppliers = supplierDAO.getAllSuppliers();
            request.setAttribute("suppliers", suppliers);

            dal.CategoryDAO categoryDAO = new dal.CategoryDAO();
            java.util.List<model.Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

            String todayStr = java.time.LocalDate.now().toString();
            request.setAttribute("today", todayStr);
            request.setAttribute("activePage", "add-import-receipt");

            request.getRequestDispatcher("/warehouse-import-mgt/add-import-receipt.jsp")
                    .forward(request, response);
        }
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
            } catch (DateTimeParseException ignored) {
            }
        }
        // Fallback to current timestamp if parsing fails
        return new Date();
    }

    /**
     * Sanitize HTML/CSS from text input to prevent XSS attacks
     */
    private String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        // Remove HTML tags
        String sanitized = input.replaceAll("<[^>]*>", "");
        // Remove CSS style attributes
        sanitized = sanitized.replaceAll("(?i)style\\s*=\\s*[\"'][^\"']*[\"']", "");
        // Remove script tags
        sanitized = sanitized.replaceAll("(?i)<script\\b[^<]*(?:(?!</script>)<[^<]*)*</script>", "");
        // Remove javascript: protocol
        sanitized = sanitized.replaceAll("(?i)javascript:", "");
        // Remove event handlers (onclick, onerror, etc.)
        sanitized = sanitized.replaceAll("(?i)on\\w+\\s*=", "");
        return sanitized;
    }

    /**
     * Validate size: XXS, XS, S, M, L, XL, XXL, XXXL (case insensitive) or
     * number 20-50
     */
    private boolean isValidSize(String size) {
        if (size == null || size.trim().isEmpty()) {
            return false;
        }
        String trimmed = size.trim().toUpperCase();
        // Check for text sizes
        String[] validTextSizes = {"XXS", "XS", "S", "M", "L", "XL", "XXL", "XXXL"};
        for (String validSize : validTextSizes) {
            if (validSize.equals(trimmed)) {
                return true;
            }
        }
        // Check for numeric sizes 20-50
        try {
            int numSize = Integer.parseInt(trimmed);
            return numSize >= 20 && numSize <= 50;
        } catch (NumberFormatException e) {
            return false;
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
