package org.example;

import com.amazonaws.services.sqs.model.Message;

public class Main {
    public static void main(String[] args) throws Exception {
        WorkerClass work = new WorkerClass();
        while (true) {
            System.out.println("works");
            Message message = work.getFromManagerToWorkerSQS();
            if (message != null) {
                work.updateFromMessage(message);
                work.bringImage();
                work.processImage();
                work.sendToManager();
                work.deleteMessage(message);
            }
        }
    }
}