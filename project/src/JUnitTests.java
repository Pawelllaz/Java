import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


import static org.junit.Assert.*;

public class JUnitTests {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void ControlServerThreadTest(){
        try{
            ControlServerThread threadTest = new ControlServerThread(null,null,null);
            fail("Expected exception");
        }catch (Exception e){}
    }

    @Test
    public void ControlDownloadThreadTest(){
        try{
            new ControlDownloadThreads(null,null,null,null,null);
            fail("Expected exception");
        }catch (Exception e){}
    }

    @Test
    public void DownloadFilesServerThread(){
        try {
            new DownloadFilesServerThread(null,null,null,null);
            fail("Expected exception");
        }catch (Exception e){}
    }

    @Test
    public void ControlClientThreadTest(){
        try {
            new ControlClientThread(null,null,null);
            fail("Expected exception");
        }catch (Exception e){}
    }

    @Test
    public void getFileNameListTest(){
        List<String> expected = new ArrayList<String>();
        expected.add("test1.txt");
        expected.add("test2.txt");
        expected.add("test3.txt");
        List<String> test = new ArrayList<String>();
        test.add("C:/java/test1.txt");
        test.add("C:/java/test2.txt");
        test.add("C:/java/test3.txt");
        List<String> list = new ArrayList<String>();
        try {
            list = ControlClientThread.getFileNameList(test);
        }catch (Exception e){}
        assertEquals(list,expected);
    }

    @Test
    public void SendFilesClientThreadTest(){
        try {
            new SendFilesClientThread(null,null);
            fail("Expected exception");
        }catch (Exception e){}
    }
}