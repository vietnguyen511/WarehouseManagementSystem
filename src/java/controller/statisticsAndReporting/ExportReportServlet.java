package controller.statisticsAndReporting;

import dal.StatisticsDAO;
import dal.ProductDAO;
import dal.CategoryDAO;
import dal.InventoryDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import model.RevenueStatDTO;
import model.Product;
import model.Category;
import model.ExportReportDTO;
import model.InventoryItem;
import model.ImportExportStatDTO;

// No Apache POI imports needed - using XML format

// iText imports for PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont;

// JFreeChart imports for chart generation
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.Color;
import java.awt.BasicStroke;
import java.io.ByteArrayOutputStream;

/**
 * ExportReportServlet - Handles export report generation and download
 * Supports multiple report types and formats (Excel, PDF)
 *
 * @author lengo
 */
@WebServlet(name = "ExportReportServlet", urlPatterns = {"/export-report"})
public class ExportReportServlet extends HttpServlet {

    /**
     * Creates a font that supports Vietnamese characters using BaseFont with Unicode encoding
     */
    private com.itextpdf.text.Font createVietnameseFont(int size, int style) {
        try {
            // Use Times-Roman with IDENTITY_H encoding for better Unicode/Vietnamese support
            BaseFont baseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            return new com.itextpdf.text.Font(baseFont, size, style);
        } catch (Exception e) {
            // Fallback: Try with Courier
            try {
                BaseFont baseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                return new com.itextpdf.text.Font(baseFont, size, style);
            } catch (Exception ex) {
                // Final fallback: Use default font factory
                try {
                    if (style == com.itextpdf.text.Font.BOLD) {
                        return FontFactory.getFont(FontFactory.TIMES_BOLD, size);
                    } else {
                        return FontFactory.getFont(FontFactory.TIMES_ROMAN, size);
                    }
                } catch (Exception finalEx) {
                    return new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, size, style);
                }
            }
        }
    }

    /**
     * Creates a bold font that supports Vietnamese characters
     */
    private com.itextpdf.text.Font createVietnameseBoldFont(int size) {
        return createVietnameseFont(size, com.itextpdf.text.Font.BOLD);
    }

    /**
     * Creates a normal font that supports Vietnamese characters
     */
    private com.itextpdf.text.Font createVietnameseNormalFont(int size) {
        return createVietnameseFont(size, com.itextpdf.text.Font.NORMAL);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        try {
            System.out.println("=== ExportReportServlet START ===");
            
            // Get parameters
            String reportType = request.getParameter("reportType");
            String format = request.getParameter("format");
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");
            String productIdParam = request.getParameter("productId");
            String categoryIdParam = request.getParameter("categoryId");
            
            System.out.println("Report Type: " + reportType);
            System.out.println("Format: " + format);
            
            // Validate required parameters
            if (reportType == null || reportType.isEmpty()) {
                request.setAttribute("errorMessage", "Please select a report type.");
                request.getRequestDispatcher("/statistics-and-reporting/export-report.jsp")
                       .forward(request, response);
                return;
            }
            
            // Parse dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = null;
            Date endDate = null;
            
            try {
                if (startDateParam != null && !startDateParam.isEmpty()) {
                    startDate = sdf.parse(startDateParam);
                }
                if (endDateParam != null && !endDateParam.isEmpty()) {
                    endDate = sdf.parse(endDateParam);
                }
            } catch (ParseException e) {
                System.out.println("Error parsing dates: " + e.getMessage());
            }
            
            // Default to last 30 days if no dates provided
            if (endDate == null) {
                endDate = new Date();
            }
            if (startDate == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startDate = cal.getTime();
            }
            
            // Parse filter IDs
            Integer productId = null;
            Integer categoryId = null;
            
            try {
                if (productIdParam != null && !productIdParam.isEmpty()) {
                    productId = Integer.parseInt(productIdParam);
                }
                if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                    categoryId = Integer.parseInt(categoryIdParam);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error parsing filter IDs: " + e.getMessage());
            }
            
            // Generate report based on type
            switch (reportType.toLowerCase()) {
                case "inventory":
                    generateInventoryReport(response, format, startDate, endDate, productId, categoryId);
                    break;
                case "import-export":
                    generateImportExportReport(response, format, startDate, endDate, productId, categoryId);
                    break;
                case "revenue":
                    generateRevenueReport(response, format, startDate, endDate, productId, categoryId);
                    break;
                case "comprehensive":
                    generateComprehensiveReport(response, format, startDate, endDate, productId, categoryId);
                    break;
                default:
                    request.setAttribute("errorMessage", "Invalid report type: " + reportType);
                    request.getRequestDispatcher("/statistics-and-reporting/export-report.jsp")
                           .forward(request, response);
                    return;
            }
            
            System.out.println("=== ExportReportServlet END ===");
            
        } catch (Exception e) {
            System.out.println("Error in ExportReportServlet: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("errorMessage", "An error occurred while generating the report: " + e.getMessage());
            request.getRequestDispatcher("/statistics-and-reporting/export-report.jsp")
                   .forward(request, response);
        }
    }

    private void generateInventoryReport(HttpServletResponse response, String format,
                                       Date startDate, Date endDate, Integer productId, Integer categoryId)
                                       throws IOException {
        try {
            // Use existing CurrentInventoryServlet logic to get data
            InventoryDAO inventoryDAO = new InventoryDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            // Get inventory data using the same logic as CurrentInventoryServlet
            List<InventoryItem> inventoryItems = inventoryDAO.getCurrentInventory(categoryId, null); // No search query for export
            List<Category> categories = categoryDAO.getAllCategories();

            System.out.println("DEBUG: Retrieved " + inventoryItems.size() + " inventory items for export");

            String fileName = "inventory_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if ("pdf".equalsIgnoreCase(format)) {
                generateInventoryPdfReport(response, inventoryItems, categories, fileName);
            } else {
                generateInventoryExcelReport(response, inventoryItems, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating inventory report: " + e.getMessage());
        }
    }

    private void generateImportExportReport(HttpServletResponse response, String format,
                                          Date startDate, Date endDate, Integer productId, Integer categoryId)
                                          throws IOException {
        try {
            // Use existing ImportExportStatsServlet logic to get data
            StatisticsDAO statsDAO = new StatisticsDAO();

            // Default grouping for export (day-based)
            String groupBy = "day";

            // Get import/export statistics using the same logic as ImportExportStatsServlet
            List<ImportExportStatDTO> statistics = statsDAO.getImportExportStatistics(startDate, endDate, groupBy);

            System.out.println("DEBUG: Retrieved " + statistics.size() + " import/export statistics for export");

            String fileName = "import_export_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if ("pdf".equalsIgnoreCase(format)) {
                generateImportExportPdfReport(response, statistics, fileName, startDate, endDate);
            } else {
                generateImportExportExcelReport(response, statistics, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating import/export report: " + e.getMessage());
        }
    }

    private void generateRevenueReport(HttpServletResponse response, String format,
                                     Date startDate, Date endDate, Integer productId, Integer categoryId)
                                     throws IOException {
        try {
            // Use existing RevenueReportServlet logic to get data
            StatisticsDAO statsDAO = new StatisticsDAO();
            ProductDAO productDAO = new ProductDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            // Default grouping for export (day-based)
            String groupBy = "day";

            // Get revenue statistics using the same logic as RevenueReportServlet
            List<RevenueStatDTO> statistics = statsDAO.getRevenueStatistics(startDate, endDate, groupBy, productId, categoryId);
            Object[] summary = statsDAO.getRevenueSummaryStatistics(startDate, endDate, productId, categoryId);

            System.out.println("DEBUG: Retrieved " + statistics.size() + " revenue statistics for export");

            String fileName = "revenue_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if ("pdf".equalsIgnoreCase(format)) {
                generateRevenuePdfReport(response, statistics, summary, fileName, startDate, endDate);
            } else {
                generateRevenueExcelReport(response, statistics, summary, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating revenue report: " + e.getMessage());
        }
    }

    private void generateComprehensiveReport(HttpServletResponse response, String format,
                                           Date startDate, Date endDate, Integer productId, Integer categoryId)
                                           throws IOException {
        try {
            // Get data from database
            StatisticsDAO dao = new StatisticsDAO();
            List<ExportReportDTO> data = dao.getComprehensiveReportData("comprehensive", startDate, endDate, productId, categoryId);

            String fileName = "comprehensive_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if ("pdf".equalsIgnoreCase(format)) {
                generatePdfReport(response, data, fileName, "Comprehensive Report");
            } else {
                generateExcelReport(response, data, fileName, "Comprehensive Report");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating comprehensive report: " + e.getMessage());
        }
    }

    private void generateExcelReport(HttpServletResponse response, List<ExportReportDTO> data,
                                   String fileName, String reportTitle) throws IOException {
        // Debug: Print to console to verify this method is being called
        System.out.println("DEBUG: generateExcelReport called with fileName: " + fileName + ".csv");

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");

        PrintWriter out = response.getWriter();

        // Add title row
        out.println(reportTitle + " - Generated on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        out.println(); // Empty line

        // Add header row based on report type
        String headerRow = getHeaderRowForReportType(reportTitle);
        out.println(headerRow);

        // Add data rows based on report type
        for (ExportReportDTO report : data) {
            String dataRow = getDataRowForReportType(reportTitle, report);
            out.println(dataRow);
        }
    }

    private void generatePdfReport(HttpServletResponse response, List<ExportReportDTO> data,
                                 String fileName, String reportTitle) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".pdf\"");

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add title
            com.itextpdf.text.Font titleFont = createVietnameseBoldFont(16);
            Paragraph title = new Paragraph(reportTitle, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add generation date
            com.itextpdf.text.Font dateFont = createVietnameseNormalFont(10);
            Paragraph date = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            document.add(new Paragraph(" ")); // Empty line

            // Create table
            PdfPTable table = new PdfPTable(15);
            table.setWidthPercentage(100);

            // Add headers
            com.itextpdf.text.Font headerFont = createVietnameseBoldFont(8);
            String[] headers = {"Date", "Product Code", "Product Name", "Category", "Unit",
                              "Current Stock", "Import Qty", "Export Qty", "Import Price",
                              "Export Price", "Import Value", "Export Value", "Stock Value",
                              "Profit Margin", "Profit %"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add data rows
            com.itextpdf.text.Font dataFont = createVietnameseNormalFont(7);
            for (ExportReportDTO report : data) {
                populatePdfRow(table, report, dataFont);
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new IOException("Error generating PDF report", e);
        }
    }

    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // If the value contains comma, quote, or newline, wrap it in quotes and escape internal quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    private void populatePdfRow(PdfPTable table, ExportReportDTO report, com.itextpdf.text.Font font) {
        // Date
        table.addCell(new PdfPCell(new Phrase(
            report.getReportDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(report.getReportDate()) : "", font)));

        // Product Code
        table.addCell(new PdfPCell(new Phrase(report.getProductCode() != null ? report.getProductCode() : "", font)));

        // Product Name
        table.addCell(new PdfPCell(new Phrase(report.getProductName() != null ? report.getProductName() : "", font)));

        // Category
        table.addCell(new PdfPCell(new Phrase(report.getCategoryName() != null ? report.getCategoryName() : "", font)));

        // Unit
        table.addCell(new PdfPCell(new Phrase(report.getUnit() != null ? report.getUnit() : "", font)));

        // Current Stock
        table.addCell(new PdfPCell(new Phrase(report.getCurrentStock() != null ? report.getCurrentStock().toString() : "", font)));

        // Import Quantity
        table.addCell(new PdfPCell(new Phrase(report.getImportQuantity() != null ? report.getImportQuantity().toString() : "", font)));

        // Export Quantity
        table.addCell(new PdfPCell(new Phrase(report.getExportQuantity() != null ? report.getExportQuantity().toString() : "", font)));

        // Import Price
        table.addCell(new PdfPCell(new Phrase(report.getImportPrice() != null ? String.format("%.2f", report.getImportPrice()) : "", font)));

        // Export Price
        table.addCell(new PdfPCell(new Phrase(report.getExportPrice() != null ? String.format("%.2f", report.getExportPrice()) : "", font)));

        // Import Value
        table.addCell(new PdfPCell(new Phrase(report.getImportValue() != null ? String.format("%.2f", report.getImportValue()) : "", font)));

        // Export Value
        table.addCell(new PdfPCell(new Phrase(report.getExportValue() != null ? String.format("%.2f", report.getExportValue()) : "", font)));

        // Stock Value
        table.addCell(new PdfPCell(new Phrase(report.getStockValue() != null ? String.format("%.2f", report.getStockValue()) : "", font)));

        // Profit Margin
        table.addCell(new PdfPCell(new Phrase(report.getProfitMargin() != null ? String.format("%.2f", report.getProfitMargin()) : "", font)));

        // Profit Percentage
        table.addCell(new PdfPCell(new Phrase(report.getProfitPercentage() != null ? String.format("%.2f%%", report.getProfitPercentage()) : "", font)));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to the export report page
        request.getRequestDispatcher("/statistics-and-reporting/export-report.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void generateInventoryExcelReport(HttpServletResponse response, List<InventoryItem> inventoryItems, String fileName) throws IOException {
        System.out.println("DEBUG: generateInventoryExcelReport called with fileName: " + fileName + ".csv");

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");

        PrintWriter out = response.getWriter();

        // Add title row
        out.println("Current Inventory Report - Generated on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        out.println(); // Empty line

        // Add header row for inventory
        out.println("Product Code,Product Name,Category,Unit,Current Stock,Import Price,Export Price,Stock Value,Stock Status");

        // Add data rows
        for (InventoryItem item : inventoryItems) {
            StringBuilder row = new StringBuilder();

            row.append(escapeCSV(item.getProductCode())).append(",");
            row.append(escapeCSV(item.getProductName())).append(",");
            row.append(escapeCSV(item.getCategoryName())).append(",");
            row.append(escapeCSV(item.getUnitName())).append(",");
            row.append(item.getQuantityOnHand()).append(",");
            row.append(item.getImportPrice() != null ? String.format("%.2f", item.getImportPrice()) : "0.00").append(",");
            row.append(item.getExportPrice() != null ? String.format("%.2f", item.getExportPrice()) : "0.00").append(",");
            row.append(item.getInventoryValue() != null ? String.format("%.2f", item.getInventoryValue()) : "0.00").append(",");
            row.append(escapeCSV(item.getStockStatus()));

            out.println(row.toString());
        }
    }

    private void generateInventoryPdfReport(HttpServletResponse response, List<InventoryItem> inventoryItems, List<Category> categories, String fileName) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".pdf\"");

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add title
            com.itextpdf.text.Font titleFont = createVietnameseBoldFont(16);
            Paragraph title = new Paragraph("Current Inventory Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add generation date
            com.itextpdf.text.Font dateFont = createVietnameseNormalFont(10);
            Paragraph date = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            document.add(new Paragraph(" ")); // Empty line

            // Create table with inventory columns
            PdfPTable table = new PdfPTable(9); // 9 columns for inventory
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Set column widths
            float[] columnWidths = {1.2f, 2.5f, 1.5f, 1f, 1.2f, 1.3f, 1.3f, 1.3f, 1.2f};
            table.setWidths(columnWidths);

            // Add headers
            com.itextpdf.text.Font headerFont = createVietnameseBoldFont(10);
            String[] headers = {"Product Code", "Product Name", "Category", "Unit", "Current Stock", "Import Price", "Export Price", "Stock Value", "Status"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add data rows
            com.itextpdf.text.Font dataFont = createVietnameseNormalFont(9);
            for (InventoryItem item : inventoryItems) {
                table.addCell(new PdfPCell(new Phrase(item.getProductCode() != null ? item.getProductCode() : "", dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getProductName() != null ? item.getProductName() : "", dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getCategoryName() != null ? item.getCategoryName() : "", dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getUnitName() != null ? item.getUnitName() : "", dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantityOnHand()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getImportPrice() != null ? String.format("%.2f", item.getImportPrice()) : "0.00", dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getExportPrice() != null ? String.format("%.2f", item.getExportPrice()) : "0.00", dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getInventoryValue() != null ? String.format("%.2f", item.getInventoryValue()) : "0.00", dataFont)));
                table.addCell(new PdfPCell(new Phrase(item.getStockStatus(), dataFont)));
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            throw new IOException("Error generating PDF inventory report", e);
        }
    }

    private void generateImportExportExcelReport(HttpServletResponse response, List<ImportExportStatDTO> statistics, String fileName) throws IOException {
        System.out.println("DEBUG: generateImportExportExcelReport called with fileName: " + fileName + ".csv");

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");

        PrintWriter out = response.getWriter();

        // Add title row
        out.println("Import/Export Statistics Report - Generated on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        out.println(); // Empty line

        // Add header row for import/export
        out.println("Date,Import Qty,Export Qty,Import Value,Export Value,Stock Difference,Value Difference,Import Receipts,Export Receipts");

        // Add data rows
        for (ImportExportStatDTO stat : statistics) {
            StringBuilder row = new StringBuilder();

            row.append(stat.getDate() != null ? new SimpleDateFormat("MM/dd/yyyy").format(stat.getDate()) : "").append(",");
            row.append(stat.getImportQuantity()).append(",");
            row.append(stat.getExportQuantity()).append(",");
            row.append(stat.getImportValue() != null ? String.format("%.2f", stat.getImportValue()) : "0.00").append(",");
            row.append(stat.getExportValue() != null ? String.format("%.2f", stat.getExportValue()) : "0.00").append(",");
            row.append(stat.getStockDifference()).append(",");
            row.append(stat.getValueDifference() != null ? String.format("%.2f", stat.getValueDifference()) : "0.00").append(",");
            row.append(stat.getImportReceiptCount()).append(",");
            row.append(stat.getExportReceiptCount());

            out.println(row.toString());
        }
    }

    private void generateImportExportPdfReport(HttpServletResponse response, List<ImportExportStatDTO> statistics, String fileName, Date startDate, Date endDate) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".pdf\"");

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add title
            com.itextpdf.text.Font titleFont = createVietnameseBoldFont(16);
            Paragraph title = new Paragraph("Import/Export Statistics Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add date range
            com.itextpdf.text.Font dateFont = createVietnameseNormalFont(10);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Paragraph dateRange = new Paragraph("Period: " + sdf.format(startDate) + " to " + sdf.format(endDate), dateFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            document.add(dateRange);

            Paragraph genDate = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), dateFont);
            genDate.setAlignment(Element.ALIGN_CENTER);
            document.add(genDate);
            document.add(new Paragraph(" ")); // Empty line

            // Create table
            PdfPTable table = new PdfPTable(9); // 9 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Set column widths
            float[] columnWidths = {1.5f, 1.2f, 1.2f, 1.5f, 1.5f, 1.3f, 1.5f, 1.2f, 1.2f};
            table.setWidths(columnWidths);

            // Add headers
            com.itextpdf.text.Font headerFont = createVietnameseBoldFont(9);
            String[] headers = {"Date", "Import Qty", "Export Qty", "Import Value", "Export Value", "Stock Diff", "Value Diff", "Import Receipts", "Export Receipts"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add data rows
            com.itextpdf.text.Font dataFont = createVietnameseNormalFont(8);
            for (ImportExportStatDTO stat : statistics) {
                table.addCell(new PdfPCell(new Phrase(stat.getDate() != null ? sdf.format(stat.getDate()) : "", dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getImportQuantity()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getExportQuantity()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(stat.getImportValue() != null ? String.format("%.2f", stat.getImportValue()) : "0.00", dataFont)));
                table.addCell(new PdfPCell(new Phrase(stat.getExportValue() != null ? String.format("%.2f", stat.getExportValue()) : "0.00", dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getStockDifference()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(stat.getValueDifference() != null ? String.format("%.2f", stat.getValueDifference()) : "0.00", dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getImportReceiptCount()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getExportReceiptCount()), dataFont)));
            }

            document.add(table);

            // Add charts to PDF
            document.add(new Paragraph(" ")); // Empty line
            document.add(new Paragraph("Charts", createVietnameseBoldFont(14)));
            document.add(new Paragraph(" ")); // Empty line

            try {
                // Add Import vs Export Quantity Chart
                byte[] quantityChartBytes = generateImportExportChart(statistics, "quantity");
                Image quantityChart = Image.getInstance(quantityChartBytes);
                quantityChart.scaleToFit(500, 300);
                quantityChart.setAlignment(Element.ALIGN_CENTER);
                document.add(quantityChart);
                document.add(new Paragraph(" ")); // Empty line

                // Add Stock Difference Chart
                byte[] differenceChartBytes = generateImportExportChart(statistics, "difference");
                Image differenceChart = Image.getInstance(differenceChartBytes);
                differenceChart.scaleToFit(500, 300);
                differenceChart.setAlignment(Element.ALIGN_CENTER);
                document.add(differenceChart);
                document.add(new Paragraph(" ")); // Empty line

                // Add Value Comparison Chart
                byte[] valueChartBytes = generateImportExportChart(statistics, "value");
                Image valueChart = Image.getInstance(valueChartBytes);
                valueChart.scaleToFit(500, 300);
                valueChart.setAlignment(Element.ALIGN_CENTER);
                document.add(valueChart);

            } catch (Exception chartException) {
                System.out.println("Warning: Could not generate charts for PDF: " + chartException.getMessage());
                document.add(new Paragraph("Charts could not be generated.", createVietnameseNormalFont(10)));
            }

            document.close();

        } catch (Exception e) {
            throw new IOException("Error generating PDF import/export report", e);
        }
    }

    private void generateRevenueExcelReport(HttpServletResponse response, List<RevenueStatDTO> statistics, Object[] summary, String fileName) throws IOException {
        System.out.println("DEBUG: generateRevenueExcelReport called with fileName: " + fileName + ".csv");

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");

        PrintWriter out = response.getWriter();

        // Add title row
        out.println("Revenue Report - Generated on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        out.println(); // Empty line

        // Add summary section
        if (summary != null && summary.length >= 4) {
            out.println("SUMMARY");
            out.println("Total Revenue," + (summary[0] != null ? String.format("%.2f", summary[0]) : "0.00"));
            out.println("Total Quantity," + (summary[1] != null ? summary[1].toString() : "0"));
            out.println("Total Receipts," + (summary[2] != null ? summary[2].toString() : "0"));
            out.println("Average Value," + (summary[3] != null ? String.format("%.2f", summary[3]) : "0.00"));
            out.println(); // Empty line
        }

        // Add header row for revenue
        out.println("Date,Quantity Sold,Total Revenue,Receipt Count,Average Value");

        // Add data rows
        for (RevenueStatDTO stat : statistics) {
            StringBuilder row = new StringBuilder();

            row.append(stat.getDate() != null ? new SimpleDateFormat("MM/dd/yyyy").format(stat.getDate()) : "").append(",");
            row.append(stat.getTotalQuantity()).append(",");
            row.append(stat.getTotalValue() != null ? String.format("%.2f", stat.getTotalValue()) : "0.00").append(",");
            row.append(stat.getReceiptCount()).append(",");
            row.append(stat.getAverageValue() != null ? String.format("%.2f", stat.getAverageValue()) : "0.00");

            out.println(row.toString());
        }
    }

    private void generateRevenuePdfReport(HttpServletResponse response, List<RevenueStatDTO> statistics, Object[] summary, String fileName, Date startDate, Date endDate) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".pdf\"");

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add title
            com.itextpdf.text.Font titleFont = createVietnameseBoldFont(16);
            Paragraph title = new Paragraph("Revenue Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add date range
            com.itextpdf.text.Font dateFont = createVietnameseNormalFont(10);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Paragraph dateRange = new Paragraph("Period: " + sdf.format(startDate) + " to " + sdf.format(endDate), dateFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            document.add(dateRange);

            Paragraph genDate = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), dateFont);
            genDate.setAlignment(Element.ALIGN_CENTER);
            document.add(genDate);
            document.add(new Paragraph(" ")); // Empty line

            // Add summary section
            if (summary != null && summary.length >= 4) {
                com.itextpdf.text.Font summaryFont = createVietnameseBoldFont(12);
                document.add(new Paragraph("Summary", summaryFont));

                com.itextpdf.text.Font summaryDataFont = createVietnameseNormalFont(10);
                document.add(new Paragraph("Total Revenue: " + (summary[0] != null ? String.format("%.2f", summary[0]) : "0.00"), summaryDataFont));
                document.add(new Paragraph("Total Quantity: " + (summary[1] != null ? summary[1].toString() : "0"), summaryDataFont));
                document.add(new Paragraph("Total Receipts: " + (summary[2] != null ? summary[2].toString() : "0"), summaryDataFont));
                document.add(new Paragraph("Average Value: " + (summary[3] != null ? String.format("%.2f", summary[3]) : "0.00"), summaryDataFont));
                document.add(new Paragraph(" ")); // Empty line
            }

            // Create table
            PdfPTable table = new PdfPTable(5); // 5 columns (removed Product Code, Product Name, Category)
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Set column widths
            float[] columnWidths = {2f, 1.5f, 2f, 1.5f, 1.5f};
            table.setWidths(columnWidths);

            // Add headers
            com.itextpdf.text.Font headerFont = createVietnameseBoldFont(9);
            String[] headers = {"Date", "Qty Sold", "Total Revenue", "Receipts", "Avg Value"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Add data rows
            com.itextpdf.text.Font dataFont = createVietnameseNormalFont(8);
            for (RevenueStatDTO stat : statistics) {
                table.addCell(new PdfPCell(new Phrase(stat.getDate() != null ? sdf.format(stat.getDate()) : "", dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getTotalQuantity()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(stat.getTotalValue() != null ? String.format("%.2f", stat.getTotalValue()) : "0.00", dataFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stat.getReceiptCount()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(stat.getAverageValue() != null ? String.format("%.2f", stat.getAverageValue()) : "0.00", dataFont)));
            }

            document.add(table);

            // Add charts to PDF
            document.add(new Paragraph(" ")); // Empty line
            document.add(new Paragraph("Charts", createVietnameseBoldFont(14)));
            document.add(new Paragraph(" ")); // Empty line

            try {
                // Add Revenue Trend Chart
                byte[] revenueChartBytes = generateRevenueChart(statistics, "revenue");
                Image revenueChart = Image.getInstance(revenueChartBytes);
                revenueChart.scaleToFit(500, 300);
                revenueChart.setAlignment(Element.ALIGN_CENTER);
                document.add(revenueChart);
                document.add(new Paragraph(" ")); // Empty line

                // Add Quantity Trend Chart
                byte[] quantityChartBytes = generateRevenueChart(statistics, "quantity");
                Image quantityChart = Image.getInstance(quantityChartBytes);
                quantityChart.scaleToFit(500, 300);
                quantityChart.setAlignment(Element.ALIGN_CENTER);
                document.add(quantityChart);

            } catch (Exception chartException) {
                System.out.println("Warning: Could not generate charts for PDF: " + chartException.getMessage());
                document.add(new Paragraph("Charts could not be generated.", createVietnameseNormalFont(10)));
            }

            document.close();

        } catch (Exception e) {
            throw new IOException("Error generating PDF revenue report", e);
        }
    }

    // Chart generation methods
    private byte[] generateImportExportChart(List<ImportExportStatDTO> statistics, String chartType) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (ImportExportStatDTO stat : statistics) {
            String dateLabel = stat.getDateLabel() != null ? stat.getDateLabel() :
                              (stat.getDate() != null ? new SimpleDateFormat("MM/dd").format(stat.getDate()) : "");

            if ("quantity".equals(chartType)) {
                dataset.addValue(stat.getImportQuantity(), "Import Qty", dateLabel);
                dataset.addValue(stat.getExportQuantity(), "Export Qty", dateLabel);
            } else if ("value".equals(chartType)) {
                dataset.addValue(stat.getImportValue(), "Import Value", dateLabel);
                dataset.addValue(stat.getExportValue(), "Export Value", dateLabel);
            } else if ("difference".equals(chartType)) {
                dataset.addValue(stat.getStockDifference(), "Stock Difference", dateLabel);
            }
        }

        JFreeChart chart;
        if ("quantity".equals(chartType)) {
            chart = ChartFactory.createLineChart(
                "Import vs Export Quantity",
                "Date",
                "Quantity",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );

            // Customize line chart
            CategoryPlot plot = chart.getCategoryPlot();
            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            renderer.setSeriesPaint(0, new Color(54, 162, 235)); // Blue for Import
            renderer.setSeriesPaint(1, new Color(255, 99, 132)); // Red for Export
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            renderer.setSeriesStroke(1, new BasicStroke(2.0f));
            plot.setRenderer(renderer);

        } else if ("difference".equals(chartType)) {
            chart = ChartFactory.createBarChart(
                "Stock Difference (Import - Export)",
                "Date",
                "Difference",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );

            // Customize bar chart
            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = new BarRenderer();
            renderer.setSeriesPaint(0, new Color(75, 192, 192)); // Teal
            plot.setRenderer(renderer);

        } else { // value chart
            chart = ChartFactory.createBarChart(
                "Value Comparison",
                "Date",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );

            // Customize bar chart
            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = new BarRenderer();
            renderer.setSeriesPaint(0, new Color(54, 162, 235)); // Blue for Import
            renderer.setSeriesPaint(1, new Color(255, 99, 132)); // Red for Export
            plot.setRenderer(renderer);
        }

        // Set chart background
        chart.setBackgroundPaint(Color.WHITE);
        chart.getCategoryPlot().setBackgroundPaint(Color.WHITE);

        // Convert chart to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 600, 400);
        return baos.toByteArray();
    }

    private byte[] generateRevenueChart(List<RevenueStatDTO> statistics, String chartType) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (RevenueStatDTO stat : statistics) {
            String label = stat.getDateLabel() != null ? stat.getDateLabel() :
                          (stat.getDate() != null ? new SimpleDateFormat("MM/dd").format(stat.getDate()) : "");

            if ("revenue".equals(chartType)) {
                dataset.addValue(stat.getTotalValue(), "Revenue", label);
            } else if ("quantity".equals(chartType)) {
                dataset.addValue(stat.getTotalQuantity(), "Quantity", label);
            }
        }

        JFreeChart chart;
        if ("revenue".equals(chartType)) {
            chart = ChartFactory.createLineChart(
                "Revenue Trend Over Time",
                "Date",
                "Revenue",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );

            // Customize line chart
            CategoryPlot plot = chart.getCategoryPlot();
            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            renderer.setSeriesPaint(0, new Color(75, 192, 192)); // Teal
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            plot.setRenderer(renderer);

        } else { // quantity chart
            chart = ChartFactory.createBarChart(
                "Quantity Sold Over Time",
                "Date",
                "Quantity",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
            );

            // Customize bar chart
            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = new BarRenderer();
            renderer.setSeriesPaint(0, new Color(255, 159, 64)); // Orange
            plot.setRenderer(renderer);
        }

        // Set chart background
        chart.setBackgroundPaint(Color.WHITE);
        chart.getCategoryPlot().setBackgroundPaint(Color.WHITE);

        // Convert chart to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 600, 400);
        return baos.toByteArray();
    }

    private String getHeaderRowForReportType(String reportTitle) {
        if (reportTitle.contains("Inventory")) {
            return "Product Code,Product Name,Category,Unit,Current Stock,Import Price,Export Price,Stock Value";
        } else if (reportTitle.contains("Import-Export")) {
            return "Date,Product Code,Product Name,Category,Import Qty,Export Qty,Import Value,Export Value";
        } else if (reportTitle.contains("Revenue")) {
            return "Date,Product Code,Product Name,Category,Export Qty,Export Price,Export Value,Profit Margin,Profit %";
        } else {
            // Comprehensive report
            return "Date,Product Code,Product Name,Category,Unit,Current Stock,Import Qty,Export Qty,Import Price,Export Price,Import Value,Export Value,Stock Value,Profit Margin,Profit %";
        }
    }

    private String getDataRowForReportType(String reportTitle, ExportReportDTO report) {
        StringBuilder row = new StringBuilder();

        if (reportTitle.contains("Inventory")) {
            // Inventory Report: Focus on current stock and pricing
            row.append(escapeCSV(report.getProductCode())).append(",");
            row.append(escapeCSV(report.getProductName())).append(",");
            row.append(escapeCSV(report.getCategoryName())).append(",");
            row.append(escapeCSV(report.getUnit())).append(",");
            row.append(report.getCurrentStock() != null ? report.getCurrentStock().toString() : "0").append(",");
            row.append(report.getImportPrice() != null ? String.format("%.2f", report.getImportPrice()) : "0.00").append(",");
            row.append(report.getExportPrice() != null ? String.format("%.2f", report.getExportPrice()) : "0.00").append(",");
            row.append(report.getStockValue() != null ? String.format("%.2f", report.getStockValue()) : "0.00");

        } else if (reportTitle.contains("Import-Export")) {
            // Import-Export Report: Focus on transaction history
            row.append(report.getReportDate() != null ? new SimpleDateFormat("MM/dd/yyyy").format(report.getReportDate()) : "").append(",");
            row.append(escapeCSV(report.getProductCode())).append(",");
            row.append(escapeCSV(report.getProductName())).append(",");
            row.append(escapeCSV(report.getCategoryName())).append(",");
            row.append(report.getImportQuantity() != null ? report.getImportQuantity().toString() : "0").append(",");
            row.append(report.getExportQuantity() != null ? report.getExportQuantity().toString() : "0").append(",");
            row.append(report.getImportValue() != null ? String.format("%.2f", report.getImportValue()) : "0.00").append(",");
            row.append(report.getExportValue() != null ? String.format("%.2f", report.getExportValue()) : "0.00");

        } else if (reportTitle.contains("Revenue")) {
            // Revenue Report: Focus on sales and profitability
            row.append(report.getReportDate() != null ? new SimpleDateFormat("MM/dd/yyyy").format(report.getReportDate()) : "").append(",");
            row.append(escapeCSV(report.getProductCode())).append(",");
            row.append(escapeCSV(report.getProductName())).append(",");
            row.append(escapeCSV(report.getCategoryName())).append(",");
            row.append(report.getExportQuantity() != null ? report.getExportQuantity().toString() : "0").append(",");
            row.append(report.getExportPrice() != null ? String.format("%.2f", report.getExportPrice()) : "0.00").append(",");
            row.append(report.getExportValue() != null ? String.format("%.2f", report.getExportValue()) : "0.00").append(",");
            row.append(report.getProfitMargin() != null ? String.format("%.2f", report.getProfitMargin()) : "0.00").append(",");
            row.append(report.getProfitPercentage() != null ? String.format("%.2f", report.getProfitPercentage()) : "0.00");

        } else {
            // Comprehensive Report: All columns
            row.append(report.getReportDate() != null ? new SimpleDateFormat("MM/dd/yyyy").format(report.getReportDate()) : "").append(",");
            row.append(escapeCSV(report.getProductCode())).append(",");
            row.append(escapeCSV(report.getProductName())).append(",");
            row.append(escapeCSV(report.getCategoryName())).append(",");
            row.append(escapeCSV(report.getUnit())).append(",");
            row.append(report.getCurrentStock() != null ? report.getCurrentStock().toString() : "0").append(",");
            row.append(report.getImportQuantity() != null ? report.getImportQuantity().toString() : "0").append(",");
            row.append(report.getExportQuantity() != null ? report.getExportQuantity().toString() : "0").append(",");
            row.append(report.getImportPrice() != null ? String.format("%.2f", report.getImportPrice()) : "0.00").append(",");
            row.append(report.getExportPrice() != null ? String.format("%.2f", report.getExportPrice()) : "0.00").append(",");
            row.append(report.getImportValue() != null ? String.format("%.2f", report.getImportValue()) : "0.00").append(",");
            row.append(report.getExportValue() != null ? String.format("%.2f", report.getExportValue()) : "0.00").append(",");
            row.append(report.getStockValue() != null ? String.format("%.2f", report.getStockValue()) : "0.00").append(",");
            row.append(report.getProfitMargin() != null ? String.format("%.2f", report.getProfitMargin()) : "0.00").append(",");
            row.append(report.getProfitPercentage() != null ? String.format("%.2f", report.getProfitPercentage()) : "0.00");
        }

        return row.toString();
    }

    @Override
    public String getServletInfo() {
        return "Export Report Servlet - Generates and downloads reports in various formats";
    }
}