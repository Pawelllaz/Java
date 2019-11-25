package MainPackage;

import java.util.ArrayList;
import java.util.HashMap;

public class AppWorker {
    private HashMap<String, String> wordMap = new HashMap<>();

    public AppWorker(){
        wordMap.put("cry", "płacz");
        wordMap.put("smile", "uśmiech");
        wordMap.put("mind", "umysł");
    }
}
