package data;

import java.io.Serializable;

public record Part(int no, int difficulty, double price) implements Serializable { }
