package agents;

import behaviours.CFDemandResponder;
import data.ProductType;
import jade.core.AgentServicesTools;
import jade.gui.AgentWindowed;
import jade.gui.GuiAgent;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.MessageTemplate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RepairCoffeeAgent extends RepairAgent {
    List<ProductType> specialites;
    @Override
    public void setup(){
        specialites = new ArrayList<>();
        List<ProductType> types = new ArrayList<>(List.of(ProductType.values()));
        Collections.shuffle(types);
        for(int i=0; i<3*types.size()/4; i++) specialites.add(types.get(i));


        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.orange);
        println("hello, my specialities are on these products:" + specialites);
        println("hello, do you want coffee ?");
        println("-".repeat(20));


        //registration to the yellow pages (Directory Facilitator Agent)
        AgentServicesTools.register(this, "repair", "coffee");
        println("I'm just registered as a repair-coffee");

        addBehaviour(new CFDemandResponder(this, MessageTemplate.MatchConversationId("ControlObject")));

    }

    public List<ProductType> getSpecialites() {
        return specialites;
    }
}
