package launch;

import data.ProductSpec;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import data.Product;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaunchAgents {
    public static void main(String[] args) {
        //log pour tracer
        var logger = Logger.getLogger(LaunchAgents.class.getName());
        logger.setLevel(Level.ALL);
        //les donnees
        System.out.println("-".repeat(20));
        System.out.println("- Les produits existants - ");
        var tab = Product.getListProducts();
        for(var p:tab)  System.out.println(p);
        System.out.println("-".repeat(20));
        System.out.println("- Les parties existantes - ");
        for(var p: ProductSpec.listSmallParts)  System.out.println(p);
        for(var p: ProductSpec.listBigParts)  System.out.println(p);
        System.out.println("-".repeat(20));
        // preparer les arguments pout le conteneur JADE
        Properties prop = new ExtendedProperties();
        // demander la fenetre de controle
        prop.setProperty(Profile.GUI, "true");
        // nommer les agents
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2; i++)
            sb.append("user_").append(i).append(":agents.UserAgent;");
        for (int i = 0; i < 4; i++)
            sb.append("coffee_").append(i).append(":agents.RepairCoffeeAgent;");
        for (int i = 0; i < 2; i++)
            sb.append("partsStore_").append(i).append(":agents.SparePartsStoreAgent;");
        for (int i = 0; i < 2; i++)
            sb.append("distributor_").append(i).append(":agents.DistributorAgent;");
        prop.setProperty(Profile.AGENTS, sb.toString());
        // creer le profile pour le conteneur principal
        ProfileImpl profMain = new ProfileImpl(prop);
        // lancer le conteneur principal
        Runtime rt = Runtime.instance();
        var agentContainer = rt.createMainContainer(profMain);
    }
}
