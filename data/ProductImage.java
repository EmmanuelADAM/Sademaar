package data;

import java.io.Serializable;

/**
 * an image of the product to be repaired containing:
 * - the product
 * - the interest the user has if this product
 * - the price acceptable for a repairing (maxPriceRepairing)
 * */
public class ProductImage implements Serializable {

    Product p;
    int interest;
    double maxPriceRepairing;
//    double thresholdPriceRepairing;
    public ProductImage(Product p){
        this.p = p;
        this.interest=(int)(Math.random()*10);
        this.maxPriceRepairing = p.price*(Math.random()*.45+0.5);
//        this.thresholdPriceRepairing = maxPriceRepairing/2;
    }


    public ProductImage(Product p, int interest, double thresholdPriceRepairing, double maxPriceRepairing) {
        this.p = p;
        this.interest = interest;
//        this.thresholdPriceRepairing = thresholdPriceRepairing;
        this.maxPriceRepairing = maxPriceRepairing;
    }

    @Override
    public String toString() {
    return p.getName();
        //        return String.format("%s, intérêt=%d, prix max de réparation=%.2f ", p , interest, maxPriceRepairing);
    }

    public String toStringDetails() {
        return String.format("%s,\n intérêt=%d, prix max de réparation=%.2f ", p , interest, maxPriceRepairing);
    }

    public Product getP() {
        return p ;
    }

    public static void main(String[] args)
    {
        var list = ProductSpec.getListProductSpec();
        list.forEach(System.out::println);
    }
}
