package data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jade.core.AID;
public class RepairState {
    LocalDateTime date;



    LocalDateTime endDate;
    AID userAgent;
    AID repairAgent;
    Product product;
    StateRepair state;
    StateRepair nextState;
    String conclusion;

    public RepairState(LocalDateTime date, AID userAgent, AID repairAgent, Product product, StateRepair state) {
        this.date = date;
        this.userAgent = userAgent;
        this.repairAgent = repairAgent;
        this.product = product;
        this.state = state;
    }

    public RepairState(RepairState original) {
        this.date = original.date;
        this.userAgent = original.userAgent;
        this.repairAgent = original.repairAgent;
        this.product = original.product;
        this.state = original.state;
        this.nextState = original.nextState;
        this.conclusion = original.conclusion;
    }


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public AID getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(AID userAgent) {
        this.userAgent = userAgent;
    }

    public AID getRepairAgent() {
        return repairAgent;
    }

    public void setRepairAgent(AID repairAgent) {
        this.repairAgent = repairAgent;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public StateRepair getState() {
        return state;
    }

    public void setState(StateRepair state) {
        this.state = state;
    }

    public StateRepair getNextState() {
        return nextState;
    }

    public void setNextState(StateRepair nextState) {
        this.nextState = nextState;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public void setEndDate(LocalDateTime endDate) {this.endDate = endDate; }
    public LocalDateTime getEndDate() { return endDate; }

    @Override
    public String toString() {
        date.format(java.time.format.DateTimeFormatter.ISO_DATE_TIME);
        return "Etat de reparation{" +
                "\n  date=" + date.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm")) +
                (endDate==null?"":"\n  , date de fin =" + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm")) + "\n") +
                (repairAgent==null?"":"\n  , agent reparateur=" + repairAgent.getLocalName()) +
                "\n  , etat=" + state +
                '}';
    }
}
