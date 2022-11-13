package org.example;

public class Main {
    public static void main(String[] args) throws Exception {
        WorkerClass work = new WorkerClass("/home/assiph/IdeaProjects/Worker/src/main/Images/ocraextended.png");
        work.processImage();
        work.putInSQS();
    }
}