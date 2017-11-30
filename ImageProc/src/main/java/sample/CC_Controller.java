package sample;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;


public class CC_Controller {
    // FXML buttons
    @FXML
    private Button cameraButton;
    @FXML
    private Button applyButton;
    @FXML
    private Button snapshotButton;
    // the FXML area for showing the current frame (before calibration)
    @FXML
    private ImageView originalFrame;
    // the FXML area for showing the current frame (after calibration)
    @FXML
    private ImageView calibratedFrame;
    // info related to the calibration process
    @FXML
    private TextField numBoards;
    @FXML
    private TextField numHorCorners;
    @FXML
    private TextField numVertCorners;

    // a timer for acquiring the video stream
    private Timer timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;
    // the saved chessboard image
    private Mat savedImage;
    // the calibrated camera frame
    private Image undistoredImage, CamStream;
    // various variables needed for the calibration
    private List<Mat> imagePoints;
    private List<Mat> objectPoints;
    private MatOfPoint3f obj;
    private MatOfPoint2f imageCorners;
    private int boardsNumber;
    private int numCornersHor;
    private int numCornersVer;
    private int successes;
    private Mat intrinsic;
    private Mat distCoeffs;
    private boolean isCalibrated;


    private File file;
    private Mat resultMat;

    /**
     * Init all the (global) variables needed in the controller
     */
    protected void init() {
        this.capture = new VideoCapture();
        this.cameraActive = false;
        this.obj = new MatOfPoint3f();
        this.imageCorners = new MatOfPoint2f();
        this.savedImage = new Mat();
        this.undistoredImage = null;
        this.imagePoints = new ArrayList<>();
        this.objectPoints = new ArrayList<>();
        this.intrinsic = new Mat(3, 3, CvType.CV_32FC1);
        this.distCoeffs = new Mat();
        this.successes = 0;
        this.isCalibrated = false;
        System.out.println("Init is successful!");
    }

    /**
     * Store all the chessboard properties, update the UI and prepare other
     * needed variables
     * The calibration process consists on showing to the cam the chessboard
     * pattern from different angles, depth and points of view. For each recognized pattern
     * we need to track:
     */
    @FXML
    protected void updateSettings() {


        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("pattern");
        alert.setContentText("   Both width and height of the pattern should \n have bigger than 2!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            alert.hide();
        } else {
            alert.hide();
        }


        init();
        this.boardsNumber = Integer.parseInt(this.numBoards.getText());
        this.numCornersHor = Integer.parseInt(this.numHorCorners.getText());
        this.numCornersVer = Integer.parseInt(this.numVertCorners.getText());
        int numSquares = this.numCornersHor * this.numCornersVer;


		/*
        some reference system’s 3D point where the chessboard is
		located (let’s assume that the Z axe is always 0):
		 */
        for (int j = 0; j < numSquares; j++)
            obj.push_back(new MatOfPoint3f(new Point3(j / this.numCornersHor, j % this.numCornersVer, 0.0f)));
        this.cameraButton.setDisable(false);
    }

    /**
     * The action triggered by pushing the button on the GUI
     */
    @FXML
    protected void startCamera() throws IOException {


        if (!this.cameraActive) {

            //getting fileChooser insted of camera. Because current we havent a camera.
            FileChooser fileChooser = new FileChooser();
            configureFileChooser(fileChooser);
            file = fileChooser.showOpenDialog(cameraButton.getScene().getWindow());
            uploadInitialProcessing();


            // start the video capture
            this.capture.open(file.getAbsolutePath());
            System.out.println("Camera has been started!");

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;


                /*

                If you are using camera insted of file chooser, you have to use following steps.

                // grab a frame every 33 ms (30 frames/sec)
                TimerTask frameGrabber = new TimerTask() {
                  @Override
                   public void run() {

                   */

                CamStream = grabFrame();
                // show the original frames
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        originalFrame.setImage(CamStream);
                        // set fixed width
                        originalFrame.setFitWidth(380);
                        // preserve image ratio
                        originalFrame.setPreserveRatio(true);
                        // show the original frames
                        calibratedFrame.setImage(undistoredImage);


                        // set fixed width
                        calibratedFrame.setFitWidth(380);
                        // preserve image ratio
                        calibratedFrame.setPreserveRatio(true);
                    }
                });
                /*
                   }
               };
                this.timer = new Timer();
                this.timer.schedule(frameGrabber, 0, 30000);

                */

                // update the button content
                this.cameraButton.setText("Stop Camera");
            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.cameraButton.setText("Start Camera");
            // stop the timer
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
            // release the camera
            this.capture.release();
            // clean the image areas
            originalFrame.setImage(null);
            calibratedFrame.setImage(null);
        }
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Image} to show
     */
    private Image grabFrame() {
//        // init everything
        Image imageToShow = null;



       /*
        if you are using camera insted of file chooser you have to use following steps.

        Mat frame = new Mat();
        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);



                have to push frame insted of resultMat


                */


        // if the frame is not empty, process it
        if (!resultMat.empty()) {

            //    this.findAndDrawPoints(BaseController.resultMat);
            // show the chessboard pattern
            this.findAndDrawPoints(resultMat);

            if (this.isCalibrated) {
                // prepare the undistored(not distorted) image

                Mat undistored = new Mat();
                Imgproc.undistort(resultMat, undistored, intrinsic, distCoeffs);
                undistoredImage = mat2Image(undistored);
                //  BaseController.resultImageView.setImage(undistoredImage);
            }

            // convert the Mat object (OpenCV) to Image (JavaFX)
            imageToShow = mat2Image(resultMat);
        }

//            }
//            catch (Exception e) {
//                // log the (full) error
//                System.err.print("ERROR");
//                e.printStackTrace();
//            }
//        }

        return imageToShow;
    }

    /**
     * Take a snapshot to be used for the calibration process
     * Here we need to save the data (2D and 3D points) if we did not make enough sample:
     */
    @FXML
    protected void takeSnapshot() {


        if (this.successes < this.boardsNumber) {


            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Providing a tip");
            alert2.setHeaderText("We don’t actually save the image but just the data we need.");
            alert2.setContentText("We should take \n the set number of “snapshots” \n from different angles and depth,\n in order to make the calibration.\n Samples up to now: " + String.valueOf(successes + 1));
            Optional<ButtonType> result2 = alert2.showAndWait();
            if (result2.get() == ButtonType.OK) {
                alert2.hide();
            } else {
                alert2.hide();
            }


            // save all the needed values
            this.imagePoints.add(imageCorners);
            imageCorners = new MatOfPoint2f();
            this.objectPoints.add(obj);
            this.successes++;
            System.out.println(successes);
        }

        // reach the correct number of images needed for the calibration
        if (this.successes == this.boardsNumber) {


            Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
            alert3.setTitle("Providing a tip");
            alert3.setHeaderText("We don’t actually save the image but just the data we need.");
            alert3.setContentText("You are in max range. No more samples. Now you can calibrate according to \n  your samples. " + String.valueOf(successes));
            Optional<ButtonType> result3 = alert3.showAndWait();
            if (result3.get() == ButtonType.OK) {
                alert3.hide();
            } else {
                alert3.hide();
            }


            this.calibrateCamera();
        }


        if (this.successes > this.boardsNumber) {


            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("stop camera");
            alert.setContentText("You have exceeded the amount");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                alert.hide();
            } else {
                alert.hide();
            }

        }

    }

    /**
     * Find and draws the points needed for the calibration on the chessboard
     *
     * @param frame the current frame
     * @return the current number of successfully identified chessboards as an
     * int
     */
    private void findAndDrawPoints(Mat frame) {
        // init
        Mat grayImage = new Mat();

        // I would perform this operation only before starting the calibration
        // process
        if (this.successes < this.boardsNumber) {
            /*
             convert the frame in gray scale
			 Before doing the findChessboardCorners convert the image to grayscale
			 and save the board size into a Size variable:
			  */
            Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
            // the size of the chessboard
            Size boardSize = new Size(this.numCornersHor, this.numCornersVer);
            // look for the inner chessboard corners
            //the image’s 2D points (operation made by OpenCV with findChessboardCorners):
            boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
                    Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
            // all the required corners have been found...
            if (found) {
                // optimization
                TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
                Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
                // save the current frame for further elaborations
                grayImage.copyTo(this.savedImage);
                // show the chessboard inner corners on screen
                Calib3d.drawChessboardCorners(frame, boardSize, imageCorners, found);

                // enable the option for taking a snapshot
                this.snapshotButton.setDisable(false);
            } else {


                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("No Corners");
                alert.setContentText("required corners have not  been found! \n try another input or \n change calibration values");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    alert.hide();
                } else {
                    alert.hide();
                }


                this.snapshotButton.setDisable(true);

            }

        }
    }

    /**
     * The effective camera calibration, to be performed once in the program
     * execution
     */
    private void calibrateCamera() {
        // init needed variables according to OpenCV docs
        List<Mat> rvecs = new ArrayList<>();
        List<Mat> tvecs = new ArrayList<>();
        intrinsic.put(0, 0, 1);
        intrinsic.put(1, 1, 1);
        // calibrate!




        Calib3d.calibrateCamera(objectPoints, imagePoints, savedImage.size(), intrinsic, distCoeffs, rvecs, tvecs);
        this.isCalibrated = true;

        // you cannot take other snapshot, at this point...


        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("No further snapshots");
        alert.setContentText("you cannot take other snapshot, at this point...");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            alert.hide();
        } else {
            alert.hide();
        }


        this.snapshotButton.setDisable(true);
    }

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    private Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
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
