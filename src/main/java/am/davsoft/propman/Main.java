package am.davsoft.propman;

import am.davsoft.propman.controllers.MainController;
import am.davsoft.propman.helpers.AppSpecUncaughtExceptionHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author David Shahbazyan
 * @since Apr 19, 2017
 */
public class Main extends Application {
    public static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    private void initLogger() throws IOException {
        Properties props = new Properties();
        InputStream input = getClass().getResourceAsStream("/log4j.properties");
        props.load(input);
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
//        LOGGER.info("Logging has been successfully initialized.");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler(new AppSpecUncaughtExceptionHandler());
        primaryStage.setTitle("Properties Manager");
//        primaryStage.getIcons().add(new Image(""));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent root = loader.load();
        primaryStage.setMinWidth(((VBox) root).getPrefWidth());
        primaryStage.setMinHeight(((VBox) root).getPrefHeight());
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> Platform.exit());
//        primaryStage.getScene().getStylesheets().add(ResourceManager.getUIThemeStyle());
        primaryStage.getScene().setFill(Color.web("DDF", 0.75));
        primaryStage.setResizable(false);
        ((MainController) loader.getController()).setCurrentStage(primaryStage);
        primaryStage.show();
        primaryStage.requestFocus();
    }
}
