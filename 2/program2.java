class Word{
	public static void main(String []args){

	if(args.length != 3){
		System.out.println("bledna liczba argumentow");
		return;
	}
	String word = args[0];
	int n1 = Integer.parseInt(args[1]);
	int n2 = Integer.parseInt(args[2]);
	if(n1 < 1 || n2 < 1){
		System.out.println("zle argumenty");
		return;
	}
	n2 += 1;

	String sub_word = word.substring(n1,n2);
	System.out.println(sub_word);

	char tab[] = word.toCharArray();
	for(int i = n1; i < n2; i++){
		System.out.print(tab[i]);		
	}
	
	}	
} 