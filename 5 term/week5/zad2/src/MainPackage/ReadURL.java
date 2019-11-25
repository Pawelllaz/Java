package MainPackage;

import java.net.*;
import java.io.*;
import java.util.LinkedList;

public class ReadURL {
    public LinkedList<String> read(String url) throws Exception{
        LinkedList<String> list = new LinkedList<>();
        URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            list.add(inputLine);
        in.close();
        return list;
    }
}