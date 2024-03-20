package behaviours;

import agents.UserAgent;
import data.Part;
import data.StateRepair;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

public class RepairRequestInitiator extends AchieveREInitiator {
    UserAgent myAgent;
    Part p = null;


    public RepairRequestInitiator(Agent a, ACLMessage msg) {
        super(a, msg);
        myAgent = (UserAgent) a;
    }

    //function triggered by a AGREE msg : the sender accept the request and will send an INFORM message with
    // its result
    @Override
    protected void handleAgree(ACLMessage agree) {
        myAgent.println("Accord reçu de " + agree.getSender().getLocalName());
    }

    //function triggered by a REFUSE msg, the sender refuse to participate in the request
    @Override
    protected void handleRefuse(ACLMessage refuse) {
        myAgent.println("Refus reçu de " + refuse.getSender().getLocalName());
        try { p = (Part)refuse.getContentObject();} catch (UnreadableException e) { throw new RuntimeException(e); }
        myAgent.println("Partie défectueuse identifiée : " + p.name());
        if(refuse.getConversationId().equals("DANGER")) {
            myAgent.println("Le msg est : \"La réparation de cette pièce est dangereuse, je recommande un achat d'un nouveau produit !\" ");
            myAgent.getOutCoffeeShop(StateRepair.NeedNewProduct);
        }
        else {
            myAgent.println("Le msg est : \"Commandez cette pièce et revenez nous voir !\"");
            myAgent.addPartToBuy(p);
            myAgent.getOutCoffeeShop(StateRepair.Ask4Parts);
        }
    //TODO: poursuivre avec l'achat de la pièce ou du produit....
    }

    //function triggered by an INFORM msg, the sender send its result
    @Override
    protected void handleInform(ACLMessage inform) {
        myAgent.println("De " + inform.getSender().getLocalName() +
                ", j'ai reçu ce résultat : " + inform.getContent());
        myAgent.getOutCoffeeShop(StateRepair.RepairSuccess);
    }


    @Override
    protected void handleFailure(ACLMessage failure) {
        myAgent.println("De " + failure.getSender().getLocalName() +
                ", j'ai reçu ce résultat: " + failure.getContent());
        myAgent.getOutCoffeeShop(StateRepair.RepairFailed);
    }

    @Override
    public int onEnd() {
        return p==null ? 0 : 1;
    }

}
