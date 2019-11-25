package MainPackage;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Arrays;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        Collator myCollator;
        String[] names1 = {"Łukasz", "Ścibor", "Stefania", "Darek", "Agnieszka",
                "Zyta", "Órszula", "Świętopełk"};
        String[] names2 = {"Łukasz", "Ścibor", "Stefania", "Darek", "Agnieszka",
                "Zyta", "Órszula", "Świętopełk"};
        String[] names3 = {"Łukasz", "Ścibor", "Stefania", "Darek", "Agnieszka",
                "Zyta", "Órszula", "Świętopełk"};

        try {
            myCollator = Collator.getInstance(new Locale("pl","PL"));
            SortClass.sortStrings(myCollator, names1);
            SortClass.fastSortStrings(myCollator, names2);
            SortClass.fastSortStrings2(myCollator, names3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(Arrays.toString(names1));
        System.out.println(Arrays.toString(names2));
        System.out.println(Arrays.toString(names3));
    }
}
