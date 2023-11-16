package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import agents.UserAgent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class AskForRdzVsBehaviour extends ContractNetInitiator {
    UserAgent a;
    public AskForRdzVsBehaviour(Agent a, ACLMessage cfp) {
        super(a, cfp);
        this.a = (UserAgent)a;
    }

    //function triggered by a PROPOSE msg
    // @param propose     the received propose message
    // @param acceptances the list of ACCEPT/REJECT_PROPOSAL to be sent back.
    //                    list that can be modified here or at once when all the messages are received
    @Override
    public void handlePropose(ACLMessage propose, List<ACLMessage> acceptances) {
        LocalDateTime creneau = null;
        try { creneau = (LocalDateTime)propose.getContentObject(); }
        catch (UnreadableException e) { throw new RuntimeException(e);}
        a.getWindow().println("%s\tproposes %s ".formatted(propose.getSender().getLocalName(), creneau.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm"))));
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
        var date = LocalDateTime.of(9999, 12, 31, 23, 59);
        var currentDate = LocalDateTime.now();
        var currentSecond = currentDate.toEpochSecond(ZoneOffset.UTC);
        //difference between dates in minutes
        var dateGap = date.toEpochSecond(ZoneOffset.UTC) - currentSecond;
        var patienceSec = a.getPatience() * 60*60*24; //(86400)
        var lastPossibleDate   = currentDate.plusDays(a.getPatience());
        var evaluationMap = a.getEvaluationMap();



        double pref = 0.0;
        double bestPref = Double.MAX_VALUE;
        double prefDate = 0.0;
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
                creneau = (LocalDateTime) proposal.getContentObject();
                if (creneau.isBefore(lastPossibleDate))
                {
                    prefDate = (double) (creneau.toEpochSecond(ZoneOffset.UTC) - currentSecond) /patienceSec;
                    prefEval = 1. - (double) evaluationMap.get(proposal.getSender()) /UserAgent.MAXRATING;
                    pref =  prefDate*a.getCoefDate() + prefEval*a.getCoefEvaluation();
                    if (pref<bestPref){
                        bestPref = pref;
                        bestProposal = proposal;
                        bestAnswer = answer;
                    }
                }
                else prefDate = prefEval = pref = Double.MAX_VALUE;
            } catch (UnreadableException e) {
                throw new RuntimeException(e);
            }
            a.println("%s\t has proposed %s ".formatted(proposal.getSender().getLocalName(), creneau.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm"))));
            a.println("\t prefDate=%.2f, prefEval=%.2f, pref=%.2f".formatted(prefDate, prefEval, pref));
        }
        a.println(".".repeat(30));
        if(bestAnswer!=null) {
            bestAnswer.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            bestAnswer.setContent("I accept the rdz-vs...");
            a.println("I choose the proposal of " + bestProposal.getSender().getLocalName());
        } else {
            a.println("I got no proposal !!!! I'll retry later :-( ...... ");
        }
        a.getWindow().println("-".repeat(40));
    }



    //function triggered by a INFORM msg : a voter accept the result
    // @Override
    protected void handleInform(ACLMessage inform) {
        a.getWindow().println("the rdz-vs is accepted by " + inform.getSender().getLocalName());
    }


}
