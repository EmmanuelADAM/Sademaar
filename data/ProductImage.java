package data;

public class ProductImage {

    Product p;
    int interest;
    double maxPriceRepairing;
    double thresholdPriceRepairing;
    ProductImage(Product p){
        this.p = p;
        interest=5;
        maxPriceRepairing = p.price*(Math.random()*.45+0.5);
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

    public static void main(String[] args)
    {
        var liste = ProductType.getListProductType();
        liste.forEach(System.out::println);
    }
}
