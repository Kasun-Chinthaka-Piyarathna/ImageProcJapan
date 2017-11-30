
package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;
import static util.Utils.mat2Image;


public class ArcLength {


    @FXML
    private Button uploadimg;

    @FXML
    private ImageView imgg1;
    @FXML
    private javafx.scene.control.TextField txt1;


    @FXML
    private TextField t4;


    @FXML
    private TextField t2;

    private File file;
    private Mat resultMat;
    private Image undistoredImage;
    private static Mat hierarchyOutputVector;


    @FXML
    private Button find_arc;

    @FXML
    public void find_arc(ActionEvent e) throws IOException {

        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //getting fileChooser insted of camera. Because current we havent a camera.
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(find_arc.getScene().getWindow());
        uploadInitialProcessing();

        Mat src = resultMat;
        // Check if image is loaded fine
        if (src.empty()) {
            System.out.println("Error opening image!");
            System.exit(-1);
        }
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);


        Mat binary = new Mat();

        //convert gray scale frame to binary frame.
        threshold(gray, binary, 100, 255, THRESH_BINARY);


        hierarchyOutputVector = new Mat();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(
                gray,
                contours,
                hierarchyOutputVector,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );


        // loop over all found contours
        for (MatOfPoint cnt : contours) {

            //  System.out.println(contours);
//
//            List<MatOfPoint2f> newContours = new ArrayList<>();
            MatOfPoint2f newPoint = new MatOfPoint2f(cnt.toArray());


            for (Point cntt : cnt.toArray()) {
                //Drawing a Circle
                Imgproc.circle(
                        gray,                 //Matrix obj of the image
                        cntt,    //Center of the circle
                        100,                    //Radius
                        new Scalar(0, 0, 255),  //Scalar object for color
                        10                      //Thickness of the circle
                );
            }

            //    System.out.println(newPoint);
            // newContours.add(newPoint);


            double perimeter = Imgproc.arcLength(newPoint, true);
            System.out.println(String.valueOf(perimeter));
            t4.setText(String.valueOf(perimeter));

        }


        Image img1 = mat2Image(gray);
        imgg1.setImage(img1);


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



