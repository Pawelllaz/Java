import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

    ControlServerThread(ServerSocket newServer, Socket newSocket, List<String> newDrives)
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
        boolean canContinue = true;

        if(getUserName())
        {
            System.out.println("couldn't get Username");
            canContinue = false;
        }
        System.out.println("Connected client: "+ userName);
        while (canContinue) {
            ServerApp.actualAction = "Updated data";
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
            try {
                synchronize = scan.nextLine();

                if (synchronize.equals("downloading files")) {
                    System.out.println("Sending files to " + userName);
                    ServerApp.actualAction = "Sending files to " + userName;

                    delay(500);
                    // synchronize
                    synchronize = scan.nextLine();
                }
                if (synchronize.equals("second step"))
                    writer.println("ready");
                else {
                    System.out.println("client " + userName + " disconnected");
                    break;
                }
            }catch (Exception e)
            {
                System.out.println("client " + userName + " disconnected");
                break;
            }

            // checking if server need to download files
            try{
                synchronize = scan.nextLine();
            }
            catch (Exception e)
            {
                System.out.println("client "+userName+" disconnected");
                break;
            }
            if (synchronize.equals("sending files")) {
                System.out.println("Downloading files from "+userName);
                ServerApp.actualAction = "Downloading files from "+userName;
                while (true) {
                    try {
                        //writer.println("waiting");
                        synchronize = scan.nextLine();
                        if (synchronize.equals("done"))
                            break;
                        new ControlDownloadThreads(server, socket, drives, synchronize, userName).start();
                        writer.println("ok");
                        delay(100);
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
        if(canContinue) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
class StartNewClientSocket extends Thread
{
    private ServerSocket server = null;
    private Socket socket = null;
    private List<String> drives;

    StartNewClientSocket(ServerSocket newServerSocket, List<String> newDrives)//, Socket newSocket)
    {
        server = newServerSocket;
        drives = newDrives;
        //socket = newSocket;
    }

    public void run()
    {
        while(true){
            try
            {
                socket = server.accept();
                new ControlServerThread(server, socket, drives).start();
                Thread.sleep(5000);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
}
//=========================================================================================================================================================
class Server
{
    private ServerSocket server = null;
    //private Socket socket = null;
    public static List<String> drives;

    public Server(int port)
    {
        try {
            server = new ServerSocket(port);
            System.out.println("Server running");
            if (initDrives())
                System.exit(0);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
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
        new StartNewClientSocket(server,drives).start();
    }

    /*public static void main(String[] args) throws Exception
    {
        int port = 1234;
        server serv = new server(port);
        serv.run();
    }*/
}

public class ServerApp extends Application
{
    // dodaj 5 dyskow ktore bedziesz odswiezac i tyle pozdro

    //private static List<String> clientFileList;
    private int port = 1234;
    //private TextField userNameTextField, userDirectoryPathTextField;
    public static ObservableList<String> drive1;
    public static ObservableList<String> drive2;
    public static ObservableList<String> drive3;
    public static ObservableList<String> drive4;
    public static ObservableList<String> drive5;
    public static ListView<String> listView1;
    public static ListView<String> listView2;
    public static ListView<String> listView3;
    public static ListView<String> listView4;
    public static ListView<String> listView5;
    private GridPane gridPane;
    public static String actualAction = "Server running";
    //private String whatIsGoingOn;
    //public static Text currentAction;

    public static void main(String[] args)
    {
        launch(args);
    }

    ListView<String> initListView(ObservableList<String> observableList)
    {
        ListView<String> listView = new ListView<String>();
        listView.setItems(observableList);
        listView.setOrientation(Orientation.VERTICAL);
        listView.setPrefSize(200,400);

        return listView;
    }

    void clearDrivesList()
    {
        drive1.clear();
        drive2.clear();
        drive3.clear();
        drive4.clear();
        drive5.clear();
    }
    void refreshDriveLists()
    {
        clearDrivesList();
        // drive 1
        File dir = new File(Server.drives.get(0));
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if(!file.getName().equals("data.txt"))
                drive1.add(file.getName());
        }
        // drive 2
        dir = new File(Server.drives.get(1));
        filesList = dir.listFiles();
        for (File file : filesList) {
            if(!file.getName().equals("data.txt"))
                drive2.add(file.getName());
        }
        // drive 3
        dir = new File(Server.drives.get(2));
        filesList = dir.listFiles();
        for (File file : filesList) {
            if(!file.getName().equals("data.txt"))
                drive3.add(file.getName());
        }
        // drive 4
        dir = new File(Server.drives.get(3));
        filesList = dir.listFiles();
        for (File file : filesList) {
            if(!file.getName().equals("data.txt"))
                drive4.add(file.getName());
        }
        // drive 5
        dir = new File(Server.drives.get(4));
        filesList = dir.listFiles();
        for (File file : filesList) {
            if(!file.getName().equals("data.txt"))
                drive5.add(file.getName());
        }

    }

    ObservableList<String> initDrive()
    {
        ObservableList<String> newObservableList = FXCollections.observableArrayList();
        newObservableList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //System.out.println("observablelist error");
            }
        });

        return newObservableList;
    }

    /*public static void refreshList()
    {
        clientFileList = ControlClientThread.clientFileNames;
        observableList.clear();
        if(clientFileList != null && clientFileList.size() > 0)
            observableList.addAll(clientFileList);
        //System.out.println(ControlClientThread.clientFileNames);
    }*/

    @Override
    public void start(Stage primaryStage)
    {

        primaryStage.setTitle("Server Application");
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        drive1 = initDrive();
        listView1 = initListView(drive1);
        drive2 = initDrive();
        listView2 = initListView(drive2);
        drive3 = initDrive();
        listView3 = initListView(drive3);
        drive4 = initDrive();
        listView4 = initListView(drive4);
        drive5 = initDrive();
        listView5 = initListView(drive5);

        Text disk = new Text("Drive 1");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(disk, 0, 0);
        disk = new Text("Drive 2");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(disk, 1, 0);
        disk = new Text("Drive 3");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(disk, 2, 0);
        disk = new Text("Drive 4");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(disk, 3, 0);
        disk = new Text("Drive 5");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(disk, 4, 0);

        gridPane.add(listView1,0,1);
        gridPane.add(listView2,1,1);
        gridPane.add(listView3,2,1);
        gridPane.add(listView4,3,1);
        gridPane.add(listView5,4,1);

        Server serv = new Server(port);
        serv.run();
        Text currentAction = new Text(actualAction);
        currentAction.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        gridPane.add(currentAction, 2, 3);

        new AnimationTimer()
        {
            private long lastUpdate = 0;
            private String lastAction = "Server running";
            @Override public void handle(   long now) {   //long currentNanoTime) {
                if(now - lastUpdate >= 2000_000_000 || !actualAction.equals(lastAction)) {//!ControlClientThread.whatAreUDoingFlag.equals(whatIsGoingOn) || now - lastUpdate >= 2000_000_000) {
                    refreshDriveLists();
                    currentAction.setText(actualAction);
                    lastAction = actualAction;
                    //whatIsGoingOn = ControlClientThread.whatAreUDoingFlag;
                    //refreshList();
                    //currentAction.setText(whatIsGoingOn);
                    lastUpdate = now;
                }
            }
        }.start();

        Scene scene = new Scene(gridPane, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
