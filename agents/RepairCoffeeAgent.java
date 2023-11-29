package agents;

import behaviours.CFRdzVsResponder;
import behaviours.RepairRequestResponder;
import data.ProductType;
import data.StateRepair;
import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.MessageTemplate;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.*;


public class RepairCoffeeAgent extends RepairAgent {
    List<ProductType> specialites;
    Map<AID, List<LocalDateTime>> maprdzvs;

    @Override
    public void setup(){
        specialites = new ArrayList<>();
        List<ProductType> types = new ArrayList<>(List.of(ProductType.values()));
        Collections.shuffle(types);
        for(int i=0; i<3*types.size()/4; i++) specialites.add(types.get(i));

        maprdzvs = new HashMap<>();

        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.orange);
        println("hello, my specialities are on these products:" + specialites);
        println("hello, do you want coffee ?");
        println("-".repeat(20));


        //registration to the yellow pages (Directory Facilitator Agent)
        AgentServicesTools.register(this, "repair", "coffee");
        println("I'm just registered as a repair-coffee");

        addBehaviour(new CFRdzVsResponder(this, MessageTemplate.MatchConversationId(StateRepair.Ask4RdzVs.toString())));

    }

    public List<ProductType> getSpecialites() {
        return specialites ;
    }

    public void addRdzVs(AID aid, LocalDateTime rdzvs){
        maprdzvs.compute(aid, (k, v) -> {
            if (v == null)  v =new ArrayList<LocalDateTime>();
            v.add(rdzvs);
            return v;
        });

        MessageTemplate mt = MessageTemplate.MatchSender(aid);
        addBehaviour(new RepairRequestResponder(this, mt));
    }

    public Map<AID, List<LocalDateTime>> getMaprdzvs() {
        return maprdzvs;
    }
}
