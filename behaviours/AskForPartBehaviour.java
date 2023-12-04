package behaviours;

import agents.UserAgent;
import data.Part;
import data.ProductImage;
import data.RendezVs;
import data.StateRepair;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AskForPartBehaviour extends ContractNetInitiator {
    UserAgent a;
    Part p;
    Map<AID, Integer> evaluationMap;
    ProductImage pi;

    public AskForPartBehaviour(Agent a, ACLMessage cfp, Part p) {
        super(a, cfp);
        this.a = (UserAgent)a;
        this.p = p;
        evaluationMap = this.a.getEvaluationMap();
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
        var currentDate = LocalDateTime.now();
        //difference between dates in minutes
        Double price = 0d;
        var patienceHours = a.getPatience() *24;
        var lastPossibleDate   = currentDate.plusDays(a.getPatience());



        double pref = 0.0;
        double bestPref = Double.MAX_VALUE;
        double prefPrice = 0.0;
        double prefEval = 0.0;
        //we keep only the proposals only
        listeProposals.removeIf(v -> v.getPerformative() != ACLMessage.PROPOSE);
        acceptances.clear();
        a.println("-".repeat(30));
        a.println("I have all the responses.. to sum-up : ");

        ACLMessage bestAnswer =null;

        LocalDateTime creneau = null;
        for (ACLMessage proposal : listeProposals) {
            //by default, we build a accept answer for each proposal
            var answer = proposal.createReply();
            answer.setPerformative(ACLMessage.REJECT_PROPOSAL);
            answer.setContent("This rdz-vs doesn't with my agenda...");
            acceptances.add(answer);
            try {
                price = (Double) proposal.getContentObject();
                //TODO:price selon le prix standard de l'objet
                prefEval = Math.max(0, 1. - (double) evaluationMap.get(proposal.getSender()) /UserAgent.MAXRATING);
                pref =  prefDate*a.getCoefDate() + prefEval*a.getCoefEvaluation();
                if (pref<bestPref){
                    bestPref = pref;
                    bestProposal = proposal;
                    bestAnswer = answer;
                }
            } catch (UnreadableException e) {
                throw new RuntimeException(e);
            }
            a.println("%s\t has proposed %s ".formatted(proposal.getSender().getLocalName(), creneau.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm"))));
            if (pref!=Double.MAX_VALUE) a.println("\t prefDate=%.2f, prefEval=%.2f, pref=%.2f".formatted(prefDate, prefEval, pref));
        }
        a.println(".".repeat(30));
        if(bestAnswer!=null) {
            bestAnswer.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            bestAnswer.setContent("I accept the rdz-vs...");
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
        a.getWindow().println("the rdz-vs is accepted by " + inform.getSender().getLocalName());
        LocalDateTime dateRdzVs = null;
        try { dateRdzVs = (LocalDateTime) inform.getContentObject();}
        catch (UnreadableException e) { throw new RuntimeException(e);}
        RendezVs rdzvs = new RendezVs(dateRdzVs, myAgent.getAID(), inform.getSender(),   pi.getP(), StateRepair.RdzVs);
        a.addRdzVs(rdzvs);
    }


}
