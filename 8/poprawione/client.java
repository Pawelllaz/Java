import java.net.*;
import java.util.Scanner;
import java.io.*;

class WrongDateException extends Exception
{
	//public final String date;
	public WrongDateException(String date)
	{
		int flag = 0;
		if(date.charAt(0) > '2' || date.charAt(0) < '0') 
			flag = 1;
		else if(date.charAt(1) > '9' || date.charAt(1) < '0')
			flag = 1;
		else if(date.charAt(2) != ':')
			flag = 1;
		else if(date.charAt(3) > '9' || date.charAt(3) < '0' || date.charAt(4) > '9' || date.charAt(4) < '0')
			flag = 1;
		if(flag == 1)
			System.out.println("zla data");
	}
}
public class client
{
	private Scanner scanWriter = null;
	private Scanner scanReader = null;
	private Socket socket = null;
	private PrintStream writer = null;
	
	static boolean check_message(String message)
	{
		if(message.length() < 1 || message.length() > 50) 
			return true;
		return false; 
	}

	
	static boolean check_date(String date)
	{
		if(date.charAt(0) > '2' || date.charAt(0) < '0') 
			return false;
		else if(date.charAt(1) > '9' || date.charAt(1) < '0')
			return false;
		else if(date.charAt(2) != ':')
			return false;
		else if(date.charAt(3) > '9' || date.charAt(3) < '0' || date.charAt(4) > '9' || date.charAt(4) < '0')
			return false;

		return true;
	}

	public client(String address, int port)
	{
		try
		{
			socket = new Socket(address, port);
			System.out.println("Polaczenie ok!, aby zakonczyc wpisz 'koniec'");
			
			scanWriter = new Scanner(System.in);
			scanReader = new Scanner(socket.getInputStream());
			writer = new PrintStream(socket.getOutputStream());
		}
		catch(UnknownHostException error) 
        	{ 
        		System.out.println(error); 
        	} 
        	catch(IOException err) 
        	{ 
        		System.out.println(err); 
        	}
		
		System.out.println("Polaczono z " + socket.getLocalPort());
		
		String line = "";
		String date = "";

		while(true)
		{
			boolean exit = false;
			try
			{	
				System.out.println("wpisz tekst do przekazania:");
				line = scanWriter.nextLine();
				if(check_message(line))
				{
					System.out.println("Blednie wprowadzone dane");
					continue;
				}

				if(line.equals("koniec"))
				{
					writer.println(line);
					exit = true;
				}
				if(exit)
					break;
				while(true)
				{
					System.out.println("wpisz godzine:");
					date = scanWriter.nextLine();
					if(check_date(date))
						break;
					System.out.println("bledna data");
				}
				writer.println(line);
				writer.println(date);
				
				System.out.println("wiadomosc wyslana");
				
				line = scanReader.nextLine();
				System.out.println("odebrano wiadomosc: "+ line);
            		} 
            		catch(Exception err) 
            		{ 
                		System.out.println(err); 
				break;
            		} 
		}
		try
        	{
            		socket.close(); 
        	} 
        	catch(IOException err) 
        	{ 
            		System.out.println(err); 
        	} 
		System.out.println("Zakonczono polaczenie z "+ socket.getLocalPort());
    	} 
  
    	public static void main(String args[])
    	{ 
      		client c = new client("127.0.0.1", 1234); 
    	} 
}