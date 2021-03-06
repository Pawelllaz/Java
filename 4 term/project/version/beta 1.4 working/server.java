import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;

//=========================================================================================================================================================
class ControlServerThread extends Thread
{
    private Socket socket = null;
    private ServerSocket server = null;
    private PrintStream writer;
    private Scanner scan;
    private List<String> drives;
    private List<String> serverFilesPaths;
    private ObjectOutputStream objectOutputStream;
    private String userName;

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

    void makeServerFilesList()
    {
        try {
            serverFilesPaths = new ArrayList<String>();
            for (int i = 1; i < 6; i++) {
                File dir = new File("C:/java/server/drive_" + i);
                File[] filesList = dir.listFiles();
                for (File file : filesList) {
                    serverFilesPaths.add(file.getPath());
                }
            }
        }
        catch (Exception e) {
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

    boolean getUserName()
    {
        try {
            userName = scan.nextLine();
            writer.println("ok");
        }
        catch (Exception e) {
            System.out.println(e);
            return true;
        }
        return false;
    }
    public void run()
    {
        String synchronize;

        if(getUserName())
        {
            System.out.println("couldn't get Username");
            System.exit(0);
        }
        System.out.println("New client "+ userName);
        while (true) {
            // send file list
            try {
                synchronize = scan.nextLine();
                if (synchronize.equals("client ready")) {
                    writer.println("server ready");
                }
                System.out.println("client "+userName+" refreshing");
                makeServerFilesList();
                objectOutputStream.writeObject(serverFilesPaths);
            } catch (Exception e) {
                System.out.println("client "+userName+" disconnected");
                break;
            }

            // checking if client need to download files
            synchronize = scan.nextLine();
            if (synchronize.equals("downloading files")) {
                System.out.println("Sending files to "+userName);

                delay(500);
                // synchronize
                synchronize = scan.nextLine();
            }
            if (synchronize.equals("second step"))
                writer.println("ready");
            else
            {
                System.out.println("client "+userName+" disconnected");
                break;
            }

            // checking if server need to download files
            synchronize = scan.nextLine();
            if (synchronize.equals("sending files")) {
                System.out.println("Downloading files to "+userName);
                while (true) {
                    try {
                        //writer.println("waiting");
                        synchronize = scan.nextLine();
                        if (synchronize.equals("done"))
                            break;
                        new ControlDownloadThreads(server, socket, drives, synchronize, userName).start();
                        writer.println("ok");
                        delay(200);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            writer.println("done");
            synchronize = scan.nextLine();
            if (!synchronize.equals("have it"))
            {
                System.out.println("client "+userName+" disconnected");
                break;
            }
            // sleep to reduce processor using
            delay(4000);
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//=========================================================================================================================================================
class ControlDownloadThreads extends Thread
{
    private Socket socket = null;
    private ServerSocket server = null;
    private List<String> drives;
    private String userName;
    private String clientFilePath;

    ControlDownloadThreads(ServerSocket newServer, Socket newSocket, List<String> newDrives, String newClientFilePath, String newUserName) throws IOException
    {
        clientFilePath = newClientFilePath;
        userName = newUserName;
        server = newServer;
        socket = newSocket;
        drives = newDrives;
    }

    private int chooseDirectory()
    {
        File file0 = new File(drives.get(0));
        int selectedDrive = 0;
        long minLenDrive = getFileFolderSize(file0);
        for(int i = 1; i < 5; i++) {
            File file = new File(drives.get(i));
            if (minLenDrive > getFileFolderSize(file)) {
                selectedDrive = i;
                minLenDrive = getFileFolderSize(file);
            }
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
        int selectedDrive;
        try
        {
            selectedDrive = chooseDirectory();
            Path path = Paths.get(clientFilePath);

            new DownloadFilesServerThread(server, socket, clientFilePath, drives.get(selectedDrive)).start();
            synchronized (this)
            {
                new SaveRecordThread(server,socket,path.getFileName().toString(),drives.get(selectedDrive),userName).start();
            }

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
//=========================================================================================================================================================
class SaveRecordThread extends Thread
{
    private Socket socket = null;
    private ServerSocket server = null;
    private FileWriter fileWriter;
    private String clientFileName;
    private String strDrivePath;
    private String userName;

    SaveRecordThread(ServerSocket newServer, Socket newSocket, String newClientFilePath, String newStrDrivePath, String newUserName)
    {
        userName = newUserName;
        socket = newSocket;
        server = newServer;
        clientFileName = newClientFilePath;
        strDrivePath = newStrDrivePath;
    }

    public void run()
    {
        strDrivePath += "/data.txt";
        try {
            fileWriter = new FileWriter(strDrivePath, true);
            fileWriter.write(userName +","+ clientFileName+"\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("data saving problem");
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
        writer = new PrintStream(socket.getOutputStream());
    }

    public void run()
    {
        String strNewFilePath = selectedDrivePath;
        strNewFilePath += "/"+Paths.get(clientFilePath).getFileName();

        try {
            Files.copy(Paths.get(clientFilePath), Paths.get(strNewFilePath), StandardCopyOption.REPLACE_EXISTING);
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//=========================================================================================================================================================
public class server
{
    private ServerSocket server = null;
    private Socket socket = null;
    private static List<String> drives;

    public server(int port) throws Exception
    {
        server = new ServerSocket(port);
        System.out.println("Server running");
        if(initDrives())
            System.exit(0);
    }

    boolean initDrives() throws Exception
    {
        drives = new ArrayList<String>();
        String strPath = "C:/java/server/";
        String strDrivePath;
        for(int i = 1; i < 6; i++)
        {
            strDrivePath = strPath;
            strDrivePath += "drive_"+i;
            drives.add(strDrivePath);
            try {
                File file = new File(strDrivePath);
                if(!file.exists()) {
                    if (!file.mkdir()) {
                        System.out.println("Setting server problem - creating disks");
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    void run()
    {
        while(true){
            try
            {
                socket = server.accept();
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