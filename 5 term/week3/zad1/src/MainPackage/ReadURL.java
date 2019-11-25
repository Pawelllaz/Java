package MainPackage;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ReadURL {
    public ArrayList<String> read(String url) throws Exception{
        ArrayList<String> names = new ArrayList<>();
        URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            names.add(inputLine);
        in.close();
        return names;
    }
}