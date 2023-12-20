package agents;

import behaviours.AskForPartBehaviour;
import behaviours.CFPartResponder;
import behaviours.CFRdzVsResponder;
import data.Part;
import data.ProductSpec;
import data.StateRepair;
import jade.core.AgentServicesTools;
import jade.gui.AgentWindowed;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class SparePartsStoreAgent extends RepairAgent {


    List<Part> parts;
    @Override
    public void setup(){
        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.cyan);
        println("Hello, do you want a piece of something ?");

        //registration to the yellow pages (Directory Facilitator Agent)
        AgentServicesTools.register(this, "repair", "SparePartsStore");
        println("I'm just registered as a Spare Parts Store");

        parts = new ArrayList<>();
        var l = ProductSpec.getListSmallParts();
        for(var p:l)
            if(Math.random()<0.35) parts.add(new Part(p, 0.7+Math.random()*0.6));
        l = ProductSpec.getListBigParts();
        for(var p:l)
            if(Math.random()<0.35) parts.add(new Part(p, 0.7+Math.random()*0.6));
        // I have my own prices

        println("I have " + parts.size() + " references of spare parts ");
        println("Here are the spare parts I can sell : ");
        for(var p:parts)
            println("\t%s".formatted(p));

        addBehaviour(new CFPartResponder(this, MessageTemplate.MatchConversationId(StateRepair.Ask4Parts.toString())));

    }


    public List<Part> getParts() {
        return parts;
    }

}
