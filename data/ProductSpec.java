package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class ProductSpec implements Serializable {

    public static List<ProductSpec> listProductType;
    public static List<Part> listSmallParts = new ArrayList<>(50);
    public static List<Part> listBigParts = new ArrayList<>(50);
    Part[] smallParts;
    Part[] bigParts;
    double standardPrice;
    ProductType type;

    private static final Random rand = new Random();

    ProductSpec(){}
    static ProductSpec createProductSpec(ProductType spec){
        ProductSpec pn = new ProductSpec();
        pn.type = spec;
        if(spec.nbSmallParts>0) {
            pn.smallParts = new Part[spec.nbSmallParts];
            double partsPrice = spec.standardPrice / 3d;
            double partPrice = 0;
            for (int i = spec.nbSmallParts - 1; i > 0; i--) {
                partPrice = partsPrice * 2 / 3;
                partsPrice = partsPrice - partPrice;
                pn.smallParts[i] = new Part(spec.name() + "sp" + i, (spec.ordinal() + 1) * 100 + i, rand.nextInt(1, 3),
                        partPrice);
                listSmallParts.add(pn.smallParts[i]);
            }
            pn.smallParts[0] = new Part(spec.name() + "sp0", (spec.ordinal() + 1) * 100, rand.nextInt(1, 3),
                    partsPrice);
            listSmallParts.add(pn.smallParts[0]);
        }
        if(spec.nbBigParts>0) {
            pn.bigParts = new Part[spec.nbBigParts];
            double partsPrice = 2 * spec.standardPrice / 3d;
            double partPrice = 0;
            for (int i = spec.nbBigParts - 1; i > 0; i--) {
                partPrice = partsPrice * 2 / 3;
                partsPrice = partsPrice - partPrice;
                pn.bigParts[i] = new Part(spec.name() + "Bp" + i, (spec.ordinal() + 1) * 1000 + i, rand.nextInt(1, 3),
                        partPrice);
                listBigParts.add(pn.bigParts[i]);
            }
            pn.bigParts[0] = new Part(spec.name() + "Bp0", (spec.ordinal() + 1) * 1000, rand.nextInt(1, 3),
                    partsPrice);
            listBigParts.add(pn.bigParts[0]);
        }
        pn.standardPrice = spec.standardPrice;

        return pn;
    }


    public static List<ProductSpec> getListProductSpec(){
        if(listProductType==null) {
            listProductType = new ArrayList<>(50);
            for(ProductType ps: ProductType.values())
                listProductType.add(createProductSpec(ps));
        }
        return listProductType;
    }

    public static List<Part> getListSmallParts() {
        if(listSmallParts==null) getListProductSpec();
        return listSmallParts ;
    }

    public static List<Part> getListBigParts() {
        if(listSmallParts==null) getListProductSpec();
        return listBigParts;
    }

    @Override
    public String toString() {
        return String.format("%s,  difficulty=%d, transportable=%b, danger=%b "+// +
                "\n\tsmallParts=%s\n\tbigParts=%s",
                this.type.name(), type.difficulty, type.transportable, type.danger,
                Arrays.toString(smallParts), Arrays.toString(bigParts));
    }

    public Part[] getSmallParts() {
        return smallParts;
    }


    public Part[] getBigParts() {
        return bigParts;
    }

    public boolean isDanger() {
        return type.danger;
    }

    public int getDifficulty() {
        return type.difficulty;
    }

    public boolean isTransportable() {
        return type.transportable;
    }

    public double getStandardPrice() {
        return standardPrice;
    }

    public ProductType getType() {
        return type;
    }

}
