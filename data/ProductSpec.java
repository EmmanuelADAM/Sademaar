package data;

import java.util.Random;


public enum ProductSpec {
    Cafetiere(true, 3,0, 2), LaveLinge(false, 3, 2, 4),
    SourisOrdi(true, 2, 0, 1), Aspirateur(true, 3, 1, 3),
    LaveVaisselle(false, 2, 3, 5);
    int nbSmallParts;
    int nbBigParts;
    boolean danger;
    int difficulty;
    boolean transportable;

    private final Random rand = new Random();

    ProductSpec(boolean transportable, int nbSmallParts, int nbBigParts, int difficulty){
        this.transportable = transportable;
        this.difficulty = difficulty;
        this.nbSmallParts = nbSmallParts;
        this.nbBigParts = nbBigParts;
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
