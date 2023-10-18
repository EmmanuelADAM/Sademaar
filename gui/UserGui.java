package gui;

import agents.UserAgent;
import data.Product;
import data.ProductImage;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import javax.swing.*;

public class UserGui {
    public static int OK_EVENT = 1;
    private JPanel jpMain;
    private JTextArea zoneTexte;
    private JButton jbOk;
    private JScrollPane jscpane;
    private JLabel jlNiveau;
    private JSlider slideNiveau;
    private JLabel jlChoixProduit;
    private JLabel jlChoixOffre;
    private JComboBox<ProductImage> comboChoixProduit;
    private JComboBox comboReparation;

    private UserAgent agent;
    UserGui(){
        jbOk.addActionListener(e -> agent.postGuiEvent(new GuiEvent(e,OK_EVENT)));
    }
    public UserGui(UserAgent agent){
        this.agent = agent;
    }

    public static UserGui createUserGui(String title, UserAgent agent){
        JFrame frame = new JFrame(title);
        UserGui userGui = new UserGui(agent);
        frame.setContentPane(userGui.jpMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        userGui.jbOk.addActionListener(e -> agent.postGuiEvent(new GuiEvent(e,OK_EVENT)));
        var produits =  agent.getProduits();
        produits.forEach(userGui.comboChoixProduit::addItem);

        return userGui;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("UserGui");
        frame.setContentPane(new UserGui().jpMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void println(String txt)
    {
        zoneTexte.append(txt + "\n");
    }

    public ProductImage getProduct(){
        return (ProductImage)comboChoixProduit.getSelectedItem();
    }
    public int getLevel(){
        return slideNiveau.getValue();
    }
}
