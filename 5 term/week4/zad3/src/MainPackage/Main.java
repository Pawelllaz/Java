package MainPackage;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Double> li = List.of((double)-5100, 43.257, (double)200000, 2000000.5);
        List<String> list1 = Numbers.formattedNumbers(li, 2, ',', 1, true);
        List<String> list2 = Numbers.formattedNumbers(li, 3, '|', 3, false);

        for (String num: list1) {
            System.out.println(num);
        }

        System.out.println();

        for (String num: list2) {
            System.out.println(num);
        }
    }
}
