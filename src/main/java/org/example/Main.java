package org.example;

import com.amazonaws.services.sqs.model.Message;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;

public class Main {
   public static void main(String[] args) throws Exception {
        /*WorkerClass work = new WorkerClass();
        while (true) {
            Message message = work.getFromManagerToWorkerSQS();
            if (message != null) {
                work.bringImage(message);
                work.processImage();
                work.sendToManager();
                work.deleteMessage(message);
            }
        }

         */
       Tesseract tesseract = new Tesseract();
       tesseract.setDatapath("/home/assiph/Desktop/key/tessdata");
       String home = System.getProperty("user.home");
       String imageProcessedText = tesseract.doOCR(new File(home + "/IdeaProjects/Worker/src/main/Images/ocraextended.png"));
       System.out.println(imageProcessedText);


    }

}