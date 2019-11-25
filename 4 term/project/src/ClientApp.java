import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
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

/**
 * Thread that download file to client
 */
class DownloadFilesClientThread extends Thread {
    private Socket socket = null;
    private String from;
    private String to;

    /**
     * Thread constructor
     * @param newSocket client socket
     * @param from path to downloading file
     * @param to path where file should be downloaded
     */
    DownloadFilesClientThread(Socket newSocket, String from, String to) throws Exception {
        if(newSocket==null||from==null||to==null)
            throw new NullPointerException();
        ControlClientThread.whatAreUDoingFlag = "downloading files...";
        socket = newSocket;
        this.from = from;
        this.to = to;
    }

    /**
     * Thraed core
     */
    public void run() {
        try {
            Thread.sleep((long) (Math.random() * 10000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
            sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//=========================================================================================================================================================

/**
 * Thread that controls synchronization with server
 */
class ControlClientThread extends Thread {
    public static List<String> serverFilesPaths;
    public static List<String> clientFileNames;
    public static String whatAreUDoingFlag = "Too many clients connected\n please wait...";
    private String clientDirectoryPath;
    private Socket socket;
    private Scanner scan;
    private PrintStream writer;
    private List<String> clientFilesPaths;
    private List<String> serverFileNames;
    private ObjectInputStream objectInputStream;
    private String userName;
    private List<String> serverFilesForUser;
    private ExecutorService executorService;
    /**
     * Thread constructor
     * @param newSocket client socket
     * @param newClientDirectoryPath path to client directory
     * @param newUserName client username
     */
    public ControlClientThread(Socket newSocket, String newClientDirectoryPath, String newUserName) throws Exception{
        clientDirectoryPath = newClientDirectoryPath;
        socket = newSocket;
        userName = newUserName;
        executorService = Executors.newCachedThreadPool();
        init();
    }

    /**
     * Function that initiate printstream scanner and printstream
     */
    void init() throws Exception {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            writer = new PrintStream(socket.getOutputStream());
            scan = new Scanner(socket.getInputStream());
    }

    /**
     * Function creates list of file names
     * @param list list of file paths
     * @return list of file names
     */
    public static List<String> getFileNameList(List<String> list) throws Exception {
        List<String> newFileNames = new ArrayList<String>();
        Path path;
        for (int i = 0; i < list.size(); i++) {
            path = Paths.get(list.get(i));

            newFileNames.add(path.getFileName().toString());
        }
        return newFileNames;
    }

    /**
     * Function creates list of client file paths
     * @return true if created otherwise false
     */
    boolean makeClientFilesList() {
        try {
            clientFilesPaths = new ArrayList<String>();
            File dir = new File(clientDirectoryPath);
            File[] filesList = dir.listFiles();
            for (File file : filesList) {
                clientFilesPaths.add(file.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    /**
     * Function that gets list of server file paths
     * @return true or false
     */
    boolean getClientServerFileLists() {
        String synchronize;
        try {
            writer.println("client ready");
            System.out.println("refreshing...");
            makeClientFilesList();
            synchronized (objectInputStream) {
                serverFilesPaths = (List<String>) objectInputStream.readObject();
            }
            try {
                clientFileNames = getFileNameList(clientFilesPaths);
                serverFileNames = getFileNameList(serverFilesPaths);
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    /**
     * Function that check client and server files
     * download file if client doesn't have server files
     */
    void checkClientFiles() {
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
                try {
                    executorService.submit( new DownloadFilesClientThread(socket, serverFilesPaths.get(index), pathToCopy));
                    sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Function create list of server files that need to be synchronized
     * @return true or false
     */
    boolean readServerRecords() {
        List<Integer> numberOfFilesPaths = new ArrayList<Integer>();
        String line;
        serverFilesForUser = new ArrayList<String>();
        for (int i = 0; i < serverFileNames.size(); i++) {
            if (serverFileNames.get(i).equals("data.txt"))
                numberOfFilesPaths.add(i);
        }

        for (int i = 0; i < numberOfFilesPaths.size(); i++) {
            try {
                File file = new File(serverFilesPaths.get(numberOfFilesPaths.get(i)));
                Scanner fileReader = new Scanner(file);

                while (fileReader.hasNextLine()) {
                    line = fileReader.nextLine();
                    String[] lineDivided = line.split(",");

                    if (lineDivided.length > 1) {
                        if (!ClientApp.listOfUserNames.contains(lineDivided[0])) {
                            ClientApp.listOfUserNames.add(lineDivided[0]);
                            ClientApp.newUserFlag = true;
                        }
                    }
                    if (lineDivided[0].equals(userName) && lineDivided.length > 1) {
                        try {
                            serverFilesForUser.add(lineDivided[1]);
                        } catch (Exception e) {
                            System.out.println();
                        }
                    }
                }
                fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }

    /**
     * Function check server files
     * send if server doesn't have client files
     * @return true or false
     */
    boolean checkServerFiles() {
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
                    executorService.submit( new SendFilesClientThread(socket, Paths.get(clientFilesPaths.get(i))));
                    //Thread.sleep(2000);
                    synchronize = scan.nextLine();
                    if (!synchronize.equals("ok"))
                        System.out.println("problem");
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Function delay main loop
     * @param delayValue amount of time that
     */
    void delay(int delayValue) {
        try {
            sleep(delayValue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function sends client username to server
     * @return true or false
     */
    boolean sendUserName() {
        String message;
        try {
            writer.println(userName);
            message = scan.nextLine();
            if (!message.equals("ok"))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    /**
     * Thread run
     */
    public void run() {
        String synchronize;

        if (sendUserName()) {
            System.out.println("couldn't send Username");
            System.exit(0);
        }

        while (true) {
            whatAreUDoingFlag = "updated data";
            if (getClientServerFileLists()) {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }

            if (readServerRecords()) {
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
            if (!synchronize.equals("ready")) {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }

            if (checkServerFiles()) {
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
            } else {
                System.out.println("server disconnected");
                whatAreUDoingFlag = "server disconnected";
                break;
            }
            // sleep 5 sec to reduce using processor
            delay(10000);
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

/**
 * Thread that send files to server
 */
class SendFilesClientThread extends Thread {
    private Socket socket;
    private String filePath;
    private PrintStream writer;

    /**
     * Thread contrustor
     * @param newSocket client socket
     * @param newPath path to file
     */
    SendFilesClientThread(Socket newSocket, Path newPath) throws IOException {
        ControlClientThread.whatAreUDoingFlag = "Sending files...";
        this.filePath = newPath.toString();
        this.socket = newSocket;
        writer = new PrintStream(socket.getOutputStream());
    }

    /**
     * Thread run
     */
    public void run() {
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

/**
 * Class which is responsible for starting main client control thread
 */
class Client {
    private Socket socket = null;
    private Path clientDirectoryPath;
    private String userName;
    private boolean clientStatus;

    /**
     * Class constructor
     * @param address socket address
     * @param port socket port
     * @param userName client username
     * @param filePath path to client directory
     */
    public Client(String address, int port, String userName, String filePath) {
        System.out.println("Hello " + userName);
        try {
            clientDirectoryPath = Paths.get(filePath);
            socket = new Socket(address, port);
            this.userName = userName;
            clientStatus = true;
        } catch (Exception e) {
            e.printStackTrace();
            clientStatus = false;
        }
        if (clientStatus)
            System.out.println("Connected to server");
    }

    /**
     * Function that returns client status
     * @return client status
     */
    public boolean getClientStatus() {
        return clientStatus;
    }

    /**
     * Function run class working
     */
    void run() {
        try {
            ControlClientThread controlClient = new ControlClientThread(socket, clientDirectoryPath.toString(), userName);
            controlClient.setDaemon(true);
            controlClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that turn off the application
     */
    public void killClient() {
        try {
            socket.close();
            System.exit(0);
        } catch (IOException err) {
            System.out.println(err);
        }

        System.out.println("Zakonczono polaczenie z " + socket.getLocalPort());
    }
}

//=========================================================================================================================================================

/**
 * JavaFx class that create application window
 */
public class ClientApp extends Application {
    public static List<String> listOfUserNames;
    private static List<String> clientFileList;
    private static ObservableList<String> observableClientNamesList;
    private static ObservableList<String> observableFileList;
    private static ListView<String> fileListView;
    private static Text currentAction;
    private static String strUserName;
    private String address = "127.0.0.1";
    private int port = 1234;
    private TextField userNameTextField, userDirectoryPathTextField;
    private GridPane gridPane;
    private Client c;
    private String whatIsGoingOn;
    private ComboBox ClientNamesComboBox;
    private HBox hBox;
    private Text shareStatus;
    public static boolean newUserFlag = true;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Function refresh list of client files
     */
    static void refreshFileList() {
        clientFileList = ControlClientThread.clientFileNames;
        observableFileList.clear();

        if (clientFileList != null && clientFileList.size() > 0)
            observableFileList.addAll(clientFileList);
    }

    /**
     * Function refresh list of usernames
     */
    static void refreshUserNamesList() {
        //listOfUserNames = newList;
        if (listOfUserNames != null && listOfUserNames.size() > 0) {
            //listOfUserNames.remove(strUserName);
            observableClientNamesList.clear();
            observableClientNamesList.addAll(listOfUserNames);
            observableClientNamesList.remove(strUserName);
        }
        System.out.println(listOfUserNames);
    }

    /**
     * Funtion create list of server usernames
     */
    void initUserNamesList() {
        listOfUserNames = new ArrayList<String>();
        observableClientNamesList = FXCollections.observableArrayList();
        observableClientNamesList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //aaa
            }
        });
    }

    /**
     * Function create list of client files
     */
    void initFileList() {
        observableFileList = FXCollections.observableArrayList();
        observableFileList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                //System.out.println("new item");
            }
        });

        fileListView = new ListView<String>();
        fileListView.setItems(observableFileList);
        fileListView.setOrientation(Orientation.VERTICAL);
        fileListView.setPrefSize(400, 600);

        gridPane.add(fileListView, 0, 2, 1, 5);
    }

    /**
     * Function create combobox list of server usernames
     */
    void createClientsComboBox() {
        Text otherCLients = new Text("Other Clients: ");
        otherCLients.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        newHBox(otherCLients);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        gridPane.add(hBox, 1, 2, 1, 1);

        ClientNamesComboBox = new ComboBox(observableClientNamesList);
        ClientNamesComboBox.setPrefHeight(30);
        ClientNamesComboBox.setPrefWidth(120);
        newHBox(ClientNamesComboBox);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(150);
        gridPane.add(hBox, 1, 3, 1, 1);
    }

    /**
     * Function create new HBox
     * @param object any object that will be putted into hbox
     */
    void newHBox(Object object) {
        hBox = new HBox(10);
        hBox.getChildren().add((Node) object);
    }

    /**
     * Function create gridpane
     */
    void initGridPane() {
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
    }

    /**
     * function create Login Scene
     */
    void initLoginScene() {
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

    /**
     * Function create texts on client window
     */
    void initTexts() {
        Text title = new Text("Client " + strUserName + "\n");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 22));
        newHBox(title);
        hBox.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 0, 0, 2, 1);

        Text fileListText = new Text("   File list");
        fileListText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        newHBox(fileListText);
        hBox.setAlignment(Pos.CENTER_LEFT);
        gridPane.add(hBox, 0, 1, 1, 1);

        shareStatus = new Text("Select file and user\nto share a file");
        shareStatus.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        newHBox(shareStatus);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.setPrefWidth(120);
        gridPane.add(hBox, 1, 5, 1, 1);

        currentAction = new Text("Synchronizing");
        currentAction.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        newHBox(currentAction);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        gridPane.add(hBox, 1, 6, 1, 1);
    }

    /**
     * Function create share file button
     * @return button
     */
    Button initShareButton() {
        Button newButton = new Button("Share");
        newButton.setPrefHeight(30);
        newButton.setPrefWidth(120);
        newHBox(newButton);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(150);
        gridPane.add(hBox, 1, 4, 1, 1);

        return newButton;
    }

    /**
     * Function that search right drive to write data
     * @param selectedFile file name
     * @return path to drive
     */
    String selectDriveToShare(String selectedFile) {
        List<String> tempList = ControlClientThread.serverFilesPaths;
        String selectedDrive = "";
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).endsWith(selectedFile)) {
                selectedDrive = tempList.get(i).substring(0, tempList.get(i).lastIndexOf('\\'));
                return selectedDrive;
            }
        }
        return "not finded";
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client Application");

        initGridPane();

        initLoginScene();

        // init button "sing in"
        Button button = new Button("Sign in");
        button.setPrefWidth(80);
        button.setPrefHeight(30);
        newHBox(button);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        gridPane.add(hBox, 0, 5);

        final Text actiontarget = new Text();
        gridPane.add(actiontarget, 0, 6);

        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                strUserName = userNameTextField.getText();
                String strUserDirectoryPath = userDirectoryPathTextField.getText();
                File file = new File(strUserDirectoryPath);
                if(strUserName.isEmpty())
                {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Enter your username!");
                }
                else if (!file.isDirectory()) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Wrong path");
                } else {
                    c = new Client(address, port, strUserName, strUserDirectoryPath);
                    if (c.getClientStatus()) {
                        c.run();
                        gridPane.getChildren().clear();

                        initTexts();
                        initUserNamesList();
                        initFileList();
                        createClientsComboBox();

                        Button exitButton = new Button("Exit");
                        exitButton.setPrefHeight(25);
                        exitButton.setPrefWidth(50);
                        newHBox(exitButton);
                        hBox.setPrefHeight(25);
                        hBox.setPrefWidth(50);
                        hBox.setAlignment(Pos.TOP_RIGHT);
                        gridPane.add(hBox, 1, 0, 1, 1);
                        exitButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                c.killClient();
                            }
                        });

                        Button shareButton = initShareButton();

                        shareButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                boolean checkUserName = ClientNamesComboBox.getSelectionModel().isEmpty();
                                boolean checkFileList = fileListView.getSelectionModel().isEmpty();
                                if (checkFileList && checkUserName) {
                                    shareStatus.setFill(Color.FIREBRICK);
                                    shareStatus.setText("Select user and file!");
                                } else if (checkUserName) {
                                    shareStatus.setFill(Color.FIREBRICK);
                                    shareStatus.setText("Select user!");
                                } else if (checkFileList) {
                                    shareStatus.setFill(Color.FIREBRICK);
                                    shareStatus.setText("Select file!");
                                } else {
                                    String selectedFile = fileListView.getSelectionModel().getSelectedItem();
                                    String selectedUser = ClientNamesComboBox.getSelectionModel().getSelectedItem().toString();
                                    String selectedDrive = selectDriveToShare(selectedFile);
                                    if (selectedDrive.endsWith("not finded")) {
                                        shareStatus.setFill(Color.FIREBRICK);
                                        shareStatus.setText("There isn't such file?");
                                    }
                                    else {
                                        try {
                                            new SaveRecordThread(selectedFile, selectedDrive, selectedUser).start();
                                        } catch (Exception err) {
                                            System.out.println(err);
                                        }
                                        shareStatus.setFill(Color.BLACK);
                                        shareStatus.setText("File shared");
                                    }
                                }
                            }
                        });

                        Thread t = new Thread(() -> {
                            while (true) {
                                try {
                                    Platform.runLater(() -> {
                                        if (!ControlClientThread.whatAreUDoingFlag.equals(whatIsGoingOn)) {
                                            whatIsGoingOn = ControlClientThread.whatAreUDoingFlag;
                                            refreshFileList();
                                            //refreshUserNamesList();
                                            currentAction.setText("Status:\n" + whatIsGoingOn);
                                        }
                                        if(newUserFlag){
                                            refreshUserNamesList();
                                            newUserFlag = false;
                                        }
                                    });
                                    Thread.sleep(2000);
                                } catch (Exception err) {
                                    System.out.println(err);
                                }
                            }
                        });
                        t.start();
                    } else {
                        actiontarget.setFill(Color.FIREBRICK);
                        actiontarget.setText("Server isn't running");
                    }
                }
            }
        });

        Scene scene = new Scene(gridPane, 700, 500);
        //Color color = Color.web("#0000FF");
        //scene.setFill(color);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

