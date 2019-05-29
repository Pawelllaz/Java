import java.net.*;
import java.util.Scanner;
//import java.net.Socket;
import java.io.*;
//import java.io.IOException;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server
{
	/*private DateTimeFormatter date = null; 
	private ServerSocket serverSocket = null;
	private ExecutorService executorService = null; 
	private Scanner scan = null;
	private PrintStream writer = null; 
	Socket socket = null;
	*/
	
	static int iterator = 1;
	public static void main(String[] args) throws IOException
	{
		//try
		//{
			ServerSocket serverSocket = new ServerSocket(1234);
			ExecutorService executorService = Executors.newFixedThreadPool(10);
			//scan = new Scanner(socket.getInputStream());
			//writer = new PrintStream(socket.getOutputStream());
			DateTimeFormatter date = DateTimeFormatter.ofPattern("HH:mm");
	
			System.out.println("Serwer uruchomiony");
		//}	
		/*catch(IOException err)
		{
			System.out.println(err);
		}*/
		System.out.println("Oczekiwanie na klientow...");

		while(true)
		{
			//try
			//{
				final Socket socket = serverSocket.accept();
           		//}
			/*catch(IOException err)
			{
				System.out.println(err);
			}*/

			Runnable connection = new Runnable() 
			{
				@Override
                		public void run() 
				{
					try
					{
						System.out.println("Polaczono z klientem: "+ iterator++);
						String line;
						LocalDateTime currentData;
						Scanner scan = new Scanner(socket.getInputStream());
						PrintStream writer = new PrintStream(socket.getOutputStream());
			
						while(true)
						{	
							line = scan.nextLine();
							if(line.equals("koniec"))
								break;
							System.out.println(line);
							
							currentData = LocalDateTime.now(); 
							line = date.format(currentData);
							writer.println(line);
						}
						socket.close();
						System.out.println("Rozlaczono z klientem");
					}
					catch(IOException err)
					{
						err.printStackTrace();
					}
				}

			};
			executorService.submit(connection);
		}
	}
}