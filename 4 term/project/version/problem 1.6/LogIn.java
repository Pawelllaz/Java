import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.animation.AnimationTimer;
import sun.rmi.runtime.Log;

import static java.lang.Thread.sleep;

class DownloadFilesClientThread extends Thread
{
    private Socket socket = null;
    private String from;
    private String to;

    DownloadFilesClientThread(Socket newSocket, String from, String to)
    {
        ControlClientThread.whatAreUDoingFlag = "downloading files...";
        socket = newSocket;
        this.from = from;
        this.to = to;
    }

    public void run()
    {
        try {
            Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
            sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//=========================================================================================================================================================
class ControlClientThread extends Thread
{
    private String clientDirectoryPath;
    private Socket socket;
    private Scanner scan;
    private PrintStream writer;
    private List<String> clientFilesPaths;
    private List<String> serverFilesPaths;
    public static List<String> clientFileNames;
    private List<String> serverFileNames;
    private ObjectInputStream objectInputStream;
    private String userName;
    private List<String> serverFilesForUser;
    public static String whatAreUDoingFlag;

    public ControlClientThread(Socket newSocket, String newClientDirectoryPath, String newUserName)
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
            writer.println("need usernames");
            List<String> newList = (List<String>) objectInputStream.readObject();
            System.out.println(newList);
            if(!LogIn.listOfUserNames.containsAll(newList))
                LogIn.refreshUserNamesList(newList);

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
                    sleep(200);
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
                    //Thread.sleep(2000);
                    synchronize = scan.nextLine();
                    if(!synchronize.equals("ok"))
                        System.out.println("problem");
                } catch (Exception e) {
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
            sleep(delayValue);
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
            whatAreUDoingFlag = "updated data";
            if(getClientServerFileLists())
            {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }

            if(readServerRecords())
            {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }
            checkClientFiles();
           // delay(1000);

            // synchronize
            try {
                writer.println("second step");
                synchronize = scan.nextLine();
            } catch (Exception e) {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }
            if (!synchronize.equals("ready"))
            {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }

            if(checkServerFiles())
            {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }

            // sleep to synchronize
            //delay(1000);

            // send thats ok
            writer.println("done");
            synchronize = scan.nextLine();
            if (synchronize.equals("done")) {
                writer.println("have it");
            }
            else
            {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }
            // sleep 5 sec to reduce using processor
            delay(5000);
        }
        try {
            socket.close();
            // System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//=========================================================================================================================================================
class SendFilesClientThread extends Thread
{
    private Socket socket;
    private String filePath;
    private PrintStream writer;

    SendFilesClientThread(Socket newSocket, Path newPath) throws IOException
    {
        ControlClientThread.whatAreUDoingFlag = "Sending files...";
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
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
//=========================================================================================================================================================
class Client
{
    private Socket socket = null;
    private Path clientDirectoryPath;
    private String userName;
    private boolean clientStatus;

    public Client(String address, int port, String userName, String filePath)
    {
        System.out.println("Hello "+ userName);
        try
        {
            clientDirectoryPath = Paths.get(filePath);
            socket = new Socket(address, port);
            this.userName = userName;
            clientStatus = true;
        }
        catch(Exception e)
        {
            System.out.println(e);
            clientStatus = false;
        }
        if(clientStatus)
            System.out.println("Connected to server");
    }

    public boolean getClientStatus()
    {
        if(clientStatus)
            return true;
        return false;
    }

    void run()
    {
        try {
            new ControlClientThread(socket, clientDirectoryPath.toString(), userName).start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void killClient()
    {
        try
        {
            socket.close();
            System.exit(0);
        }
        catch(IOException err)
        {
            System.out.println(err);
        }

        System.out.println("Zakonczono polaczenie z "+ socket.getLocalPort());
    }
}
//=========================================================================================================================================================
public class LogIn extends Application
{
    // usuwaj nazwy klientow i popraw liste klientow dodaj przycisk do udostepniania, testy i javadoc
    private static List<String> clientFileList;
    private String address = "127.0.0.1";
    private int port = 1234;
    private TextField userNameTextField, userDirectoryPathTextField;
    private static ObservableList<String> observableClientNamesList;
    //private static ListView<String> ClientNamesListView;
    private static ObservableList<String> observableFileList;
    private static ListView<String> FileListView;
    private GridPane gridPane;
    private Client c;
    private String whatIsGoingOn;
    private static Text currentAction;
    public static List<String> listOfUserNames;
    private ComboBox ClientNamesComboBox;
    private HBox hBox;
    private static String strUserName;

    public static void main(String[] args)
    {
        launch(args);
    }

    void initUserNamesList()
    {
        listOfUserNames = new ArrayList<String>();
        observableClientNamesList = FXCollections.observableArrayList();
        observableClientNamesList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //aaa
            }
        });
    }

    void initFileList()
    {
        observableFileList = FXCollections.observableArrayList();
        observableFileList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //System.out.println("new item");
            }
        });

        FileListView = new ListView<String>();
        FileListView.setItems(observableFileList);
        FileListView.setOrientation(Orientation.VERTICAL);
        FileListView.setPrefSize(400,600);

        gridPane.add(FileListView, 0, 2,2,3);
    }

    void createClientsComboBox()
    {
        Text otherCLients = new Text("Other Clients: ");
        otherCLients.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        newHBox(otherCLients);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        gridPane.add(hBox,2,2,1,1);

        ClientNamesComboBox = new ComboBox(observableClientNamesList);
        ClientNamesComboBox.setPrefHeight(30);
        ClientNamesComboBox.setPrefWidth(100);
        gridPane.add(ClientNamesComboBox,2,3,1,1);
    }

    static void refreshFileList()
    {
        clientFileList = ControlClientThread.clientFileNames;
        observableFileList.clear();

        if(clientFileList != null && clientFileList.size() > 0)
            observableFileList.addAll(clientFileList);
    }

    static void refreshUserNamesList(List<String> newList)
    {
        listOfUserNames = newList;
        if(listOfUserNames != null && listOfUserNames.size() > 0)
        {
            listOfUserNames.remove(strUserName);
            observableClientNamesList.clear();
            observableClientNamesList.addAll(listOfUserNames);
        }
    }

    void newHBox(Object object)
    {
        hBox = new HBox(10);
        hBox.getChildren().add((Node) object);
    }

    void initGridPane()
    {
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
    }

    void initLoginScene()
    {
        Text sceneTitle = new Text("Log In");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(sceneTitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("Username:");
        gridPane.add(userNameLabel, 0, 1);


        userNameTextField = new TextField();
        gridPane.add(userNameTextField, 0, 2);
        userNameTextField.setPrefWidth(2000);


        Label userDirectoryPathLabel = new Label("User directory path:");
        gridPane.add(userDirectoryPathLabel, 0, 3);

        userDirectoryPathTextField = new TextField();
        gridPane.add(userDirectoryPathTextField, 0, 4);
    }

    void initTexts()
    {
        Text title = new Text("Client "+strUserName+"\n");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 22));
        newHBox(title);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 0, 0,3,1);

        Text fileListText = new Text("File list");
        fileListText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        newHBox(fileListText);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 0, 1,1,1);

        currentAction = new Text("Synchronizing");
        currentAction.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        newHBox(currentAction);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(hBox, 1, 1,1,1);
    }

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("Login to Server");

        initGridPane();

        initLoginScene();

        // init button "sing in"
        Button button = new Button("Sign in");
        newHBox(button);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        gridPane.add(hBox, 0, 5);

        final Text actiontarget = new Text();
        gridPane.add(actiontarget, 0, 6);

        button.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent e)
            {

                strUserName = userNameTextField.getText();
                String strUserDirectoryPath = userDirectoryPathTextField.getText();
                File file = new File(strUserDirectoryPath);
                if(!file.isDirectory()) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Wrong path");
                }
                else {
                    c = new Client(address, port, strUserName, strUserDirectoryPath);
                    if(c.getClientStatus()) {
                        c.run();
                        gridPane.getChildren().clear();

                        initTexts();
                        initUserNamesList();
                        initFileList();
                        createClientsComboBox();

                        Thread t = new Thread(() -> {
                            while(true) {
                                try {
                                    Platform.runLater(() -> {
                                        if (!ControlClientThread.whatAreUDoingFlag.equals(whatIsGoingOn)) {
                                            whatIsGoingOn = ControlClientThread.whatAreUDoingFlag;
                                            refreshFileList();
                                            currentAction.setText("Status: " + whatIsGoingOn);
                                        }
                                    });
                                    Thread.sleep(2000);
                                } catch (Exception err) {
                                    System.out.println(err);
                                }
                            }
                        });
                        t.start();
                        /*new AnimationTimer() {
                            private long lastUpdate = 0;

                            @Override
                            public void handle(long now) {
                                if (!ControlClientThread.whatAreUDoingFlag.equals(whatIsGoingOn) || now - lastUpdate >= 2000_000_000) {
                                    whatIsGoingOn = ControlClientThread.whatAreUDoingFlag;
                                    refreshList();
                                    currentAction.setText("Status: "+whatIsGoingOn);
                                    lastUpdate = now;
                                }
                            }
                        }.start();*/
                    }
                    else {
                        actiontarget.setFill(Color.FIREBRICK);
                        actiontarget.setText("Server isn't running");
                    }
                }
            }
        });

        Scene scene = new Scene(gridPane, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

