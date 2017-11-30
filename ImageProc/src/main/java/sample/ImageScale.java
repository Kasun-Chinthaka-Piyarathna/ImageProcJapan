package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageScale {

    @FXML
    Button imagescale;

    @FXML
    static TextArea d1;

    private File file;
    private Mat resultMat;


    javafx.scene.control.Label label;
    ImageView myImageView;
    javafx.scene.control.ScrollPane scrollPane;

    Boolean autoFix = true;

    Image myImage;


//    ImageScale() {
//        display_txt();
//    }


    public static void main(){
        String display_txt = "Scale display on screen for \n a given image. You can \n upload an image. You \n can see the original \n resolution. And it also \nfacilitates to auto fix to a \ncertain scale.";
        d1.setText(display_txt);
    }


    @FXML
    public void imagescale(ActionEvent e) throws IOException {

        Stage primaryStage = new Stage();

        javafx.scene.control.Button btnLoad = new javafx.scene.control.Button("Load");
        btnLoad.setOnAction(btnLoadEventListener);
        javafx.scene.control.Button btnAutoFix = new javafx.scene.control.Button("Auto Fix");
        btnAutoFix.setOnAction(btnAutoFixEventListener);
        javafx.scene.control.Button btnNoFix = new javafx.scene.control.Button("No Fix");
        btnNoFix.setOnAction(btnNoFixEventListener);

        label = new javafx.scene.control.Label();
        myImageView = new ImageView();
        scrollPane = new javafx.scene.control.ScrollPane();
        scrollPane.setPrefSize(1000, 1000);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(myImageView);

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(btnLoad, btnAutoFix, btnNoFix);

        VBox rootBox = new VBox();
        rootBox.getChildren().addAll(buttonBox, scrollPane, label);

        Scene scene = new Scene(rootBox, 400, 500);


        primaryStage.setTitle("Image Processing R&D");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    EventHandler<ActionEvent> btnLoadEventListener
            = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent t) {

            FileChooser fileChooser = new FileChooser();
            configureFileChooser(fileChooser);
            file = fileChooser.showOpenDialog(null);
            try {
                uploadInitialProcessing();
            } catch (IOException ee) {
                ee.printStackTrace();
            }


            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(file);
                myImage = SwingFXUtils.toFXImage(bufferedImage, null);

                setFix();

            } catch (IOException ex) {
                Logger.getLogger(MouseClickCount.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    };

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

    EventHandler<ActionEvent> btnAutoFixEventListener
            = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent t) {
            autoFix = true;
            setFix();
        }

    };

    EventHandler<ActionEvent> btnNoFixEventListener
            = new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent t) {
            autoFix = false;
            setFix();
        }

    };

    private void setFix() {
        if (autoFix) {
            myImageView.setFitWidth(400);
            myImageView.setFitHeight(400);
        } else {
            myImageView.setFitWidth(0);
            myImageView.setFitHeight(0);
        }

        myImageView.setPreserveRatio(true);
        myImageView.setSmooth(true);
        myImageView.setCache(true);

        myImageView.setImage(myImage);

        scrollPane.setContent(null);
        scrollPane.setContent(myImageView);

        //int boundWidth = (int)myImageView.getBoundsInLocal().getWidth();
        //int boundHeight = (int)myImageView.getBoundsInLocal().getHeight();
        int boundWidth = (int) myImageView.getBoundsInParent().getWidth();
        int boundHeight = (int) myImageView.getBoundsInParent().getHeight();

        label.setText("width: " + boundWidth
                + " x height: " + boundHeight);
    }


}
