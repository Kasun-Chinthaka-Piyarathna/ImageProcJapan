package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

public class VideoBinarization {
    @FXML
    Button vbinary, mcameraa;


    BufferedImage bufferedImage;

    private File file;
    private Mat resultMat;
    private Image undistoredImage;


    @FXML
    TextField displaytest;


    @FXML
    public void VideoBinarization(ActionEvent e) throws IOException {

//
//        displaytest.setText("");

        //getting fileChooser insted of camera. Because current we havent a camera.
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(vbinary.getScene().getWindow());
        uploadInitialProcessing();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String filePath =file.getAbsolutePath();
        if (!Paths.get(filePath).toFile().exists()) {
            System.out.println("File " + filePath + " does not exist!");
            return;
        }

        VideoCapture camera = new VideoCapture(filePath);


        if (!camera.isOpened()) {
            System.out.println("Error! Camera can't be opened!");
            return;
        }


        int w = (int) camera.get(CAP_PROP_FRAME_WIDTH);
        int h = (int) camera.get(CAP_PROP_FRAME_HEIGHT);
        int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');
        VideoWriter writer = new VideoWriter("my.avi",
                fourcc, 33.33, new Size(w, h), false);

        Mat frame = new Mat();


        while (true) {
            if (camera.read(frame)) {
                System.out.println("Frame Obtained");
                System.out.println("Captured Frame Width " +
                        frame.width() + " Height " + frame.height());
                Imgcodecs.imwrite("camera.jpg", frame);
                System.out.println("OK");


                while (camera.read(frame)) {
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
                    System.out.println("Frame Obtained");
                    System.out.println("Captured Frame Width " +
                            frame.width() + " Height " + frame.height());
                    Imgcodecs.imwrite("camera.jpg", frame);
                    System.out.println("OK");


                    //convert original color frame into gray scale frame.
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
//
//                    //convert gray scale frame to binary frame.
                    threshold(frame, frame, 100, 255, THRESH_BINARY);


//
////
                    bufferedImage = matToBufferedImage(frame);
//
//                    byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
//
//                    outpeted_frame.put(0, 0, pixels);
                    writer.write(frame);


                    showWindow(bufferedImage);


                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Extracting Frames");
                    alert.setHeaderText("You are in a process.");
                    alert.setContentText("Do you want to get next frame!!!");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        alert.hide();
                    } else {
                        break;
                    }


                }
                writer.release();
                camera.release();

                break;


            }
        }
    }

    private void uploadInitialProcessing() throws IOException {

        try {
            // BufferedImage bufferedImage = ImageIO.read(file);
            resultMat = Imgcodecs.imread(file.getAbsolutePath());
        } catch (Exception e) {
        }

    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Videos");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Videos", "*.*")

        );
    }

    private static BufferedImage matToBufferedImage(Mat frame) {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);
        int i = 0;


        return image;
    }

    private void showWindow(BufferedImage img) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(img.getWidth(), img.getHeight() + 100);
        frame.setTitle("Image captured");
        frame.setVisible(true);
    }

    @FXML
    public void mcamera(ActionEvent ee) throws IOException {



        //getting fileChooser insted of camera. Because current we havent a camera.
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(vbinary.getScene().getWindow());
        uploadInitialProcessing();

//
//        new Thread(() -> method1()).start();
//        new Thread(() -> method2()).start();


//        VideoCapture camera1 = new VideoCapture(0);

        VideoCapture camera1 = new VideoCapture(file.getAbsolutePath());

        if (!camera1.isOpened()) {
            System.out.println("Error! Camera1 can't be opened!");
            return;
        }

//        VideoCapture camera2 = new VideoCapture(1); // next id
        VideoCapture camera2 = new VideoCapture(file.getAbsolutePath());

        if (!camera2.isOpened()) {
            System.out.println("Error! Camera2 can't be opened!");
            return;
        }


        Mat frame1 = new Mat();
        Mat frame2 = new Mat();

        while (true) {
            if (!camera1.read(frame1)) {
                System.out.println("cam1 disconnected");
                return;
            } else {

                System.out.println("Frame Obtained");
                System.out.println("Captured Frame Width " +
                        frame1.width() + " Height " + frame1.height());
                Imgcodecs.imwrite("camera.jpg", frame1);
                System.out.println("OK");


                bufferedImage = matToBufferedImage(frame1);
                showWindow(bufferedImage);
            }
            if (!camera2.read(frame2)) {
                System.out.println("cam2 disconnected");
                return;
            } else {
                System.out.println("Frame Obtained");
                System.out.println("Captured Frame Width " +
                        frame2.width() + " Height " + frame2.height());
                Imgcodecs.imwrite("camera.jpg", frame2);
                System.out.println("OK");


                //convert original color frame into gray scale frame.
                Imgproc.cvtColor(frame2, frame2, Imgproc.COLOR_BGR2GRAY);

                //convert gray scale frame to binary frame.
                threshold(frame2, frame2, 100, 255, THRESH_BINARY);

                bufferedImage = matToBufferedImage(frame2);
                showWindow(bufferedImage);

            }
        }


    }


}
