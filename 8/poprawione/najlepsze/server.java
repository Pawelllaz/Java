import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 
import java.text.*;

public class server
{

	private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	static int iterator = 1;

	public static void main(String[] args) throws Exception
	{
		ServerSocket serverSocket = new ServerSocket(1234);
		ExecutorService executorService = Executors.newFixedThreadPool(20);
	
		System.out.println("Serwer uruchomiony");
		System.out.println("Oczekiwanie na klientow...");

		while(true)
		{
			final Socket socket = serverSocket.accept();

			Runnable connection = new Runnable() 
			{
				@Override
                		public void run() 
				{
					try
					{
						System.out.println("Polaczono z klientem: "+ socket.getPort());
						String line, date, currDate = "";
						Scanner scan = new Scanner(socket.getInputStream());
						PrintStream writer = new PrintStream(socket.getOutputStream());
						Date now = new Date();

						while(true)
						{	
							line = scan.nextLine();
							if(line.equals("koniec"))
								break;							
							date = scan.nextLine();

							System.out.println("("+ socket.getPort() +") przesyla wiadomosc: \n\t"+ line);
							System.out.println("("+ socket.getPort() +") godzina odeslania: "+ date);
							
							while(true)
							{
								now = new Date();
								if(date.equals(format.format(now)))
									break;
							}
							writer.println(line);
							System.out.println("("+ socket.getPort() +") odeslano wiadomosc o "+ date);
						}
						socket.close();
						System.out.println("Rozlaczono z klientem: "+ socket.getPort());
					}
					catch(IOException err)
					{
						err.printStackTrace();
					}
					catch(Exception err)
					{
						System.out.println(err);
					}
				}

			};
			executorService.submit(connection);
		}
	}
}