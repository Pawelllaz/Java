package MainPackage;

import javax.swing.*;
import java.util.ArrayList;

public class RacerGUI extends JFrame {
    private JLabel myLabel;
    private JPanel myPanel;
    private JList racersNamesJList;
    private JList racersTimeJList;
    private JLabel lastRacerLabel;
    private DefaultListModel<String> racersNamesModel;
    private DefaultListModel<String> racersTimeModel;

    public RacerGUI(){
        setRacersNamesModel();
        setRacersTimeModel();
        add(myPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("this is title");
        setSize(400,500);
    }

    public void refreshList(ArrayList<String> namesList, ArrayList<String> timeList){
        refreshNamesModel(namesList);
        refreshTimeModel(timeList);
    }

    public void refreshLastRacer(String lastRacerStr){
        lastRacerLabel.setText(lastRacerStr);
    }

    private void setRacersNamesModel(){
        racersNamesModel = new DefaultListModel<String>();
        racersNamesJList.setModel(racersNamesModel);
    }

    private void setRacersTimeModel(){
        racersTimeModel = new DefaultListModel<String>();
        racersTimeJList.setModel(racersTimeModel);
    }

    private void refreshNamesModel(ArrayList<String> list){
        racersNamesModel.clear();
        racersNamesModel.addAll(list);
    }
    private void refreshTimeModel(ArrayList<String> list){
        racersTimeModel.clear();
        racersTimeModel.addAll(list);
    }
}
