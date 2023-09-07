package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class ProductType {

    static List<ProductType> listProductType;
    static List<Part> listSmallParts = new ArrayList<>(50);
    static List<Part> listBigParts = new ArrayList<>(50);
    Part[] smallParts;
    Part[] bigParts;
    double price;
    ProductSpec spec;

    private static final Random rand = new Random();

    ProductType(){}
    static ProductType createProductType(ProductSpec spec){
        ProductType pn = new ProductType();
        pn.spec = spec;
        pn.smallParts = new Part[spec.nbSmallParts];
        for (int i=0; i<spec.nbSmallParts; i++) {
            pn.smallParts[i] = new Part((spec.ordinal() + 1)*100 + i, rand.nextInt(1, 3),
                    Math.floor(100.0 * rand.nextDouble(2., 20.)) / 100.0);
            listSmallParts.add(pn.smallParts[i]);
        }
        pn.bigParts = new Part[spec.nbBigParts];
        for (int i=0; i<spec.nbBigParts; i++){
            pn.bigParts[i] = new Part((spec.ordinal() + 1)*1000 + i, rand.nextInt(1,spec.difficulty+1),
                    Math.floor(100.*rand.nextDouble(30., 100.))/100.);
            listBigParts.add(pn.bigParts[i]);
        }

        pn.price = Arrays.stream(pn.smallParts).mapToDouble(Part::price).sum();
        pn.price += Arrays.stream(pn.bigParts).mapToDouble(Part::price).sum();
        pn.price *= 1.5d;

        return pn;
    }


    public static List<ProductType> getListProductType(){
        if(listProductType==null) {
            listProductType = new ArrayList<>(50);
            for(ProductSpec ps:ProductSpec.values())
                listProductType.add(createProductType(ps));
        }
        return listProductType;
    }


    @Override
    public String toString() {
        return String.format("%s,  difficulty=%d, transportable=%b, danger=%b ",// +
//                "\n\tsmallParts=%s\n\tbigParts=%s",
                this.spec.name(), spec.difficulty, spec.transportable, spec.danger);
//                Arrays.toString(smallParts), Arrays.toString(bigParts));
    }

    public Part[] getSmallParts() {
        return smallParts;
    }


    public Part[] getBigParts() {
        return bigParts;
    }

    public boolean isDanger() {
        return spec.danger;
    }

    public int getDifficulty() {
        return spec.difficulty;
    }

    public boolean isTransportable() {
        return spec.transportable;
    }

    public double getPrice() {
        return price;
    }

    public ProductSpec getSpec() {
        return spec;
    }

}
