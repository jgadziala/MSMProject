package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    GraphicsContext gc;
    List<Color> colors;
    Color cellColor = Color.BLACK;
    Thread thread, mcThread;

    Board board;
    Random rand;
    int width;
    int height;
    int cellSize, cellSizeY;
    Color backgroundColor = Color.LIGHTYELLOW;
    private volatile boolean running = true;
    private volatile boolean mcRunning = true;
    private Stage dialogStage;
    private Main main;


    @FXML
    TextField heightTF;

    @FXML
    TextField widthTF;
    @FXML
    Button startButton;
    @FXML
    Canvas canvas;
    @FXML
    TextField textField;

    @FXML
    CheckBox checkbox;
    @FXML
    ChoiceBox choiceBox;

    @FXML
    public void initialize() {
        rand = new Random();


    }

    @FXML
    public void generate() {
        cellSize = cellSizeY = 1;

        width = Integer.parseInt(heightTF.getText());
        height = Integer.parseInt(widthTF.getText());
        generateBoard(width, height, cellSize);
        checkbox.setSelected(true);
        textField.setText(3 + "");

//     board = new Board(width,height,"todo", 10);

        choiceBox.getItems().addAll("Moore'a", "von Neumann'a");
        choiceBox.setValue("von Neumann'a");
        startButton.setText("start");
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

    void generateBoard(int width, int height, int cellSize) {
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

    @FXML
    public void handleStart() {
        if(startButton.getText().equals("start")) {
            running = true;
            thread = new Thread(() -> {
                while (running) {
                    Platform.runLater(() -> startFunction());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            });
            thread.start();
            startButton.setText("STOP!");
        }else if(startButton.getText().equals("STOP!")){
            running = false;
            thread.interrupt();
            startButton.setText("start");
            drawBoard();
        }
    }

    public void startFunction(){
        boolean isFinished;
        board.setPeriod(checkbox.isSelected());
        board.setNeighbourhoodSelectionType((String) choiceBox.getValue());
        isFinished = board.nextCycle();
        if(isFinished) {
            drawBoard();
            running = false;
            thread.interrupt();
            startButton.setText("start");
        }

    }


    public void drawBoard(){
        for(int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                if(board.getCellState(i,j) && board.getCellGrainType(i,j) != -1){
                    //System.out.println(i + " "+ j);
                    //gc.setFill(board.getCellColor(i,j));
                    //gc.setFill(cellColor);
                    gc.setFill(colors.get(board.getCellGrainType(i,j)-1));
                    gc.fillRect(i*cellSize,j*cellSizeY,cellSize,cellSizeY);
                }else{
                    if(board.getCellGrainType(i,j) == -1)
                        gc.setFill(Color.BLACK);
                    else{
                        gc.setFill(backgroundColor);
                    }
                    gc.fillRect(i*cellSize,j*cellSizeY,cellSize,cellSizeY);
                }
            }
        }
    }
}
