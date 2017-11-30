package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;
import util.ImgWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static util.Utils.mat2Image;

public class InterCircleDistance {


    @FXML
    private Button uploadimg;

    @FXML
    private ImageView imgg1;
    @FXML
    private javafx.scene.control.TextField txt1;


    @FXML
    private TextField t1;

    @FXML
    private TextField t2;

    private File file;
    private Mat resultMat;
    private Image undistoredImage;

    @FXML
    public void uploadimg(ActionEvent e) throws IOException {

        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //getting fileChooser insted of camera. Because current we havent a camera.
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(uploadimg.getScene().getWindow());
        uploadInitialProcessing();


        //    String default_file = "/home/chinthaka/Downloads/wow/chess/mulcir2.png";
        //String filename = ((args.length > 0) ? args[0] : default_file);
        // Load an image
//        Mat src = Imgcodecs.imread(default_file, Imgcodecs.IMREAD_COLOR);
        Mat src = resultMat;
        // Check if image is loaded fine
        if (src.empty()) {
            System.out.println("Error opening image!");
            System.exit(-1);
        }
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);


        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double) gray.rows() / 16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 1000); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles

        Point[] arr = new Point[10];
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            arr[x] = center;
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
        }

        //Mat Img = new Mat();
        Point pt1 = arr[0];

        t1.setText("center 1: "+String.valueOf(pt1));
        System.out.println(pt1);
        Point pt2 = arr[1];
        t2.setText("center 2: "+String.valueOf(pt2));
        System.out.println(pt2);
        Imgproc.line(src, pt1, pt2, new Scalar(0, 0, 255), 10);


        double x1, x2, y1, y2;
        double dis;
        x1 = pt1.x;
        y1 = pt1.y;
        x2 = pt2.x;
        y2 = pt2.y;
        dis = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        //  System.out.println("distancebetween" + "(" + x1 + "," + y1 + ")," + "(" + x2 + "," + y2 + ")===>" + dis);


        Image img1 = mat2Image(src);
        imgg1.setImage(img1);
        txt1.setText(String.valueOf("Distance between centers: " + "\n" + dis));


        //ImgWindow.newWindow(src);

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

}



