package agents;

import jade.core.AgentServicesTools;
import jade.gui.SimpleWindow4Agent;

import java.awt.*;

public class SparePartsStoreAgent extends RepairAgent {
    @Override
    public void setup(){
        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.cyan);
        println("hello, do you want a piece of something ?");

        //registration to the yellow pages (Directory Facilitator Agent)
        AgentServicesTools.register(this, "repair", "SparePartsStore");
        println("I'm just registered as a Spare Parts Store");

        addListeningACFP();
    }

}
