package data;

import java.io.Serializable;
import java.util.Random;


public enum ProductType implements Serializable {
    Cafetiere(true, 3,0, 2, 40d),
    LaveLinge(false, 3, 2, 4, 200d),
    SourisOrdi(true, 2, 0, 1, 40d),
    Aspirateur(true, 3, 1, 3, 100d),
    LaveVaisselle(false, 2, 3, 5, 200d);
    int nbSmallParts;
    int nbBigParts;
    boolean danger;
    int difficulty;
    boolean transportable;
    double standardPrice;

    private final Random rand = new Random();

    ProductType(boolean transportable, int nbSmallParts, int nbBigParts, int difficulty, double standardPrice){
        this.transportable = transportable;
        this.difficulty = difficulty;
        this.nbSmallParts = nbSmallParts;
        this.nbBigParts = nbBigParts;
        this.standardPrice = standardPrice;
        if(difficulty==5) danger = true;
    }

/*    @Override
    public String toString() {
        return String.format("%s,  difficulty=%d, transportable=%b, danger=%b " +
                "\n\tnb smallParts=%d\n\tnb bigParts=%d",
                this.name(), difficulty, transportable, danger, nbSmallParts, nbBigParts);
    }
*/

    public boolean isDanger() {
        return danger;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public boolean isTransportable() {
        return transportable;
    }


}
