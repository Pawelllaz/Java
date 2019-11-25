import java.net.*;
import java.sql.Struct;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import  java.io.File.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;
import java.util.*;


// smutny widok kodu ktory pewnie sie nie przyda :(
/*class WatchingClientDirectory
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
			StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_DELETE,
			StandardWatchEventKinds.ENTRY_MODIFY);

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
}*/

class DownloadFilesClientThread extends Thread
{
    private Socket socket = null;
    //private PrintStream writer;
    //private Scanner scan;
    private String from;
    private String to;

    DownloadFilesClientThread(Socket newSocket, String from, String to)
    {
        socket = newSocket;
        this.from = from;
        this.to = to;
    }

    void copyFile(String from, String to)
    {
        try
        {
            Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("copy from: "+from+", to: "+to);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void run()
    {
         copyFile(from, to);
    }
}

class ControlClientThread extends Thread
{
    private String clientDirectoryPath;
    private Socket socket;
    private Scanner scan;
    private PrintStream writer;
    private List<String> clientFilesPaths;
    private List<String> serverFilesPaths;
    private List<String> clientFileNames;
    private List<String> serverFileNames;
    private ObjectInputStream objectInputStream;


    ControlClientThread(Socket newSocket, String newClientDirectoryPath)
    {
        clientDirectoryPath = newClientDirectoryPath;
        socket = newSocket;
        init();
    }

    void init()
    {
        try
        {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            writer = new PrintStream(socket.getOutputStream());
            scan = new Scanner(socket.getInputStream());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    List<String> getFileNameList(List<String> list) throws Exception
    {
        List<String> newFileNames = new ArrayList<String>();
        Path path;
        for(int i = 0; i < list.size(); i++)
        {
            path = Paths.get(list.get(i));

            newFileNames.add(path.getFileName().toString());
        }
        return newFileNames;
    }

    void makeClientFilesList()
    {
        try {
            clientFilesPaths = new ArrayList<String>();
            File dir = new File(clientDirectoryPath);
            File[] filesList = dir.listFiles();
            for (File file : filesList)
            {
                clientFilesPaths.add(file.getPath());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void getClientServerFileLists()
    {
        String synchronize;
        try {
            writer.println("client ready");
            synchronize = scan.nextLine();
            if (!synchronize.equals("server ready")) {
                System.out.println("Huston we have a problem");//writer.println("send me");
            }
            System.out.println(synchronize);
            makeClientFilesList();
            serverFilesPaths = (List<String>) objectInputStream.readObject();
            try {
                clientFileNames = getFileNameList(clientFilesPaths);
                serverFileNames = getFileNameList(serverFilesPaths);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("servera:  " + serverFilesPaths);
        System.out.println("clienta:  " + clientFilesPaths);
    }

    void checkClientFiles()
    {
        String pathToCopy;
        int flag = 0;

        for (int i = 0; i < serverFileNames.size(); i++) {
            if (clientFileNames.indexOf(serverFileNames.get(i)) == -1) {
                if (flag == 0) {
                    writer.println("downloading files");
                    System.out.println("downloading files");
                    flag++;
                }
                pathToCopy = clientDirectoryPath;
                pathToCopy += "\\" + Paths.get(serverFilesPaths.get(i)).getFileName();

                new DownloadFilesClientThread(socket, serverFilesPaths.get(i), pathToCopy).start();
            }
        }
    }

    void checkServerFiles()
    {
        int flag = 0;
        for (int i = 0; i < clientFileNames.size(); i++) {
            if (serverFileNames.indexOf(clientFileNames.get(i)) == -1) {
                if (flag == 0) {
                    writer.println("sending files");
                    System.out.println("sending files");
                    flag++;
                }
                try {
                    new SendFilesClientThread(socket, Paths.get(clientFilesPaths.get(i))).start();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }
    void delay(int delayValue)
    {
        try {
            Thread.sleep(delayValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void run()
    {
        String synchronize;

        while (true)
        {
            // zrob, zeby te funkcje cos zwracaly to jak sie cos wysypie bedzie moglo sie zresetowac
            getClientServerFileLists();

            checkClientFiles();

            // sleep
            delay(1000);

            // synchronize
            writer.println("second step");
            synchronize = scan.nextLine();
            if (!synchronize.equals("ready"))
                System.out.println("error");

           checkServerFiles();


            // sleep to synchronize
            delay(1000);

            // send thats ok
            writer.println("done");
            synchronize = scan.nextLine();
            if (synchronize.equals("done")) {
                writer.println("have it");
            }
            // sleep 5 sec to reduce using processor
            delay(5000);
        }
    }
}

class SendFilesClientThread extends Thread
{
    private Socket socket;
    private String filePath;
    private PrintStream writer;

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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class client
{
    private Socket socket = null;
    private Path clientDirectoryPath;

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
            new ControlClientThread(socket,clientDirectoryPath.toString()).start();
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

        client c = new client(address, port, "pawel", "a"/*args[0], args[1]*/);
        c.run();
        //c.killClient();
    }
}