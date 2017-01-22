package de.mj.cordova.plugin.filelogger;

interface Queue<T> {
    void put(T m) throws InterruptedException;
    T get() throws InterruptedException;
}

