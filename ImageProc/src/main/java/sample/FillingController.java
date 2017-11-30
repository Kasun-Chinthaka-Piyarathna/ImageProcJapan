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
import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;
import org.opencv.core.*;
import org.opencv.imgproc.*;

public class FillingController {

    @FXML
    private Slider fillingSlider;

    private MessageModel model;
    private Mat src;

    @FXML
    public void initialize() {
        configSlider();
    }

    private void fillingImage(){
        src = model.getImage().clone();
        Imgproc.Canny(src, src, 60 * 0.5, 60);
        Mat mask = new Mat(src.rows() + 2, src.cols() + 2, CvType.CV_8U);
        Imgproc.floodFill(src, mask, new Point(0, 0), new Scalar(255), new Rect(), new Scalar(0), new Scalar(0),Imgproc.FLOODFILL_MASK_ONLY);
        model.setImage(src);
    }

    public void setModel(MessageModel model) {
        this.model = model;
    }

    private void configSlider(){
        fillingSlider.setMin(1);
        fillingSlider.setMax(15);
        fillingSlider.setValue(1);

        fillingSlider.valueProperty().addListener(fillingSliderChangeListener);

    }

    ChangeListener<Number> fillingSliderChangeListener = (observable, oldValue, newValue) -> fillingImage();
}
