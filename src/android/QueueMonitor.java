package de.mj.cordova.plugin.filelogger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class QueueMonitor<T> implements Queue<T> {
    private BlockingQueue<T> queue;
    private boolean closed;

    QueueMonitor(int n) {
        this.queue = new LinkedBlockingQueue<T>(n);
    }

    public void put(T m) throws InterruptedException {
        put(m, false);
    }

    public void put(T m, boolean close) throws InterruptedException {
        this.closed = close;
        queue.offer(m);
    }

    public T get() throws InterruptedException {
        if (!closed || !isEmpty()) {
            return queue.take();
        } else {
            return null;
        }
    }

    public boolean contains(T obj) {
        return queue.contains(obj);
    }

    public void remove(T obj) {
        if (queue.contains(obj)) {
            queue.remove(obj);
        }
    }

    public int size() {
        return queue.size();
    }

    public void reset() {
        while (!queue.isEmpty()) {
            queue.poll();
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean isClosed() {
        return closed;
    }

}

