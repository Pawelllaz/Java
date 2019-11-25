package MainPackage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
    private FileHandler fileHandler = null;
    private Logger logger = null;

    public MyLogger(String filePath){
        try {
            fileHandler = new FileHandler(filePath);
            logger = Logger.getLogger("MyLogger");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        logger.info(message);
    }
}
