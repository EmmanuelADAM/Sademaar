package agents;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.gui.AgentWindowed;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class RepairAgent extends AgentWindowed {


    void addListeningACFP()
    {

        MessageTemplate model = MessageTemplate.MatchConversationId("id");

        ContractNetResponder behaviourRepair = new ContractNetResponder(this, model) {

            //function triggered by a PROPOSE msg : send back the ranking
            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
                println("~".repeat(40));
                int hasard = (int)(Math.random()*8) - 4;
                println(cfp.getSender().getLocalName() + " proposes this options: " + cfp.getContent());
                ACLMessage answer = cfp.createReply();
                if(hasard<=0 )answer.setPerformative(ACLMessage.REFUSE);
                else answer.setPerformative(ACLMessage.PROPOSE);

//                String choice = makeItsChoice(cfp.getContent());
                answer.setContent(String.valueOf(hasard));
                return answer;
            }
            

            //function triggered by a ACCEPT_PROPOSAL msg : the polling station agent  accept the vote
            //@param cfp : the initial cfp message
            //@param propose : the proposal I sent
            //@param accept : the acceptation sent by the auctioneer
            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                println("=".repeat(15));
                println(" I proposed " + propose.getContent());
                println(cfp.getSender().getLocalName() + " accepted my poposal and sent the result:  " + accept.getContent());
                ACLMessage msg = accept.createReply();
                msg.setPerformative(ACLMessage.INFORM);
                msg.setContent("ok !");
                return msg;
            }

            //function triggered by a REJECT_PROPOSAL msg : the auctioneer rejected my vote !
            //@param cfp : the initial cfp message
            //@param propose : the proposal I sent
            //@param accept : the reject sent by the auctioneer
            @Override
            protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
                println("=".repeat(10));
                println("PROPOSAL REJECTED");
                println(cfp.getSender().getLocalName() + " asked to repair elt no " + cfp.getContent());
                println(" I proposed " + propose.getContent());
                println(cfp.getSender().getLocalName() + " refused ! with this message: " + reject.getContent());
            }


        };

        addBehaviour(behaviourRepair);
    }

}
