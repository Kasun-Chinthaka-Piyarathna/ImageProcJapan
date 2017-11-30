
package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import util.*;
import util.BallCluster;
import util.BallDetector;
import util.Circle;
import util.ImgWindow;

import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static util.Utils.mat2Image;


public class BallDetection {

    @FXML
    Button shape1, shape2, shape3;

    private File file;
    private Mat resultMat;
    private Image undistoredImage;
    private Image img1, img2, img3;


    @FXML
    javafx.scene.image.ImageView imgv1, imgv2, imgv3;


    @FXML
    public void ball_detector(ActionEvent e) throws IOException {


        String arr[] = new String[2];
        String notification[] = new String[2];
        String step[] = new String[2];
        notification[0] = "Please choose the parent Image";
        notification[1] = "Please choose the child Image";
        step[0] = "First Step";
        step[1] = "Second Step";

        for (int i = 0; i < 2; i++) {


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(step[i]);
            alert.setHeaderText("You are in a process.");
            alert.setContentText(notification[i]);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                alert.hide();
            } else {
                break;
            }

            //getting fileChooser insted of camera. Because current we havent a camera.
            FileChooser fileChooser = new FileChooser();
            configureFileChooser(fileChooser);
            file = fileChooser.showOpenDialog(shape2.getScene().getWindow());
            uploadInitialProcessing();
            arr[i] = file.getAbsolutePath();


        }


//		CVLoader.load();
        ImgWindow wnd = ImgWindow.newWindow();
        Calibration calib = new Calibration(1280, 800);
        calib.setBackgroundImage(Imgcodecs.imread(arr[0]));
//        calib.setBackgroundImage(Imgcodecs.imread("/home/chinthaka/Downloads/wow/background.png"));
        BallDetector detector = new BallDetector(calib);
        Mat camera = Imgcodecs.imread(arr[1]);
//        Mat camera = Imgcodecs.imread("/home/chinthaka/Downloads/wow/camera.png");
//
//		 while(true) {
        detect(wnd, detector, camera);
//		 }
    }

    @FXML
    public void HoughCircles(ActionEvent e) throws IOException {


        //getting fileChooser insted of camera. Because current we havent a camera.
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(shape1.getScene().getWindow());
        uploadInitialProcessing();

        CVLoader.load();
//		Mat orig = Imgcodecs.imread("data/topdown-6.jpg");

        Mat orig = Imgcodecs.imread(file.getAbsolutePath());
        Mat gray = new Mat();
        orig.copyTo(gray);

        // blur
//		Imgproc.medianBlur(gray, gray, 5);
//		Imgproc.GaussianBlur(gray, gray, new Size(3, 3), 100);

        // convert to grayscale
        Imgproc.cvtColor(gray, gray, Imgproc.COLOR_BGR2GRAY);

        /**
         * Hough Circle Transform
         * change this value to detect circles with different distances to each other
         * change the last two parameters
         * (min_radius & max_radius) to detect larger circles
         */


        // do hough circles
        Mat circles = new Mat();
        int minRadius = 10;
        int maxRadius = 18;
        Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minRadius, 120, 10, minRadius, maxRadius);
        System.out.println(circles);

       // ImgWindow.newWindow(gray);
        ImgWindow wnd = ImgWindow.newWindow(orig);

        while (!wnd.closed) {
            wnd.setImage(orig);
            Graphics2D g = wnd.begin();
            g.setColor(Color.MAGENTA);
            g.setStroke(new BasicStroke(3));
            for (int i = 0; i < circles.cols(); i++) {
                double[] circle = circles.get(0, i);
                g.drawOval((int) circle[0] - (int) circle[2], (int) circle[1] - (int) circle[2], (int) circle[2] * 2, (int) circle[2] * 2);
            }
            wnd.end();
        }
    }

    @FXML
    public void HoughLines(ActionEvent e) throws IOException {


        //getting fileChooser insted of camera. Because current we havent a camera.
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(shape3.getScene().getWindow());
        uploadInitialProcessing();


        System.out.println("HoughLines");

        // load the image
        Mat img = resultMat;
//        Mat img = Imgcodecs.imread("/home/chinthaka/Downloads/wow/chess/line4.jpg");

        // generate gray scale and blur
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(gray, gray, new Size(3, 3));

        // detect the edges
        Mat edges = new Mat();
        int lowThreshold = 50;
        int ratio = 3;
        Imgproc.Canny(gray, edges, lowThreshold, lowThreshold * ratio);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 50, 50, 10);

        for (int i = 0; i < lines.cols(); i++) {
            double[] val = lines.get(0, i);
            Imgproc.line(img, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
        }


        img1 = mat2Image(edges);
        imgv1.setImage(img1);

        img2 = mat2Image(gray);
        imgv2.setImage(img2);

        img3 = mat2Image(img);
        imgv3.setImage(img3);

//        ImgWindow.newWindow(edges);
//        ImgWindow.newWindow(gray);
//        ImgWindow.newWindow(img);
    }


    private void uploadInitialProcessing() throws IOException {

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            resultMat = Imgcodecs.imread(file.getAbsolutePath());
        } catch (Exception e) {
        }

    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }


    private static void detect(ImgWindow wnd, BallDetector detector, Mat camera) {
        detector.detect(camera);

        Mat result = new Mat();
        camera.copyTo(result, detector.getMask());

        for (Circle ball : detector.getBalls()) {
            Imgproc.circle(result, new Point(ball.x, ball.y), (int) ball.radius, new Scalar(0, 255, 0), 2);
        }

        for (int i = 0; i < detector.getBallClusters().size(); i++) {
            BallCluster cluster = detector.getBallClusters().get(i);
            Imgproc.drawContours(result, Arrays.asList(cluster.getContour()), 0, new Scalar(0, 0, 255), 2);
            for (Circle circle : cluster.getEstimatedCircles()) {
                Imgproc.circle(result, new Point(circle.x, circle.y), (int) circle.radius, new Scalar(255, 0, 255), 2);
            }
            Imgproc.putText(result, cluster.getNumBalls() + " balls", cluster.getMinRect().center, Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 0));
        }

        Imgproc.putText(result, wnd.mouseX + ", " + wnd.mouseY, new Point(20, 30), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 255));
        wnd.setImage(result);
    }
}
