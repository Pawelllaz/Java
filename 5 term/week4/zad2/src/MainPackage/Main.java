package MainPackage;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        TestGUI testGUI = new TestGUI();
        testGUI.setVisible(true);

        HashMap<String, String> wordMap = new HashMap<>();
        wordMap.put("cry", "płacz");
        wordMap.put("smile", "uśmiech");
        wordMap.put("mind", "umysł");

        testGUI.setWordToTranslate(wordMap.get("cry"));
    }
}
