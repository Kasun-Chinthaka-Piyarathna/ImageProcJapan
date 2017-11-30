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
package gui;

import config.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public abstract class Presentation {

    @FXML
    private Button button;

    protected ScreensConfig config;

    public Presentation(ScreensConfig config) {
        this.config = config;
//
//        Stage stage = (Stage) button.getScene().getWindow();
//        stage.setFullScreen(true);


    }
//    public Presentation(){
//
//    }

}
