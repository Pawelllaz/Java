package MainPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        var words = List.of("Foo", "Bar", "Foo", "Buzz", "Foo", "Buzz", "Fizz", "Fizz");

        var map = new HashMap<String, Integer>();
        words.forEach(word -> {
            map.merge(word, 1, Integer::sum);
        });

        System.out.println(map);
        System.out.println(funContains(words));
        System.out.println(funGet(words));
        System.out.println(funGetOrDefault(words));
        System.out.println(funPutIfAbsent(words));
    }

    private static Map funContains(List<String> words){
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();

        for (String word: words) {
            if(resultMap.containsKey(word))
                resultMap.put(word, resultMap.get(word) + 1);
            else
                resultMap.put(word,1);
        }

        return resultMap;
    }

    private static Map funGet(List<String> words){
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();

        for (String word: words) {
            if(resultMap.get(word) != null)
                resultMap.put(word, resultMap.get(word) + 1);
            else
                resultMap.put(word,1);
        }

        return resultMap;
    }

    private static Map funGetOrDefault(List<String> words){
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();

        for (String word: words)
            resultMap.put(word, resultMap.getOrDefault(word,0) + 1);

        return resultMap;
    }

    private static Map funPutIfAbsent(List<String> words){
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();

        Integer v = 0;
        for (String word: words) {
            v = resultMap.get(word);
            if(v == null)
                v=0;
            if(resultMap.putIfAbsent(word, v + 1) != null)
                resultMap.put(word, v + 1);
        }
        return resultMap;
    }
}
