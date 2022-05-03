package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Task1 {
    private static final int NUM_THREADS = 10;

    public static List<String> generate(final int from, final int to, final int count) {
        if (from < 0 || to < 0 || !isInRange(count, 0, to - from + 1)) throw new IllegalArgumentException();

        List<String> generated = new ArrayList<>(count);

        Thread[] threads = new Thread[10];

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    Random random = new Random();

                    while (generated.size() < count){
                        int randomNum = random.nextInt(from, to + 1);

                        String str =randomNum + ", " + KanjiLib.convert(randomNum);


                            adder(generated, str, count);

                    }
                }
            };
        }

        for (Thread thread : threads){
            thread.start();
        }

        for (Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return generated;
    }

    private static synchronized void adder(List<String> generated, String str, final int count){
        if (!generated.contains(str) && generated.size() < count){
            generated.add(str);
        }
    }

    private static boolean isInRange(int count, int from, int to) {
        return from <= count && count <= to;
    }
}