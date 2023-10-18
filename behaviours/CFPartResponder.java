package behaviours;

import agents.RepairAgent;
import data.Product;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

public class CFPartResponder extends ContractNetResponder {
    Product product;
    int userLevel;
    RepairAgent myAgent;
    public CFPartResponder(RepairAgent a, MessageTemplate mt) {
        super(a, mt);
    }
    //function triggered by a PROPOSE msg : send back the ranking
    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        myAgent.println("~".repeat(40));
        int hasard = (int)(Math.random()*8) - 4;
        Object[]tabContent = null;
        try {  tabContent = (Object[])cfp.getContentObject();
        } catch (UnreadableException e) { throw new RuntimeException(e);}
        product = (Product)tabContent[0];
        userLevel = (int)tabContent[1];
        myAgent.println("%s  has a problem with a %s".formatted(cfp.getSender().getLocalName(), product.getSpec()));
        myAgent.println("its level of expertise with this object is of %d/5".formatted(userLevel));

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
        myAgent.println("=".repeat(15));
        myAgent.println(" I proposed " + propose.getContent());
        myAgent.println(cfp.getSender().getLocalName() + " accepted my poposal and sent the result:  " + accept.getContent());
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
        myAgent.println("=".repeat(10));
        myAgent.println("PROPOSAL REJECTED");
        myAgent.println(cfp.getSender().getLocalName() + " asked to repair elt no " + cfp.getContent());
        myAgent.println(" I proposed " + propose.getContent());
        myAgent.println(cfp.getSender().getLocalName() + " refused ! with this message: " + reject.getContent());
    }


}
