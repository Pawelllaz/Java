package MainPackage;

import java.util.ArrayList;
import java.util.List;

public class Numbers {
    public static List<String> formattedNumbers(List<Double> nums, int group, char separator, int
            nDigits, boolean padding){

        ArrayList<String> tempList = new ArrayList<>();

        int digitsCounted = 0;
        for (Double number: nums) {
            number = round(number, nDigits);
            String temp = addSeparator(number, group,separator);
            tempList.add(temp);
            if(countDigits(temp) > digitsCounted)
                digitsCounted = countDigits(temp);
        }

        ArrayList<String> resultList = new ArrayList<>();
        for (String strNum:tempList) {
            String temp = addSpace(strNum,digitsCounted);
            if(!padding && nDigits > 0 && check(temp))
                temp = setNDigits(temp,nDigits);
            else if(padding && nDigits > 0)
                temp = setNDigits(temp, nDigits);
            else
                temp=removePadding(temp, padding);
            //System.out.println(temp);
            resultList.add(temp);
        }
        return resultList;
    }

    private static boolean check(String strNumber) {
            for (int i = 0; i < strNumber.length(); i++) {
                if (strNumber.charAt(i) == '.' && Character.isDigit(strNumber.charAt(i + 1)) && strNumber.charAt(i+1) != '0') {
                    return true;
                }
            }
        return false;
    }
    
    private static String setNDigits(String strNumber, int nDigits){
        int iterator = 0;
        try {
            for(int i=0;i<strNumber.length();i++){
                if(strNumber.charAt(i)=='.'){
                    for (int j=i+1;;j++){
                        iterator++;
                        if(strNumber.length()<=j)
                            strNumber+='0';
                        if(iterator==nDigits)
                            break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return strNumber;
    }

    private static String removePadding(String strNumber, boolean complete){
        for(int i=0;i<strNumber.length();i++){
            if(strNumber.charAt(i)=='.')
                if(complete)
                    return strNumber.substring(0,i);
                else {
                    if(Character.isDigit(strNumber.charAt(i+1)) && strNumber.charAt(i+1) == '0')
                        return strNumber.substring(0,i);
                }
        }
        return  strNumber;
    }

    private static String addSpace(String strNumber, int maxAmountOfDigits){
        int nummberAmountOfDigits = countDigits(strNumber);
        int spaceToAdd = maxAmountOfDigits - nummberAmountOfDigits;
        String spaces = "";
        //if(number.charAt(0)=='-')
          //  spaces+=" ";
        for(int i =0;i < spaceToAdd;i++) {
            spaces += " ";
        }
        return spaces.concat(strNumber);
    }

    private static String addSeparator(Double number, int group, char separator){
        String strNumber = getStringInteger(number);        // str number without ndigits
        int iterator = strNumber.length();

        int checkGroup = 0;
        while (iterator>0){
            if(checkGroup==group) {
                if(strNumber.charAt(iterator - 1) == '-')
                    break;
                String sub1 = strNumber.substring(0,iterator);
                String sub2 = strNumber.substring(iterator);
                strNumber = sub1+separator+sub2;
                checkGroup=0;
            }
            checkGroup++;
            iterator--;
        }
        return strNumber+getStrNDigits(number);
    }

    private static String getStringInteger(Double number){
        return String.valueOf(number.intValue());
    }

    private static String getStrNDigits(Double number){
        String strNumber = String.valueOf(number);
        for(int i = 0; i < strNumber.length();i++){
            if(strNumber.charAt(i) == '.')
                return strNumber.substring(i);
        }

        return null;
    }

    private static int countDigits(String strNumber){
        for(int i = 0; i < strNumber.length();i++){
            if(strNumber.charAt(i) == '.')
                return i;
        }
        return 0;
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
