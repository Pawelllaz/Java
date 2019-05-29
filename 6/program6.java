import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;

class vector{

	public static void main(String[] args) throws WektoryRoznejDlugosciException{
		int flag = 0;
		List<Integer> vec1, vec2;
		vec1 = new ArrayList<Integer>();
		vec2 = new ArrayList<Integer>();
		while(flag == 0){
			flag = 1;
			vec1 = readVector();
			vec2 = readVector();
			
			try{
				if(vec1.size() != vec2.size()){
					System.out.println("Wektory sa roznej dlugosci!");
					throw new WektoryRoznejDlugosciException(vec1.size(), vec2.size());
				}
			}
			catch(WektoryRoznejDlugosciException e){
				System.out.println("dlugosc wektora pierwszego: "+ e.vec1 +", a drugiego: "+ e.vec2);
				flag = 0;
			}
		}
		
		try{
	        	PrintWriter file = new PrintWriter("sumaVec.txt");
	        	System.out.print("Suma wektorow wynosi: ");
	            	for(int i = 0; i < vec1.size(); i++){
	                	file.print(+(vec1.get(i) + vec2.get(i)) +" ");
	                	System.out.print(+(vec1.get(i) + vec2.get(i)) +" ");
	            	}
	            
	            	file.close();
	        }
	        catch (IOException e){
	        	System.out.println("error, nie utworzono pliku!");
	        }	
	}
	private static List<Integer> readVector(){
		Scanner scan = new Scanner(System.in);
		List<Integer> vec = new ArrayList<Integer>();
		int number = 0;
		int flag = 0;
		while(flag == 0){
			vec = new ArrayList<Integer>();
			System.out.println("wpisz wektor:");
			String[] tab = scan.nextLine().split(" ");
			
			for(String s : tab){
				flag = 1;
				try{
					number = Integer.parseInt(s);
				}
				catch(NumberFormatException e){
					System.out.println("niepoprawny!");
					flag = 0;
					break;	
				}
				if(flag == 1)
					vec.add(number);
			}
		}
		return vec;
	}
} 

class WektoryRoznejDlugosciException extends Exception{	
	public final int vec1, vec2;
	public WektoryRoznejDlugosciException(int vec1, int vec2){
		this.vec1 = vec1;
		this.vec2 = vec2;
	}
}