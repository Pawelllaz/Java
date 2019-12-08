package MainPackage;

import java.util.ArrayList;

class MaxSearchAlgorithms {

    ArrayList FromLeftToRightScan(int[] tab){
        System.out.println("from left to right");
        ArrayList<Integer> resultList = new ArrayList<>();

        Integer max = Integer.MIN_VALUE;
        for(int i = 0; i< tab.length;i++){
                if(tab[i]>max) {
                    max = tab[i];
                    resultList.add(max);
                }
        }
        return resultList;
    }

    ArrayList FromRightToLeftScan(int[] tab){
        System.out.println("from right to left");

        ArrayList<Integer> resultList = new ArrayList<>();

        Integer max = Integer.MIN_VALUE;
        for(int i = tab.length-1; i>=0;i--){
            if(tab[i]>max) {
                max = tab[i];
                resultList.add(max);
            }
        }
        return resultList;
    }

    ArrayList byEvenScan(int[] tab){
        System.out.println("by even");
        ArrayList<Integer> resultList = new ArrayList<>();

        Integer max = Integer.MIN_VALUE;
        for(int i = 0; i< tab.length;i+=2){
            if(tab[i]>max) {
                max = tab[i];
                resultList.add(max);
            }
        }
        for(int i = 1; i< tab.length;i+=2){
            if(tab[i]>max) {
                max = tab[i];
                resultList.add(max);
            }
        }
        return resultList;
    }

    void notToUse(){
        System.out.println("nothing to show");
    }
}
