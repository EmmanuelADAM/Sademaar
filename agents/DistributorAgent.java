package agents;

import data.Product;
import jade.core.AgentServicesTools;
import jade.gui.SimpleWindow4Agent;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

public class DistributorAgent extends RepairAgent {
    List<Product> productList;

    @Override
    public void setup(){
        window = new SimpleWindow4Agent(getLocalName(),this);
        window.setBackgroundTextColor(Color.LIGHT_GRAY);
        println("hello, do you want a new object  ?");

        AgentServicesTools.register(this, "repair", "distributor");
        //registration to the yellow pages (Directory Facilitator Agent)
        println("I'm just registered as a Distributor");

        productList = new ArrayList<>();
        var l = Product.getListProducts() ;
        for(var p:l)    
            if(Math.random()<0.35) productList.add(p);
        // I have my own prices
        for(var p:productList)p.setPrice(p.getPrice()*(0.7+Math.random()*0.6));

        println("I have " + productList.size() + " references of products ");
        println("Here are the products I can sell : ");
        for(var p:productList)
            println("\t%s pour %.2f€".formatted(p.getName(), p.getPrice()));

    }

}
