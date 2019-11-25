package MainPackage;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

public class SortClass {
    public static void sortStrings(Collator collator, String[] tab) {
        for(int i = 1; i < tab.length; i++) {
            String temp = tab[i];
            int j = i;
            while(j > 0) {
                if (collator.compare(temp, tab[j - 1]) < 0) {
                    tab[j] = tab[j - 1];
                    tab[j - 1] = temp;
                }
                j--;
            }
        }
    }

    public static void fastSortStrings(Collator collator, String[] tab) {
        Arrays.sort(tab, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return collator.compare(o1, o2);
            }
        });
    }

    public static void fastSortStrings2(Collator collator, String[] tab) {
        Arrays.sort(tab, (o1, o2) -> collator.compare(o1, o2));
    }
}
