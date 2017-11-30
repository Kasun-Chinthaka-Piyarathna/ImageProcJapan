package sample;

import config.*;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.*;
import model.*;
import org.apache.logging.log4j.*;
import org.opencv.core.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


@Service
public class Main extends Application {

    public static int count_number = 0;

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //     logger.info("Starting application");

        try {


            Platform.setImplicitExit(true);
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            ScreensConfig screens = context.getBean(ScreensConfig.class);


            LanguageModel lang = context.getBean(LanguageModel.class);
            screens.setLangModel(lang);


            screens.setPrimaryStage(primaryStage);
            screens.showMainScreen();
            screens.loadFirst();
        } catch (Exception e) {
            System.err.println(e);
        }
    }


}
