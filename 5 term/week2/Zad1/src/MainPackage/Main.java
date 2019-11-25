package MainPackage;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	    Scanner scan = new Scanner(System.in);
	    System.out.println("Wpisz pierwszy wyraz:");
	    String s1 = scan.nextLine();
	    System.out.println("Wpisz drugi wyraz");
	    String s2 = scan.nextLine();

	    Levenshtein levenshtein = new Levenshtein();
	    System.out.println("Odleglosc wynosi: " + levenshtein.LevQWERTY(s1,s2));
    }
}

