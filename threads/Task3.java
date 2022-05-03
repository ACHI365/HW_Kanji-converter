package threads;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Task3 {
    private static final int NUM_THREADS = 10;
    private static final int CHANNEL_CAPACITY = 100;
    private static final int POISON_PILL = -1;

    private  List<String> words = null;
    private Thread thread = null;
    private Thread[] threads = null;

    private static RW lock = new RW();

    public List<String> get() throws InterruptedException {
        thread.join();
        var queue = new Stack<>();
        return words;
    }

    public List<Thread> getThreads() {
        for (Thread thread : threads){
            if (thread.isInterrupted())
                interrupt();
        }
        return Arrays.stream(threads).toList();
    }

    public void interrupt() {
        thread.interrupt();
        for (Thread thread : threads){
            thread.interrupt();
        }
    }

    public Task3(final int from, final int to, final int count) {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1)) throw new IllegalArgumentException();

        List<String> generated = new ArrayList<>(count);

        List<Integer> numbers = new ArrayList<>(count);

        words = generated;

        List<Integer> container = new LinkedList<>();

        thread = new Thread(){
            @Override
            public void run() {
                Random random = new Random();

                while (numbers.size() < count){
                    int randomNum = random.nextInt(from, to + 1);

                    edit(randomNum, numbers, container, count);

                }
                for (int i = 0; i < 10; i++){
                    container.add(POISON_PILL);
                }
            }
        };

        threads = new Thread[10];


        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    while (true){

                        if (!convert(container, generated, count))
                            break;

                    }

                }
            };
        }

        thread.start();

        for (Thread thread : threads){
            thread.start();
        }

    }

    private static boolean convert(List<Integer> container, List<String> generated, int count){
        try {
            lock.startRead();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        int temp = container.remove(0);
        if (temp == POISON_PILL)
            return false;

        String str = temp + ", " + KanjiLib.convert(temp);

        generated.add(str);

        lock.endRead();

        return true;
    }

    private static void edit(int randomNum, List<Integer> numbers, List<Integer> container, int count){

        try {
            lock.startWrite();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!numbers.contains(randomNum) && container.size() < count){
            numbers.add(randomNum);
            container.add(randomNum);
        }

        lock.endWrite();
    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}