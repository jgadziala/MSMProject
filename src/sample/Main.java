package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initRootLayout();
        showMainOverview();
    }

    public void initRootLayout() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class
                .getResource("root.fxml"));
        try {
            rootLayout = (BorderPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootLayout.setPrefWidth(1200);
        rootLayout.setPrefHeight(800);

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

        RootController rootController = loader.getController();
        rootController.setMain(this);

        primaryStage.show();
    }

    public void showMainOverview() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("sample.fxml"));
        AnchorPane mainOverview = null;
        try {
            mainOverview = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rootLayout.setCenter(mainOverview);
        this.controller = loader.getController();
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Controller getController() {
        return controller;
    }

}
