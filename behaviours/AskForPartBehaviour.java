package behaviours;

import agents.UserAgent;
import data.Part;
import data.ProductSpec;
import data.StateRepair;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**ContractNet Behaviour asking for a part to the SparePartsAgent*/
public class AskForPartBehaviour extends ContractNetInitiator {
    /**user agent making the request*/
    UserAgent a;
    /**part to buy*/
    Part p;
    /**evaluation of the sellers*/
    Map<AID, Integer> evaluationMap;
    /**price of the chosen part*/
    Double price = -1d;



    public AskForPartBehaviour(Agent a, ACLMessage cfp, Part p) {
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
            a.getWindow().println("%s\tpropose %.2f,\t mon évaluation de cet agent est de  %d/%d".formatted(propose.getSender().getLocalName(), prix, evaluationMap.get(propose.getSender()), UserAgent.MAXRATING));
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
        price = 0d;
        double pref = 0.0;
        double bestPref = Double.MAX_VALUE;
        double prefPrice = 0.0;
        double prefEval = 0.0;
        double maxPrice = 0.0;
        List<Part> list = null;
        if(p.serialNumber()/1000==0)  list = ProductSpec.getListSmallParts();
        else list = ProductSpec.getListBigParts();
        int index = list.indexOf(p);
        maxPrice = list.get(index).price();
        // we keep only the proposals only
        listeProposals.removeIf(v -> v.getPerformative() != ACLMessage.PROPOSE);
        acceptances.clear();
        a.println("-".repeat(30));
        a.println("J'ai toutes les propositions, pour résumer : ");

        ACLMessage bestAnswer =null;

        for (ACLMessage proposal : listeProposals) {
            //by default, we build a accept answer for each proposal
            var answer = proposal.createReply();
            answer.setPerformative(ACLMessage.REJECT_PROPOSAL);
            answer.setContent("J'ai déjà une offre...");
            acceptances.add(answer);
            try {
                price = (Double) proposal.getContentObject();
                prefPrice = price / maxPrice;
                prefEval = Math.max(0, 1. - (double) evaluationMap.get(proposal.getSender()) /UserAgent.MAXRATING);
                pref =  prefPrice*a.getCoefPrice() + prefEval*a.getCoefEvaluation();
                if (pref<bestPref){
                    bestPref = pref;
                    bestProposal = proposal;
                    bestAnswer = answer;
                }
            } catch (UnreadableException e) {
                throw new RuntimeException(e);
            }
            a.println("%s\t a proposé %.2f ".formatted(proposal.getSender().getLocalName(), price));
            if (pref!=Double.MAX_VALUE) a.println("\t prix ressenti=%.2f, confiance ressentie=%.2f, valeur=%.2f".formatted(prefPrice, prefEval, pref));
        }
        a.println(".".repeat(30));
        if(bestAnswer!=null) {
            bestAnswer.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            bestAnswer.setContent("J'accepte la proposition...");
            a.println("Je choisis la proposition de " + bestProposal.getSender().getLocalName());
        } else {
            a.println("Je n'ai reçu aucune proposition !!!! Je retenterai :-( ...... ");
            a.setCurrentRepair(null);
        }
        a.getWindow().println("-".repeat(40));
    }



    //function triggered by a INFORM msg : a voter accept the result
    // @Override
    protected void handleInform(ACLMessage inform) {
        a.getWindow().println("La vente est acceptée par " + inform.getSender().getLocalName());
        Object content = null;
        try { content = inform.getContentObject();}
        catch (UnreadableException e) { a.println("the content is not an object : " + content);throw new RuntimeException(e);}
        Part acceptedPart = null;
        if (content instanceof Part) acceptedPart = (Part) content;
        else a.println("the content is not a part : " + acceptedPart);
        a.setBuyedPart(inform.getSender(), acceptedPart, price);
    }


}
