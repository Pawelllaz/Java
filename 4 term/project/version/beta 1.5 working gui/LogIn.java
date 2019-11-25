import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.geometry.Orientation;
import javafx.animation.AnimationTimer;

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
            try {
                writer.println("second step");
                synchronize = scan.nextLine();
            } catch (Exception e) {
                System.out.println("server disconnected");
                break;
            }
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
            System.exit(0);
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


class Client
{
    private Socket socket = null;
    private Path clientDirectoryPath;
    private String userName;

    public Client(String address, int port, String userName, String filePath)
    {
        System.out.println("Hello "+ userName);
        try
        {
            clientDirectoryPath = Paths.get(filePath);
            socket = new Socket(address, port);
            System.out.println("Connected to server");
            this.userName = userName;

            run();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    void run()
    {
            try {
                new ControlClientThread(socket, clientDirectoryPath.toString(), userName).start();
                //ControlClientThread.
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

    /*public static void main(String args[])
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
    }*/
}

public class LogIn extends Application
{
    private static List<String> clientFileList;
    private String address = "127.0.0.1";
    private int port = 1234;
    private TextField userNameTextField, userDirectoryPathTextField;
    public static ObservableList<String> observableList;
    public static ListView<String> listView;
    private GridPane gridPane;
    private Client c;
    private String whatIsGoingOn;
    public static Text currentAction;

    public static void main(String[] args)
    {
        launch(args);
    }

    void initLists()
    {
        observableList = FXCollections.observableArrayList();
        observableList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //System.out.println("observablelist error");
            }
        });

        listView = new ListView<String>();
        listView.setItems(observableList);
        listView.setOrientation(Orientation.VERTICAL);
        listView.setPrefSize(200,400);

        gridPane.add(listView, 0, 1);
    }

    public static void refreshList()
    {
        clientFileList = ControlClientThread.clientFileNames;
        observableList.clear();
        if(clientFileList != null && clientFileList.size() > 0)
            observableList.addAll(clientFileList);
        //System.out.println(ControlClientThread.clientFileNames);
    }

    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("Login to Server");
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

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

        Button button = new Button("Sign in");
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(button);
        gridPane.add(hbox, 0, 5);

        final Text actiontarget = new Text();
        gridPane.add(actiontarget, 0, 6);

        button.setOnAction(new EventHandler<ActionEvent>()
        {

            @Override
            public void handle(ActionEvent e)
            {
                String strUserName = userNameTextField.getText();
                //System.out.println(""+login);

                String strUserDirectoryPath = userDirectoryPathTextField.getText();
                //System.out.println(""+path);

                //dodac spraqdzanie poprawnosci loginu i sciezki

                //primaryStage.close(); //zamyka okno zlogowaniem, ale cyz w dobrym miescu;
                c = new Client(address, port, strUserName, strUserDirectoryPath);
                gridPane.getChildren().clear();

                sceneTitle.setText(strUserName+" file list");
                sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                gridPane.add(sceneTitle, 0, 0, 2, 1);

                initLists();
                refreshList();
                currentAction = new Text(ControlClientThread.whatAreUDoingFlag);
                currentAction.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
                gridPane.add(currentAction, 10, 1);

                new AnimationTimer()
                {
                    private long lastUpdate = 0;
                    @Override public void handle(   long now) {   //long currentNanoTime) {
                        if(!ControlClientThread.whatAreUDoingFlag.equals(whatIsGoingOn) || now - lastUpdate >= 2000_000_000) {
                            whatIsGoingOn = ControlClientThread.whatAreUDoingFlag;
                            refreshList();
                            currentAction.setText(whatIsGoingOn);
                            lastUpdate = now;
                        }
                    }
                }.start();

            }
        });

        Scene scene = new Scene(gridPane, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

