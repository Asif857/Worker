package org.example;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        Tesseract tesseract = new Tesseract();
        try {
            tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
            // the path of your tess data folder
            // inside the extracted file
            String inputFilePath = "/home/assiph/IdeaProjects/Worker/src/main/Images/ocraextended.png";
            String text = tesseract.doOCR(new File(inputFilePath));

            // path of your image file
            System.out.print(text);
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }

    }
}