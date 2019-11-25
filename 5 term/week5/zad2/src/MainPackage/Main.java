package MainPackage;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ReadURL readURL = new ReadURL();
        LinkedList<String> words = readURL.read("http://szgrabowski.kis.p.lodz.pl/zpo19/1500.txt");
        LearningBoy learningBoy = new LearningBoy();

        LearningBoy.learn(10,3,0.5,words);
        //System.out.println(words.get(5));
    }


}
