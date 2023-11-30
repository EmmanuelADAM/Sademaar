package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**class representing a product
 * - name
 * - specification
 * - price
 * This class create the list of products used un the simulation;
 * for each specification, new products are created with a variation of price regarding the standard price included in the specification
 * */
public class Product implements Serializable {
    /**name of the product*/
    String name;
    /**specification that indicate the nb of parts, its danger, standard price...*/
    ProductSpec spec;
    /**unique id of the product*/
    long id;
    /**price of the product*/
    double price;
    /**nb of created products*/
    static int nbProducts = 0;
    /**list of  created products*/
    public static List<Product> listProducts;
    /**products have a price between standard price +/- VARIATION%*/
    public static final int VARIATION = 30;
    /**between 2 product the variation is of VARIATIONSTEP% the  standard price */
    public static final int VARIATIONSTEP = 10;

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


    /**for each specification,
     * new products are created with a price from standard price - VARIATION% to standard price + VARIATION%
     * by step of VARIATIONSTEP% of the standard price.
     *  @return the list of created products  */
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

    /**choose randomly a part of the product that is identified as faulty*/
    public Part defineFauyltyPart() {
        //TODO: modifier pour garantir que part soit un tableau non vide
        Random hasard = new Random();
        var parts = spec.getSmallParts();
        if(hasard.nextBoolean()) parts = spec.getBigParts();
        int nb = hasard.nextInt(parts.length);
        return parts[nb];
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

    public void setPrice(double price) {
        this.price = price ;
    }

    public static void main(String[] args)
    {
        var tab = Product.getListProducts();
        for(var p:tab)  System.out.println(p);
        System.out.println("-".repeat(20));
    }
}
