import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//=========================================================================================================================================================

/**
 * This is thread which controls synchronization on server
 * Each appeared client gets that thread
 */
class ControlServerThread extends Thread {
    private Socket socket = null;
    private ServerSocket server = null;
    private PrintStream writer;
    private Scanner scan;
    private List<String> drives;
    private List<String> serverFilesPaths;
    private ObjectOutputStream objectOutputStream;
    private String userName;
    private ExecutorService thPool;

    /**
     * Thread contructor that takes server socket, client socket and list of drives paths
     * @param newServer server socket
     * @param newSocket client socket
     * @param newDrives list of drives paths
     */
    ControlServerThread(ServerSocket newServer, Socket newSocket, List<String> newDrives)throws Exception{
        server = newServer;
        socket = newSocket;
        drives = newDrives;
        init();
        thPool = Executors.newCachedThreadPool();
    }

    /**
     * initiate printStream an scanner to communitace to client
     */
    private void init() throws Exception{
            writer = new PrintStream(socket.getOutputStream());
            scan = new Scanner(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * makes server file paths list
     */
    void makeServerFilesList() {
        try {
            serverFilesPaths = new ArrayList<String>();
            for (int i = 1; i < 6; i++) {
                File dir = new File("C:/java/server/drive_" + i);
                File[] filesList = dir.listFiles();
                for (File file : filesList) {
                    serverFilesPaths.add(file.getPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ServerApp.actualAction = "Server files ERROR";
        }
    }

    /**
     * delay main Thread
     * @param delayValue amount of time that delay will continue
     */
    void delay(int delayValue) {
        try {
            Thread.sleep(delayValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets client username and send "ok"
     * if receive message
     * @return return true if get username otherwise false
     */
    boolean getUserName() {
        try {
            userName = scan.nextLine();
            writer.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    /**
     * Main function of thread
     * let keep server be synchronized with client
     */
    public void run() {
        String synchronize;
        boolean canContinue = true;

        if (getUserName()) {
            System.out.println("couldn't get Username");
            canContinue = false;
        }
        System.out.println("Connected client: " + userName);
        while (canContinue) {
            if (ServerApp.updatedData)
                ServerApp.actualAction = "Updated data";
            // send file list and usernames
            try {
                synchronize = scan.nextLine();
                if (!synchronize.equals("client ready")) {
                    //writer.println("server ready");
                    System.out.println("problem");
                }
                System.out.println("client " + userName + " refreshing");
                makeServerFilesList();
                synchronized (objectOutputStream) {
                    objectOutputStream.writeObject(serverFilesPaths);
                }
            } catch (Exception e) {
                System.out.println("client " + userName + " disconnected");
                break;
            }

            // checking if client need to download files
            try {
                synchronize = scan.nextLine();

                if (synchronize.equals("downloading files")) {
                    System.out.println("Sending files to " + userName);
                    ServerApp.actualAction = "Sending files to " + userName;
                    ServerApp.updatedData = false;
                    //delay(500);
                    // synchronize
                    synchronize = scan.nextLine();
                }
                if (synchronize.equals("second step"))
                    writer.println("ready");
                else {
                    System.out.println("client " + userName + " disconnected");
                    break;
                }
            } catch (Exception e) {
                System.out.println("client " + userName + " disconnected");
                break;
            }
            ServerApp.updatedData = true;
            // checking if server need to download files
            try {
                synchronize = scan.nextLine();
            } catch (Exception e) {
                System.out.println("client " + userName + " disconnected");
                break;
            }
            if (synchronize.equals("sending files")) {
                System.out.println("Downloading files from " + userName);
                ServerApp.actualAction = "Downloading files from " + userName;
                while (true) {
                    try {
                        //writer.println("waiting");
                        synchronize = scan.nextLine();
                        if (synchronize.equals("done"))
                            break;
                        new ControlDownloadThreads(server, socket, drives, synchronize, userName).start();
                        writer.println("ok");
                        delay(150);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            writer.println("done");
            synchronize = scan.nextLine();
            if (!synchronize.equals("have it")) {
                System.out.println("client " + userName + " disconnected");
                break;
            }
            // sleep to reduce processor using
            delay(9000);
        }
        if (canContinue) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

//=========================================================================================================================================================

/**
 * Thread that control downloading to right drive
 */
class ControlDownloadThreads extends Thread {
    private Socket socket = null;
    private ServerSocket server = null;
    private List<String> drives;
    private String userName;
    private String clientFilePath;

    /**
     * Thread constructor
     * @param newServer server socket
     * @param newSocket client socket
     * @param newDrives list of drive paths
     * @param newClientFilePath path to client file
     * @param newUserName username
     */
    ControlDownloadThreads(ServerSocket newServer, Socket newSocket, List<String> newDrives, String newClientFilePath, String newUserName) throws Exception {
        if(newServer == null || newSocket == null || newClientFilePath == null || newDrives == null || newUserName == null)
            throw new NullPointerException();
        clientFilePath = newClientFilePath;
        userName = newUserName;
        server = newServer;
        socket = newSocket;
        drives = newDrives;
    }

    /**
     * Function returns directory size
     * @param dir directory file
     * @return size of directory
     */
    private static long getFileFolderSize(File dir) {
        long size = 0;
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile())
                    size += file.length();
                else
                    size += getFileFolderSize(file);
            }
        } else if (dir.isFile())
            size += dir.length();

        return size;
    }

    /**
     * Function returns drive iterator which size is the smallest
     * @return iterator of list of drives
     */
    private int chooseDirectory() {
        File file0 = new File(drives.get(0));
        int selectedDrive = 0;
        long minLenDrive = getFileFolderSize(file0);
        for (int i = 1; i < 5; i++) {
            File file = new File(drives.get(i));
            if (minLenDrive > getFileFolderSize(file)) {
                selectedDrive = i;
                minLenDrive = getFileFolderSize(file);
            }
        }
        return selectedDrive;
    }

    /**
     * Thread core
     */
    public void run() {
        try {
            Thread.sleep((long) (Math.random() * 10000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int selectedDrive;
        try {
            selectedDrive = chooseDirectory();
            Path path = Paths.get(clientFilePath);

            new DownloadFilesServerThread(server, socket, clientFilePath, drives.get(selectedDrive)).start();
            synchronized (this) {
                new SaveRecordThread(path.getFileName().toString(), drives.get(selectedDrive), userName).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//=========================================================================================================================================================

/**
 * Thread that save information about files into drive
 * it write username and file to data.txt into drive
 */
class SaveRecordThread extends Thread {
    private FileWriter fileWriter;
    private String clientFileName;
    private String strDrivePath;
    private String userName;

    /**
     * Thread constructor
     * @param newClientFileName name of file that will be written
     * @param newStrDrivePath path to drive
     * @param newUserName client username
     */
    public SaveRecordThread(String newClientFileName, String newStrDrivePath, String newUserName) throws Exception{
        if(newClientFileName == null || newStrDrivePath == null || newUserName == null)
            throw new NullPointerException();
        userName = newUserName;
        clientFileName = newClientFileName;
        strDrivePath = newStrDrivePath;
    }

    /**
     * Thread core
     */
    public void run() {
        synchronized (this) {
            strDrivePath += "/data.txt";
            try {
                fileWriter = new FileWriter(strDrivePath, true);
                fileWriter.write(userName + "," + clientFileName + "\n");
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("data saving problem");
            }
        }
    }
}

//=========================================================================================================================================================

/**
 * Thread that download a file from client
 */
class DownloadFilesServerThread extends Thread {
    private Socket socket = null;
    private ServerSocket server = null;
    private PrintStream writer;
    private Scanner scan;
    private String clientFilePath;
    private String selectedDrivePath;

    /**
     * Thread constructor
     * @param newServer server socket
     * @param newSocket client socket
     * @param clientFilePath path to client file
     * @param selectedDrivePath path to drive
     */
    DownloadFilesServerThread(ServerSocket newServer, Socket newSocket, String clientFilePath, String selectedDrivePath) throws IOException {
        server = newServer;
        socket = newSocket;
        this.clientFilePath = clientFilePath;
        this.selectedDrivePath = selectedDrivePath;
        writer = new PrintStream(socket.getOutputStream());
    }

    /**
     * Thread core
     */
    public void run() {
        String strNewFilePath = selectedDrivePath;
        strNewFilePath += "/" + Paths.get(clientFilePath).getFileName();

        try {
            Files.copy(Paths.get(clientFilePath), Paths.get(strNewFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//=========================================================================================================================================================

/**
 * Thread that is responsible for starting control threads when new client appear
 */
class StartNewClientSocket extends Thread {
    private ServerSocket server = null;
    private Socket socket = null;
    private List<String> drives;
    private ExecutorService executorService;
    //private ExecutorService thPool;
    /**
     * Thread constructor
     * @param newServerSocket server socket
     * @param newDrives list of drive paths
     */
    StartNewClientSocket(ServerSocket newServerSocket, List<String> newDrives) throws Exception
    {
        executorService = Executors.newFixedThreadPool(5);
        if(newDrives==null || newDrives==null)
            throw new NullPointerException();
        server = newServerSocket;
        drives = newDrives;
        //socket = newSocket;
    }

    /**
     * Thread core
     */
    public void run() {
        while (true) {
            try {
                socket = server.accept();
                //new ControlServerThread(server,socket,drives).start();
                executorService.submit(new ControlServerThread(server, socket, drives));
                //Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

//=========================================================================================================================================================

/**
 * Class that initiate thread to catch new clients
 * It also creates drive if needed
 */
class Server {
    public static List<String> drives;
    private ServerSocket server = null;

    /**
     * Class constructor
     * Start new server socket
     * Create list of drive paths
     * Create drive if needed
     * @param port number of port
     */
    public Server(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server running");
            if (initDrives())
                System.exit(0);
            StartNewClientSocket newClient = new StartNewClientSocket(server, drives);
            newClient.setDaemon(true);
            newClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function create drive if doesn't exists
     * Creates list of drive paths
     * @return
     * @throws Exception
     */
    boolean initDrives() throws Exception {
        drives = new ArrayList<String>();
        String strPath = "C:/java/server/";
        String strDrivePath;
        for (int i = 1; i < 6; i++) {
            strDrivePath = strPath;
            strDrivePath += "drive_" + i;
            drives.add(strDrivePath);
            try {
                File file = new File(strDrivePath);
                if (!file.exists()) {
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
}

//=========================================================================================================================================================

/**
 * JavaFX class that makes window
 */
public class ServerApp extends Application {
    public static String actualAction = "Server running";
    public static boolean updatedData = false;
    private ObservableList<String> drive1;
    private ObservableList<String> drive2;
    private ObservableList<String> drive3;
    private ObservableList<String> drive4;
    private ObservableList<String> drive5;
    private ListView<String> listView1;
    private ListView<String> listView2;
    private ListView<String> listView3;
    private ListView<String> listView4;
    private ListView<String> listView5;
    private int port = 1234;
    private GridPane gridPane;
    private HBox hBox;
    private Text currentAction;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Function that create new HBox to private "hBox"
     * @param object any object to be putted in hBox
     */
    void newHBox(Object object) {
        hBox = new HBox(10);
        hBox.getChildren().add((Node) object);
    }

    /**
     * Function that initiate new ListView
     * @param observableList observale list that will be set in new ListView
     * @return new ListView
     */
    ListView<String> initListView(ObservableList<String> observableList) {
        ListView<String> listView = new ListView<String>();
        listView.setItems(observableList);
        listView.setOrientation(Orientation.VERTICAL);
        listView.setPrefSize(200, 400);

        return listView;
    }

    /**
     * Function to clear list of drive files
     */
    void clearDrivesList() {
        drive1.clear();
        drive2.clear();
        drive3.clear();
        drive4.clear();
        drive5.clear();
    }

    /**
     * Function that refresh list of drive files
     */
    void refreshDriveLists() {
        clearDrivesList();

        // drive 1
        File dir = new File(Server.drives.get(0));
        if (!dir.exists())
            System.exit(0);
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (!file.getName().equals("data.txt"))
                drive1.add(file.getName());
        }
        // drive 2
        dir = new File(Server.drives.get(1));
        if (!dir.exists())
            System.exit(0);
        filesList = dir.listFiles();
        for (File file : filesList) {
            if (!file.getName().equals("data.txt"))
                drive2.add(file.getName());
        }
        // drive 3
        dir = new File(Server.drives.get(2));
        if (!dir.exists())
            System.exit(0);
        filesList = dir.listFiles();
        for (File file : filesList) {
            if (!file.getName().equals("data.txt"))
                drive3.add(file.getName());
        }
        // drive 4
        dir = new File(Server.drives.get(3));
        if (!dir.exists())
            System.exit(0);
        filesList = dir.listFiles();
        for (File file : filesList) {
            if (!file.getName().equals("data.txt"))
                drive4.add(file.getName());
        }
        // drive 5
        dir = new File(Server.drives.get(4));
        if (!dir.exists())
            System.exit(0);
        filesList = dir.listFiles();
        for (File file : filesList) {
            if (!file.getName().equals("data.txt"))
                drive5.add(file.getName());
        }

    }

    /**
     * Function that initiate new observablelist for list of drive files
     * @return new observabelist
     */
    ObservableList<String> initDrive() {
        ObservableList<String> newObservableList = FXCollections.observableArrayList();
        newObservableList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //System.out.println("observablelist error");
            }
        });

        return newObservableList;
    }

    /**
     * Function initiate all lists of drive files
     */
    void initServerLists() {
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
    }

    /**
     * Function that set gridpane
     */
    void initGridPane() {
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
    }

    /**
     * Function that set texts on gridpane
     */
    void initTexts() {
        Text title = new Text("SERVER AKA SERVER\n");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        newHBox(title);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 0, 0, 5, 1);

        Text disk = new Text("Drive 1");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        newHBox(disk);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 0, 1);
        disk = new Text("Drive 2");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        newHBox(disk);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 1, 1);
        disk = new Text("Drive 3");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        newHBox(disk);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 2, 1);
        disk = new Text("Drive 4");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        newHBox(disk);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 3, 1);
        disk = new Text("Drive 5");
        disk.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        newHBox(disk);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 4, 1);

        currentAction = new Text(actualAction);
        currentAction.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        newHBox(currentAction);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.setPrefHeight(50);
        gridPane.add(hBox, 0, 3, 5, 1);
    }

    /**
     * Application core
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Server Application");
        initGridPane();
        initServerLists();
        initTexts();

        gridPane.add(listView1, 0, 2);
        gridPane.add(listView2, 1, 2);
        gridPane.add(listView3, 2, 2);
        gridPane.add(listView4, 3, 2);
        gridPane.add(listView5, 4, 2);

        new Server(port);


        new AnimationTimer() {
            private long lastUpdate = 0;
            private String lastAction = "Server status: running";

            @Override
            public void handle(long now) {   //long currentNanoTime) {
                if (now - lastUpdate >= 2000_000_000 || !actualAction.equals(lastAction)) {//!ControlClientThread.whatAreUDoingFlag.equals(whatIsGoingOn) || now - lastUpdate >= 2000_000_000) {
                    refreshDriveLists();
                    currentAction.setText("Server status: " + actualAction);
                    lastAction = actualAction;
                    lastUpdate = now;
                    //System.out.println(Server.listOfUserNames);
                }
            }
        }.start();

        Scene scene = new Scene(gridPane, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
