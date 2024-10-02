
package it2e.bakeryapp.db;

import java.util.Scanner;

public class IT2EBAKERYAPPDB {
 public void addProduct(){
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        System.out.print("Enter Product(Bread):  ");
        String pname = sc.next();
        System.out.print("Enter Quantity: ");
        int pqty = sc.nextInt();
        System.out.print("Enter Price: ");
        float pprice = sc.nextFloat();
        System.out.print("Product Status: ");
        String pstatus = sc.next();

        String sql = "INSERT INTO tbl_product ( p_name, p_qty, p_price, p_status) VALUES (?, ?, ?, ?)";


        conf.addRecord(sql, pname, pqty, pprice, pstatus);


        }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("1. ADD");
        System.out.println("2. VIEW");
        System.out.println("3. UPDATE");
        System.out.println("4. DELETE");
        System.out.println("5. EXIT");
        
        System.out.print("Enter Action: ");
        int action = sc.nextInt();
             
        switch(action){
            case 1:
                IT2EBAKERYAPPDB test = new IT2EBAKERYAPPDB();
                test.addProduct();
            break;
                  
        }
       
    } 
}
