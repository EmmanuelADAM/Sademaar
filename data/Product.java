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
    public static final int NB_PRODS = 100;

    Product(String name, ProductSpec type){
        this.name = name;
        this.spec = type;
        price = type.getPrice()*(1.+Math.random()*.1);
        id = ++nbProducts;
    }

    @Override
    public String toString() {
        return String.format("Product{ %d : %s - %s - %.2f€}", id, name, spec, price);
    }


    static public List<Product> getListProducts() {
        if (listProducts == null) {
            listProducts = new ArrayList<>(NB_PRODS);
            int nbSpec = ProductType.values().length;
            int nbBySpec = NB_PRODS /nbSpec;
            var listeType = ProductSpec.getListProductType();
            for(var type:listeType){
                for(int i=0; i<nbBySpec; i++) {
                    listProducts.add(new Product(type.getType().name()+"-"+ i, type));
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
