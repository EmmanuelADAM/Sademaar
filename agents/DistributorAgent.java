package agents;

import behaviours.CFPartResponder;
import behaviours.CFProductResponder;
import data.Product;
import data.StateRepair;
import jade.core.AgentServicesTools;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.MessageTemplate;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DistributorAgent extends RepairAgent {
    List<Product> productList;

    @Override
    public void setup(){
        window = new SimpleWindow4Agent(getLocalName(),this);
        window.setBackgroundTextColor(Color.LIGHT_GRAY);
        println("Bonjour");
        AgentServicesTools.register(this, "repair", "distributor");
        //registration to the yellow pages (Directory Facilitator Agent)
        println("Je suis enregistré en tant que distributeur");
        Random hasard = new Random();
        coord = new Point(hasard.nextInt(100), hasard.nextInt(100));
        println("Je suis au point (%d,%d)".formatted(coord.x, coord.y) );
        println("Bonjour, voulez-vous une pièce de rechange  ?");

        productList = new ArrayList<>();
        var l = Product.getListProducts() ;
        for(var p:l)    
            if(Math.random()<0.35) productList.add(p);
        // I have my own prices
        for(var p:productList)p.setPrice(p.getPrice()*(0.7+Math.random()*0.6));

        println("J'ai " + productList.size() + " références de produits. ");
        println("Voici la liste : ");
        for(var p:productList)
            println("\t%s pour %.2f€".formatted(p.getName(), p.getPrice()));

        addBehaviour(new CFProductResponder(this, MessageTemplate.MatchConversationId(StateRepair.NeedNewProduct.toString())));

    }



    public List<Product> getProductList() {
        return productList;
    }
}
