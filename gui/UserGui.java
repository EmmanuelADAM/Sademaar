package gui;

import agents.UserAgent;
import data.ProductImage;
import data.RepairState;
import data.Repair;
import jade.gui.GuiEvent;

import javax.swing.*;
import java.awt.*;

public class UserGui {
    final public static int OK_EVENT = 1;
    final public static int RESET_EVENT = 0;
    private JPanel jpMain;
    private JTextArea zoneTexte;
    private JButton jbOk;
    private JScrollPane jscTexte;
    private JLabel jlNiveau;
    private JSlider slideNiveau;
    private JLabel jlChoixProduit;
    private JLabel jlRepairs;
    private JComboBox<ProductImage> comboChoixProduit;
    private JLabel jlRdzVs;
    private JComboBox<Repair> comboRepairs;
    private JComboBox<RepairState> comboRepairRdzVs;
    private JButton bReset;
    private JLabel jlpatience;
    private JSlider sliderPatience;
    private JTextArea textRepairing;
    private JScrollPane jscRepairing;

    private UserAgent agent;
    UserGui(){
        jbOk.addActionListener(e -> agent.postGuiEvent(new GuiEvent(e,OK_EVENT)));
        bReset.addActionListener(e -> agent.postGuiEvent(new GuiEvent(e,RESET_EVENT)));
        jscTexte.setPreferredSize(new Dimension(500, 300));
        jscTexte.setMinimumSize(new Dimension(500, 300));
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
        userGui.bReset.addActionListener(e -> agent.postGuiEvent(new GuiEvent(e,RESET_EVENT)));
        var produits =  agent.getProducts();
        //redefine toString for combo box
        produits.forEach(userGui.comboChoixProduit::addItem);
        //add a tooltip text depending on the item selected
        userGui.comboChoixProduit.setToolTipText(produits.get(0).toStringDetails());
        userGui.comboChoixProduit.addItemListener(e -> {
            ProductImage product = (ProductImage)userGui.comboChoixProduit.getSelectedItem();
            userGui.comboChoixProduit.setToolTipText(product.toStringDetails());
        });
        userGui.comboRepairs.addItemListener(e -> {
            userGui.comboRepairRdzVs.removeAllItems();
            Repair repair = (Repair)userGui.comboRepairs.getSelectedItem();
            repair.getListRepairStates().forEach(userGui.comboRepairRdzVs::addItem);
        });

        return userGui;
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


    public int getPatience(){
        return sliderPatience.getValue();
    }

    public void addRepair(Repair repair) {
        comboRepairs.addItem(repair);
        textRepairing.append(repair.toString() + "\n");
    }
    public void addRepairRdzVs(RepairState rdzVs) {
        comboRepairRdzVs.addItem(rdzVs);
        textRepairing.append(rdzVs.toString() + "\n");
    }

    public void addRepairState(RepairState repairState) {
        textRepairing.append(repairState.toString() + "\n");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("UserGui");
        frame.setContentPane(new UserGui().jpMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        ToolTipManager.sharedInstance().setEnabled(true);
        frame.setVisible(true);
    }
}
