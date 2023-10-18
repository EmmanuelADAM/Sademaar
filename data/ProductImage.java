package data;

import java.io.Serializable;

public class ProductImage implements Serializable {

    Product p;
    int interest;
    double maxPriceRepairing;
    double thresholdPriceRepairing;
    public ProductImage(Product p){
        this.p = p;
        this.interest=(int)(Math.random()*10);
        this.maxPriceRepairing = p.price*(Math.random()*.45+0.5);
        this.thresholdPriceRepairing = maxPriceRepairing/2;
    }


    public ProductImage(Product p, int interest, double thresholdPriceRepairing, double maxPriceRepairing) {
        this.p = p;
        this.interest = interest;
        this.thresholdPriceRepairing = thresholdPriceRepairing;
        this.maxPriceRepairing = maxPriceRepairing;
    }

    @Override
    public String toString() {
        return String.format("Product %s, interest=%d, thresholdPriceRepairing=%.2f, maxPriceRepairing=%.2f ", p , interest, thresholdPriceRepairing, maxPriceRepairing);
    }

    public Product getP() {
        return p;
    }

    public static void main(String[] args)
    {
        var liste = ProductSpec.getListProductType();
        liste.forEach(System.out::println);
    }
}
