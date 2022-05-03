package threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class Task2 {
    private static final int NUM_THREADS = 10;
    private static final int CHANNEL_CAPACITY = 100;
    private static final int POISON_PILL = -1;

    public static List<String> generate(final int from, final int to, final int count) {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1)) throw new IllegalArgumentException();

        List<String> generated = new ArrayList<>(count);
        ReentrantLock listLock = new ReentrantLock();

        Queue<Integer> queue = new LinkedList<>();

        Runnable producerJob = new Runnable() {
            private final Queue<Integer> buffer = queue;
            @Override
            public void run() {
                List<Integer> used = new ArrayList<>();
                while(used.size() <= count) {
                    Integer number = ThreadLocalRandom.current().nextInt(from, to);
                    if(used.size() == count)
                        number = POISON_PILL;
                    if(used.contains(number))
                        continue;
                    synchronized(buffer) {
                        while(buffer.size() == CHANNEL_CAPACITY) {
                            try {
                                buffer.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        buffer.add(number);
                        buffer.notifyAll();
                    }
                    used.add(number);
                    if(number == POISON_PILL)
                        break;
                }
            }
        };

        Runnable consumerJob = new Runnable() {
            private final Queue<Integer> buffer = queue;
            @Override
            public void run() {
                while(true) {
                    int number;
                    synchronized(buffer) {
                        while(buffer.isEmpty()) {
                            try {
                                buffer.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(buffer.peek() == POISON_PILL) {
                            break;
                        }
                        number = buffer.poll();
                        buffer.notifyAll();
                    }
                    String text = number + ", " + KanjiLib.convert(number);
                    listLock.lock();
                    generated.add(text);
                    listLock.unlock();
                }
            }
        };

        Thread producer = new Thread(producerJob);
        producer.start();

        Thread[] consumers = new Thread[NUM_THREADS];
        for(int i = 0; i < NUM_THREADS; i++) {
            consumers[i] = new Thread(consumerJob);
            consumers[i].start();
        }

        try {
            producer.join();
            for(Thread t : consumers)
                t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return generated;
    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}
