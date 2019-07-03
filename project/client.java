import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;
import java.util.*;

class WatchingClientDirectory
{
	private WatchService watchService;
	private Path clientDirectoryPath;
	private Socket socket;
	
	WatchingClientDirectory(Socket newSocket, Path newClientDirectoryPath) throws Exception
	{
		clientDirectoryPath = newClientDirectoryPath;
		socket = newSocket;
		init();
	}
	
	void init()
	{
		try
		{
			watchService = FileSystems.getDefault().newWatchService();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	void startWatching() throws Exception
	{
		String strFilePath;
		
		clientDirectoryPath.register(watchService,
			StandardWatchEventKinds.ENTRY_CREATE/*,
			StandardWatchEventKinds.ENTRY_DELETE,
			StandardWatchEventKinds.ENTRY_MODIFY*/);
			
		WatchKey watchKey;
		while ((watchKey = watchService.take()) != null) 
		{
			for (WatchEvent<?> event : watchKey.pollEvents()) 
				{
				strFilePath = clientDirectoryPath.toString();
				strFilePath += "/"+event.context().toString();
				new SendFilesClientThread(socket, Paths.get(strFilePath)).start();
			}
			
			watchKey.reset();
		}
	}
}

class SendFilesClientThread extends Thread
{
	private Socket socket;
	private String filePath;
	private PrintStream writer;
	private Scanner scan;
	
	SendFilesClientThread(Socket newSocket, Path newPath) throws IOException
	{
		this.filePath = newPath.toString();
		this.socket = newSocket;
		init();
	}
	
	void init() 
	{
		try
		{
			writer = new PrintStream(socket.getOutputStream());
			scan = new Scanner(socket.getInputStream());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public void run()
	{
			writer.println(filePath);
			System.out.println("sending: "+filePath);
			
			String isOk = scan.nextLine();
			if(isOk.equals("ok"))
				System.out.println("ok");				
        
	}  
}


public class client
{
	private Socket socket = null;
	private WatchingClientDirectory watcher;
	private Path clientDirectoryPath;
	//private List<String> files = new ArrayList<String>();
	
	public client(String address, int port, String userName, String filePath)
	{
		System.out.println("Hello "+ userName);
		try
		{
			clientDirectoryPath = Paths.get("C:/java/client");
			socket = new Socket(address, port);
			System.out.println("Connected to server");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	void run()
	{
		try
		{
			watcher = new WatchingClientDirectory(socket, clientDirectoryPath);
			watcher.startWatching();;
		}	
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	void killClient()
	{
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
		String address = "127.0.0.1";
		int port = 1234;
		
   		client c = new client(address, port, args[0], args[1]);
		c.run();
		//c.killClient();	
  	}
	
}