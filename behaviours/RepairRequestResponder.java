package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class RepairRequestResponder extends AchieveREResponder {

    public RepairRequestResponder(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    public ACLMessage handleRequest(ACLMessage request){
        ACLMessage answer = null;
        return null;
    }
}
