package MainPackage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    public static void main(String[] args) throws IOException {
        writeIt();


    }

    private static void writeIt() throws IOException {
        Words words = new Words();
        Gson gson = new Gson();
        FileWriter fileWriter = new FileWriter("PolEngTest.json");
        
        words.krzyczeć  = newList(new String[] {"shout", "cry","scream"});

        words.zrobić  = newList(new String[] {"do", "make","execute"});

        words.podróż  = newList(new String[] {"travel", "trip","journey"});

        words.zagrać  = newList(new String[] {"act", "play","show"});

        words.konstrukcja  = newList(new String[] {"construction", "structure","design"});

        words.narzędzia  = newList(new String[] {"tools", "implements","kit"});

        words.problem  = newList(new String[] {"problem", "issue","trouble"});

        words.rower  = newList(new String[] {"bike", "bicycle","cycle"});

        words.zadanie  = newList(new String[] {"task", "request","work"});

        words.człowiek  = newList(new String[] {"man", "human","person"});

        //fileWriter.write(gson.toJson(words));
        gson.toJson(words, fileWriter);
        fileWriter.close();
    }

    private static ArrayList<String> newList(String[] s){
        ArrayList<String> list = new ArrayList<>();
        for (String tempstr: s
             ) {
            list.add(tempstr);
        }
        return list;
    }
}
