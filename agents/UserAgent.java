package agents;

import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.domain.FIPANames;
import jade.gui.AgentWindowed;
import jade.gui.GuiEvent;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.ACLMessage;
import behaviours.AskForReparationBehaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UserAgent extends AgentWindowed {
    /**
     * skill in "repairing" from 0 (not understand) to 5 (repairman like)
     */
    int skill;
    /**
     * list of potential helpers (agent registered in the type of service "repair"
     */
    List<AID> helpers;

    @Override
    public void setup() {
        this.window = new SimpleWindow4Agent(getLocalName(), this);
        window.setButtonActivated(true);

        skill = (int) (Math.random() * 6);
        println("hello, I have a repair skill of " + skill + " / 3");
        helpers = new ArrayList<>(10);
    }

    @Override
    public void onGuiEvent(GuiEvent evt) {
        //I suppose there is only one type of event, clic on go
        //search about repairing agents
        var tabAIDs = AgentServicesTools.searchAgents(this, "repair", null);
        helpers.addAll(Arrays.stream(tabAIDs).toList());

        println("-".repeat(30));
        for (AID aid : helpers)
            println("found this agent that could help me : " + aid.getLocalName());
        println("-".repeat(30));

        addCFP();
    }

    /**add a CFP from user to list of helpers*/
    private void addCFP(){
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setConversationId("id");
        int randint = (int)(Math.random()*3);
        msg.setContent(String.valueOf(randint));

        msg.addReceivers(helpers.toArray(AID[]::new));
        println("-".repeat(40));

        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));


        var askReparation = new AskForReparationBehaviour(this, msg);
        addBehaviour(askReparation);
    }

    /**here we simplify the scenario. A breakdown is about 1 elt..
     * so whe choose a no between 1 to 4 and ask who can repair at at wich cost.*/
    private void breakdown(){

    }

}
