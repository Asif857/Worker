package org.example;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorkerClass {
    private AmazonSQS sqs;
    private String s3Url;
    private final Tesseract tesseract;
    private String imageProcessedText = null;
    private String error = null;
    private final String processedDataSQSUrl = "https://sqs.us-east-1.amazonaws.com/712064767285/processedImagedata";
    public WorkerClass(String s3Url){
        this.s3Url = s3Url;
        this.tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        sqs = AmazonSQSClientBuilder.standard().build();
    }
    public void processImage() throws Exception{
        try {
            if (imageProcessedText != null){
                System.out.println("Already processed");
            }
            else{
                imageProcessedText = tesseract.doOCR(new File(s3Url));
            }
        }
        catch (TesseractException e) {
            error = s3Url + " failed because: " + e.getMessage();
        }
    }
    public void putInSQS(){
        String ans = null;
        if (error != null){
            ans = error;
        }
        else{
            error = s3Url + " " + imageProcessedText;
        }
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put("Message", new MessageAttributeValue()
                .withStringValue(imageProcessedText)
                .withDataType("String"));
        SendMessageRequest requestMessageSend = new SendMessageRequest()
                .withQueueUrl(processedDataSQSUrl)
                .withMessageBody("Hello")
                .withMessageAttributes(messageAttributes);
        SendMessageResult result = sqs.sendMessage(requestMessageSend);
        System.out.println(result.getMessageId());
    }


}
