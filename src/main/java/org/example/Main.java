package org.example;

import com.amazonaws.services.sqs.model.Message;

public class Main {
    public static void main(String[] args) throws Exception {
        WorkerClass work = new WorkerClass();
        while (true) {
            Message message = work.getFromManagerToWorkerSQS();
            work.updateFromMessage(message);
            work.bringImage();
            work.processImage();
            work.sendToManager();
            work.deleteMessage(message);
        }
    }
}