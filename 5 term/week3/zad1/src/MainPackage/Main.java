package MainPackage;

import com.sun.source.tree.WhileLoopTree;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        ArrayList<String> ridersNames = null;
        try {
            ReadURL readURL = new ReadURL();
            ridersNames = readURL.read("http://szgrabowski.kis.p.lodz.pl/zpo19/nazwiska.txt");
        } catch (Exception e){
            System.out.println(e);
        }
        HashSet<String> randomNames = new HashSet<>();
        Random rand = new Random();

        while(randomNames.size()<12) {
            int randNum = rand.nextInt(ridersNames.size());
            String temp = ridersNames.get(randNum);
            randomNames.add(temp);
        }


        HashMap mapRacers = makeMap(randomNames);
        Racer racer = new Racer(mapRacers);
        try {
            racer.Race();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static HashMap makeMap(HashSet<String> namesSet) {
        HashMap<Integer, String> namesMap = new HashMap<>();
        for (String name : namesSet) {
            int delay = generateGauss();
            while(namesMap.containsKey(delay))
                delay=generateGauss();
            namesMap.put(delay, name);
        }
        return namesMap;
    }

    private static int generateGauss() {
        Random rand = new Random();
        double randNum = rand.nextGaussian() * 40 + 290;
        if (randNum < 240)
            return 240;
        else if (randNum > 350)
            return 350;
        return (int) randNum;
    }
}