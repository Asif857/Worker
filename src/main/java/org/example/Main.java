package org.example;

public class Main {
    public static void main(String[] args) throws Exception {
        WorkerClass work = new WorkerClass();
        work.bringImage();
        work.processImage();
        work.putInSQS();
    }
}