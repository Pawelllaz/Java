package MainPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        String filename = "a.txt";
        //File file = new File("a.txt");
        Integer max;
        Integer min;

        try{
            Stream<String> stream = Files.lines(Paths.get(filename));

            // maximum
            max = stream.filter(
                    line -> line
                            .split(" ")[2]
                            .equals("PL"))
                    .map(line -> Integer
                            .parseInt(line.split(" ")[3]))
                    .sorted(Comparator.reverseOrder())
                    .limit(2)
                    .reduce(0,Integer::sum);
            System.out.println(max);
            stream.close();

            // minimum
            stream = Files.lines(Paths.get(filename));
            min = stream.filter(
                    line -> line
                            .split(" ")[2]
                            .equals("PL"))
                    .map(line -> Integer
                            .parseInt(line.split(" ")[3]))
                    .sorted()
                    .limit(2)
                    .reduce(0,Integer::sum);
            System.out.println(min);
            stream.close();

            // counting
            stream = Files.lines(Paths.get(filename));
            /*stream.map(
                    line -> line
                            .split(" ")[2])
                    .sorted(Comparator.naturalOrder())
                    .peek(System.out::println);
*/
            Map<String, Long> result = stream.map(
                    line -> line.split(" ")[2])
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
