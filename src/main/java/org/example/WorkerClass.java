package org.example;

import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import net.lingala.zip4j.ZipFile;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WorkerClass {
    private final AmazonSQS sqsClient;
    private String imagePath;
    private String s3URL;
    private String localApplication;
    private final Tesseract tesseract;
    private String imageProcessedText;
    private String error = null;
    private final String processedDataSQSUrl = "https://sqs.us-east-1.amazonaws.com/712064767285/processedDataSQS.fifo";
    private final String managerToWorkerSQSURL = "https://sqs.us-east-1.amazonaws.com/712064767285/managerToWorkerSQS.fifo";
    public WorkerClass(){
        this.tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        sqsClient = AmazonSQSClientBuilder.standard().build();
    }
    public void setCredentials() throws IOException, GitAPIException {
        String home = System.getProperty("user.home");
        Git.cloneRepository()
                .setURI("https://github.com/Asif857/NotCreds.git")
                .setDirectory(Paths.get(home + "/IdeaProjects/Worker/src/main/creds").toFile())
                .call();
        String zipFilePath = home + "/IdeaProjects/Worker/src/main/creds/aws_creds.zip";
        String destDir = home + "/.aws";
        unzip(zipFilePath, destDir);
        deleteDirectory();
    }
    private void unzip(String zipFilePath, String destDir) throws IOException {
        ZipFile zipFile = new ZipFile(zipFilePath);
        zipFile.setPassword("project1".toCharArray());
        zipFile.extractAll(destDir);
    }
    private void deleteDirectory() throws IOException {
        FileUtils.deleteDirectory(new File("/home/assiph/IdeaProjects/Worker/src/main/creds"));
    }
    private void deleteImage(){
        try {
            Files.delete(Path.of(imagePath));
        } catch (IOException e) {
            System.err.println("Unable to delete "
                    + imagePath
                    + " due to...");
            e.printStackTrace();
        }
    }
    //TODO
    public void setFromSQS(String SQSUrl){
        s3URL = "";
        localApplication = "hey";
    }

    public void bringImage() throws IOException {
        setFromSQS(managerToWorkerSQSURL);
        s3URL = "https://stackoverflow.com/questions/53520358/delete-image-from-the-folder";
        String type = s3URL.substring(s3URL.length() - 3,s3URL.length());
        URL url = new URL(s3URL);
        imagePath = "/home/assiph/IdeaProjects/Worker/src/main/Images/image." + type;
        try {
            BufferedImage img = ImageIO.read(url);
            File file = new File(imagePath);
            ImageIO.write(img, type, file);
        }catch(Exception e){
            error = s3URL + " " + e.getMessage();
        }
    }

    public void processImage() throws Exception{
        try {
                imageProcessedText = tesseract.doOCR(new File(imagePath));
        }
        catch (Exception e) {
            error = s3URL + " failed because: " + e.getMessage();
        }
    }
    public void putInSQS(){
        String messageType = "Message";
        String messageValue = imageProcessedText;
        if (error != null){
            messageType = "ERROR";
            messageValue = error;
        }
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put(messageType, new MessageAttributeValue()
                .withStringValue(messageValue)
                .withDataType("String"));
        SendMessageRequest requestMessageSend = new SendMessageRequest()
                .withQueueUrl(processedDataSQSUrl)
                .withMessageBody(s3URL)
                .withMessageAttributes(messageAttributes)
                .withMessageDeduplicationId(s3URL)
                .withMessageGroupId(localApplication);
        SendMessageResult result = sqsClient.sendMessage(requestMessageSend);
        deleteImage();
        System.out.println(result.getMessageId());
    }


}
