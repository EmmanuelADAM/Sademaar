package data;

import java.io.Serializable;

public record Part(String name, int serialNumber, int difficulty, double price,  boolean dangerous) implements Serializable {
    public Part(Part p, double priceVariation) {
        this(p.name, p.serialNumber, p.difficulty, p.price*priceVariation, p.dangerous);
    }
    public String toString() {
        return String.format("Part{ ref. %d : %s - diff. %d - danger. %b - price %.2f€}", serialNumber, name, difficulty,dangerous, price) ;
    }
}
