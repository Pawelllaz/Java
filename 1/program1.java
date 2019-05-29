import java.lang.Math;

class delta{
	public static void main(String []args){
	
		if(args.length != 3){
			System.out.println("bledna liczba argumentow");
			return;
		}
		int a = Integer.parseInt(args[0]);
		int b = Integer.parseInt(args[1]);
		int c = Integer.parseInt(args[2]);

		double delta = (b*b)-(4*a*c);
		
		if(a==0)
			System.out.println("x = "+ -c/(double)b);
		else if (delta<0){
			System.out.println("rozwiazanie w dziedzinie urojonej: ");
          		double x1=-b/(double)(2*a);
          		double x2=Math.sqrt(-delta)/(double)(2*a);
          		if(x2<0)
          		{
            			System.out.print("x1: "+ x1 +" + "+ x2 +"i");
            			System.out.println(", x2: "+x1 +" + "+ x2*(-1) +"i");
          		}
          		else
          		{
             			System.out.println("x1: "+ x1 +" + "+ x2 +"i");
             			System.out.println("x2: "+ x1 +" + "+ x2*(-1) +"i");
          		}
		}
		else if(delta == 0)
			System.out.println("x = "+ (-b)/(2*a));
		else
			System.out.println("x1 = "+ (-b - Math.sqrt(delta))/(2*a) +", x2 = "+ (-b + Math.sqrt(delta))/(2*a));
		
	}	
} 