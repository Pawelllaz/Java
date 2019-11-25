package MainPackage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class LearningBoy {
    public static void learn(int nDays, int kRemember, Double p, LinkedList<String> allWords){
        if((p> 1 || p< 0) || kRemember > nDays){
            System.out.println("Wrong arguments");
            return;
        }
        ArrayList<String> wordsToFortget = new ArrayList<>();
        LinkedList<String> words = new LinkedList<>();
        String newWord = null;

        for(int i = 1; i <= nDays;i++){
            System.out.println("Day "+i);
            // 1 slowo
            newWord = generateNewWord(words,allWords);
            words.add(newWord);
            System.out.print("New words: "+newWord+" ");
            // 2 slowo
            newWord = generateNewWord(words,allWords);
            words.add(newWord);
            System.out.println(newWord);

            if(i >= kRemember) {
                if(words.size()>0) {
                    wordsToFortget.add(words.get(0));
                    words.remove(0);
                }
                if(words.size()>0) {
                    wordsToFortget.add(words.get(0));
                    words.remove(0);
                }
            }

            if(wordsToFortget.size() > 0){
                if(generateProbability(p)) {
                    String str = wordsToFortget.remove(generateRandom(0, wordsToFortget.size() - 1));
                    System.out.print("Forgotten words: " + str + " ");
                    if (generateProbability(p)) {
                        str = wordsToFortget.remove(generateRandom(0, wordsToFortget.size() - 1));
                        System.out.print(str);
                    }
                }
                else
                    System.out.print("Forgotten words: ---");

            }
            else {
                System.out.print("Forgotten words: ---");
            }
            ArrayList<String> newList = new ArrayList<>();
            newList.addAll(wordsToFortget);
            newList.addAll(words);
            System.out.println("\n"+newList+"\n");
        }
    }

    private static boolean generateProbability(Double probability){
        return new Random().nextDouble() <= probability;
    }

    private static String generateNewWord(LinkedList<String> myWords, LinkedList<String> allWords){
        String newWord = allWords.get(generateRandom(0,allWords.size()-1));
        while (myWords.contains(newWord) && myWords.size() > 0){
            newWord = allWords.get(generateRandom(0,allWords.size()-1));
        }
        return newWord;
    }

    private static int generateRandom(int min, int max){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
