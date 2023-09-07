package data;

public class ProductImage {

    Product p;
    int interest;
    double maxPriceRepairing;
    ProductImage(Product p){
        this.p = p;
        interest=5;
        maxPriceRepairing = p.price*(Math.random()*.45+0.5);
    }

    public ProductImage(Product p, int interest, double maxPriceRepairing) {
        this.p = p;
        this.interest = interest;
        this.maxPriceRepairing = maxPriceRepairing;
    }

    @Override
    public String toString() {
        return String.format("Product %s, interest=%d, maxPriceRepairing=%.2f ", p , interest, maxPriceRepairing);
    }

    public static void main(String[] args)
    {
        var liste = ProductType.getListProductType();
        liste.forEach(System.out::println);
    }
}
