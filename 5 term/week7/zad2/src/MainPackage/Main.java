package MainPackage;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {
        //Long N = Long.parseLong(args[0]);
        //Double m = Double.parseDouble(args[1]);
        //Double s = Double.parseDouble(args[2]);
        Long N = (long)100;
        Double m = (double)500;
        Double s = (double)100;
        if(s < 0){
            System.out.println("Wrong deviation");
            return;
        }

        String fileNameFrom = "rands.dat";
        String fileNameTo = "rands.txt";

        List list = createListOfDoubles(N, m, s);
        //System.out.println(list);
        if(!writeNumbers(fileNameFrom, list)){
            System.out.println("Saving data error");
            return;
        }
        readAndWriteToTxt(fileNameFrom, fileNameTo);
    }

    private static void readAndWriteToTxt(String fileNameFrom, String fileNameTo) throws IOException {
        DataInputStream input = new DataInputStream(new FileInputStream(fileNameFrom));
        File fileTo = new File(fileNameTo);
        //fileTo.createNewFile();
        FileWriter fileWriter = new FileWriter(fileTo);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("pl", "PL"));
        while (input.available() > 0) {
            Double number = input.readDouble();
            System.out.println(numberFormat.format(number));
            fileWriter.write(numberFormat.format(number)+"\n");
        }

        fileWriter.close();

        input.close();
    }

    private static boolean writeNumbers(String filename, List<Double> list) throws Exception {
        File file = new File(filename);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        /*try {
            if(file.createNewFile())
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //System.out.println("list");
        //dataOutputStream.writeBytes("asd");
        for(double number: list){
            dataOutputStream.writeDouble(number);
            //System.out.println(number);
        }
        dataOutputStream.close();

        return true;
    }

    private static List<Double> createListOfDoubles(Long N, double mean, double deviation){
        ArrayList<Double> list = new ArrayList<>();
        for(long i=0;i<N;i++)
            list.add(getRandomNumber(mean, deviation));
        //System.out.println(list);
        return list;
    }

    private static double getRandomNumber(double mean, double deviation){
        Random random = new Random();
        Double number = random.nextGaussian() * deviation + mean;

        if(number < 0)
            number*=-1;
        assert (number > 0);
        return number;
    }
}
