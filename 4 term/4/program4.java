import java.util.Random;
import java.nio.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.*;
import java.io.*;

class saver{
	
	public static void main(String[] args)
	{
		String alphabet = "qwertyuioplkjhgfdsazxcvbnm1234567890";
	
		Random rand = new Random();
		char[] tabToSave = new char[1000];

		for(int i=0;i<1000;i++)
			tabToSave[i] = alphabet.charAt(rand.nextInt(alphabet.length()));
		
		try
		{
			System.out.println("write NIO: "+  writeNIO("test_nio.txt", tabToSave) +"ms");
            		System.out.println("read NIO: "+  readNIO("test_nio.txt") +"ms");
            		System.out.println("write IO: "+  writeIO("test_io.txt", tabToSave) +"ms");
            		System.out.println("read IO: "+  readIO("test_io.txt") +"ms");
		}
		catch(FileNotFoundException error){
			error.printStackTrace();
		}
		catch(Exception error){
			error.printStackTrace();
		}
	
	}
	
	public static double writeNIO(String fileName, char tabToSave[]) throws Exception
	{
		double start = System.nanoTime();
		Path path = Paths.get(fileName);
		FileChannel fchannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		ByteBuffer buff = ByteBuffer.allocate(tabToSave.length*2);
		CharBuffer charBuff = buff.asCharBuffer();
		charBuff.put(tabToSave);
		fchannel.write(buff);
		fchannel.close();
		return (System.nanoTime()-start)/1000000;
	}	
	
	public static double readNIO(String fileName) throws Exception
	{
		double start = System.nanoTime();
		Path path = Paths.get(fileName);
		FileChannel fchannel = FileChannel.open(path, StandardOpenOption.READ);
		ByteBuffer buff =ByteBuffer.allocate((int)fchannel.size());
		CharBuffer charBuff = buff.asCharBuffer();
		fchannel.read(buff);
		fchannel.close();
		System.out.println(charBuff);
		return (System.nanoTime()-start)/1000000;
	}

	public static double writeIO(String fileName, char tabToSave[]) throws Exception
	{
		double start = System.nanoTime();
		OutputStream out = new FileOutputStream(fileName);
		Writer wr = new OutputStreamWriter(out);
		wr.write(tabToSave);
		wr.close();
		out.close();
		return (System.nanoTime()-start)/1000000;	
	}

	public static double readIO(String fileName) throws Exception
	{
		double start = System.nanoTime();
		FileReader fr = new FileReader(fileName);
		BufferedReader buffread = new BufferedReader(fr);
		String sCurrentLine = buffread.readLine();
		System.out.println(sCurrentLine);
		buffread.close();
		return (System.nanoTime()-start)/1000000;
	}
} 