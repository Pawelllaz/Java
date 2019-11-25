package MainPackage;

import java.io.*;
import java.util.LinkedList;

// popraw contains
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("gen1:");
        search("gen1.dat");

        System.out.println("gen2:");
        search("gen2.dat");

        System.out.println("gen3:");
        search("gen3.dat");

    }

    private static void search(String filename) throws IOException {
        DataInputStream input = new DataInputStream(
                new FileInputStream(filename));

        LinkedList<Integer> list = new LinkedList<Integer>();

        long index = 0, firstI=0, secondI=0;
        while (input.available() > 0) {
            int number = input.readInt();
            if(list.size() > 1001) {
                list.remove();
                index++;
            }
            if(list.contains(number)) {
                firstI = index+list.indexOf(number);
                secondI = index+list.size()-1;

                System.out.println("BAD SEQ, " + firstI + ", " + secondI+", period length = "+(secondI-firstI));
                break;
            }
            list.add(number);
        }
        if(firstI==0 && secondI==0)
            System.out.println("GOOD SEQ");

        input.close();
    }
}
