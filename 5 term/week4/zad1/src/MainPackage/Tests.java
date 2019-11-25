package MainPackage;

import org.junit.Test;
import static org.junit.Assert.*;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Arrays;
import java.util.Locale;

public class Tests {

    @Test
    public void correctnessTest(){
        Collator myCollator = null;
        try {
            myCollator = Collator.getInstance(new Locale("pl","PL"));
        }
        catch(Exception e){e.printStackTrace();}

        String[] tab1 = {"Łukasz", "Ścibor", "Stefania", "Darek", "Agnieszka",
                "Zyta", "Órszula", "Świętopełk"};

        String[] tab2 = {"Łukasz", "Ścibor", "Stefania", "Darek", "Agnieszka",
                "Zyta", "Órszula", "Świętopełk"};

        SortClass.sortStrings(myCollator, tab1);
        SortClass.fastSortStrings(myCollator, tab2);
        System.out.println(Arrays.asList(tab1));
        System.out.println(Arrays.asList(tab2));

        assertArrayEquals(tab1, tab2);
    }

    @Test
    public void speedTest(){
        Collator myCollator = null;
        try {
            myCollator = Collator.getInstance(new Locale("pl","PL"));
        }
        catch(Exception e){e.printStackTrace();}

        // TEST 1
        long timeBeforeTest = System.currentTimeMillis();
        for(int i = 0; i < 100000; i++){
            String[] tab1 = {"Łukasz", "Ścibor", "Stefania", "Darek", "Agnieszka",
                    "Zyta", "Órszula", "Świętopełk"};
            SortClass.sortStrings(myCollator, tab1);
        }
        System.out.println(System.currentTimeMillis() - timeBeforeTest);

        // TEST 2
        timeBeforeTest = System.currentTimeMillis();
        for(int i = 0; i < 100000; i++){
            String[] tab1 = {"Łukasz", "Ścibor", "Stefania", "Darek", "Agnieszka",
                    "Zyta", "Órszula", "Świętopełk"};
            SortClass.fastSortStrings(myCollator, tab1);
        }
        System.out.println(System.currentTimeMillis() - timeBeforeTest);

        // TEST 3
        timeBeforeTest = System.currentTimeMillis();
        for(int i = 0; i < 100000; i++){
            String[] tab1 = {"Łukasz", "Ścibor", "Stefania", "Darek", "Agnieszka",
                    "Zyta", "Órszula", "Świętopełk"};
            SortClass.fastSortStrings2(myCollator, tab1);
        }
        System.out.println(System.currentTimeMillis() - timeBeforeTest);
    }
}