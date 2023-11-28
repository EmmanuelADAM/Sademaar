package agents;

import behaviours.RepairRequestInitiator;
import data.*;
import gui.UserGui;
import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.domain.FIPANames;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import behaviours.AskForRdzVsBehaviour;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/** an agent that represent a user
 * - the user choose an object to repair, define its level of expertise regarding it
 * - the user can ask for a rendez-vous to repair coffee agents (it sends the details about the object)
 *  - the choice is based on the date and confidence about the repair coffee
 * - next, the user request for a repair to the repair coffee agent with which it has a rendez-vous
 * */
public class UserAgent extends GuiAgent {
    /**max rating for diverse purpose (user skill, user evaluation of a repair coffee, ...*/
    public static final int MAXRATING = 5;
    /**
     * skill in "repairing" from 0 (not understand) to 5 (repairman like)
     */
    int skill;
    /**
     * list of potential helpers (agent registered in the type of service "repair"
     */
    List<AID> helpers;

    /**data used by the user to represent its products*/
    List<ProductImage> products;
    /**data used by the user to represent its products*/
    List<Repair> repairs;

    Repair currentRepair;

    /**map each aid of a repair agent to its evaluation*/
    Map<AID, Integer> evaluationMap;

    /**importance of the date to take a rdz-vs*/
    double coefDate;
    /**importance of the evaluation to take a rdz-vs*/
    double coefEvaluation;

    /**max days of patience for a rdz-vs*/
    int patience;

    /**gui used by the user agent*/
    UserGui window;
    /**setup of the user agent
     * - build a list with some objects choosed randomly
     * - define randomly the different coef (patience, evaluation)
     * - build the gui*/
    @Override
    public void setup() {
        //rchoose randomly some products among the existent
        Random hasard = new Random();
        //add some products choosen randomly in the list Product.getListProducts()
        products = new ArrayList<>();
        int nbTypeOfProducts = ProductType.values().length;
        int nbPoductsByType = Product.NB_PRODS / nbTypeOfProducts;
        var existingProducts = Product.getListProducts();
        //add products
        for(int i=0; i<nbTypeOfProducts; i++)
            if(hasard.nextBoolean())
                products.add(new ProductImage(existingProducts.get(hasard.nextInt(nbPoductsByType) + (i*nbPoductsByType))));
        //we need at least one product
        if(products.isEmpty())  products.add(new ProductImage(existingProducts.get(hasard.nextInt(nbPoductsByType*nbTypeOfProducts))));

        //init the coefs
        evaluationMap = new HashMap<>();
        repairs = new ArrayList<>();
        coefDate = ((int)(hasard.nextDouble()*10)+1)/10.0;
        coefEvaluation = ((int)(hasard.nextDouble()*10)+1)/10.0;
        patience = hasard.nextInt(3,21);
        // create the window
        this.window = UserGui.createUserGui(getLocalName(), this);//SimpleWindow4Agent(getLocalName(), this);
        println("Hello!");
        println("Choisissez un produit à réparer, votre niveau d'expérience dans la réparation de ce produit et envoyer un appel à l'aide");
        println("Puis choisissez une option de réparation qui vous est proposé");
        println("-".repeat(30));
        helpers = new ArrayList<>(10);
    }

    @Override
    /**on event from the gui :
     * - ask for a rendez-vous*/
    public void onGuiEvent(GuiEvent evt) {
        ProductImage pi = null;
        if(currentRepair==null) {
            pi = window.getProduct();
            currentRepair = new Repair(getAID(), pi);
            repairs.add(currentRepair);
            window.addRepair(currentRepair);
            println("I want to repair this : " + pi);
        }

        //for the moment, I suppose there is only one type of event, click on go
        switch(currentRepair.getState()){
            case Ask4RdzVs -> ask4RdzVs(pi);
            case RdzVs ->  ask4Repair();
            case Done -> repairDone();
        }
    }

    private void ask4RdzVs(ProductImage pi) {
        //- search about repair coffees
        Random hasard = new Random();
        helpers.clear();
        var tabAIDs = AgentServicesTools.searchAgents(this, "repair", "coffee");
        helpers.addAll(Arrays.stream(tabAIDs).toList());
        //add the helpers to the map if they are not already there (the give a random level of confidence)
        helpers.forEach(helper->evaluationMap.computeIfAbsent(helper, k -> hasard.nextInt(MAXRATING)+1));

        //some blabla
        println("-".repeat(30));

        int level = window.getLevel();
        println("My skill to repair this product is of : " + window.getLevel());

        println("-".repeat(30));
        var helpersLocalNames = helpers.stream().map(AID::getLocalName).toList();
        println("found this agent that could help me : " + helpersLocalNames);

        println("-".repeat(30));
        println("to make my choice, my preference about the date is of " + coefDate);
        println("my preference about the evaluation of the repair cofee is of " + coefEvaluation);
        println("and patience is of " + patience + " days max");

        println("-".repeat(30));
        //launch the CFP for a rendez-vs
        addCFP(pi, level);
    }

    private void ask4Repair(){
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setConversationId(StateRepair.Ask4Repair.toString());
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.setContent("repair please");
        msg.addReceiver(currentRepair.getListRendezVs().getLast().repairAgent());
        addBehaviour(new RepairRequestInitiator(this, msg));

    }

    private void repairDone(){
    }

    /**add a CFP from user to list of helpers*/
    private void addCFP(ProductImage pi, int level)  {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setConversationId("id");
        try { msg.setContentObject(new Object[]{pi.getP(), level}); }
        catch (IOException e) { throw new RuntimeException(e);}

        msg.addReceivers(helpers.toArray(AID[]::new));
        println("-".repeat(40));

        msg.setConversationId(StateRepair.Ask4RdzVs.toString());
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));

        var askReparation = new AskForRdzVsBehaviour(this, msg, pi);
        addBehaviour(askReparation);
    }

    public void println(String msg)
    {
        window.println(msg);
    }


    public UserGui getWindow(){return window;}

    public List<ProductImage> getProducts() {
        return products;
    }


    /**here we simplify the scenario. A breakdown is about 1 elt..
     * so whe choose a no between 1 to 4 and ask who can repair at at wich cost.*/
    private void breakdown(){
    }

    public int getPatience() {
        return patience;
    }

    public void setPatience(int patience) {
        this.patience = patience;
    }

    public Map<AID, Integer> getEvaluationMap() {
        return evaluationMap;
    }

    public double getCoefDate() {
        return coefDate;
    }

    public void setCoefDate(double coefDate) {
        this.coefDate = coefDate;
    }

    public double getCoefEvaluation() {
        return coefEvaluation;
    }

    public void setCoefEvaluation(double coefEvaluation) {
        this.coefEvaluation = coefEvaluation;
    }

    public void addRdzVs(RendezVs rdzVs){
        currentRepair.addRendezVs(rdzVs);
        window.addRepairRdzVs(rdzVs);
    }

    public Repair getCurrentRepair() {
        return currentRepair;
    }

    public void setCurrentRepair(Repair currentRepair) {
        this.currentRepair = currentRepair;
    }
}
