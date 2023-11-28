package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
    String name;
    ProductSpec spec;
    long id;
    double price;
    static int nbProducts = 0;
    public static List<Product> listProducts;
    static final int VARIATION = 30;
    static final int VARIATIONSTEP = 10;

    Product(String name, ProductSpec type){
        this.name = name;
        this.spec = type;
        price = type.getStandardPrice();
        id = ++nbProducts;
    }

    Product(String name, ProductSpec type, double price){
        this(name,type);
        this.price = price;
    }


        @Override
    public String toString() {
        return String.format("Product{ %d : %s - %s - %.2f€}", id, name, spec, price);
    }


    static public List<Product> getListProducts() {
         if (listProducts == null) {
            listProducts = new ArrayList<>(2*VARIATION/VARIATIONSTEP + VARIATIONSTEP);
            int nbSpec = ProductType.values().length;
            var listeSpec = ProductSpec.getListProductSpec();
            for(var type:listeSpec){
                for(int i=-VARIATION; i<=VARIATION; i+=VARIATIONSTEP) {
                    listProducts.add(new Product(type.getType().name()+"-"+ (i<0?"M"+(-i):"P"+i)  , type, type.getStandardPrice() * (1+i/100d)));
                }
            }
        }
        return listProducts;
    }

    public String getName() {
        return name;
    }

    public ProductSpec getSpec() {
        return spec;
    }

    public long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public static void main(String[] args)
    {
        var tab = Product.getListProducts();
        for(var p:tab)  System.out.println(p);
        System.out.println("-".repeat(20));
    }
}
