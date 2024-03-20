package agents;

import behaviours.AskForProductBehaviour;
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
import behaviours.AskForPartBehaviour;

import java.awt.Point;
import java.io.IOException;
import java.util.*;
import java.util.List;

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

    /**replacement part that owns the agent*/
    List<Part> parts;

    /**data used by the user to represent its products*/
    List<Repair> repairs;

    Repair currentRepair;

    /**map each aid of a repair agent to its evaluation*/
    Map<AID, Integer> evaluationMap;


    /**importance of the distance to take a rdz-vs*/
    double coefDist;
    /**importance of the date to take a rdz-vs*/
    double coefDate;
    /**importance of the price to choose a product or a part*/
    double coefPrice;
    /**importance of the evaluation to take a rdz-vs*/
    double coefEvaluation;

    /**coordinate of the user*/
    Point coord;

    /**simple object to provide random number, choice, ....*/
    Random hasard;

    /**gui used by the user agent*/
    UserGui window;
    /**setup of the user agent
     * - build a list with some objects choosed randomly
     * - define randomly the different coef (patience, evaluation)
     * - build the gui*/
    @Override
    public void setup() {
        //rchoose randomly some products among the existent
        hasard = new Random();
        //add some products choosen randomly in the list Product.getListProducts()
        products = new ArrayList<>();
        parts = new ArrayList<>();
        int nbTypeOfProducts = ProductType.values().length;
        int nbPoductsByType = 2*Product.VARIATION/Product.VARIATIONSTEP +1;
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
        coefDist = ((int)(hasard.nextDouble()*10)+1)/10.0;
        coefDate = ((int)(hasard.nextDouble()*10)+1)/10.0;
        coefEvaluation = ((int)(hasard.nextDouble()*10)+1)/10.0;
        coord = new Point(hasard.nextInt(100), hasard.nextInt(100));
        // create the window
        this.window = UserGui.createUserGui(getLocalName(), this);//SimpleWindow4Agent(getLocalName(), this);
        println("Hello!, je suis l'agent utilisateur");
        println("Je suis au point (%d,%d)".formatted(coord.x, coord.y) );
        println("Choisissez un produit à réparer, votre niveau d'expérience dans la réparation de ce produit et envoyer un appel à l'aide");
        println("Puis choisissez une option de réparation qui vous est proposé");
        println("-".repeat(30));
        helpers = new ArrayList<>(10);
    }

    @Override
    /**on event from the gui :
     * - ask for a rendez-vous*/
    public void onGuiEvent(GuiEvent evt) {
        switch (evt.getType()) {
            case UserGui.OK_EVENT -> followStepRepair();
            case UserGui.RESET_EVENT -> resetRepair();
            default -> println("unknown event : " + evt);
        }
    }

    private void followStepRepair() {
        ProductImage pi = null;
        if(currentRepair==null || currentRepair.getState() == StateRepair.NoRdzVs) {
            pi = window.getProduct();
            pi.getP().getFaultyPart();
            currentRepair = new Repair(getAID(), pi);
            repairs.add(currentRepair);
            currentRepair.setUserPatience(window.getPatience());
            currentRepair.setUserLevel(window.getLevel());
            window.addRepair(currentRepair);
            println("Je souhaite réparer ceci : " + pi.getP().getName());
        }
        else pi = currentRepair.getProductImg();
        println("Réparation en cours : " + currentRepair);

        //for the moment, I suppose there is only one type of event, click on go
        switch(currentRepair.getState()){
            case Ask4RdzVs -> ask4RdzVs(pi);
            case Ask4Parts -> ask4Parts();
            case RdzVs -> ask4Repair();
            case RepairFailed ->  coffeeShopFailed();
            case NeedNewProduct ->  ask4NewProduct();
            case PartReceived ->  ask4RdzVs(pi);
            case Done -> repairDone();
            case NoPart -> println("La dernière réparation a échoué; aucune pièce ne peut être réparée ou changée... Il vous faudrait acheter un nouveau produit, désolé.....");
            default -> println("unknown state : " + currentRepair.getState());
        }
    }

    private void resetRepair() {
        println("-".repeat(30));
        println("reset repair");
        currentRepair = null;
    }

    private void ask4RdzVs(ProductImage pi) {
        //- search about repair coffees
        helpers.clear();
        var tabAIDs = AgentServicesTools.searchAgents(this, "repair", "coffee");
        helpers.addAll(Arrays.stream(tabAIDs).toList());
        //add the helpers to the map if they are not already there (the give a random level of confidence)
        helpers.forEach(helper->evaluationMap.computeIfAbsent(helper, k -> hasard.nextInt(MAXRATING)+1));

        //some blabla
        println("-".repeat(30));

        int level = window.getLevel();
        println("Mon expérience pour réparer ce produit est de  : " + window.getLevel());
        currentRepair.setUserLevel(level);

        int patience = window.getPatience();
        println("Mon délai de patience avant l'obtention du rendez-vous est de : %d jours ".formatted(patience));
        currentRepair.setUserPatience(patience);

        println("-".repeat(30));
        var helpersLocalNames = helpers.stream().map(AID::getLocalName).toList();
        println("J'ai trouvé ces agents qui pourrient éventuellemet m'aider : " + helpersLocalNames);

        println("-".repeat(30));
        println("Pour faire mon choix, l'intérêt que je porte au délai est de %.2f,".formatted(coefDate));
        println("   l'intérêt que je porte à l'évaluation du repair café est de %.2f.,".formatted(coefEvaluation));
        println("   et l'intérêt que je porte à la distance du repair café est de %.2f.".formatted(coefDist));
        println("Et mon délai d'attente est de %d jours maximum".formatted(patience));

        println("-".repeat(30));
        //launch the CFP for a rendez-vs
        addCFP(pi, level);
    }

    private void ask4Repair(){
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setConversationId(StateRepair.Ask4Repair.toString());
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        Product p = currentRepair.getProductImg().getP();
        Object[] o = new Object[]{p, currentRepair.getParts()};
        try { msg.setContentObject(o); } catch (IOException e) { throw new RuntimeException(e); }
        msg.addReceivers(currentRepair.getListRepairStates().getLast().getRepairAgent());
        addBehaviour(new RepairRequestInitiator(this, msg));
    }

    private void repairDone(){
        var repairState = new RepairState(currentRepair.getListRepairStates().getLast());
        repairState.setState(StateRepair.RepairSuccess);
        currentRepair.addRepairState(repairState);
        currentRepair.setEnd(true);
        currentRepair.setEndDate(repairState.getDate());
        window.addRepairState(repairState);
        window.addRepair(currentRepair);
        println("Réparation correctement effectuée !");
        println("Cela vous a coûté %.2f € !".formatted(currentRepair.getCost()));
    }

    private void ask4Parts() {
        var repairState = new RepairState(currentRepair.getListRepairStates().getLast());
        repairState.setState(StateRepair.Ask4Parts);
        currentRepair.addRepairState(repairState);
        window.addRepairState(repairState);
        println("Lancement d'un appel d'offres auprès des distributeurs de pièces");
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setConversationId("id");
        Part p = getPartToBuy();
        try { msg.setContentObject(p); }
        catch (IOException e) { throw new RuntimeException(e);}

        var helpers = AgentServicesTools.searchAgents(this, "repair", "SparePartsStore");
        //add the helpers to the map if they are not already there (the give a random level of confidence)
        for(AID helper : helpers)
            evaluationMap.computeIfAbsent(helper, k -> hasard.nextInt(MAXRATING)+1);

        msg.addReceivers(helpers);
        println("-".repeat(40));

        msg.setConversationId(StateRepair.Ask4Parts.toString());
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));

        var askPart = new AskForPartBehaviour(this, msg, p);
        addBehaviour(askPart);
    }

    private void ask4NewProduct() {
        println("Lancement d'un appel d'offres auprès des distributeurs de produits");
        //TODO: correct to ask a new product(here it's the copy of the ask for part)
        println("Lancement d'un appel d'offres auprès des distributeurs de pièces");
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setConversationId("id");
        Product p = null;
        try { msg.setContentObject(p); }
        catch (IOException e) { throw new RuntimeException(e);}

        var helpers = AgentServicesTools.searchAgents(this, "repair", "SparePartsStore");
        //add the helpers to the map if they are not already there (the give a random level of confidence)
        for(AID helper : helpers)
            evaluationMap.computeIfAbsent(helper, k -> hasard.nextInt(MAXRATING)+1);

        msg.addReceivers(helpers);
        println("-".repeat(40));

        msg.setConversationId(StateRepair.Ask4Parts.toString());
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));

        var askPart = new AskForProductBehaviour(this, msg, p);
        addBehaviour(askPart);
    }

    private void coffeeShopFailed() {
        println("Echec du repair café, lancement d'une recherche de rendez vous auprès d'autres repair cafés");
        //TODO
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

    /**fucnction lauched when the user get ou of the coffe shop*/
    public void getOutCoffeeShop(StateRepair state){
        currentRepair.setState(state);
        window.println("Vous etes sorti du centre de reparation, state = %s".formatted(state));
        if (Objects.requireNonNull(state) == StateRepair.RepairSuccess) {
            repairDone();
        }
    }

    public void addCost(double cost){currentRepair.addCost(cost);}

    public void println(String msg)
    {
        window.println(msg);
    }


    public UserGui getWindow(){return window;}

    public List<ProductImage> getProducts() {
        return products;
    }

    public double getCoefDist() {
        return coefDist;
    }

    public void setCoefDist(double coefDist) {
        this.coefDist = coefDist;
    }

    /**here we simplify the scenario. A breakdown is about 1 elt..
     * so whe choose a no between 1 and 4 and ask who can repair and at which cost.*/
    private void breakdown(){
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

    public double getCoefPrice() { return coefPrice; }

    public void setCoefEvaluation(double coefEvaluation) {
        this.coefEvaluation = coefEvaluation;
    }



    public void addRepairState(RepairState repairState) {
        var currentRepairState = currentRepair.getListRepairStates().getLast();
        if(repairState.getState() == StateRepair.NoRdzVs){
            currentRepairState.setNextState(StateRepair.NoRdzVs);
            currentRepair.setState(StateRepair.NoRdzVs);
        }
        else{
            currentRepairState.setNextState(repairState.getState());
            currentRepair.addRepairState(repairState);
            window.addRepairRdzVs(repairState);
        }
    }

    public Repair getCurrentRepair() {
        return currentRepair;
    }

    public void setCurrentRepair(Repair currentRepair) {
        this.currentRepair = currentRepair;
    }

    public void addPartToBuy(Part p){
        currentRepair.getParts().add(new Part(p, 0.0));
    }
    public Point getCoord() {
        return coord;
    }

    public Part getPartToBuy(){
        var parts = currentRepair.getParts();
        var f = parts.stream().filter(p->p.price()==0);
        return f.toList().getFirst();
    }

    /**add a part in the parts owned by the agent*/
    public void setBuyedPart(AID seller, Part p, double price){
        currentRepair.getParts().remove(p);
        currentRepair.getParts().add(p);
        var currentRepairState = currentRepair.getListRepairStates().getLast();
        currentRepairState.setNextState(StateRepair.PartReceived);
        currentRepair.setState(StateRepair.PartReceived);
        currentRepair.addCost(price);
        parts.add(p);
    }
}
