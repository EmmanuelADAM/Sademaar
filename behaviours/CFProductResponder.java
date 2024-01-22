package behaviours;

import agents.DistributorAgent;
import agents.SparePartsStoreAgent;
import data.Part;
import data.Product;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

import java.io.IOException;

/**Contract net responder for a CFP msg regarding a part   */
public class CFProductResponder extends ContractNetResponder {
    Product product;
    DistributorAgent myAgent;
    public CFProductResponder(DistributorAgent a, MessageTemplate mt) {
        super(a, mt);
        myAgent = a;
    }
    //function triggered by a PROPOSE msg : send back the ranking
    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        myAgent.println("~".repeat(40));
        try {  product = (Product)cfp.getContentObject();
        } catch (UnreadableException e) { throw new RuntimeException(e);}
        myAgent.println("%s ask for this kind of part '%s' ".formatted(cfp.getSender().getLocalName(), product.getName()));
        ACLMessage answer = cfp.createReply();

        var products = myAgent.getProductList();
        int index = products.indexOf(product);
        if ( index == -1) {
            myAgent.println("I don't sell this product");
            answer.setPerformative(ACLMessage.REFUSE);
            throw new RefuseException(answer);
        }
        answer.setPerformative(ACLMessage.PROPOSE);
        try { answer.setContentObject(products.get(index).getPrice()); }
        catch (IOException e) { throw new RuntimeException(e);}

        return answer;
    }


    //function triggered by a ACCEPT_PROPOSAL msg : the polling station agent  accept the vote
    //@param cfp : the initial cfp message
    //@param propose : the proposal I sent
    //@param accept : the acceptation sent by the auctioneer
    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        ACLMessage msg = accept.createReply();
        myAgent.println("=".repeat(15));
        try {
            myAgent.println(" I proposed " + propose.getContentObject());
            msg.setContentObject(product); }
        catch (UnreadableException|IOException e) { throw new RuntimeException(e); }
        myAgent.println(cfp.getSender().getLocalName() + " accepted my proposal and sent the result:  " + accept.getContent());
        msg.setPerformative(ACLMessage.INFORM);
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
