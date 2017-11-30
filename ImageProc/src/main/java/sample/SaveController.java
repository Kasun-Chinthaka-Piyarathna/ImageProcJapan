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
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import model.*;
import org.opencv.imgcodecs.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;

public class SaveController extends Modal {

    @FXML
    private Button saveButton;
    @FXML
    private ChoiceBox saveChoiceBox;
    @FXML
    private TextField saveLocation;

    @Autowired
    MessageModel model;

    public SaveController(ScreensConfig config) {
        super(config);
    }

    @FXML
    public void initialize(){
        configChoiceBox();
    }

    @FXML
    public void saveButtonClickAction(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File("/home/chinthaka/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(saveButton.getScene().getWindow());
        try{
            Imgcodecs imageCodecs = new Imgcodecs();
            String file2 = selectedDirectory.getAbsolutePath()+"/"+saveLocation.getText()+changeSaveOptions();

            imageCodecs.imwrite(file2, model.getImage());
            dialog.close();
        } catch (Exception e){
            System.out.println("Error occurred while saving image");
        }
    }

    private void configChoiceBox(){
        saveChoiceBox.setItems(FXCollections.observableArrayList(
                ".png", ".jpg", ".tif"));
//        saveChoiceBox.getSelectionModel().selectedIndexProperty().addListener(saveChangeListener);
        saveChoiceBox.setValue(".png");
    }

//    ChangeListener<Number> saveChangeListener = (observable, oldValue, nweValue) -> changeSaveOptions();

    private String changeSaveOptions(){
        return saveChoiceBox.getSelectionModel().getSelectedItem().toString();
    }


}
