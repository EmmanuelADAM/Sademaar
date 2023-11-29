package data;

import java.io.Serializable;

public record Part(String name, int serialNumber, int difficulty, double price) implements Serializable {
    public Part(Part p, double priceVariation) {
        this(p.name, p.serialNumber, p.difficulty, p.price*priceVariation);
    }
    public String toString() {
        return String.format("Part{ ref. %d : %s - diff. %d - %.2f€}", serialNumber, name, difficulty, price) ;
    }
}
