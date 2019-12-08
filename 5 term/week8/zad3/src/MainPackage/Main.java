package MainPackage;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Main {
    private static CyclicBarrier cyclicBarrier;

    public static void main(String[] args) throws InterruptedException, IOException {
        //final Object lock = new Object();
        Semaphore sem1 = new Semaphore(1);
        Semaphore sem2 = new Semaphore(0);
        Semaphore sem3 = new Semaphore(0);
        String[] strings={"aaaaaa", "bbb", "ccccccccccc", "ddddddddd"};

        cyclicBarrier= new CyclicBarrier(strings.length, new MyRunnable());

        int max = maxLen(strings);
        for(int i = 0; i < strings.length; i++){
            MyThread t = new MyThread(strings[i], i, cyclicBarrier, max, sem1, sem2, sem3);
            t.start();
        }

        Thread.sleep(500);
        System.out.println();
        MyThread t1 = new MyThread(strings[0],3, cyclicBarrier, max, sem1, sem2, sem3);
        MyThread t2 = new MyThread(strings[1],1, cyclicBarrier, max, sem1, sem2, sem3);
        MyThread t3 = new MyThread(strings[2],0, cyclicBarrier, max, sem1, sem2, sem3);
        MyThread t4 = new MyThread(strings[3],2, cyclicBarrier, max, sem1, sem2, sem3);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

    private static int maxLen(String[] str){
        int max=0;
        for (String s: str
             ) {
                if(s.length()> max)
                    max = s.length();
        }
        return max;
    }

}
