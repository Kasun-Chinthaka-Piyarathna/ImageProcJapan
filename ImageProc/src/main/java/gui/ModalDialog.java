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

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;
import org.apache.logging.log4j.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ModalDialog extends Stage{

    private static final Logger logger = LogManager.getLogger(ModalDialog.class);
    private Scene scene;

    public ModalDialog(Modal controller, URL fxml, Window owner, ResourceBundle bundle) {
        this(controller, fxml, owner, StageStyle.DECORATED, Modality.APPLICATION_MODAL, bundle);
    }

    public ModalDialog(final Modal controller, URL fxml, Window owner, StageStyle style, Modality modality, ResourceBundle bundle) {
        super(style);
        initOwner(owner);
        initModality(modality);
        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setResources(bundle);
        try {
            loader.setControllerFactory(aClass -> controller);
            controller.setDialog(this);
            scene = new Scene((Parent) loader.load());
            setScene(scene);
        } catch (IOException e) {
            logger.error("Error loading modal class", e);
            throw new RuntimeException(e);
        }
    }

    public ObservableList<String> getStyleSheets() {
        return scene.getStylesheets();
    }
}
