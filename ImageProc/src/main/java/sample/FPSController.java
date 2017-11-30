package sample;

import config.ScreensConfig;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javax.imageio.ImageIO;
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


public class FPSController {


    BufferedImage bufferedImage;
    @FXML
    private CheckMenuItem greyscale;


    JFrame frame2 = new JFrame();


    @FXML
    Button playvedio2;


    @FXML
    Button displayfps, takeframes;

    MediaPlayer mediaPlayer;
    Media media;

    @FXML
    MediaView mediaview;


    @FXML
    TextField setfpx, cycle_count, current_rate, current_time, cycle_duration, cliplength, lapsey;

    private static final String MEDIA_URL =
            "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
    Double fps;


    private File file;
    private Mat resultMat;
    private Image undistoredImage;


    @FXML
    public void playvedio(ActionEvent e) {


//         create media player
        media = new Media(MEDIA_URL);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaview.setMediaPlayer(mediaPlayer);

        VideoCapture videoCapture = new VideoCapture();
        videoCapture.open(MEDIA_URL);
        fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        System.out.println("fps: " + fps);
        setfpx.setText((String.valueOf(fps)));


    }

    @FXML
    public void takeframesbyframes(ActionEvent e) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        //getting fileChooser insted of camera. Because current we havent a camera.
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(takeframes.getScene().getWindow());
        uploadInitialProcessing();


//
//        String filePath = "/home/chinthaka/Downloads/wow/giphy.mp4";
        String filePath = file.getAbsolutePath();
        if (!Paths.get(filePath).toFile().exists()) {
            System.out.println("File " + filePath + " does not exist!");
            return;
        }

        VideoCapture camera = new VideoCapture(filePath);

        if (!camera.isOpened()) {
            System.out.println("Error! Camera can't be opened!");
            return;
        }
        Mat frame = new Mat();

        while (true) {
            if (camera.read(frame)) {
                System.out.println("Frame Obtained");
                System.out.println("Captured Frame Width " +
                        frame.width() + " Height " + frame.height());
                Imgcodecs.imwrite("camera.jpg", frame);
                System.out.println("OK");


                while (camera.read(frame)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Frame Obtained");
                    System.out.println("Captured Frame Width " +
                            frame.width() + " Height " + frame.height());
                    Imgcodecs.imwrite("camera.jpg", frame);
                    System.out.println("OK");


                    bufferedImage = matToBufferedImage(frame);
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
    public void fps(ActionEvent e) {

        double rate = mediaPlayer.getRate();
        double cycle_count2 = mediaPlayer.getCycleCount();
        double current_rate2 = mediaPlayer.getCurrentRate();
        Duration current_time2 = mediaPlayer.getCurrentTime();
        Duration cycle_duration2 = mediaPlayer.getCycleDuration();


        Duration duration = mediaPlayer.getTotalDuration();
        System.out.println("Duration: " + duration);
        System.out.println("cycle_count" + cycle_count2);
        System.out.println("current_rate" + current_rate2);
        System.out.println("current_time" + current_time2);
        System.out.println("cycle_duration" + cycle_duration2);

        cycle_count.setText(String.valueOf(cycle_count2));
        current_rate.setText(String.valueOf(current_rate2));
        current_time.setText(String.valueOf(current_time2));
        cycle_duration.setText(String.valueOf(cycle_duration2));


        String[] hourMin = String.valueOf(duration).split(" ");
        System.out.println(hourMin[0]);

        int clip_length = 5;
        double length_of_time_lapse = 0;
        length_of_time_lapse = (fps * clip_length) / (Double.parseDouble(hourMin[0]) / 1000);
        double length_of_time_lapse2 = 1 / length_of_time_lapse;
        System.out.println((Double.parseDouble(hourMin[0]) / 1000));
        System.out.println("length_of_time_lapse" + (length_of_time_lapse2));
        cliplength.setText(String.valueOf(clip_length));
        lapsey.setText(String.valueOf(length_of_time_lapse2));
    }


}











