/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.InventoryItem;

/**
 * InventoryDAO - Data Access Object for Inventory reporting
 * Handles complex queries joining Products and Categories tables
 * 
 * @author lengo
 */
public class InventoryDAO extends DBContext {
    
    /**
     * Get current inventory with optional filters
     * @param categoryId Filter by category (null or 0 for all categories)
     * @param searchQuery Search by product code or name (null or empty for no search)
     * @return List of InventoryItem objects
     */
    public List<InventoryItem> getCurrentInventory(Integer categoryId, String searchQuery) {
        List<InventoryItem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT ");
		sql.append("    v.product_id, ");
		sql.append("    v.product_code, ");
		sql.append("    v.product_name, ");
		sql.append("    v.category_id, ");
		sql.append("    v.category_name, ");
		sql.append("    v.unit, ");
		sql.append("    v.import_price, ");
		sql.append("    v.export_price, ");
		sql.append("    v.status, ");
		sql.append("    v.quantity_on_hand, ");
		sql.append("    v.inventory_value ");
		sql.append("FROM vw_CurrentInventory v ");
        
        // Add category filter if provided
        if (categoryId != null && categoryId > 0) {
			sql.append("WHERE v.category_id = ? ");
        }
        
        // Add search filter if provided
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
			if (categoryId != null && categoryId > 0) {
				sql.append("AND (v.product_code LIKE ? OR v.product_name LIKE ?) ");
			} else {
				sql.append("WHERE (v.product_code LIKE ? OR v.product_name LIKE ?) ");
			}
        }
        
		sql.append("ORDER BY v.product_code ASC");
        
        try {
            PreparedStatement st = connection.prepareStatement(sql.toString());
            
            int paramIndex = 1;
            
            // Set category parameter
            if (categoryId != null && categoryId > 0) {
                st.setInt(paramIndex++, categoryId);
            }
            
            // Set search parameters
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchPattern = "%" + searchQuery.trim() + "%";
                st.setString(paramIndex++, searchPattern);
                st.setString(paramIndex++, searchPattern);
            }
            
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                InventoryItem item = new InventoryItem();
                item.setProductId(rs.getInt("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantityOnHand(rs.getInt("quantity_on_hand"));
                item.setUnitName(rs.getString("unit"));
                item.setImportPrice(rs.getBigDecimal("import_price"));
                item.setExportPrice(rs.getBigDecimal("export_price"));
                item.setStatus(rs.getBoolean("status"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setCategoryName(rs.getString("category_name"));
                
                // Calculate inventory value
                BigDecimal inventoryValue = rs.getBigDecimal("inventory_value");
                item.setInventoryValue(inventoryValue != null ? inventoryValue : BigDecimal.ZERO);
                
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error in getCurrentInventory: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Get current inventory with pagination
     * @param categoryId optional category filter
     * @param searchQuery optional search keyword
     * @param page 1-based page index
     * @param pageSize number of items per page
     * @return paginated list of InventoryItem
     */
    public List<InventoryItem> getCurrentInventoryPaged(Integer categoryId, String searchQuery, int page, int pageSize) {
        List<InventoryItem> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT ");
		sql.append("    v.product_id, ");
		sql.append("    v.product_code, ");
		sql.append("    v.product_name, ");
		sql.append("    v.category_id, ");
		sql.append("    v.category_name, ");
		sql.append("    v.unit, ");
		sql.append("    v.import_price, ");
		sql.append("    v.export_price, ");
		sql.append("    v.status, ");
		sql.append("    v.quantity_on_hand, ");
		sql.append("    v.inventory_value ");
		sql.append("FROM vw_CurrentInventory v ");
        
        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (p.code LIKE ? OR p.name LIKE ?) ");
        }
		sql.append("ORDER BY v.product_code ASC ");
		sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try {
            PreparedStatement st = connection.prepareStatement(sql.toString());
            int paramIndex = 1;
            if (categoryId != null && categoryId > 0) {
                st.setInt(paramIndex++, categoryId);
            }
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchPattern = "%" + searchQuery.trim() + "%";
                st.setString(paramIndex++, searchPattern);
                st.setString(paramIndex++, searchPattern);
            }
            int offset = Math.max(page - 1, 0) * Math.max(pageSize, 0);
            st.setInt(paramIndex++, offset);
            st.setInt(paramIndex++, pageSize);
            
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                InventoryItem item = new InventoryItem();
                item.setProductId(rs.getInt("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantityOnHand(rs.getInt("quantity_on_hand"));
                item.setUnitName(rs.getString("unit"));
                item.setImportPrice(rs.getBigDecimal("import_price"));
                item.setExportPrice(rs.getBigDecimal("export_price"));
                item.setStatus(rs.getBoolean("status"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setCategoryName(rs.getString("category_name"));
                java.math.BigDecimal inventoryValue = rs.getBigDecimal("inventory_value");
                item.setInventoryValue(inventoryValue != null ? inventoryValue : java.math.BigDecimal.ZERO);
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error in getCurrentInventoryPaged: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Count total inventory rows matching filters
     */
    public int countCurrentInventory(Integer categoryId, String searchQuery) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) AS total FROM vw_CurrentInventory v WHERE 1=1 ");
        if (categoryId != null && categoryId > 0) {
			sql.append("AND v.category_id = ? ");
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
			sql.append("AND (v.product_code LIKE ? OR v.product_name LIKE ?) ");
        }
        try {
            PreparedStatement st = connection.prepareStatement(sql.toString());
            int paramIndex = 1;
            if (categoryId != null && categoryId > 0) {
                st.setInt(paramIndex++, categoryId);
            }
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchPattern = "%" + searchQuery.trim() + "%";
                st.setString(paramIndex++, searchPattern);
                st.setString(paramIndex++, searchPattern);
            }
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error in countCurrentInventory: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get all current inventory without filters
     * @return List of all InventoryItem objects
     */
    public List<InventoryItem> getAllInventory() {
        return getCurrentInventory(null, null);
    }
    
    /**
     * Get inventory statistics
     * @return Array with [totalProducts, totalValue, lowStockCount, outOfStockCount]
     */
	public Object[] getInventoryStatistics() {
		String sql =
			"SELECT " +
			"    COUNT(*) AS total_products, " +
			"    SUM(v.inventory_value) AS total_value, " +
			"    SUM(CASE WHEN v.quantity_on_hand > 0 AND v.quantity_on_hand <= 20 THEN 1 ELSE 0 END) AS low_stock_count, " +
			"    SUM(CASE WHEN v.quantity_on_hand = 0 THEN 1 ELSE 0 END) AS out_of_stock_count " +
			"FROM vw_CurrentInventory v";
		
		try {
			PreparedStatement st = connection.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			if (rs.next()) {
				return new Object[] {
					rs.getInt("total_products"),
					rs.getBigDecimal("total_value"),
					rs.getInt("low_stock_count"),
					rs.getInt("out_of_stock_count")
				};
			}
		} catch (SQLException e) {
			System.out.println("Error in getInventoryStatistics: " + e.getMessage());
			e.printStackTrace();
		}
		
		return new Object[] {0, BigDecimal.ZERO, 0, 0};
	}
    
    /**
     * Test method to verify database connection and data retrieval
     */
    public static void main(String[] args) {
        InventoryDAO dao = new InventoryDAO();
        
        // Test 1: Get all inventory
        System.out.println("=== Test 1: All Inventory ===");
        List<InventoryItem> allItems = dao.getAllInventory();
        System.out.println("Total items: " + allItems.size());
        for (InventoryItem item : allItems) {
            System.out.println(item);
        }
        
        // Test 2: Filter by category
        System.out.println("\n=== Test 2: Filter by Category (ID=1) ===");
        List<InventoryItem> categoryItems = dao.getCurrentInventory(1, null);
        System.out.println("Items in category 1: " + categoryItems.size());
        for (InventoryItem item : categoryItems) {
            System.out.println(item);
        }
        
        // Test 3: Search by keyword
        System.out.println("\n=== Test 3: Search 'phone' ===");
        List<InventoryItem> searchItems = dao.getCurrentInventory(null, "phone");
        System.out.println("Search results: " + searchItems.size());
        for (InventoryItem item : searchItems) {
            System.out.println(item);
        }
        
        // Test 4: Get statistics
        System.out.println("\n=== Test 4: Inventory Statistics ===");
        Object[] stats = dao.getInventoryStatistics();
        System.out.println("Total Products: " + stats[0]);
        System.out.println("Total Value: " + stats[1]);
        System.out.println("Low Stock Count: " + stats[2]);
        System.out.println("Out of Stock Count: " + stats[3]);
    }
}

