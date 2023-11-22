package data;

import agents.RepairAgent;
import agents.UserAgent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/***/
public class Repair {
    /**owner of the product*/
    UserAgent owner;
    /**the product to repair*/
    Product product;
    /**identified parts of the product*/
    List<Part> parts;
    /**list of repair agents implied in the repair*/
    List<RepairAgent> repairAgents;
    /**current state of the repair*/
    StateRepair state;
    /**chronology of the repair (one date by state)*/
    List<LocalDateTime> dates;
    /**act of repairing ended or not*/
    boolean end;
    /**global evaluation of the repair*/
    int evaluation;

    public Repair(UserAgent owner, Product product) {
        this.owner = owner;
        this.product = product;
        this.parts = new ArrayList<>();
        this.repairAgents = new ArrayList<>();
        this.state = StateRepair.Ask4RdzVs;
        this.dates = new ArrayList<>();
        this.evaluation = 0;
        this.end = false;
    }

}
