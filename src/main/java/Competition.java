import com.google.common.collect.EvictingQueue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by master on 10.11.2016.
 */
public class Competition {
    Queue<String> firstQueue;
    Queue<String> secondQueue;
    Thread first;
    Thread second;
    ArrayList<String> result;
    Competition() throws FileNotFoundException{
        firstQueue= EvictingQueue.create(5);
        secondQueue= EvictingQueue.create(5);
        first = new Thread(new WritingFromFile());
        second = new Thread(new FirstToSecond());



    }

    WritingFromFile writingFromFile;
    public class WritingFromFile implements Runnable{
    BufferedReader bufferedReader = new BufferedReader(new FileReader("bop.txt"));
        public WritingFromFile() throws FileNotFoundException {
        }
        public void run() {
            while (true){
                try {
                    firstQueue.add(bufferedReader.readLine());
                }catch (NullPointerException e){
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    public class FirstToSecond implements Runnable{
        public void run() {
            while (first.isAlive()||!firstQueue.isEmpty()) {
                try {
                    secondQueue.add(firstQueue.remove());
                    Thread.sleep(6);
                } catch (NoSuchElementException e) {
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }
    }
    public class SecondToThird implements Runnable{
        public void run() {
            while (first.isAlive()||!firstQueue.isEmpty()) {
                try {
                    secondQueue.add(firstQueue.remove());
                    Thread.sleep(6);
                } catch (NoSuchElementException e) {
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        HashMap<Integer, Integer> statistics = new HashMap<Integer, Integer>();
        for(int i=0; i<100;i++) {
            Competition competition = new Competition();
            synchronized (competition) {
                competition.first.start();
                competition.second.start();
            }
            competition.first.join();
            competition.second.join();
            if(statistics.containsKey(competition.secondQueue.size())){
                statistics.put(competition.secondQueue.size(),statistics.get(competition.secondQueue.size())+1);
            }else{
                statistics.put(competition.secondQueue.size(),1);
            }
        }
        Set<Integer> keys = statistics.keySet();
        for(Integer key:keys){
            System.out.println("length: "+key+";count: "+statistics.get(key));
        }


    }

}
