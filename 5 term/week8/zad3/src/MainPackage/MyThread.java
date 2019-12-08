package MainPackage;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class MyThread extends Thread {
    private String str;
    private CyclicBarrier cyclicBarrier;
    private Integer counter;
    private int maxLength;
    private Semaphore sem1;
    private Semaphore sem2;
    private Semaphore sem3;

    public MyThread(String str, Integer counter, CyclicBarrier cyclicBarrier, int maxLength,
                    Semaphore sem1, Semaphore sem2, Semaphore sem3){
        this.str=str;
        this.counter = counter;
        this.maxLength = maxLength;
        this.cyclicBarrier=cyclicBarrier;
        this.sem1 = sem1;
        this.sem2 = sem2;
        this.sem3 = sem3;
    }

    public void run(){
        for(int i = 0; i< maxLength;i++) {
            if(counter.equals(0)){
                if (i < str.length()) {
                    System.out.print(str.charAt(i));
                }
                sem1.release();
            }
            else if(counter.equals(1)) {
                try {
                    sem1.acquire();
                    if (i < str.length()) {
                        System.out.print(str.charAt(i));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sem2.release();
            }
            else if(counter.equals(2)){
                try {
                    sem2.acquire();
                    if (i < str.length()) {
                        System.out.print(str.charAt(i));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sem3.release();
            }
            else if (counter.equals(3)){
                try {
                    sem3.acquire();
                    if (i < str.length()) {
                        System.out.print(str.charAt(i));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
