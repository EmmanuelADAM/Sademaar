package agents;

import data.Product;
import data.ProductImage;
import gui.UserGui;
import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.domain.FIPANames;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import behaviours.AskForReparationBehaviour;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.*;

public class UserAgent extends GuiAgent {
    /**
     * skill in "repairing" from 0 (not understand) to 5 (repairman like)
     */
    int skill;
    /**
     * list of potential helpers (agent registered in the type of service "repair"
     */
    List<AID> helpers;

    List<ProductImage> produits;

    UserGui window;
    @Override
    public void setup() {
        Random hasard = new Random();
        var listProducts = Product.getListProducts();
        Collections.shuffle(listProducts);
        int nbProducts = hasard.nextInt(1,Product.NB_PRODS/20);
        produits = new ArrayList<>(nbProducts);
        for(int i=0; i<nbProducts; i++){
            produits.add(new ProductImage(listProducts.get(i)));
        }

        this.window = UserGui.createUserGui(getLocalName(), this);//SimpleWindow4Agent(getLocalName(), this);
        println("Hello!");
        println("Choisissez un produit à réparer, votre niveau d'expérience dans la réparation de ce produit et envoyer un appel à l'aide");
        println("Puis choisissez une option de répation qui vous est proposé");
        println("-".repeat(30));
        helpers = new ArrayList<>(10);
    }

    @Override
    public void onGuiEvent(GuiEvent evt) {
        //I suppose there is only one type of event, clic on go
        //search about repairing agents
        helpers.clear();
        var tabAIDs = AgentServicesTools.searchAgents(this, "repair", "coffee");
        helpers.addAll(Arrays.stream(tabAIDs).toList());

        println("-".repeat(30));

        ProductImage pi = window.getProduct();
        println("I want to repair this : " + pi);
        int level = window.getLevel();
        println("My skill to repair this product is of : " + window.getLevel());

        println("-".repeat(30));
        var helpersLocalNames = helpers.stream().map(AID::getLocalName).toList();
        println("found this agent that could help me : " + helpersLocalNames);

        println("-".repeat(30));

        addCFP(pi, level);
    }

    /**add a CFP from user to list of helpers*/
    private void addCFP(ProductImage pi, int level)  {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setConversationId("id");
        try { msg.setContentObject(new Object[]{pi.getP(), level}); }
        catch (IOException e) { throw new RuntimeException(e);}

        msg.addReceivers(helpers.toArray(AID[]::new));
        println("-".repeat(40));

        msg.setConversationId("ControlObject");
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));


        var askReparation = new AskForReparationBehaviour(this, msg);
        addBehaviour(askReparation);
    }

    public void println(String msg)
    {
        window.println(msg);
    }


    public UserGui getWindow(){return window;}

    public List<ProductImage> getProduits() {
        return produits;
    }


    /**here we simplify the scenario. A breakdown is about 1 elt..
     * so whe choose a no between 1 to 4 and ask who can repair at at wich cost.*/
    private void breakdown(){

    }

}
