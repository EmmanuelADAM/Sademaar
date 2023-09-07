package agents;

import jade.core.AgentServicesTools;
import jade.gui.SimpleWindow4Agent;

import java.awt.*;

public class DistributorAgent extends RepairAgent {
    @Override
    public void setup(){
        window = new SimpleWindow4Agent(getLocalName(),this);
        window.setBackgroundTextColor(Color.GRAY);
        println("hello, do you want a new object  ?");

        AgentServicesTools.register(this, "repair", "distributor");
        //registration to the yellow pages (Directory Facilitator Agent)
        println("I'm just registered as a Distributor");

        addListeningACFP();
    }

}
