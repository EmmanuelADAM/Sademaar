package behaviours;

import agents.RepairAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.util.Random;

public class RepairRequestResponder extends AchieveREResponder {
    RepairAgent myAgent;

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
        myAgent.println("received  " + request.getContent());
        ACLMessage answer = request.createReply();
        //parfois l'agent choisi de refuser la demande
        if (hasard.nextBoolean()) {
            answer.setPerformative(ACLMessage.AGREE);
            myAgent.println("I'm ok to answer...");
            myAgent.println("-".repeat(40));
        } else {
            answer.setPerformative(ACLMessage.REFUSE);
            myAgent.println("I refuse the request !");
            myAgent.println("(o)".repeat(20));
        }
        return answer;
    }

    //Function used to return a result by a message
    //param : request = initial initial request, response = agreement I just sent
    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
        String content = request.getContent();

        ACLMessage answer = request.createReply();
        answer.setPerformative(ACLMessage.INFORM);
        answer.setContent("result = ok" );
        myAgent.println("I send: ok");
        myAgent.println("(o)".repeat(20));
        return answer;
    }
}
