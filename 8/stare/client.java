import java.net.*;
import java.util.Scanner;
import java.io.*;

public class client
{
	private Scanner scanWriter = null;
	private Scanner scanReader = null;
	private Socket socket = null;
	private PrintStream writer = null;
	
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

		String line = "";
		
		while(true)
		{
			try
			{	
				System.out.println("wpisz tekst do przekazania:");
				line = scanWriter.nextLine();
				writer.println(line);
				if(line.equals("koniec"))
					break;

				line = scanReader.nextLine();
				System.out.println("odebrano: "+ line);
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
    	} 
  
    	public static void main(String args[])
    	{ 
      		client c = new client("127.0.0.1", 1234); 
    	} 
}