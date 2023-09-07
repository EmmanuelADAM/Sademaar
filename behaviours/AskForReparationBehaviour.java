package behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import agents.UserAgent;

import java.util.ArrayList;
import java.util.List;


public class AskForReparationBehaviour extends ContractNetInitiator {
    UserAgent a;
    public AskForReparationBehaviour(Agent a, ACLMessage cfp) {
        super(a, cfp);
        this.a = (UserAgent)a;
    }

    //function triggered by a PROPOSE msg
    // @param propose     the received propose message
    // @param acceptances the list of ACCEPT/REJECT_PROPOSAL to be sent back.
    //                    list that can be modified here or at once when all the messages are received
    @Override
    public void handlePropose(ACLMessage propose, List<ACLMessage> acceptances) {
        a.getWindow().println("Agent %s proposes %s ".formatted(propose.getSender().getLocalName(), propose.getContent()));
    }

    //function triggered by a REFUSE msg
    @Override
    protected void handleRefuse(ACLMessage refuse) {
        a.getWindow().println("REFUSE ! I received a refuse from " + refuse.getSender().getLocalName());
    }

    //function triggered when all the responses are received (or after the waiting time)
    //@param theirVotes the list of message sent by the voters
    //@param myAnswers the list of answers for each voter
    @Override
    protected void handleAllResponses(List<ACLMessage> responses, List<ACLMessage> acceptances) {
        ArrayList<ACLMessage> listeProposals = new ArrayList<>(responses);
        //we keep only the proposals only
        listeProposals.removeIf(v -> v.getPerformative() != ACLMessage.PROPOSE);
        acceptances.clear();

        ACLMessage bestProposal =null;
        ACLMessage bestAnswer =null;
        var bestPrice = Integer.MAX_VALUE;

        for (ACLMessage proposal : listeProposals) {
            //by default, we build a accept answer for each proposal
            var answer = proposal.createReply();
            answer.setPerformative(ACLMessage.REJECT_PROPOSAL);
            acceptances.add(answer);
            var content = Integer.parseInt(proposal.getContent());
            a.getWindow().println(proposal.getSender().getLocalName() + " has proposed " + content);
            if (content<bestPrice){
                bestPrice = content;
                bestProposal = proposal;
                bestAnswer = answer;
            }

        }

        if (bestProposal !=null)
        {
            bestProposal.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            a.getWindow().println("I choose the proposal of " + bestProposal.getSender().getLocalName());
        }

        a.getWindow().println("-".repeat(40));

    }

    //function triggered by a INFORM msg : a voter accept the result
    // @Override
    protected void handleInform(ACLMessage inform) {
        a.getWindow().println("the vote is accepted by " + inform.getSender().getLocalName());
    }


}
