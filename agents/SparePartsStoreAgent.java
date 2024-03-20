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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SparePartsStoreAgent extends RepairAgent {


    List<Part> parts;
    @Override
    public void setup(){
        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.cyan);
        println("Bonjour");
        //registration to the yellow pages (Directory Facilitator Agent)
        AgentServicesTools.register(this, "repair", "SparePartsStore");
        println("Je suis enregistré en tant que vendeur de pièces détâchées");
        Random hasard = new Random();
        coord = new Point(hasard.nextInt(100), hasard.nextInt(100));
        println("Je suis au point (%d,%d)".formatted(coord.x, coord.y) );
        println("Bonjour, voulez-vous une pièce de rechange  ?");

        parts = new ArrayList<>();
        var l = ProductSpec.getListSmallParts();
        for(var p:l)
            if(Math.random()<0.35) parts.add(new Part(p, 0.7+Math.random()*0.6));
        l = ProductSpec.getListBigParts();
        for(var p:l)
            if(Math.random()<0.35) parts.add(new Part(p, 0.7+Math.random()*0.6));
        // I have my own prices

        println("J'ai " + parts.size() + " références de pièces détachées.");
        println("Voici la liste : ");
        for(var p:parts)
            println("\t%s".formatted(p));

        addBehaviour(new CFPartResponder(this, MessageTemplate.MatchConversationId(StateRepair.Ask4Parts.toString())));

    }


    public List<Part> getParts() {
        return parts;
    }

}
