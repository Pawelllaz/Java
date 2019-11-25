package MainPackage;
import java.util.HashMap;

public class Levenshtein {

    public double LevQWERTY(String s1, String s2) {

        HashMap<Character, String> myMap = new HashMap<Character, String>();

        String all = "qwertyuiopasdfghjklzxcvbnm";
        for (int i = 0; i< all.length();i++){
            if(all.charAt(i)=='q' || all.charAt(i)=='a' || all.charAt(i)=='z')
                myMap.put(all.charAt(i),String.valueOf(all.charAt(i+1)));
            else if(all.charAt(i)=='p' || all.charAt(i)=='l' || all.charAt(i)=='m')
                myMap.put(all.charAt(i),String.valueOf(all.charAt(i-1)));
            else {
                String a = String.valueOf(all.charAt(i-1));
                String b = String.valueOf(all.charAt(i+1));
                myMap.put(all.charAt(i), a + b);
            }
        }

        int len0 = s1.length() + 1;
        int len1 = s2.length() + 1;

        // the array of distances
        double[] cost = new double[len0];
        double[] newcost = new double[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++)
            cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for (int i = 1; i < len0; i++) {
                // matching current letters in both strings
                double match = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                if (match == 1) {
                    String a = myMap.get(s1.charAt(i - 1));
                    String b = String.valueOf(s2.charAt(j - 1));
                    if (a.contains(b)){
                        match -= 0.5;
                        System.out.println("wywolanie");
                }}
                // computing cost for each transformation
                double cost_replace = cost[i - 1] + match;
                double cost_insert = cost[i] + 1;
                double cost_delete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            double[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }
}