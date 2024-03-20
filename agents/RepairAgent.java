package agents;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.gui.AgentWindowed;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import java.awt.Point;

public class RepairAgent extends  AgentWindowed{

    /**coordinate of the repair agent*/
    Point coord;

    @Override
    public void println(String msg){super.println(msg);}

    public Point getCoord() {
        return coord;
    }

}
