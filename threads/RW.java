package threads;

import java.util.Queue;

public class RW {

    private int countReaders = 0;
    private int countConverters = 0;
    private int size = 0;
    private int already = 0;

    public synchronized void startRead() throws InterruptedException {
        while (countConverters > 0 || countReaders < 0 || size == 0 ){
            wait();
        }

        countConverters ++;
        countReaders++;
    }

    public synchronized void endRead() {
        countReaders--;
        countConverters = 0;
        size--;
        if (countReaders == 0) {
            notifyAll();
        }
    }

    public synchronized void startWrite() throws InterruptedException {
        while (countReaders != 0 || size == 100) {
            wait();
        }
        countReaders--;
    }

    public synchronized void endWrite() {
        countReaders = 0;
        size++;
        notifyAll();
    }
}