package data;

import java.util.Random;

public class Product {

    String name;
    ProductType type;
    long id;
    double price;
    static int nbProducts = 0;
    static Product[]tabProducts;
    static final int NBTOPROD = 100;

    Product(String name, ProductType type){
        this.name = name;
        this.type = type;
        price = type.getPrice()*(1.+Math.random()*.1);
        id = ++nbProducts;
    }

    @Override
    public String toString() {
        return String.format("Product{ %d : %s - %s - %.2f€}", id, name, type, price);
    }


    static public Product[] getTabProducts() {
        if (tabProducts == null) {
            Random r = new Random();
            tabProducts = new Product[NBTOPROD];
            int nbSpec = ProductSpec.values().length;
            int nbBySpec = NBTOPROD/nbSpec;
            var listeType = ProductType.getListProductType();
            int j=0;
            for(var type:listeType){
                for(int i=0; i<nbBySpec; i++) {
                    tabProducts[j] = new Product(type.getSpec().name()+ i, type);
                    j++;
                }
            }
        }
        return tabProducts;
    }

    public static void main(String[] args)
    {
        var tab = Product.getTabProducts();
        for(var p:tab)  System.out.println(p);
        System.out.println("-".repeat(20));
    }
}
