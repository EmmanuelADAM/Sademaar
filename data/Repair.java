package data;

import agents.RepairAgent;
import agents.UserAgent;
import java.util.List;

public class Repair {
    UserAgent owner;
    Product product;
    List<Part> parts;
    RepairAgent repairAgent;
    boolean success;
    int evaluation;
}
