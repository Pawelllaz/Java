import java.lang.Math;
import java.util.Scanner;

class Random_number{
	public static void main(String []args){
	Scanner scan = new Scanner(System.in);
	int rand = (int)(Math.random() * 100);
	int counter=1;

	while(true){
		System.out.println("\npodaj liczbe: ");
		int number = scan.nextInt();
		if(number>rand)
			System.out.println("za duzo, liczba prob: "+ counter++);
		else if(number<rand)
			System.out.println("za malo, liczba prob: "+ counter++);
		else{
			System.out.println("bravo, liczba prob: "+ counter++);
			return;
		}
	}
	}	
} 