package gui;

import agents.UserAgent;
import data.ProductImage;
import data.RepairState;
import data.Repair;
import jade.gui.GuiEvent;

import javax.swing.*;

public class UserGui {
    final public static int OK_EVENT = 1;
    final public static int RESET_EVENT = 0;
    private JPanel jpMain;
    private JTextArea zoneTexte;
    private JButton jbOk;
    private JScrollPane jscpane;
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

    private UserAgent agent;
    UserGui(){
        jbOk.addActionListener(e -> agent.postGuiEvent(new GuiEvent(e,OK_EVENT)));
        bReset.addActionListener(e -> agent.postGuiEvent(new GuiEvent(e,RESET_EVENT)));
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
        produits.forEach(userGui.comboChoixProduit::addItem);
        userGui.comboRepairs.addItemListener(e -> {
            userGui.comboRepairRdzVs.removeAllItems();
            Repair repair = (Repair)userGui.comboRepairs.getSelectedItem();
            repair.getListRepairStates().forEach(userGui.comboRepairRdzVs::addItem);
        });

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


    public int getPatience(){
        return sliderPatience.getValue();
    }

    public void addRepair(Repair repair) {
        comboRepairs.addItem(repair);
    }
    public void addRepairRdzVs(RepairState rdzVs) {
        comboRepairRdzVs.addItem(rdzVs);
    }
}
