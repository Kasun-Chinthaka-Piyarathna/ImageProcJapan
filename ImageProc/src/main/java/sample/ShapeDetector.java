package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static util.Utils.mat2Image;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class ShapeDetector {


    private static final boolean LOG_MEM_USAGE = true;
    /**
     * detect only red objects
     */
    private static final boolean DETECT_RED_OBJECTS_ONLY = true;
    /**
     * the lower red HSV range (lower limit)
     */
    private static final Scalar HSV_LOW_RED1 = new Scalar(0, 100, 100);
    /**
     * the lower red HSV range (upper limit)
     */
    private static final Scalar HSV_LOW_RED2 = new Scalar(10, 255, 255);
    /**
     * the upper red HSV range (lower limit)
     */
    private static final Scalar HSV_HIGH_RED1 = new Scalar(160, 100, 100);
    /**
     * the upper red HSV range (upper limit)
     */
    private static final Scalar HSV_HIGH_RED2 = new Scalar(179, 255, 255);
    /**
     * definition of RGB red
     */
    private static final Scalar RGB_RED = new Scalar(255, 0, 0);
    /**
     * frame size width
     */
    private static final int FRAME_SIZE_WIDTH = 640;
    /**
     * frame size height
     */
    private static final int FRAME_SIZE_HEIGHT = 480;
    /**
     * whether or not to use a fixed frame size -> results usually in higher FPS
     * 640 x 480
     */
    private static final boolean FIXED_FRAME_SIZE = true;
    /**
     * whether or not to use the database to display
     * an image on top of the camera
     * when false the objects are labeled with writing
     */
    private static final boolean DISPLAY_IMAGES = false;
    /**
     * image thresholded to black and white
     */
    private static Mat bw;
    /**
     * image converted to HSV
     */
    private static Mat hsv;
    private static Mat gray;
    private static Mat dst;

    /**
     * the image thresholded for the lower HSV red range
     */
    private static Mat lowerRedRange;
    /**
     * the image thresholded for the upper HSV red range
     */
    private static Mat upperRedRange;
    /**
     * the downscaled image (for removing noise)
     */
    private static Mat downscaled;
    /**
     * the upscaled image (for removing noise)
     */
    private static Mat upscaled;
    /**
     * the image changed by findContours
     */
    private static Mat contourImage;
    /**
     * the found contour as hierarchy vector
     */
    private static Mat hierarchyOutputVector;
    /**
     * approximated polygonal curve with specified precision
     */
    private static MatOfPoint2f approxCurve;

    private static String changingType;

    private File file;
    private Mat resultMat;
    private Image undistoredImage;


    public static String detected_shape;

    @FXML
    TextField text_shape;


    @FXML
    Button detectshapes;

    @FXML
    private ImageView image1;

    @FXML
    Button toexcel;


    @FXML
    private TextField s1, s2, s3;
    public static String angle1, angle2, range;

    @FXML
    protected void detectshapes() throws IOException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        //getting fileChooser insted of camera. Because current we havent a camera.
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(detectshapes.getScene().getWindow());
        uploadInitialProcessing();


        bw = new Mat();
        hsv = new Mat();
        dst = new Mat();
        gray = new Mat();
        lowerRedRange = new Mat();
        upperRedRange = new Mat();
        downscaled = new Mat();
        upscaled = new Mat();
        contourImage = new Mat();

        hierarchyOutputVector = new Mat();
        approxCurve = new MatOfPoint2f();

        Mat frame = resultMat;
        undistoredImage = mat2Image(resultMat);
        image1.setImage(undistoredImage);
        ShapeDetector shapeDetector = new ShapeDetector();
        shapeDetector.onCameraFrame(frame);
        text_shape.setText(detected_shape);

        System.out.println("angle1" + angle1);
        System.out.println("angle2" + angle2);
        System.out.println("range" + range);
        if (angle1 == null && angle2 == null && range == null) {

            s1.setText("Not useful");
            s2.setText("Not useful");
            s3.setText("Not useful");
        } else {
            s1.setText(angle1);
            s2.setText(angle2);
            s3.setText(range);
        }


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


    @FXML
    public Mat onCameraFrame(Mat inputFrame) {


        if (DETECT_RED_OBJECTS_ONLY) {
            Imgproc.cvtColor(inputFrame, gray, Imgproc.COLOR_BGR2RGBA);
        } else {

            Imgproc.cvtColor(inputFrame, gray, Imgproc.COLOR_BGR2GRAY);
        }
        Imgproc.cvtColor(inputFrame, dst, Imgproc.COLOR_BGR2RGBA);


        // down-scale and upscale the image to filter out the noise
        Imgproc.pyrDown(gray, downscaled, new Size(gray.cols() / 2, gray.rows() / 2));
        Imgproc.pyrUp(downscaled, upscaled, gray.size());

        if (DETECT_RED_OBJECTS_ONLY) {
            // convert the image from RGBA to HSV
            Imgproc.cvtColor(upscaled, hsv, Imgproc.COLOR_RGB2HSV);


            //  threshold(hsv, bw, 100, 255, THRESH_BINARY);

            // threshold the image for the lower and upper HSV red range
            Core.inRange(hsv, HSV_LOW_RED1, HSV_LOW_RED2, lowerRedRange);
            Core.inRange(hsv, HSV_HIGH_RED1, HSV_HIGH_RED2, upperRedRange);
            // put the two thresholded images together
            Core.addWeighted(lowerRedRange, 1.0, upperRedRange, 1.0, 0.0, bw);


            // apply canny to get edges only
            System.out.println(bw);
            Imgproc.Canny(bw, bw, 0, 255);
        } else {
            // Use Canny instead of threshold to catch squares with gradient shading
            Imgproc.Canny(upscaled, bw, 0, 255);
        }


        // dilate canny output to remove potential
        // holes between edge segments
        Imgproc.dilate(bw, bw, new Mat(), new Point(-1, 1), 1);

        // find contours and store them all as a list
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        contourImage = bw.clone();
        Imgproc.findContours(
                contourImage,
                contours,
                hierarchyOutputVector,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        System.out.println("contours" + contours);

        // loop over all found contours
        for (MatOfPoint cnt : contours) {
            MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());

            // approximates a polygonal curve with the specified precision
            Imgproc.approxPolyDP(
                    curve,
                    approxCurve,
                    0.02 * Imgproc.arcLength(curve, true),
                    true
            );

            int numberVertices = (int) approxCurve.total();
            double contourArea = Imgproc.contourArea(cnt);

            //     Log.d(TAG, "vertices:" + numberVertices);

            // ignore to small areas
            if (Math.abs(contourArea) < 100
                // || !Imgproc.isContourConvex(
                    ) {
                continue;
            }

            // triangle detection
            if (numberVertices == 3) {
                if (DISPLAY_IMAGES) {
                    //  doSomethingWithContent("triangle");
                    //      Imgproc.drawContours(contourImage, contours,-1, new Scalar(255, 255, 255), -2);

                } else {
                    setLabel(dst, "TRI", cnt);
                    detected_shape = "Triangle found!";
                    System.out.println("Detected triangle");
                }
                angle1 = null;
                angle2 = null;
                range = null;


            }

            // rectangle, pentagon and hexagon detection
            if (numberVertices >= 4 && numberVertices <= 6) {

                List<Double> cos = new ArrayList<>();
                for (int j = 2; j < numberVertices + 1; j++) {
                    cos.add(
                            angle(
                                    approxCurve.toArray()[j % numberVertices],
                                    approxCurve.toArray()[j - 2],
                                    approxCurve.toArray()[j - 1]
                            )
                    );
                }
                Collections.sort(cos);

                System.out.println("All angles: " + cos);

                double mincos = cos.get(0);
                double maxcos = cos.get(cos.size() - 1);
                angle1 = String.valueOf(mincos);
                angle2 = String.valueOf(maxcos);

                // s2.setText(String.valueOf(maxcos));


                System.out.println(mincos + "   " + maxcos);


                // rectangle detection
                if (numberVertices == 4
                        && mincos >= -0.1 && maxcos <= 0.3
                        ) {

                    range = "mincos >= -0.1 && maxcos <= 0.3";
                    if (DISPLAY_IMAGES) {
                        System.out.println("rectangle");

                        doSomethingWithContent("rectangle");
                    } else {


                        /**
                         * How to differ square from a rectangle.
                         * a square will have an aspect ratio that is approximately
                         * equal to one, otherwise, the shape is a rectangle
                         */

                        Rect boundingBox = Imgproc.boundingRect(cnt);
                        double aspectRatio = (double) boundingBox.width / boundingBox.height;
                        if (0.95 <= aspectRatio && aspectRatio <= 1.05) {
                            detected_shape = "Sqaure Detected!";
                        } else {


                            setLabel(dst, "RECT", cnt);
                            detected_shape = "Rectangle Detected!";
                        }
                    }
                } else if (numberVertices == 4) {
                    detected_shape = "4 points angles!";
                }
                // pentagon detection
                else if (numberVertices == 5
                        && mincos >= -0.34 && maxcos <= -0.27) {
                    range = " mincos >= -0.34 && maxcos <= -0.27";
                    if (!DISPLAY_IMAGES) {
                        setLabel(dst, "PENTA", cnt);
                        detected_shape = "Pentagon Detected!";
                    }
                }
                // hexagon detection


                else if (numberVertices == 6
                        && mincos >= -0.57 && maxcos <= -0.43) {

                    range = "mincos >= -0.57 && maxcos <= -0.43";
                    // mincos -0.55 maxcos -0.45
                    if (!DISPLAY_IMAGES) {
                        setLabel(dst, "HEXA", cnt);
                        detected_shape = "Hexagon Detected!";

                    }
                }
            }
            // circle detection
            else {
                Rect r = Imgproc.boundingRect(cnt);
                int radius = r.width / 2;

                if (Math.abs(
                        1 - (
                                r.width / r.height
                        )
                ) <= 0.2 &&
                        Math.abs(
                                1 - (
                                        contourArea / (Math.PI * radius * radius)
                                )
                        ) <= 0.2
                        ) {
                    if (!DISPLAY_IMAGES) {
                        detected_shape = "Circle Detected!";
                        setLabel(dst, "CIR", cnt);
                    }
                }
                angle1 = null;
                angle2 = null;
                range = null;

            }


        }

        // return the matrix / image to show on the screen
        return dst;

    }

    /**
     * Helper function to find a cosine of angle between vectors
     * from pt0->pt1 and pt0->pt2
     */
    private static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2)
                / Math.sqrt(
                (dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10
        );
    }

    /**
     * display a label in the center of the given contur (in the given image)
     *
     * @param im      the image to which the label is applied
     * @param label   the label / text to display
     * @param contour the contour to which the label should apply
     */
    private static void setLabel(Mat im, String label, MatOfPoint contour) {
        int fontface = Core.FONT_HERSHEY_SIMPLEX;
        double scale = 3;//0.4;
        int thickness = 3;//1;
        int[] baseline = new int[1];

        Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
        Rect r = Imgproc.boundingRect(contour);

        Point pt = new Point(
                r.x + ((r.width - text.width) / 2),
                r.y + ((r.height + text.height) / 2)
        );

        Imgproc.putText(im, label, pt, fontface, scale, RGB_RED, thickness);

    }


    /**
     * makes an logcat/console output with the string detected
     * displays also a TOAST message and finally sends the command to the overlay
     *
     * @param content the content of the detected barcode
     */
    private void doSomethingWithContent(String content) {

        final String command = content;


        changeCanvas(command);

    }

    /**
     * change the canvas by given command
     *
     * @param changingType the command as String
     */
    public void changeCanvas(String changingType) {
        // force redraw with the given command
        // but only if same command has not been used before
        if (this.changingType == null
                || !this.changingType.equals(changingType)) {
            this.changingType = changingType;
        }


        if (changingType != null) {
            switch (changingType) { // looking for the chosen command
                case "rectangle":
                    drawRectangle();
                    break;
                case "triangle":
                    drawTriangle();
                    break;
                default:
            }
        }
    }

    private static void drawRectangle() {
        System.out.println("rectangle");
    }

    private static void drawTriangle() {
        System.out.println("triangle");
    }


    @FXML
    public void ToExcel(ActionEvent e) throws IOException {


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Excel Sheet Helper");
        alert.setHeaderText("*_*");
        alert.setContentText("copy measured data to excel sheet.");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {


            TableView<Angle> table = new TableView<Angle>();

            ObservableList<Angle> teamMembers = getTeamMembers();
            table.setItems(teamMembers);

            TableColumn<Angle, String> min_angleCol = new TableColumn<Angle, String>("Min Angle");
            min_angleCol.setCellValueFactory(new PropertyValueFactory<Angle, String>("min_angle"));
            TableColumn<Angle, String> max_angleCol = new TableColumn<Angle, String>("Max Angle");
            max_angleCol.setCellValueFactory(new PropertyValueFactory<Angle, String>("max_angle"));
            TableColumn<Angle, String> identified_shapeCol = new TableColumn<Angle, String>("Shape");
            identified_shapeCol.setCellValueFactory(new PropertyValueFactory<Angle, String>("identified_shape"));
            TableColumn<Angle, String> angle_rangeCol = new TableColumn<Angle, String>("Angle Range");
            angle_rangeCol.setCellValueFactory(new PropertyValueFactory<Angle, String>("angle_range"));


            ObservableList<TableColumn<Angle, ?>> columns = table.getColumns();
            columns.add(min_angleCol);
            columns.add(max_angleCol);
            columns.add(identified_shapeCol);
            columns.add(angle_rangeCol);

            Workbook workbook = new HSSFWorkbook();
            Sheet spreadsheet = workbook.createSheet("sample");

            Row row = spreadsheet.createRow(0);

            for (int j = 0; j < table.getColumns().size(); j++) {
                row.createCell(j).setCellValue(table.getColumns().get(j).getText());
            }

            for (int i = 0; i < table.getItems().size(); i++) {
                row = spreadsheet.createRow(i + 1);
                for (int j = 0; j < table.getColumns().size(); j++) {
                    if (table.getColumns().get(j).getCellData(i) != null) {
                        row.createCell(j).setCellValue(table.getColumns().get(j).getCellData(i).toString());
                    } else {
                        row.createCell(j).setCellValue("");
                    }
                }
            }

            FileOutputStream fileOut = new FileOutputStream("img_rnd_workbook.xls");
            workbook.write(fileOut);
            fileOut.close();


            alert.hide();

        } else {

            alert.hide();
        }


    }

    private ObservableList<Angle> getTeamMembers() {
        ObservableList<Angle> angle = FXCollections.observableArrayList();
        Angle angle_1 = new Angle();
        angle_1.setmin_angle(angle1);
        angle_1.setmax_angle(angle2);
        angle_1.setidentified_shape(detected_shape);
        angle_1.setangle_range(range);
        angle.add(angle_1);


        return angle;
    }

    public class Angle {
        private StringProperty min_angle;

        public void setmin_angle(String value) {
            min_angleProperty().set(value);
        }

        public String getmin_angle() {
            return min_angleProperty().get();
        }

        public StringProperty min_angleProperty() {
            if (min_angle == null) min_angle = new SimpleStringProperty(this, "min_angle");
            return min_angle;
        }

        private StringProperty max_angle;

        public void setmax_angle(String value) {
            max_angleProperty().set(value);
        }

        public String getmax_angle() {
            return max_angleProperty().get();
        }

        public StringProperty max_angleProperty() {
            if (max_angle == null) max_angle = new SimpleStringProperty(this, "max_angle");
            return max_angle;
        }

        private StringProperty identified_shape;

        public void setidentified_shape(String value) {
            identified_shapeProperty().set(value);
        }

        public String getidentified_shape() {
            return identified_shapeProperty().get();
        }

        public StringProperty identified_shapeProperty() {
            if (identified_shape == null) identified_shape = new SimpleStringProperty(this, "identified_shape");
            return identified_shape;
        }

        private StringProperty angle_range;

        public void setangle_range(String value) {
            angle_rangeProperty().set(value);
        }

        public String getangle_range() {
            return angle_rangeProperty().get();
        }

        public StringProperty angle_rangeProperty() {
            if (angle_range == null) angle_range = new SimpleStringProperty(this, "angle_range");
            return angle_range;
        }
    }


}
