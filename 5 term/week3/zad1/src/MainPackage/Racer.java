package MainPackage;

import java.io.PipedOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Racer {
    private ArrayList<Integer> metaArray = new ArrayList<>();
    private PriorityQueue<Integer> metaQueue = new PriorityQueue<>();
    private HashMap<Integer, String> racersMap = null;
    RacerGUI racerGUI = null;
    private MyLogger myLogger;

    public Racer(HashMap<Integer, String> newMap){
        racersMap = newMap;
        racerGUI = new RacerGUI();
        racerGUI.setVisible(true);
        myLogger = new MyLogger("LogFile.log");
    }

    public void Race() throws InterruptedException {
        int i = 0;
        for (Integer delay : racersMap.keySet()) {
            startRacer(delay, racersMap.get(delay), i++);
            try {
                Thread.sleep(2400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startRacer(int delay, String name, int iterator){
        Timer timer = new Timer("Timer");
        TimerTask task = new TimerTask() {
            public void run() {
                myLogger.sendMessage(String.valueOf("Racer "+name+" started"));
                for (int i = 0; i < delay; i++) {
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                timer.cancel();
                timer.purge();
                myLogger.sendMessage(String.valueOf("Racer "+name+" finished, time: "+delay+" sec"));
                metaArray.add(delay);
                if(iterator == 11)
                    racerGUI.refreshLastRacer("wyścig zakończony!");
                else
                    racerGUI.refreshLastRacer(name+": "+delay+" sekund");
                racerGUI.refreshList(getFinishNames(), getFinishTimes());
            }
        };
        timer.schedule(task, 0);
    }

    private ArrayList getFinishNames(){
        ArrayList<String> finishNames = new ArrayList<>();
        metaQueue.addAll(metaArray);
        while (!metaQueue.isEmpty())
            finishNames.add(racersMap.get(metaQueue.poll()));
        if(finishNames.size()>4){
            for(int i = finishNames.size()-1; i>3;i--)
                finishNames.remove(i);
        }
        return finishNames;
    }

    private ArrayList getFinishTimes(){
        ArrayList<String> finishTimes = new ArrayList<>();
        metaQueue.addAll(metaArray);
        while (!metaQueue.isEmpty())
            finishTimes.add(String.valueOf(metaQueue.poll()+" sekund"));
        if(finishTimes.size()>4){
            for(int i = finishTimes.size()-1; i>3;i--)
                finishTimes.remove(i);
        }
        return finishTimes;
    }
}