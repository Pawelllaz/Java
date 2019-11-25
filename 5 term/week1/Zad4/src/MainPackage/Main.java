package MainPackage;

import javax.swing.*;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        String message = input();

        if(validateMessage(message)){
            MessageValidateException msgExc = new MessageValidateException();
            output("Blednie wprowazdone wyrazenie");
        }
        else {
            String converted = convert(message);
            output(converted);
        }
    }

    private static String input(){
        String message = JOptionPane.showInputDialog("Wpisz wyrażenie");
        return message;
    }

    private static boolean validateMessage(String message){
        for(int i = 0; i < message.length();i++){
            if(message.charAt(i)>='0' && message.charAt(i)<='9')
                if(i+1 < message.length() && message.charAt(i+1)>='0' && message.charAt(i+1)<='9')
                    if(i+2 < message.length() && message.charAt(i+2)>='0' && message.charAt(i+2)<='9')
                        return true;
            if(message.charAt(i)==' ')
                if(i+1 < message.length() && message.charAt(i+1) == ' ')
                    return true;
            if(message.charAt(i)=='+')
                if(i+1 < message.length() && message.charAt(i+1) == '+')
                    return true;
            if(message.charAt(i)=='-')
                if(i+1 < message.length() && message.charAt(i+1) == '-')
                    return true;
            if(message.charAt(i)=='*')
                if(i+1 < message.length() && message.charAt(i+1) == '*')
                    return true;
            if(message.charAt(i)!='*' && message.charAt(i)!='-' && message.charAt(i)!='+' && message.charAt(i)!=' ' && message.charAt(i)!='\0')
                if(message.charAt(i)>'9' || message.charAt(i)<'0')
                    return true;
        }
        return false;
    }

    private static void output(String message){

        JOptionPane.showMessageDialog(null,message);
    }

    private static String convert(String message){
        HashMap<String, String> numbers = new HashMap<String, String>();
        numbers.put("1", "jeden ");
        numbers.put("2", "dwa ");
        numbers.put("3", "trzy ");
        numbers.put("4", "cztery ");
        numbers.put("5", "piec ");
        numbers.put("6", "szesc ");
        numbers.put("7", "siedem ");
        numbers.put("8", "osiem ");
        numbers.put("9", "dziewiec ");
        numbers.put("10", "dziesiec ");
        numbers.put("11", "jedenascie ");
        numbers.put("12", "dwanascie ");
        numbers.put("13", "trzynascie ");
        numbers.put("14", "czternascie ");
        numbers.put("15", "pietnascie ");
        numbers.put("16", "szesnascie ");
        numbers.put("17", "siedemnascie ");
        numbers.put("18", "osiemnascie ");
        numbers.put("19", "dziewietnascie ");
        numbers.put("20", "dwadziescia ");
        numbers.put("30", "trzydziesci ");
        numbers.put("40", "czterdziesci ");
        numbers.put("50", "piecdziesiat ");
        numbers.put("60", "szescdziesiat ");
        numbers.put("70", "siedemdziesiat ");
        numbers.put("80", "osiemdziesiat ");
        numbers.put("90", "dziewiecdziesiat ");
        numbers.put("+", "plus ");
        numbers.put("-", "minus ");
        numbers.put("*", "razy ");

        String converted = "";
        for(int i = 0;i<message.length();i++){
            if(message.charAt(i)>='1' && message.charAt(i)<='9'){
                if(i+1 < message.length() && message.charAt(i+1)>='0' && message.charAt(i+1)<='9'){
                    // znalazlem dwucyfrowa
                    //prosty replace
                    char n1 = message.charAt(i);
                    char n2 = message.charAt(i+1);
                    String number = String.valueOf(n1);
                    number+=String.valueOf(n2);

                    if(message.charAt(i) == '1')
                        converted += numbers.get(number);
                    else {
                        String temp = String.valueOf(n1);
                        temp += String.valueOf('0');
                        converted += numbers.get(temp);
                        converted += numbers.get(String.valueOf(n2));
                    }
                    i++;
                }
                else {
                    if(numbers.get(String.valueOf(message.charAt(i))) != null)
                    converted += numbers.get(String.valueOf(message.charAt(i)));
                }
            }
            else {
                if(numbers.get(String.valueOf(message.charAt(i))) != null)
                    converted += numbers.get(String.valueOf(message.charAt(i)));
            }
        }
        return converted;
    }
}
