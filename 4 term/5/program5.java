import java.util.Random;
import java.util.*;
import java.io.*;
import java.io.FileReader;
import java.util.Scanner;

class reader{
	
	public static void main(String[] args)
	{
		Random rand = new Random();
		Scanner scan = new Scanner(System.in);
		FileReader reader = null;
		
		System.out.println("wprowadz nazwe pliku");
		String fileName = scan.nextLine();

		try
		{
			writeIO(fileName);
		}
		catch(Exception error){
			System.out.println("error: "+ error);
		}
		
		
		int randNumber;
		
		try
		{
			reader = new FileReader(fileName);
		}
		catch(Exception e){
			System.out.println("error: "+ e);
		}
		while(true)
		{
			System.out.println("Nacisnij dowolny klawisz");
			randNumber = rand.nextInt(5) + 1;
			
			scan.next();
			try
			{
				if(read(reader,randNumber) == 1){
					break;
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
				break;			
			}
		}
		
	}

	public static void writeIO(String fileName) throws Exception
	{
		OutputStream out = new FileOutputStream("sciezka_do_pliku.txt");
		Writer wr = new OutputStreamWriter(out);
		wr.write(fileName);
		wr.close();
		out.close();
	}

	public static int read(FileReader r, int randNumber) throws Exception
	{
		int ch;

		for(int i = 0; i < randNumber; i++)
		{
			ch = r.read();
			if(ch == -1){
				return 1;
			}
			System.out.print((char)ch);
		}
		System.out.print("\n");
		return 0;
	}
} 