package data;

import java.time.LocalDateTime;
import java.util.Objects;

import jade.core.AID;
public record RendezVs (
    LocalDateTime date,
    AID userAgent,
    AID repairAgent,
    Product product,
    StateRepair state
            ){
    @Override
    public String toString() {
        return "RendezVs{" +
                "date=" + date +
                (repairAgent==null?"":", repairAgent=" + repairAgent.getLocalName())
                + ", state=" + state +
                '}';
    }
}
