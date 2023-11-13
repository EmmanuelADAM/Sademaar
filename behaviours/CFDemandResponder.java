package behaviours;

import agents.RepairCoffeeAgent;
import data.Product;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CFDemandResponder extends ContractNetResponder {
    Product product;
    int userLevel;
    RepairCoffeeAgent myAgent;
    LocalDateTime rdzvs;
    public CFDemandResponder(RepairCoffeeAgent a, MessageTemplate mt) {
        super(a, mt);myAgent = a;
    }
    //function triggered by a PROPOSE msg : send back the ranking
    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        myAgent.println("~".repeat(40));
        Object[]tabContent = null;
        try {  tabContent = (Object[])cfp.getContentObject();
        } catch (UnreadableException e) { throw new RuntimeException(e);}
        product = (Product)tabContent[0];
        userLevel = (int)tabContent[1];
        myAgent.println("%s  has a problem with a %s".formatted(cfp.getSender().getLocalName(), product.getSpec()));
        myAgent.println("its level of expertise with this object is of %d/5".formatted(userLevel));
        ACLMessage answer = cfp.createReply();
        try {
            if(myAgent.getSpecialites().contains(product.getSpec().getType())) {
                rdzvs = LocalDateTime.now();
                LocalDate date = LocalDate.now();
                Random hasard = new Random();
                date = date.plusDays(hasard.nextInt(15));
                rdzvs = LocalDateTime.of(date, LocalTime.of(hasard.nextInt(8, 19), 0));
                myAgent.println("I propose a rendez-vous at:" + rdzvs.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm")));
                answer.setContentObject(rdzvs);
            }
            else {
                answer.setPerformative(ACLMessage.REFUSE);
                answer.setContent("I'm not specialized in this type of object ("+product.getSpec().getType()+")");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(myAgent.getSpecialites().contains(product.getSpec().getType())) {
            answer.setPerformative(ACLMessage.PROPOSE);
            myAgent.println("I propose a rdz-vs : " + rdzvs.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm")));

        }
        else answer.setPerformative(ACLMessage.REFUSE);

//              String choice = makeItsChoice(cfp.getContent());
        return answer;
    }


    //function triggered by a ACCEPT_PROPOSAL msg : the polling station agent  accept the vote
    //@param cfp : the initial cfp message
    //@param propose : the proposal I sent
    //@param accept : the acceptation sent by the auctioneer
    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        myAgent.println("=".repeat(15));
        myAgent.println(" I proposed a rdz-vs : " + rdzvs.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm")));
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
        myAgent.println(cfp.getSender().getLocalName() + " asked to repair elt no " + product);
        myAgent.println(" I proposed a rdz-vs:" + rdzvs.format(DateTimeFormatter.ofPattern("dd/MM/yy, HH:mm")));
        myAgent.println(cfp.getSender().getLocalName() + " refused ! with this message: " + reject.getContent());
    }


}
