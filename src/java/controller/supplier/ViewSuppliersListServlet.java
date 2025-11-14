package controller.supplier;

import dal.SupplierDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.Supplier;

public class ViewSuppliersListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String status = request.getParameter("status"); // "active" | "inactive" | null
        String sortBy = request.getParameter("sortBy"); // name, email, phone, address, status
        String sortDir = request.getParameter("sortDir"); // asc | desc

        int page = 1;
        int size = 10;
        try {
            String p = request.getParameter("page");
            if (p != null && !p.trim().isEmpty()) page = Math.max(1, Integer.parseInt(p));
        } catch (NumberFormatException ignored) {}
        try {
            String s = request.getParameter("size");
            if (s != null && !s.trim().isEmpty()) size = Math.max(1, Integer.parseInt(s));
        } catch (NumberFormatException ignored) {}

        int offset = (page - 1) * size;

        SupplierDAO dao = new SupplierDAO();
        try {
            int total = dao.countAll(search, status);
            List<Supplier> suppliers = dao.findAll(offset, size, sortBy, sortDir, search, status);

            int totalPages = (int) Math.ceil((double) total / size);

            request.setAttribute("suppliers", suppliers);
            request.setAttribute("total", total);
            request.setAttribute("page", page);
            request.setAttribute("size", size);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("search", search != null ? search : "");
            request.setAttribute("statusFilter", status != null ? status : "");
            request.setAttribute("sortBy", sortBy != null ? sortBy : "name");
            request.setAttribute("sortDir", sortDir != null ? sortDir : "asc");

            request.setAttribute("activePage", "suppliers");
            request.getRequestDispatcher("/supplier-mgt/supplier-list.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load suppliers: " + e.getMessage());
            request.getRequestDispatcher("/supplier-mgt/supplier-list.jsp").forward(request, response);
        }
    }
}


