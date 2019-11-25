import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.*;
import java.nio.file.*;

//=========================================================================================================================================================
class ControlServerThread extends Thread
{
    private Socket socket = null;
    private ServerSocket server = null;
    private PrintStream writer;
    private Scanner scan;
    private List<String> drives;
    //private List<String> clientFilesPaths;
    private List<String> serverFilesPaths;
    private ObjectOutputStream objectOutputStream;

    ControlServerThread(ServerSocket newServer, Socket newSocket, List<String> newDrives) throws Exception
    {
        server = newServer;
        socket = newSocket;
        drives = newDrives;
        init();
    }

    private void init()
    {
        try
        {
            writer = new PrintStream(socket.getOutputStream());
            scan = new Scanner(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    void makeServerFilesList() {
        // dorobic CSV
        try {
            serverFilesPaths = new ArrayList<String>();
            File dir = new File("C:/java/server");
            File[] filesList = dir.listFiles();
            for (File file : filesList) {
                serverFilesPaths.add(file.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        while (true) {
            // send file list
            try {
                synchronize = scan.nextLine();
                System.out.println(synchronize);
                if (synchronize.equals("client ready")) {
                    writer.println("server ready");
                }
                makeServerFilesList();
                objectOutputStream.writeObject(serverFilesPaths);
            } catch (Exception e) {
                System.out.println(e);
            }

            // checking if client need to download files
            synchronize = scan.nextLine();
            if (synchronize.equals("downloading files")) {
                System.out.println("Sending files");

                delay(500);
                // synchronize
                synchronize = scan.nextLine();
            }
            if (synchronize.equals("second step"))
                writer.println("ready");

            // checking if server need to download files
            synchronize = scan.nextLine();
            if (synchronize.equals("sending files")) {
                while (true) {
                    System.out.println("Downloading files...");
                    try {
                        synchronize = scan.nextLine();   // wymaga przerobienia na ControlDownloadFiles
                        if (synchronize.equals("done"))
                            break;
                        new DownloadFilesServerThread(server, socket, synchronize, "C:/java/server/").start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            writer.println("done");
            synchronize = scan.nextLine();
            if (!synchronize.equals("have it"))
                System.out.println("error");
            // sleep to reduce processor using
            delay(4000);
        }
    }
}
//=========================================================================================================================================================
class ControlDownloadThreads extends Thread
{
    private Socket socket = null;
    private ServerSocket server = null;
    //private PrintStream writer;
    private Scanner scan;
    private List<String> drives;

    ControlDownloadThreads(ServerSocket newServer, Socket newSocket, List<String> newDrives) throws IOException
    {
        server = newServer;
        socket = newSocket;
        scan = new Scanner(socket.getInputStream());
        drives = newDrives;
    }

    private int chooseDirectory()
    {
        File file = new File(drives.get(0));
        int selectedDrive = 0;
        long minLenDrive = getFileFolderSize(file);
        for(int i = 1; i < 5; i++)
        {
            file = new File(drives.get(i));
            if(minLenDrive > file.length())
            {
                selectedDrive = i;
                minLenDrive = getFileFolderSize(file);
                //System.out.println(drives.get(i));
            }
            System.out.println("pojemnosc dysku "+ i+" wynosi: "+ getFileFolderSize(file)+"\nselected "+selectedDrive);
        }
        return selectedDrive;
    }

    private static long getFileFolderSize(File dir)
    {
        long size = 0;
        if(dir.isDirectory()) {
            for(File file : dir.listFiles()) {
                if(file.isFile())
                    size+=file.length();
                else
                    size+= getFileFolderSize(file);
            }
        }
        else if(dir.isFile())
            size+=dir.length();

        return size;
    }

    public void run()
    {
        String clientFilePath = "";
        int selectedDrive;
        while(true)
        {
            try
            {
                selectedDrive = chooseDirectory();
                clientFilePath = scan.nextLine();
                new DownloadFilesServerThread(server, socket, clientFilePath, drives.get(selectedDrive)).start();
            }
            catch(Exception e)
            {
                System.out.println(e);
                break;
            }
        }
    }
}
//=========================================================================================================================================================
class DownloadFilesServerThread extends Thread
{
    private Socket socket = null;
    private ServerSocket server = null;
    private PrintStream writer;
    private Scanner scan;
    private String clientFilePath;
    private String selectedDrivePath;

    DownloadFilesServerThread(ServerSocket newServer, Socket newSocket, String clientFilePath, String selectedDrivePath) throws IOException
    {
        server = newServer;
        socket = newSocket;
        this.clientFilePath = clientFilePath;
        this.selectedDrivePath = selectedDrivePath;
        init();
    }

    void init()
    {
        try
        {
            writer = new PrintStream(socket.getOutputStream());
            //scan = new Scanner(socket.getInputStream());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    void copyFile(String from, String to)
    {
        try
        {
            Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("kopiowanie z: "+from+", do: "+to);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void run()
    {
        String strNewFilePath = selectedDrivePath;
        strNewFilePath += Paths.get(clientFilePath).getFileName();
        //writer.println("ok");
        System.out.println("downloading file: "+ Paths.get(clientFilePath).getFileName());
        copyFile(clientFilePath, strNewFilePath);
    }
}
//=========================================================================================================================================================
public class server
{
    private ServerSocket server = null;
    private Socket socket = null;
    //private Path serverDirectoryPath;
    private static List<String> drives;

    public server(int port) throws Exception
    {
        server = new ServerSocket(port);
        System.out.println("Server running");
        initDrives();
    }

    void initDrives() throws Exception
    {
        drives = new ArrayList<String>();
        //serverDirectoryPath = Paths.get("C:/java/server");
        String strPath = "C:/java/server/";
        String strDrivePath;
        for(int i = 1; i < 6; i++)
        {
            strDrivePath = strPath;
            strDrivePath += "dir"+i+"/";
            drives.add(strDrivePath);
            //System.out.println(drives.get(i - 1));
        }
    }

    void run()
    {
        while(true){
            try
            {
                socket = server.accept();
                System.out.println("new Client appear");
                new ControlServerThread(server, socket, drives).start();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        int port = 1234;
        server serv = new server(port);
        serv.run();
    }
}