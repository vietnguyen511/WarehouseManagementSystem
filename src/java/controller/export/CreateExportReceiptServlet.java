package controller.export;

import java.io.IOException;
import dal.ActivityLogHelper;
import dal.ExportReceiptDAO;
import dal.CustomerDAO;
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
 * CreateExportReceiptServlet - handles creation of export receipts and details.
 *
 * Notes:
 * - This servlet now attempts to resolve variant_id for each item using a ProductVariantDAO
 *   (method findVariantIdByProductCodeSizeColor). If you don't have that DAO yet, please add
 *   the ProductVariantDAO class (example provided in previous messages).
 * - ExportReceiptDAO.createReceiptWithDetails(receipt) is expected to insert the header and
 *   all details in a single transaction (commit/rollback managed inside DAO).
 */
public class CreateExportReceiptServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Load customers for dropdown
        dal.CustomerDAO customerDAO = new dal.CustomerDAO();
        java.util.List<model.Customer> customers = customerDAO.getAllCustomers();
        request.setAttribute("customers", customers);

        // Load categories for dropdown
        dal.CategoryDAO categoryDAO = new dal.CategoryDAO();
        java.util.List<model.Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Provide today's date for default value
        String today = LocalDate.now().toString();
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        System.out.println("=== CREATE EXPORT RECEIPT POST START ===");

        try {
            // Basic receipt info
            String customerIdStr = request.getParameter("customerId");
            String exportDateStr = request.getParameter("exportDate");
            String totalAmountStr = request.getParameter("totalAmount");
            String note = request.getParameter("note");

            if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer is required.");
            }

            int customerId = Integer.parseInt(customerIdStr);
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

            // Try to initialize ProductVariantDAO for variant lookup (if available)
            dal.ProductVariantDAO pvDao = null;
            try {
                pvDao = new dal.ProductVariantDAO();
            } catch (Throwable t) {
                // If ProductVariantDAO class not present, we'll continue without variant resolution.
                System.out.println("ProductVariantDAO not available — variant lookup skipped. Add ProductVariantDAO for better mapping.");
            }

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

                // parse quantity & price
                int qty = 1;
                try {
                    qty = Integer.parseInt(qtyStr);
                } catch (NumberFormatException e) {
                    qty = 1;
                }
                d.setQuantity(qty);

                BigDecimal price;
                try {
                    price = new BigDecimal(priceStr);
                } catch (NumberFormatException e) {
                    price = BigDecimal.ZERO;
                }
                d.setPrice(price);
                d.setAmount(price.multiply(BigDecimal.valueOf(d.getQuantity())));

                // Attempt to resolve variant_id using ProductVariantDAO (if present)
                int resolvedVariantId = 0;
                if (pvDao != null) {
                    try {
                        // This method should return 0 if not found.
                        resolvedVariantId = pvDao.findVariantIdByProductCodeSizeColor(d.getProductCode(),
                                                                                     d.getSize() == null ? "" : d.getSize(),
                                                                                     d.getColor() == null ? "" : d.getColor());
                        if (resolvedVariantId > 0) {
                            d.setVariantId(resolvedVariantId);
                        } else {
                            // not found -> leave variantId as 0 (DAO insert should handle null/0 appropriately)
                            d.setVariantId(0);
                        }
                    } catch (Exception ex) {
                        // log and continue; do not fail entire creation because of mismatch
                        System.out.println("Warning: variant lookup failed for productCode=" + d.getProductCode() + ", size=" + d.getSize() + ", color=" + d.getColor());
                        ex.printStackTrace();
                        d.setVariantId(0);
                    }
                } else {
                    // no pvDao available -> set variantId = 0 to indicate unknown
                    d.setVariantId(0);
                }

                details.add(d);
                index++;
            }

            int totalQty = details.stream().mapToInt(ExportDetail::getQuantity).sum();
            receipt.setTotalQuantity(totalQty);
            receipt.setDetails(details);

            // Persist within transaction via DAO
            System.out.println("Export details parsed: count=" + details.size());
            ExportReceiptDAO dao = new ExportReceiptDAO();
            System.out.println("Attempting to create export receipt with details...");

            // Important: ensure createReceiptWithDetails does transaction (insert receipt, insert details, commit)
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
    }
}
