package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    GraphicsContext gc;
    List<Color> colors;
    Color cellColor = Color.BLACK;

    Board board;
    Random rand;
    int width;
    int height;
    int cellSize, cellSizeY;

    private Stage dialogStage;
    private Main main;


    @FXML
    TextField heightTF;

    @FXML
    TextField widthTF;

    @FXML
    Canvas canvas;
    @FXML
    TextField textField;

    @FXML
    public void initialize() {
        rand = new Random();
        textField.setText(3 + "");

    }

    @FXML
    public void generate() {

        width = Integer.parseInt(heightTF.getText());
        height = Integer.parseInt(widthTF.getText());

//     board = new Board(width,height,"todo", 10);

        generateBoard(width, height);
    }


    @FXML
    public void handleRand() {

        int numberOfCells = Integer.parseInt(textField.getText());
        if (numberOfCells > (width * height)) {
            numberOfCells = width * height;
            textField.setText(numberOfCells + "");
        }
        randFunc(numberOfCells);
    }

    public void randFunc(int numberOfCells) {
            int size = colors.size();
            //colors = new Color[numberOfCells];
            for (int i = size; i < numberOfCells + size; i++) {

                int x = rand.nextInt(width);
                int y = rand.nextInt(height);
                float r = rand.nextFloat();
                float g = rand.nextFloat();
                float b = rand.nextFloat();

                //Color randomColor = new Color.(r, g, b);
                if (!board.getCellState(x, y)) {
                    board.setCellState(x, y, true);
                    board.setCellGrainType(x, y, i + 1);

                    board.setCellColor(x, y, Color.color(r, g, b));
                    gc.setFill(board.getCellColor(x, y));
                    //colors[i]=board.getCellColor(x,y);
                    colors.add(board.getCellColor(x, y));
                    gc.fillRect(x * cellSize, y * cellSizeY, cellSize, cellSizeY);
                } else i--;

            }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    void generateBoard(int width, int height) {
        this.cellSize = this.cellSizeY = cellSize;
        this.width = width;
        this.height = height;
        canvas.setWidth(width * cellSize);
        canvas.setHeight(height * cellSizeY);
        colors = new ArrayList<>();
        board = new Board(width, height);
        gc = canvas.getGraphicsContext2D();

        //drawLines();
        gc.setFill(cellColor);
    }

}
