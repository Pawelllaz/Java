package MainPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestGUI extends JFrame {
    private int Points = 0;
    private JPanel myPanel;
    private JLabel wordToTranslate;
    private JTextField translatedWord;
    private JButton acceptWord;
    private JLabel appTitle;

    public TestGUI(){
        setAcceptWord();
        setAppTitle();
        add(myPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("this is title");
        setSize(400,500);
    }

    public void setWordToTranslate(String word){
        wordToTranslate.setText(word);
    }
    private String getInputWord(){
        return translatedWord.getText();
    }

    private void setAcceptWord(){
        acceptWord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // porownaj slowa, ustaw nowe i licz do 5
                getInputWord();
            }
        });
    }

    private void setAppTitle(){
        appTitle.setFont(new Font(Font.SERIF,Font.BOLD,25));
    }
}
