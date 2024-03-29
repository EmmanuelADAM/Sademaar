package behaviours;

import data.ProductImage;
import data.RepairState;
import data.StateRepair;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import agents.UserAgent;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AskForRdzVsBehaviour extends ContractNetInitiator {
    UserAgent a;
    LocalDateTime startingDate;
    Map<AID, Integer> evaluationMap;
    ProductImage pi;

    public AskForRdzVsBehaviour(Agent a, ACLMessage cfp, ProductImage pi) {
        super(a, cfp);
        this.a = (UserAgent)a;
        this.pi = pi;
        startingDate = LocalDateTime.now(ZoneOffset.UTC);
        evaluationMap = this.a.getEvaluationMap();
    }

    //function triggered by a PROPOSE msg
    // @param propose     the received propose message
    // @param acceptances the list of ACCEPT/REJECT_PROPOSAL to be sent back.
    //                    list that can be modified here or at once when all the messages are received
    @Override
    public void handlePropose(ACLMessage propose, List<ACLMessage> acceptances) {
        LocalDateTime creneau = null;
        try {
            Object[]tabContent = (Object[])propose.getContentObject();
            creneau = (LocalDateTime)tabContent[0];
            var coord = (Point)tabContent[1];
            a.getWindow().println("%s propose %s,\n   mon degré de confiance enver lui est de %d/%d,\n    il est en (%d,%d)".formatted(propose.getSender().getLocalName(), creneau.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm")), evaluationMap.get(propose.getSender()), UserAgent.MAXRATING, coord.x, coord.y));
//            var duration = Duration.between(startingDate, creneau);
//            a.getWindow().println("\t in %d hours..".formatted(duration.toHours()));
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
        Duration dateGap = null;
        var currentRepair = a.getCurrentRepair();
        var patienceHours = currentRepair.getUserPatience() *24;
        var lastPossibleDate   = currentDate.plusDays(currentRepair.getUserPatience());

        double pref = 0.0;
        double bestPref = Double.MAX_VALUE;
        double prefDate = 0.0;
        double prefEval = 0.0;
        //for this simulation, the position are in a square 100x100
        double maxDist = 150;
        double prefDist = 0.0;
        Point coord;
        //we keep only the proposals only
        listeProposals.removeIf(v -> v.getPerformative() != ACLMessage.PROPOSE);
        acceptances.clear();
        a.println("-".repeat(30));
        a.println("J'ai reçu toutes les réponses... Pour résumer : ");

        ACLMessage bestAnswer =null;

        LocalDateTime creneau = null;
        for (ACLMessage proposal : listeProposals) {
            //by default, we build a accept answer for each proposal
            var answer = proposal.createReply();
            answer.setPerformative(ACLMessage.REJECT_PROPOSAL);
            answer.setContent("Ce rdz-vs ne coïncide pas avec mon agenda...");
            acceptances.add(answer);
            try {
                Object[]tabContent = (Object[])proposal.getContentObject();
                creneau = (LocalDateTime)tabContent[0];
                coord = (Point)tabContent[1];
                if (creneau.isBefore(lastPossibleDate))
                {
                    dateGap = Duration.between(startingDate, creneau);
                    prefDate = (double)dateGap.toHours()  / patienceHours;
                    prefEval = Math.max(0, 1. - (double) evaluationMap.get(proposal.getSender()) /UserAgent.MAXRATING);
                    prefDist = a.getCoord().distance(coord)/maxDist;
                    pref =  prefDate*a.getCoefDate() + prefEval*a.getCoefEvaluation() + prefDist*a.getCoefDist();
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
            a.println("%s\t a proposé %s ".formatted(proposal.getSender().getLocalName(), creneau.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm"))));
            if (pref!=Double.MAX_VALUE)
                a.println("\t intérêt pour la date=%.2f, intérêt pour la confiance =%.2f, intérêt pour la distance =%.2f, pref=%.2f".formatted(prefDate, prefEval, prefDist, pref));
        }
        a.println(".".repeat(30));
        if(bestAnswer!=null) {
            bestAnswer.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            bestAnswer.setContent("J'accepte le rdz-vs...");
            a.println("Je choisis la proposition de " + bestProposal.getSender().getLocalName());
        } else {
            a.println("Je n'ai eu aucune proposition intéressante !!! Je retenterai plus tard :-( ...... ");
            RepairState repairState = new RepairState(null, myAgent.getAID(), null,   pi.getP(), StateRepair.NoRdzVs);
            a.addRepairState(repairState);
        }
        a.getWindow().println("-".repeat(40));
    }



    //function triggered by a INFORM msg : a voter accept the result
    // @Override
    protected void handleInform(ACLMessage inform) {
        a.getWindow().println("Le rendez-vous est accepté par " + inform.getSender().getLocalName());
        LocalDateTime dateRdzVs = null;
        try { dateRdzVs = (LocalDateTime) inform.getContentObject();}
        catch (UnreadableException e) { throw new RuntimeException(e);}
        RepairState repairState = new RepairState(dateRdzVs, myAgent.getAID(), inform.getSender(),   pi.getP(), StateRepair.RdzVs);
        a.addRepairState(repairState);
    }


}
