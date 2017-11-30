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

import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;
import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.util.Optional;

import static org.opencv.imgproc.Imgproc.*;

public class FilterController {

    @FXML
    private Slider bilateralFilterValue;
    @FXML
    private Slider bilateralSigmaValue;
    @FXML
    private Slider boxFilterValue;
    @FXML
    private Slider filter2DValue;
    @FXML
    private Slider erosionValue;
    @FXML
    private Slider dilateValue;
    @FXML
    private ChoiceBox morphologicalOperations;

    private MessageModel model;
    private Mat src;
    private Mat unedit;

    @FXML
    public void initialize() {
        configSliders();
        configChoiceBox();
    }

    private void bilateralFilter() {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Bilateral Filter");
        alert.setContentText("Do you want to change values?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK

            initiateImage();

            int filterValue = bilateralFilterValue.valueProperty().intValue();
            int sigmaValue = bilateralSigmaValue.valueProperty().intValue();
            Mat dst = new Mat();
            Imgproc.bilateralFilter(src, dst, filterValue, sigmaValue, sigmaValue, Core.BORDER_DEFAULT);
            model.setImage(dst);


        } else {
            alert.hide();
        }

    }

    void configSliders() {
        bilateralFilterValue.setMin(1);
        bilateralFilterValue.setMax(15);
        bilateralFilterValue.setValue(1);
        bilateralFilterValue.valueProperty().addListener(bilateralSliderChangeListener);

        bilateralSigmaValue.setMin(0);
        bilateralSigmaValue.setMax(255);
        bilateralSigmaValue.setValue(0);
        bilateralSigmaValue.valueProperty().addListener(bilateralSliderChangeListener);

        boxFilterValue.setMin(0);
        boxFilterValue.setMax(255);
        boxFilterValue.setValue(0);
        boxFilterValue.valueProperty().addListener(boxSliderChangeListener);

        filter2DValue.setMin(0);
        filter2DValue.setMax(5);
        filter2DValue.setValue(0);
        filter2DValue.valueProperty().addListener(filter2DSliderChangeListener);

        dilateValue.setMin(0);
        dilateValue.setMax(10);
        dilateValue.setValue(0);
        dilateValue.valueProperty().addListener(dilateChangeListener);

        erosionValue.setMin(0);
        erosionValue.setMax(10);
        erosionValue.setValue(0);
        erosionValue.valueProperty().addListener(erosionChangeListener);
    }

    ChangeListener<Number> bilateralSliderChangeListener = (observable, oldValue, newValue) -> bilateralFilter();
    ChangeListener<Number> boxSliderChangeListener = (observable, oldValue, newValue) -> boxFilter();
    ChangeListener<Number> filter2DSliderChangeListener = (observable, oldValue, newValue) -> filter2DFilter();
    ChangeListener<Number> dilateChangeListener = (observable, oldValue, newValue) -> dilateImage();
    ChangeListener<Number> erosionChangeListener = (observable, oldValue, newValue) -> erosionImage();

    private void configChoiceBox() {
        morphologicalOperations.setItems(FXCollections.observableArrayList(
                "None", "MORPH_TOPHAT", "MORPH_BLACKHAT", "MORPH_CROSS", "MORPH_OPEN", "MORPH_CLOSE", "MORPH_GRADIENT"));
        morphologicalOperations.getSelectionModel().selectedIndexProperty().addListener(morphologicalChangeListener);
        morphologicalOperations.setValue("None");
    }


    ChangeListener<Number> morphologicalChangeListener = (observable, oldValue, nweValue) -> morphologicalOperations();

    public void setModel(MessageModel model) {
        this.model = model;
    }

    private void boxFilter() {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Box Filter");
        alert.setContentText("Do you want to change values?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK


            initiateImage();

            int boxValue = boxFilterValue.valueProperty().intValue();

            Mat frame = new Mat();
            Size size = new Size(boxValue, boxValue);
            Point point = new Point(-1, -1);
            Imgproc.boxFilter(src, frame, -1, size, point, true, Core.BORDER_DEFAULT);
            model.setImage(frame);


        } else {
            alert.hide();
        }
    }

    private void filter2DFilter() {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Filter2D");
        alert.setContentText("Do you want to change values?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK

            initiateImage();

            int filterValue = filter2DValue.valueProperty().intValue();
            Mat frame = new Mat();
            Mat kernel = Mat.ones(filterValue, filterValue, CvType.CV_32F);

            for (int i = 0; i < kernel.rows(); i++) {
                for (int j = 0; j < kernel.cols(); j++) {
                    double[] m = kernel.get(i, j);

                    for (int k = 1; k < m.length; k++) {
                        m[k] = m[k] / (2 * 2);
                    }
                    kernel.put(i, j, m);
                }
            }
            Imgproc.filter2D(src, frame, -1, kernel);
            model.setImage(frame);
        } else {
            alert.hide();
        }
    }

    private void dilateImage() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Dilate");
        alert.setContentText("Do you want to change values?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK

            initiateImage();

            int dilate = dilateValue.valueProperty().intValue();
            Mat dst = new Mat();
            Mat kernel = getStructuringElement(Imgproc.MORPH_RECT,
                    new Size((2 * 2) + dilate, (2 * 2) + dilate));
            Imgproc.dilate(src, dst, kernel);
            model.setImage(dst);

        } else {
            alert.hide();
        }
    }

    private void erosionImage() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Erosion");
        alert.setContentText("Do you want to change values?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK

            initiateImage();

            int erosion = erosionValue.valueProperty().intValue();
            Mat dst = new Mat();
            Mat kernel = getStructuringElement(Imgproc.MORPH_RECT,
                    new Size((2 * 2) + erosion, (2 * 2) + erosion));
            Imgproc.erode(src, dst, kernel);
            model.setImage(dst);
        } else {
            alert.hide();
        }
    }

    private void initiateImage() {

        if (src == null) {
            src = model.getImage().clone();
            unedit = src.clone();
        } else {
            src = unedit.clone();
        }
    }

    private void morphologicalOperations() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        int select = morphologicalOperations.getSelectionModel().getSelectedIndex();
        initiateImage();

        Mat dst = new Mat();
        Mat kernel = Mat.ones(5, 5, CvType.CV_32F);

        if (select == 1) {
            Imgproc.morphologyEx(unedit, dst, Imgproc.MORPH_CROSS, kernel);
        } else if (select == 2) {
            Imgproc.morphologyEx(unedit, dst, Imgproc.MORPH_OPEN, kernel);
        } else if (select == 3) {
            Imgproc.morphologyEx(unedit, dst, Imgproc.MORPH_CLOSE, kernel);
        } else if (select == 4) {
            Imgproc.morphologyEx(unedit, dst, Imgproc.MORPH_GRADIENT, kernel);
        } else if (select == 5) {
            Imgproc.morphologyEx(unedit, dst, Imgproc.MORPH_TOPHAT, kernel);
        } else if (select == 6) {
            Imgproc.morphologyEx(unedit, dst, Imgproc.MORPH_BLACKHAT, kernel);
        } else {
            dst = unedit.clone();
        }

        model.setImage(dst);

    }
}
