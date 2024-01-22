package behaviours;

import agents.UserAgent;
import data.Part;
import data.Product;
import data.ProductSpec;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**ContractNet Behaviour asking for a part to the SparePartsAgent*/
public class AskForProductBehaviour extends ContractNetInitiator {
    /**user agent making the request*/
    UserAgent a;
    /**part to buy*/
    Product p;
    /**evaluation of the sellers*/
    Map<AID, Integer> evaluationMap;


    public AskForProductBehaviour(Agent a, ACLMessage cfp, Product p) {
        super(a, cfp);
        this.a = (UserAgent)a;
        this.p = p;
        evaluationMap = this.a.getEvaluationMap();
        this.reset(cfp);
    }

    //function triggered by a PROPOSE msg
    // @param propose     the received propose message
    // @param acceptances the list of ACCEPT/REJECT_PROPOSAL to be sent back.
    //                    list that can be modified here or at once when all the messages are received
    @Override
    public void handlePropose(ACLMessage propose, List<ACLMessage> acceptances) {
        Double prix = 0d;
        try {
            prix = (Double)propose.getContentObject();
            a.getWindow().println("%s\tproposes %.2f,\t my eval about it is %d/%d".formatted(propose.getSender().getLocalName(), prix, evaluationMap.get(propose.getSender()), UserAgent.MAXRATING));
      }
        catch (UnreadableException e) { throw new RuntimeException(e);}
    }

    //function triggered by a REFUSE msg
    @Override
    protected void handleRefuse(ACLMessage refuse) {
        a.getWindow().println(refuse.getSender().getLocalName() + "\tdecline ! ");
        a.getWindow().println("\t - " + refuse.getContent());
    }

    /**function triggered when all the responses are received (or after the waiting time)
    *@param responses the list of message sent by the voters
    *@param acceptances the list of answers for each voter*/
    @Override
    protected void handleAllResponses(List<ACLMessage> responses, List<ACLMessage> acceptances) {
        ArrayList<ACLMessage> listeProposals = new ArrayList<>(responses);
        ACLMessage bestProposal = null;
        //difference between dates in minutes
        Double price = 0d;
        double maxPrice = 0.0;
//TODO: some times its bugs with big parts (???) (ex LaveVaisselle-Bp1)
// we keep only the proposals only
        listeProposals.removeIf(v -> v.getPerformative() != ACLMessage.PROPOSE);
        acceptances.clear();
        a.println("-".repeat(30));
        a.println("I have all the responses.. to sum-up : ");

        ACLMessage bestAnswer =null;

        for (ACLMessage proposal : listeProposals) {
            //by default, we build a accept answer for each proposal
            var answer = proposal.createReply();
            answer.setPerformative(ACLMessage.REJECT_PROPOSAL);
            answer.setContent("Sorry, I got a better offer...");
            acceptances.add(answer);
            try {
                price = (Double) proposal.getContentObject();
                if (maxPrice<price){
                    maxPrice = price;
                    bestProposal = proposal;
                    bestAnswer = answer;
                }
            } catch (UnreadableException e) {
                throw new RuntimeException(e);
            }
            a.println("%s\t has proposed %.2f ".formatted(proposal.getSender().getLocalName(), price));
        }
        a.println(".".repeat(30));
        if(bestAnswer!=null) {
            bestAnswer.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            bestAnswer.setContent("I accept the proposal...");
            a.println("I choose the proposal of " + bestProposal.getSender().getLocalName());
        } else {
            a.println("I got no proposal !!!! I'll retry later :-( ...... ");
            a.setCurrentRepair(null);
        }
        a.getWindow().println("-".repeat(40));
    }



    //function triggered by a INFORM msg : a voter accept the result
    // @Override
    protected void handleInform(ACLMessage inform) {
        a.getWindow().println("the sell is accepted by " + inform.getSender().getLocalName());
        Object content = null;
        try { content = inform.getContentObject();}
        catch (UnreadableException e) { a.println("the content is not an object : " + content);throw new RuntimeException(e);}
        Part acceptedPart = null;
        if (content instanceof Part) acceptedPart = (Part) content;
        else a.println("the content is not a part : " + acceptedPart);
        a.setBuyedPart(inform.getSender(), acceptedPart);
    }


}
