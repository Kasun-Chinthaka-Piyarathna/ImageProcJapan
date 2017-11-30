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
package config;

import gui.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;
import model.*;
import org.slf4j.*;
import org.springframework.context.annotation.*;
import sample.*;

import java.net.*;
import java.util.*;

@Configuration
@Lazy
public class ScreensConfig implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(ScreensConfig.class);

    public static final int WIDTH = 1300;
    public static final int HEIGHT = 700;
    public static final String STYLE_FILE = "/css/main.css";

    private Stage stage;
    private Scene scene;
    private LanguageModel lang;
    private StackPane root;


    @FXML
    private ImageView imageView;

    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void setLangModel(LanguageModel lang) {

        if (this.lang != null) {
            this.lang.deleteObserver(this);
        }
        lang.addObserver(this);
        this.lang = lang;


//
//        System.out.println("Current Locale: " + Locale.getDefault());
//        ResourceBundle mybundle = ResourceBundle.getBundle("lang");
//
//        System.out.println("Say how are you in US English: " + mybundle.getString("how_are_you"));
//
//        Locale.setDefault(new Locale("ja", "JP"));
//
//        System.out.println("Current Locale: " + Locale.getDefault());
//        mybundle = ResourceBundle.getBundle("lang");
//        System.out.println("Say how are you in Malaysian Malaya language: " + mybundle.getString("how_are_you"));

    }

    public ResourceBundle getBundle() {
        return lang.getBundle();
    }

    public void showMainScreen() {
        root = new StackPane();
        root.getStylesheets().add(STYLE_FILE);
        root.getStyleClass().add("main-window");
        stage.setTitle("Image Processing");
        scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setResizable(true);

        stage.setOnHiding(event -> {
            System.exit(0);
            // TODO you could add code to open an "are you sure you want to exit?" dialog
        });

        stage.show();
        stage.setFullScreen(true);

    }

    private void setNode(Node node) {
        root.getChildren().setAll(node);
    }

    private void setNodeOnTop(Node node) {
        root.getChildren().add(node);
    }

    public void removeNode(Node node) {
        root.getChildren().remove(node);
    }


    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        loadFirst();
    }

    @Bean
    @Scope("prototype")
    BaseController firstPresentation() {
        return new BaseController(this);
    }

    @Bean
    @Scope("prototype")
    ThresholdController popupPresentation() {
        return new ThresholdController(this);
    }

    @Bean
    @Scope("prototype")
    SaveController savePresentation() {
        return new SaveController(this);
    }

    public void loadFirst() {
        setNode(getNode(firstPresentation(), getClass().getResource("/fxml/sample.fxml")));
    }

    private Node getNode(final Presentation control, URL location) {
        FXMLLoader loader = new FXMLLoader(location, lang.getBundle());
//        loader.setControllerFactory(aClass -> control);
        loader.setController(control);

        try {
            return (Node) loader.load();
        } catch (Exception e) {
            logger.error("Error casting node", e);
            return null;
        }
    }

    public void loadPopupThreshold() {
        ModalDialog modal = new ModalDialog(popupPresentation(), getClass().getResource("/fxml/threshold.fxml"),
                stage.getOwner(), lang.getBundle());
//        modal.setTitle(lang.getBundle().getString("popup.title"));
        modal.setTitle("Threshold Image");
        modal.getStyleSheets().add(STYLE_FILE);
        modal.show();
    }

    public void loadSaveImage(){
        ModalDialog modalDialog = new ModalDialog(savePresentation(),getClass().getResource("/fxml/save.fxml"),stage.getOwner(),lang.getBundle());
        modalDialog.setTitle("Save Image");
        modalDialog.getStyleSheets().add(STYLE_FILE);
        modalDialog.show();
    }

}
