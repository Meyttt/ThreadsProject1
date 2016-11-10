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
    private Queue<String> firstQueue;
    private Queue<String> secondQueue;
    private Thread first;
    private Thread second;
    private Thread third;
    private ArrayList<String> result;
    Competition() throws FileNotFoundException{
        firstQueue= EvictingQueue.create(5);
        secondQueue= EvictingQueue.create(5);
        first = new Thread(new WritingFromFile());
        second = new Thread(new FirstToSecond());
        third = new Thread(new SecondToThird());
        result = new ArrayList<String>();

    }
    public WritingFromFile getWriting() throws FileNotFoundException {
        return new WritingFromFile();
    }
    public class WritingFromFile implements Runnable{
    BufferedReader bufferedReader = new BufferedReader(new FileReader("bop.txt"));
        public WritingFromFile() throws FileNotFoundException {
        }
        public void run() {
            while (true){
                try {
                    firstQueue.add(bufferedReader.readLine());
                    Thread.sleep(5);
                }catch (NullPointerException e){
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            System.out.println("Reading was ended");
        }
    }
    public FirstToSecond getF2S(){
        return new FirstToSecond();
    }
    public class FirstToSecond implements Runnable{
        public void run() {
//                System.out.println(first.isAlive());
//                System.out.println(firstQueue.isEmpty());
//                System.out.println();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    secondQueue.add(firstQueue.remove());
                    Thread.sleep(2);
                } catch (NoSuchElementException e) {
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

        }
    }
    public SecondToThird getS2T(){
        return new SecondToThird();
    }
    public class SecondToThird implements Runnable{
        public void run() {
//            while (second.isAlive()||!secondQueue.isEmpty()) {
                try {
                    //System.out.println(firstQueue.element());
                    result.add(secondQueue.remove());
                    Thread.sleep(6);
                } catch (NoSuchElementException e) {
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
//            }
        }
    }
    public boolean firstEmpty(){
        return firstQueue.isEmpty();
    }
    public boolean secondEmpty(){
        return secondQueue.isEmpty();
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        HashMap<Integer, Integer> statistics = new HashMap<Integer, Integer>();
        for(int i=0; i<100;i++) {
            Competition competition = new Competition();
            synchronized (competition) {
                Thread thread1 = new Thread(competition.getWriting());
                Thread thread2 = new Thread(competition.getF2S());
                Thread thread3 = new Thread(competition.getS2T());
                thread3.start();
                thread2.start();
                thread1.start();

            }
            competition.first.join();
            competition.second.join();
            if(statistics.containsKey(competition.result.size())){
                statistics.put(competition.result.size(),statistics.get(competition.result.size())+1);
            }else{
                statistics.put(competition.result.size(),1);
            }
        }
        Set<Integer> keys = statistics.keySet();
        for(Integer key:keys){
            System.out.println("length: "+key+";count: "+statistics.get(key));
        }


    }

}
