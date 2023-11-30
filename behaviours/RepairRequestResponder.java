package behaviours;

import agents.RepairAgent;
import data.Part;
import data.Product;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

import java.io.IOException;
import java.util.Random;

public class RepairRequestResponder extends AchieveREResponder {
    RepairAgent myAgent;
    Part faultyPart;

    public RepairRequestResponder(Agent a, MessageTemplate mt) {
        super(a, mt);
        myAgent = (RepairAgent) a;
    }

    //function triggered by a REQUEST msg :
    // the agents decides to refuse or to agree with the request
    //if it agrees it hase to send an inform message next
    //return the answer to the request
    @Override
    protected ACLMessage handleRequest(ACLMessage request) {
        Random hasard = new Random();
        myAgent.println(request.getSender().getLocalName() + " is arrived. ");
        ACLMessage answer = request.createReply();
        Product product = null;
        try { product = (Product)request.getContentObject();}
        catch (UnreadableException e) { throw new RuntimeException(e);}
        myAgent.println(" with this product : " + product.getName());

        // identify the faulty part
        // sometimes the breakdown doesn't necessitate part, just a small repair
        // 80% of the time, the breakdown will require a part
        if (hasard.nextDouble()<0.8){
            answer.setPerformative(ACLMessage.REFUSE);
            faultyPart = product.defineFauyltyPart();
            try { answer.setContentObject(faultyPart);} catch (IOException e) { throw new RuntimeException(e); }
            myAgent.println("Partie deffectueuse identifiée : " + faultyPart.name());
            myAgent.println("-".repeat(40));
            if(faultyPart.dangerous())
            {
                answer.setConversationId("DANGER");
                myAgent.println("La réparation de cette pièce est dangereuse, je recommande un achat d'un nouveau produit ! ");
            }
            else {
                answer.setConversationId("CMD");
                myAgent.println("Je recommande un achat de cette prièce et revenez nous voir !");
            }
        }
        else{
            answer.setPerformative(ACLMessage.AGREE);
            myAgent.println("On va tenter de réparer cela ensemble...");
        }
            myAgent.println("-".repeat(40));
        return answer;
    }

    //Function used to return a result by a message
    //param : request = initial initial request, response = agreement I just sent
    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
        ACLMessage answer = request.createReply();
        if(Math.random()<0.8) {
            answer.setPerformative(ACLMessage.INFORM);
            answer.setContent("Réparation effectuée avec succès" );
            myAgent.println("Réparation effectuée avec succès");
        }
        else {
            answer.setPerformative(ACLMessage.FAILURE);
            answer.setContent("Réparation impossible" );
            myAgent.println("Réparation impossible");
        }
        myAgent.println("(o)".repeat(20));
        return answer;
    }
}
