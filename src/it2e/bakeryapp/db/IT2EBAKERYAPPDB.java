package it2e.bakeryapp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class IT2EBAKERYAPPDB {
    
    public void addProduct() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        System.out.print("Enter Product (Bread): ");
        String pname = sc.next();
        System.out.print("Enter Quantity: ");
        int pqty = sc.nextInt();
        System.out.print("Enter Selling Price: ");
        float pprice = sc.nextFloat();

        String pstatus = (pqty > 1) ? "Available" : "Out of Stock";
        String sql = "INSERT INTO tbl_product (p_name, p_qty, p_price, p_status) VALUES (?, ?, ?, ?)";

        conf.addRecord(sql, pname, pqty, pprice, pstatus);
    }

    public void viewInventory() {
    config conf = new config();
    String sqlQuery = "SELECT p_id, p_name, p_qty, p_price, p_status FROM tbl_product"; 
    String[] columnHeaders = {"Product ID", "Product Name", "Quantity", "Price", "Status"}; 
    String[] columnNames = {"p_id", "p_name", "p_qty", "p_price", "p_status"};

    conf.viewRecords(sqlQuery, columnHeaders, columnNames);
}
    
    public void updateProduct() {
    Scanner sc = new Scanner(System.in);
    config dbConfig = new config();

    System.out.print("Enter Product ID to update: ");
    int productId = sc.nextInt();
    sc.nextLine(); // Consume the newline

    // SQL statement to check if the product exists
    String sqlCheck = "SELECT * FROM tbl_product WHERE p_id = ?";
    
    // Use the existing method to check if the product exists
    if (!dbConfig.checkIfProductExists(sqlCheck, productId)) {
        System.out.println("Product with ID " + productId + " does not exist. Please try again.");
        return; 
    }
    // Proceed to update if the product exists
    System.out.print("Enter new Product Name: ");
    String newName = sc.nextLine();

    System.out.print("Enter new Quantity: ");
    int newQty = sc.nextInt();

    System.out.print("Enter new Selling Price: ");
    float newPrice = sc.nextFloat();

    String newStatus = (newQty > 0) ? "Available" : "Out of Stock";

    String sqlUpdate = "UPDATE tbl_product SET p_name = ?, p_qty = ?, p_price = ?, p_status = ? WHERE p_id = ?";
    
    // Call the updateRecord method in the config class
    dbConfig.updateRecord(sqlUpdate, newName, newQty, newPrice, newStatus, productId);

    System.out.println("Product updated successfully!");
    
}
    
    public void deleteProduct() {
    Scanner sc = new Scanner(System.in);
    config dbConfig = new config();

    // SQL statement to check if the product exists
    String sqlCheck = "SELECT * FROM tbl_product WHERE P_id = ?";
    // SQL Delete statement to delete a product by its ID
    String sqlDelete = "DELETE FROM tbl_product WHERE P_id = ?";

    System.out.print("Enter Product ID to delete: ");
    int productIdToDelete = sc.nextInt();

    // Check if the product exists
    boolean productExists = dbConfig.checkIfProductExists(sqlCheck, productIdToDelete);

    if (productExists) {
        // Call the deleteRecord method if the product exists
        dbConfig.deleteRecord(sqlDelete, productIdToDelete);
    } else {
        // Product not found
        System.out.println("Product with ID " + productIdToDelete + " not found.");
    }
}
 public void addSales() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter Product Name: ");
        String pname = sc.next();
        System.out.print("Enter Quantity Sold: ");
        int qtySold = sc.nextInt();

        String checkQuery = "SELECT p_qty, p_price FROM tbl_product WHERE p_name = ?";
        String updateProductQuery = "UPDATE tbl_product SET p_qty = ? WHERE p_name = ?";
        String insertSalesQuery = "INSERT INTO tbl_sales (p_name, qty_sold, s_date, s_price, t_revenue) VALUES (?, ?, CURRENT_DATE, ?, ?)";
        
        try (Connection conn = config.connectDB()) {
            conn.setAutoCommit(false); // Start transaction
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, pname);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    int currentQty = rs.getInt("p_qty");
                    float pPrice = rs.getFloat("p_price");

                    if (currentQty >= qtySold) {
                        int updatedQty = currentQty - qtySold;

                        // Update inventory
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateProductQuery)) {
                            updateStmt.setInt(1, updatedQty);
                            updateStmt.setString(2, pname);
                            updateStmt.executeUpdate();
                        }

                        // Insert sales record
                        float revenue = qtySold * pPrice;
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSalesQuery)) {
                            insertStmt.setString(1, pname);
                            insertStmt.setInt(2, qtySold);
                            insertStmt.setFloat(3, pPrice);
                            insertStmt.setFloat(4, revenue);
                            insertStmt.executeUpdate();
                        }

                        conn.commit(); // Commit the transaction
                        System.out.println("Sale added successfully! Inventory updated.");
                    } else {
                        System.out.println("Not enough stock for the product.");
                    }
                } else {
                    System.out.println("Product not found in inventory.");
                }
            } catch (SQLException e) {
                conn.rollback(); // Rollback transaction in case of error
                System.out.println("Error processing sale: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Error establishing database connection: " + e.getMessage());
        }
    }

    public void viewSalesRecords() {
        config conf = new config();
        String sqlQuery = "SELECT p_name, qty_sold, s_date, s_price, t_revenue FROM tbl_sales";
        String[] columnHeaders = {"Product Name", "Quantity Sold", "Sale Date", "Price", "Revenue"};
        String[] columnNames = {"p_name", "qty_sold", "s_date", "s_price", "t_revenue"};

        conf.viewRecords2(sqlQuery, columnHeaders, columnNames);

        // Calculate total revenue
        String totalRevenueQuery = "SELECT SUM(t_revenue) AS total_revenue FROM tbl_sales";
        try (Connection conn = config.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(totalRevenueQuery);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                float totalRevenue = rs.getFloat("total_revenue");
                System.out.println("\nTotal Revenue: $" + totalRevenue);
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total revenue: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
       Scanner sc = new Scanner(System.in);
        IT2EBAKERYAPPDB test = new IT2EBAKERYAPPDB(); // Assuming this is your database class
        String choice;

        do {
            // Main menu 
            System.out.println("\n=== Bakery Management System ===");
            System.out.println("1. Inventory");
            System.out.println("2. Sales");
            System.out.println("3. Exit");
            System.out.print("Enter Choice: ");
            choice = sc.nextLine();

            switch (choice) {
                case "1": // Inventory CRUD operations
                    inventoryCrud(sc, test);
                    break;

                case "2": // Sales CRUD operations
                    salesCrud(sc, test);
                    break;

                case "3": // Exit option
                    System.out.println("Exiting the application...");
                    break;

                default:
                    System.out.println("Invalid choice! Please select again.");
                    break;
            }

        } while (!choice.equals("3")); 

        sc.close();
    }

    private static void inventoryCrud(Scanner sc, IT2EBAKERYAPPDB test) {
        int action;

        do {
            System.out.println("\n=== Inventory Management ===");
            System.out.println("1. ADD Product");
            System.out.println("2. VIEW Products");
            System.out.println("3. UPDATE Product");
            System.out.println("4. DELETE Product");
            System.out.println("5. BACK to Main Menu");

            System.out.print("Enter Action: ");
            action = sc.nextInt();
            sc.nextLine(); 

            switch (action) {
                case 1:
                    test.addProduct();
                    break;

                case 2:
                    test.viewInventory();
                    break;

                case 3:
                    test.updateProduct();
                    break;

                case 4:
                    test.deleteProduct();
                    break;

                case 5:
                    System.out.println("Returning to Main Menu...");
                    break;

                default:
                    System.out.println("Invalid action! Please select again.");
                    break;
            }

        } while (action != 5); 
    }

    private static void salesCrud(Scanner sc, IT2EBAKERYAPPDB test) {
        int action;

        do {
            System.out.println("\n=== Sales Management ===");
            System.out.println("1. ADD Sales Record");
            System.out.println("2. VIEW Sales Records");
            System.out.println("3. UPDATE Sales Record");
            System.out.println("4. DELETE Sales Record");
            System.out.println("5. BACK to Main Menu");

            System.out.print("Enter Action: ");
            action = sc.nextInt();
            sc.nextLine(); // Consume newline character

            switch (action) {
                case 1:
                  test.addSales();
                    break;

                case 2:
                  test.viewSalesRecords();
                    break;

                case 3:
                    
                    break;

                case 4:
                    
                    break;

                case 5:
                    System.out.println("Returning to Main Menu...");
                    break;

                default:
                    System.out.println("Invalid action! Please select again.");
                    break;
            }

        } while (action != 5);
        
        sc.close();
    }
}

