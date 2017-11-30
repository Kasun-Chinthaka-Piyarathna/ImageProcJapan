package sample;

import util.Utils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ObjRecognitionController {
    // FXML camera button
    @FXML
    private Button cameraButton;
    // the FXML area for showing the current frame
    @FXML
    private ImageView originalFrame;
    // the FXML area for showing the mask
    @FXML
    private ImageView maskImage;
    // the FXML area for showing the output of the morphological operations
    @FXML
    private ImageView morphImage;
    // FXML slider for setting HSV ranges
    @FXML
    private Slider hueStart;
    @FXML
    private Slider hueStop;
    @FXML
    private Slider saturationStart;
    @FXML
    private Slider saturationStop;
    @FXML
    private Slider valueStart;
    @FXML
    private Slider valueStop;
    // FXML label to show the current values set with the sliders
    @FXML
    private Label hsvCurrentValues;

    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture = new VideoCapture();
    // a flag to change the button behavior
    private boolean cameraActive;

    // property for object binding
    private ObjectProperty<String> hsvValuesProp;


    private File file;
    Image fxImage;

    /**
     * The action triggered by pushing the button on the GUI
     */
    @FXML
    private void startCamera() {
        // bind a text property with the string containing the current range of
        // HSV values for object detection
        hsvValuesProp = new SimpleObjectProperty<>();
        this.hsvCurrentValues.textProperty().bind(hsvValuesProp);

        // set a fixed width for all the image to show and preserve image ratio
        this.imageViewProperties(this.originalFrame, 280);
        this.imageViewProperties(this.maskImage, 120);
        this.imageViewProperties(this.morphImage, 120);

        if (!this.cameraActive) {
            // start the video capture
            this.capture.open(0);

            Mat frame = grabFrame();
            // convert and show the frame
            Image imageToShow = Utils.mat2Image(frame);
            updateImageView(originalFrame, imageToShow);
            System.out.println("hello 3");


            this.timer = Executors.newSingleThreadScheduledExecutor();
//            this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

            // update the button content
            this.cameraButton.setText("Stop Camera");


        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.cameraButton.setText("Start Camera");

            // stop the timer
            this.stopAcquisition();
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


    private Image uploadInitialProcessing() throws IOException {

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

        } catch (Exception e) {
//			logger.debug("Exception occurred while uploading image : {}", e);
        }
        return fxImage;

    }


    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Image} to show
     */
    private Mat grabFrame() {

        System.out.println("hello everyone");
        Mat frame2 = new Mat();


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String filePath = "/home/chinthaka/Downloads/wow/wowwwwww.jpg";
        if (!Paths.get(filePath).toFile().exists()) {
            System.out.println("File " + filePath + " does not exist!");

        }

        VideoCapture camera = new VideoCapture(filePath);

        if (!camera.isOpened()) {
            System.out.println("Error! Camera can't be opened!");

        }


        while (true) {
            if (camera.read(frame2)) {
                System.out.println("Frame Obtained");
                System.out.println("Captured Frame Width " +
                        frame2.width() + " Height " + frame2.height());
                Imgcodecs.imwrite("camera.jpg", frame2);
                System.out.println("OK");

                break;
            }
        }
        try {
            if (!frame2.empty()) {
                System.out.println("hello 2");

                // init
                Mat blurredImage = new Mat();
                Mat hsvImage = new Mat();
                Mat mask = new Mat();
                Mat morphOutput = new Mat();

                // remove some noise
                Imgproc.blur(frame2, blurredImage, new Size(7, 7));

                // convert the frame to HSV
                Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

                // get thresholding values from the UI
                // remember: H ranges 0-180, S and V range 0-255
                Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
                        this.valueStart.getValue());
                Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
                        this.valueStop.getValue());

                // show the current selected HSV range
                String valuesToPrint = "Hue range: " + minValues.val[0] + "-" + maxValues.val[0]
                        + "\tSaturation range:  " + minValues.val[1] + "-" + maxValues.val[1] + "\n" + "Value range: "
                        + minValues.val[2] + "-" + maxValues.val[2];
                Utils.onFXThread(this.hsvValuesProp, valuesToPrint);

                // threshold HSV image to select tennis balls
                Core.inRange(hsvImage, minValues, maxValues, mask);
                // show the partial output
                this.updateImageView(this.maskImage, Utils.mat2Image(mask));

                // morphological operators
                // dilate with large element, erode with small ones
                Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
                Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

                Imgproc.erode(mask, morphOutput, erodeElement);
                Imgproc.erode(morphOutput, morphOutput, erodeElement);

                Imgproc.dilate(morphOutput, morphOutput, dilateElement);
                Imgproc.dilate(morphOutput, morphOutput, dilateElement);

                // show the partial output
                this.updateImageView(this.morphImage, Utils.mat2Image(morphOutput));

                // find the tennis ball(s) contours and show them
                frame2 = this.findAndDrawBalls(morphOutput, frame2);


            }
        } catch (Exception e) {
            // log the (full) error
            System.err.print("Exception during the image elaboration...");
            e.printStackTrace();
        }




        return frame2;
    }


    /**
     * Given a binary image containing one or more closed surfaces, use it as a
     * mask to find and highlight the objects contours
     *
     * @param maskedImage the binary image to be used as a mask
     * @param frame       the original {@link Mat} image to be used for drawing the
     *                    objects contours
     * @return the {@link Mat} image with the objects contours framed
     */
    private Mat findAndDrawBalls(Mat maskedImage, Mat frame) {
        // init
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        // find contours
        Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        // if any contour exist...
        if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
            // for each contour, display it in blue
            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
                Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
            }
        }

        return frame;
    }

    /**
     * Set typical {@link ImageView} properties: a fixed width and the
     * information to preserve the original image ration
     *
     * @param image     the {@link ImageView} to use
     * @param dimension the width of the image to set
     */
    private void imageViewProperties(ImageView image, int dimension) {
        // set a fixed width for the given ImageView
        image.setFitWidth(dimension);
        // preserve the image ratio
        image.setPreserveRatio(true);
    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view  the {@link ImageView} to update
     * @param image the {@link Image} to show
     */
    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    /**
     * On application close, stop the acquisition from the camera
     */
    protected void setClosed() {
        this.stopAcquisition();
    }

}
