package behaviours;

import agents.RepairAgent;
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
import java.util.Collections;

/**Contract net responder for a CFP msg regarding a part   */
public class CFPartResponder extends ContractNetResponder {
    Part part;
    SparePartsStoreAgent myAgent;
    public CFPartResponder(SparePartsStoreAgent a, MessageTemplate mt) {
        super(a, mt);
        myAgent = a;
    }
    //function triggered by a PROPOSE msg : send back the ranking
    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        myAgent.println("~".repeat(40));
        try {  part = (Part)cfp.getContentObject();
        } catch (UnreadableException e) { throw new RuntimeException(e);}
        myAgent.println("%s demande ce type de pièce '%s' ".formatted(cfp.getSender().getLocalName(), part.name()));
        ACLMessage answer = cfp.createReply();

        var parts = myAgent.getParts();
        int index = parts.indexOf(part);
        if ( index == -1) {
            var content = "Je ne vends pas cette pièce..";
            myAgent.println(content);
            answer.setContent(content);
            answer.setPerformative(ACLMessage.REFUSE);
            throw new RefuseException(answer);
        }
        answer.setPerformative(ACLMessage.PROPOSE);
        try { answer.setContentObject(parts.get(index).price()); }
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
        myAgent.println("=".repeat(10));
        myAgent.println("PROPOSITION ACCEPTEE");
        myAgent.println(cfp.getSender().getLocalName() + " a demandé cet élément " + part.name());
        try {
            myAgent.println(" J'ai proposé un prix de %.2f €".formatted((Double)propose.getContentObject()));
            msg.setContentObject(part); }
        catch (UnreadableException|IOException e) { throw new RuntimeException(e); }
        myAgent.println(cfp.getSender().getLocalName() + " a accepté ma proposition et a retourné :  " + accept.getContent());
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
        myAgent.println("PROPOSITION REJETEE");
        myAgent.println(cfp.getSender().getLocalName() + " a demandé cet élément " + part.name());
        try { myAgent.println(" J'ai proposé un prix de %.2f €".formatted((Double)propose.getContentObject()));}
        catch (UnreadableException e) { throw new RuntimeException(e); }
        myAgent.println(cfp.getSender().getLocalName() + " a refusé avec ce message : " + reject.getContent());
    }
}
