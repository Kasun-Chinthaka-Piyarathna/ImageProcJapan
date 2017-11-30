/*
 *   (C) Copyright 1996-2017 hSenid Software International (Pvt) Limited.
 *   All Rights Reserved.
 *
 *   These materials are unpublished, proprietary, confidential source code of
 *   hSenid Software International (Pvt) Limited and constitute a TRADE SECRET
 *   of hSenid Software International (Pvt) Limited.
 *
 *   hSenid Software International (Pvt) Limited retains all title to and intellectual
 *   property rights in these materials.
 *
 */
package sample;

import config.*;
import gui.*;
import javafx.beans.value.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import model.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ThresholdController extends Modal implements Initializable {

    @FXML
    private ImageView thresholdImageView;
    @FXML
    private Button convertButton;
    @FXML
    private Slider hueSliderMin;
    @FXML
    private Slider saturationSliderMin;
    @FXML
    private Slider valueSliderMin;
    @FXML
    private Slider hueSliderMax;
    @FXML
    private Slider saturationSliderMax;
    @FXML
    private Slider valueSliderMax;

    @Autowired
    private MessageModel model;

    double adjHueMin, adjSaturationMin, adjValueMin, adjHueMax, adjSaturationMax, adjValueMax;
    private Mat result;

    public ThresholdController(ScreensConfig config) {
        super(config);
    }


    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        thresholdImageView.setImage(convertMatToImage(model.getImage()));
        configureHSVSlider();
        configureSaturationSlider();
        configureValueSlider();
    }

    private Image convertMatToImage(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }


    private void configureHSVSlider() {
        hueSliderMin.disableProperty().setValue(false);
        hueSliderMin.setPrefWidth(300);
        hueSliderMin.setMin(0);
        hueSliderMin.setMax(255);
        hueSliderMin.setMajorTickUnit(25);
        hueSliderMin.setShowTickMarks(true);
        hueSliderMin.setShowTickLabels(true);
        hueSliderMin.setValue(0);
        hueSliderMin.valueProperty().addListener(sliderChangeListener);

        hueSliderMax.disableProperty().setValue(false);
        hueSliderMax.setPrefWidth(300);
        hueSliderMax.setMin(0);
        hueSliderMax.setMax(255);
        hueSliderMax.setMajorTickUnit(25);
        hueSliderMax.setShowTickMarks(true);
        hueSliderMax.setShowTickLabels(true);
        hueSliderMax.setValue(255);
        hueSliderMax.valueProperty().addListener(sliderChangeListener);
    }

    private void configureSaturationSlider() {
        saturationSliderMin.disableProperty().setValue(false);
        saturationSliderMin.setPrefWidth(300);
        saturationSliderMin.setMin(0);
        saturationSliderMin.setMax(255);
        saturationSliderMin.setMajorTickUnit(25);
        saturationSliderMin.setShowTickMarks(true);
        saturationSliderMin.setShowTickLabels(true);
        saturationSliderMin.setValue(0);
        saturationSliderMin.valueProperty().addListener(sliderChangeListener);

        saturationSliderMax.disableProperty().setValue(false);
        saturationSliderMax.setPrefWidth(300);
        saturationSliderMax.setMin(0);
        saturationSliderMax.setMax(255);
        saturationSliderMax.setMajorTickUnit(25);
        saturationSliderMax.setShowTickMarks(true);
        saturationSliderMax.setShowTickLabels(true);
        saturationSliderMax.setValue(255);
        saturationSliderMax.valueProperty().addListener(sliderChangeListener);
    }

    private void configureValueSlider() {
        valueSliderMin.disableProperty().setValue(false);
        valueSliderMin.setPrefWidth(300);
        valueSliderMin.setMin(0);
        valueSliderMin.setMax(255);
        valueSliderMin.setMajorTickUnit(25);
        valueSliderMin.setShowTickMarks(true);
        valueSliderMin.setShowTickLabels(true);
        valueSliderMin.setValue(0);
        valueSliderMin.valueProperty().addListener(sliderChangeListener);

        valueSliderMax.disableProperty().setValue(false);
        valueSliderMax.setPrefWidth(300);
        valueSliderMax.setMin(0);
        valueSliderMax.setMax(255);
        valueSliderMax.setMajorTickUnit(25);
        valueSliderMax.setShowTickMarks(true);
        valueSliderMax.setShowTickLabels(true);
        valueSliderMax.setValue(255);
        valueSliderMax.valueProperty().addListener(sliderChangeListener);
    }

    ChangeListener<Number> sliderChangeListener = (observable, oldValue, newValue) -> updateImageHSV();

    private void updateImageHSV() {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        adjHueMin = hueSliderMin.valueProperty().doubleValue();
        adjSaturationMin = saturationSliderMin.valueProperty().doubleValue();
        adjValueMin = valueSliderMin.valueProperty().doubleValue();

        adjHueMax = hueSliderMax.valueProperty().doubleValue();
        adjSaturationMax = saturationSliderMax.valueProperty().doubleValue();
        adjValueMax = valueSliderMax.valueProperty().doubleValue();

        Mat img = model.getImage();
        Mat hsv = img.clone();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsv, new Scalar(adjHueMin, adjSaturationMin, adjValueMin), new Scalar(adjHueMax,
                adjSaturationMax, adjValueMax), hsv);

        result = hsv;
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", hsv, buffer);
        thresholdImageView.setImage(new Image(new ByteArrayInputStream(buffer.toArray())));
    }

    @FXML
    public void updateChanges() {
        model.setImage(result);
        dialog.close();
    }
}

