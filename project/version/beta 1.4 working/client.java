import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

class DownloadFilesClientThread extends Thread
{
    private Socket socket = null;
    private String from;
    private String to;

    DownloadFilesClientThread(Socket newSocket, String from, String to)
    {
        socket = newSocket;
        this.from = from;
        this.to = to;
    }

    public void run()
    {
        try {
            Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private String userName;
    private List<String> serverFilesForUser;

    ControlClientThread(Socket newSocket, String newClientDirectoryPath, String newUserName)
    {
        clientDirectoryPath = newClientDirectoryPath;
        socket = newSocket;
        userName = newUserName;
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

    boolean makeClientFilesList()
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
            return true;
        }
        return false;
    }

    boolean getClientServerFileLists()
    {
        String synchronize;
        try {
            writer.println("client ready");
            synchronize = scan.nextLine();
            if (!synchronize.equals("server ready")) {
                System.out.println("Huston we have a problem");
                return true;
            }
            System.out.println("refreshing...");
            makeClientFilesList();
            serverFilesPaths = (List<String>) objectInputStream.readObject();
            try {
                clientFileNames = getFileNameList(clientFilesPaths);
                serverFileNames = getFileNameList(serverFilesPaths);
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
            return true;
        }
        return false;
    }

    void checkClientFiles()
    {
        String pathToCopy;
        int flag = 0;

        for (int i = 0; i < serverFilesForUser.size(); i++) {
            if (clientFileNames.indexOf(serverFilesForUser.get(i)) == -1) {
                if (flag == 0) {
                    writer.println("downloading files");
                    System.out.println("downloading files");
                    flag++;
                }
                pathToCopy = clientDirectoryPath;
                pathToCopy += "\\" + serverFilesForUser.get(i);

                int index = serverFileNames.indexOf(serverFilesForUser.get(i));
                new DownloadFilesClientThread(socket, serverFilesPaths.get(index), pathToCopy).start();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    boolean readServerRecords()
    {
        List<Integer> numberOfFilesPaths = new ArrayList<Integer>();
        String line;
        serverFilesForUser = new ArrayList<String>();
        for(int i=0; i < serverFileNames.size(); i++) {
            if (serverFileNames.get(i).equals("data.txt"))
                numberOfFilesPaths.add(i);
        }

        for(int i = 0; i < numberOfFilesPaths.size(); i++) {
            try {
                File file = new File(serverFilesPaths.get(numberOfFilesPaths.get(i)));
                Scanner fileReader = new Scanner(file);

                while (fileReader.hasNextLine()) {
                    line = fileReader.nextLine();
                    String[] lineDivided = line.split(",");

                    if(lineDivided[0].equals(userName) && lineDivided.length > 1) {
                        try {
                            serverFilesForUser.add(lineDivided[1]);
                        }
                        catch (Exception e)
                        {
                            System.out.println("");
                        }
                    }
                }
                fileReader.close();
            }
            catch (Exception e)
            {
                System.out.println(e);
                return true;
            }
        }
        return false;
    }

    boolean checkServerFiles()
    {
        String synchronize;

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
                    synchronize = scan.nextLine();
                    if(!synchronize.equals("ok"))
                        System.out.println("problem");
                } catch (IOException e) {
                    System.out.println(e);
                    return true;
                }
            }
        }
        return false;
    }
    void delay(int delayValue)
    {
        try {
            Thread.sleep(delayValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean sendUserName()
    {
        String message;
        try {
            writer.println(userName);
            message = scan.nextLine();
            if(!message.equals("ok"))
                return true;
        } catch (Exception e) {
            System.out.println(e);
            return true;
        }
        return false;
    }
    public void run()
    {
        String synchronize;

        if(sendUserName())
        {
            System.out.println("couldn't send Username");
            System.exit(0);
        }

        while (true)
        {
            if(getClientServerFileLists())
            {
                System.out.println("server disconnected");
                break;
            }

            if(readServerRecords())
            {
                System.out.println("server disconnected");
                break;
            }
            checkClientFiles();
            delay(1000);

            // synchronize
            writer.println("second step");
            synchronize = scan.nextLine();
            if (!synchronize.equals("ready"))
            {
                System.out.println("server disconnected");
                break;
            }

            if(checkServerFiles())
            {
                System.out.println("server disconnected");
                break;
            }

            // sleep to synchronize
            delay(1000);

            // send thats ok
            writer.println("done");
            synchronize = scan.nextLine();
            if (synchronize.equals("done")) {
                writer.println("have it");
            }
            else
            {
                System.out.println("server disconnected");
                break;
            }
            // sleep 5 sec to reduce using processor
            delay(5000);
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SendFilesClientThread extends Thread
{
    private Socket socket;
    private String filePath;
    private PrintStream writer;
    //private Scanner scan;

    SendFilesClientThread(Socket newSocket, Path newPath) throws IOException
    {
        this.filePath = newPath.toString();
        this.socket = newSocket;
        writer = new PrintStream(socket.getOutputStream());
    }

    public void run()
    {
        synchronized (this) {
            writer.println(filePath);
        }

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
    private String userName;

    public client(String address, int port, String userName, String filePath)
    {
        System.out.println("Hello "+ userName);
        try
        {
            clientDirectoryPath = Paths.get(filePath);
            socket = new Socket(address, port);
            System.out.println("Connected to server");
            this.userName = userName;
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
            new ControlClientThread(socket,clientDirectoryPath.toString(), userName).start();
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
        Scanner scan = new Scanner(System.in);
        System.out.print("Wprowadz nazwe uzytkownika: ");
        String userName = scan.nextLine();
        System.out.println("Wprowadz sciezke do folderu: ");
        String path = scan.nextLine();
        client c = new client(address, port, userName,path);
        c.run();
        //c.killClient();
    }
}