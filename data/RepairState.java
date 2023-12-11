package data;

import java.time.LocalDateTime;

import jade.core.AID;
public class RepairState {
    LocalDateTime date;
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

    @Override
    public String toString() {
        return "RepairState{" +
                "date=" + date +
                (repairAgent==null?"":", repairAgent=" + repairAgent.getLocalName())
                + ", state=" + state +
                '}';
    }
}
