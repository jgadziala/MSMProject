package sample;

import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Random;

public class Controller {
    Board board;
    Random rand;
    int width;
    int height;
    private Stage dialogStage;
    private Main main;


    @FXML
    TextField heightTF;

    @FXML
    TextField widthTF;

    @FXML
    public void initialize() {
        rand = new Random();
        width = Integer.parseInt(heightTF.getText());
        height = Integer.parseInt(widthTF.getText());

     board = new Board(width,height,"todo", 10);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    public void setMain(Main main) {
        this.main = main;
    }

}
