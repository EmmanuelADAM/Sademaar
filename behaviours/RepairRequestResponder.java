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
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RepairRequestResponder extends AchieveREResponder {
    RepairAgent myAgent;
    Part faultyPart;
    List<Part> userParts;

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
        Object[] o = null;
        try { o = (Object[])request.getContentObject();}
        catch (UnreadableException e) {
            myAgent.println("UnreadableException : contentObject = " + o ); throw new RuntimeException(e);}
        try { product = (Product) o[0]; }
        catch (ClassCastException e) { myAgent.println("ClassCastException : object = " + product ); throw new RuntimeException(e);}
        try { userParts = (List<Part>) o[1]; }
        catch (ClassCastException e) { myAgent.println("ClassCastException : parts = " + userParts ); throw new RuntimeException(e);}

        myAgent.println(" with this product : " + product.getName());

        // identify the faulty part
        // sometimes the breakdown doesn't necessitate part, just a small repair
        // 80% of the time, the breakdown will require a part
        faultyPart = product.getFaultyPart();

        if (hasard.nextDouble()<0.2 || userParts!=null && userParts.contains(faultyPart)) {
            answer.setPerformative(ACLMessage.AGREE);
            myAgent.println("On va tenter de réparer cela ensemble...");
        }
        else
        {
            answer.setPerformative(ACLMessage.REFUSE);
            try { answer.setContentObject(faultyPart);} catch (IOException e) { throw new RuntimeException(e); }
            myAgent.println("Partie défectueuse identifiée : " + faultyPart.name());
            myAgent.println("-".repeat(40));
            if(faultyPart.dangerous())
            {
                answer.setConversationId("DANGER");
                myAgent.println("La réparation de cette pièce est dangereuse, je recommande un achat d'un nouveau produit ! ");
            }
            else {
                answer.setConversationId("CMD");
                myAgent.println("Je recommande un achat de cette pièce et revenez nous voir !");
            }
        }
            myAgent.println("-".repeat(40));
        return answer;
    }

    //Function used to return a result by a message
    //param : request = initial initial request, response = agreement I just sent
    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
        ACLMessage answer = request.createReply();
        if(Math.random()<0.8 || userParts.contains(faultyPart)) {
            answer.setPerformative(ACLMessage.INFORM);
            answer.setContent("Réparation effectuée avec succès" );
            myAgent.println("Réparation effectuée avec succès");
            if(userParts.contains(faultyPart))
                myAgent.println("car l'utilisateur avait la bonne piece");
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
