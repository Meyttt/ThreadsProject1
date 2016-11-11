/**
 * Created by Александра on 11.11.2016.
 */

import com.google.common.collect.EvictingQueue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Game {
    private EvictingQueue<String> firstQueue;
    private EvictingQueue<String> secondQueue;
    ArrayList<String> result;
    private BufferedReader bufferedReader;
    private List<Character> letters = new ArrayList<Character>();
    public Game() throws FileNotFoundException {
        this.firstQueue = EvictingQueue.create(5);
        this.secondQueue = EvictingQueue.create(5);
        this.result = new ArrayList<String>();
        this.bufferedReader= new BufferedReader(new FileReader("bop.txt"));
        for(char c='a';c<='z';c++){
            letters.add(c);
        }
        for(char c='A';c<='Z';c++){
            letters.add(c);
        }
        for(char c='0';c<='9';c++){
            letters.add(c);
        }
        letters.add(' ');
    }
    private class Reading implements Runnable{
        public void run() {
            while(true) {
                String str = null;
                try {
                    str = bufferedReader.readLine();
                    firstQueue.add(str);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e1) {
                    System.out.println("Readed ended in " + this);
                    break;
                }

                try {
                    Thread.sleep(0,50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class FirstToSecond implements Runnable{
        public void run() {
            Thread thread1 = new Thread(new Reading());
            thread1.start();
            while(true) {
                try {
                    secondQueue.add(firstQueue.remove());
                } catch (NoSuchElementException e) {
                }
                if(firstQueue.isEmpty()&&!thread1.isAlive()){
                    break;
                }
                try {
                    Thread.sleep(0,200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    private class SecondToResult implements Runnable{

        public void run() {
            Thread thread2 = new Thread(new FirstToSecond());
            thread2.start();
            while(true){
                try{
                   result.add(secondQueue.remove());
                }catch (NoSuchElementException e){

                }
                if (!thread2.isAlive()&&secondQueue.isEmpty()){
                    break;
                }
                try {
                    Thread.sleep(0,100);
                }catch (InterruptedException e) {
                }

            }
        }
    }
    public SecondToResult start(){
        return new SecondToResult();
    }
    public static HashMap<Integer,Integer> setStatistics(HashMap<Integer,Integer> map, Integer key){
        if (map.containsKey(key)){
            map.put(key,map.get(key)+1);
        }else{
            map.put(key,1);
        }
        return map;
    }
    public static void viewStatistics(HashMap<Integer,Integer> map){
        Set<Integer> keys = map.keySet();
        for(Integer key:keys){
            System.out.println("Value: "+key+"; count: "+map.get(key));
        }
    }

    public boolean allDone(Map<Character,Boolean> map){
        Set<Character> keys = map.keySet();
        for(Character key:keys){
            if (!map.get(key)){
                return false;
            }
        }

        return true;
    }
    public String deleteExcess(String originalString){
        Random random = new Random(42);
        String result = "";
        for(int i=0;i<originalString.length();i++){
            HashMap<Character,Boolean> mapLetters = new HashMap<>();
            for(Character ch:letters){
                mapLetters.put(ch,false);
            }
            char currentCh=originalString.charAt(i);
            while (!allDone(mapLetters)){
                Character nextChar = letters.get(random.nextInt(letters.size()));


                if(nextChar.equals(currentCh)){
                    result+=currentCh;
                    mapLetters.put(nextChar,true);
                    break;
                }else{
                    mapLetters.put(nextChar,true);
                }
            }
        }
        return result;
    }
    private class Deleting extends Thread{

        @Override
        public void run() {
            while(true){
                String newString;
                newString=secondQueue.remove();
                String changedString = deleteExcess(newString);
                secondQueue.add(changedString);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        HashMap<Integer,Integer> statistics = new HashMap<Integer, Integer>();
        for(int i=0; i<10;i++){
            Game game = new Game();
            Thread thread = new Thread(game.start());
            thread.start();
            thread.join();
            statistics= setStatistics(statistics,game.result.size());
        }
        viewStatistics(statistics);
    }

}
