package org.example;

import com.amazonaws.services.sqs.model.Message;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class Main {
   public static void main(String[] args) throws Exception {
       WorkerClass work = new WorkerClass();
        while (true) {
            Message message = work.getFromManagerToWorkerSQS();
            if (message != null) {
                File image = work.bringImage(message);
                work.processImage(image);
                work.sendToManager();
                work.deleteMessage(message);
            }
        }


      /* WorkerClass work = new WorkerClass();
       System.out.println(System.getProperty("jna.library.path"));
       String home = System.getProperty("user.home");
       //System.out.println(imageProcessedText);



       */

    }
    /*public static void bringImage(Message message) throws IOException {
        String home = System.getProperty("user.home");
        //updateFromMessage(message);
        String imageUrl = "http://ct.mob0.com/Fonts/CharacterMap/ocraextended.png";
        String type = imageUrl.substring(imageUrl.length() - 3, imageUrl.length());
        URL url = new URL(imageUrl);

        String imagePath = home + "/image." + type;
        try {
            BufferedImage img = ImageIO.read(url);
            File file = new File(imagePath);
            ImageIO.write(img, type, file);
        }catch(Exception e){
            String error = imageUrl + " " + e.getMessage();
        }
    }

     */

}