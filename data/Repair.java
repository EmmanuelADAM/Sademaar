package data;

import agents.RepairAgent;
import jade.core.AID;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/***/
public class Repair {
    /**owner of the product*/
    AID owner;
    /**the product to repair*/
    ProductImage productImg;

    /**identified parts of the product*/
    List<Part> parts;
    /**list of repair agents implied in the repair*/
    List<RepairAgent> repairAgents;
    /**current state of the repair*/
    StateRepair state;
    /**chronology of the repairs (a list of rendez-vous)*/
    List<RepairState> listRepairStates;
    /**act of repairing ended or not*/
    boolean end;
    /**global evaluation of the repair*/
    int evaluation;
    /**date when the user start to search how to repair*/
    LocalDateTime startDate;
    /**date when the repair ends (with success or not)*/
    LocalDateTime endDate;
    /**cost of the repair*/
    double cost;
    /**user level of expertise*/
    double userLevel;
    /**user patience*/
    int userPatience;


    public Repair(AID owner, ProductImage productImg) {
        this.owner = owner;
        this.productImg = productImg;
        this.parts = new ArrayList<>();
        this.repairAgents = new ArrayList<>();
        this.state = StateRepair.Ask4RdzVs;
        this.evaluation = 0;
        this.end = false;
        this.startDate = LocalDateTime.now();
        this.listRepairStates = new ArrayList<>();
        addFirstRdzVs();
    }

    private void addFirstRdzVs() {
        listRepairStates.add(new RepairState(startDate, owner, null, productImg.getP(), state));
    }
    public void addRepairState(RepairState repairState) {
        listRepairStates.add(repairState);
        state = repairState.getState();
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public List<RepairAgent> getRepairAgents() {
        return repairAgents;
    }

    public void setRepairAgents(List<RepairAgent> repairAgents) {
        this.repairAgents = repairAgents;
    }

    public StateRepair getState() {
        return state;
    }

    public void setState(StateRepair state) {
        this.state = state;
    }

    public List<RepairState> getListRepairStates() {
        return listRepairStates;
    }

    public void setListRepairStates(List<RepairState> listRepairStates) {
        this.listRepairStates = listRepairStates;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public ProductImage getProductImg() {
        return productImg;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void addCost(Double cost) {
        this.cost += cost;
    }

    public double getUserLevel() {
        return userLevel;
    }
    public int getUserPatience() {
        return userPatience;
    }

    public void setUserLevel(double userLevel) {
        this.userLevel = userLevel;
    }

    public void setUserPatience(int userPatience) {
        this.userPatience = userPatience;
    }




    @Override
    public String toString() {
        return "Reparation{" +
                "\n imgProduit=" + productImg.p.name +
                "\n  , finie=" + end +
                "\n  , début=" + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
(endDate==null?"":"\n  , date de fin =" + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm"))) +
                "\n  , coût=%.2f".formatted(cost) +
                "\n  , état=" + state +
                "\n  , pièces=" + parts +
                "\n  , expérience utilisateur=" + userLevel +
                "\n  , délai d'attente maximum=" + userPatience +
                '}';
    }
}
