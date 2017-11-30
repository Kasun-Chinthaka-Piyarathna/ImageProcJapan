package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MouseClickCount {


    // Counts the number of clicks
    private int counter = 0;

    // The label to display the counter value.
    @FXML
    private TextField text1;

    // Button that counts up when clicked.
    @FXML
    private Button but1;

    // Button that counts down when clicked.
    @FXML
    private Button but2;




    @FXML
    public void CountUp(ActionEvent e) {
        if (e.getSource() == but1) {
            counter++;
            text1.setText("Current count is: " + counter);
        }


    }

    @FXML
    public void CountDown(ActionEvent e) {



        if (e.getSource() == but2) {
            counter--;
            text1.setText("Current count is: " + counter);
        }

    }


}
