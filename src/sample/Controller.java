package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Controller {
    GraphicsContext gc;
    List<Color> colors;
    Color cellColor = Color.BLACK;
    Thread thread, mcThread;

    Board board;
    Random rand;
    int width;
    int height;
    int cellSize=1;
    int cellSizeY=1;
    Color backgroundColor = Color.WHITE;
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
    TextField nrOfGrains;

    @FXML
    CheckBox checkbox;
    @FXML
    ChoiceBox choiceBox;
    @FXML
    TextField xTextField;
    @FXML
    TextField yTextField;
    @FXML
    TextField inclusionsAmountTextField;
    @FXML
    TextField inclusionsSizeTextField;
//    @FXML
//    ChoiceBox inclusionsTypeChoiceBox;
    @FXML
    TextField rule4Probability;
    @FXML
    public void initialize() {
        rand = new Random();
        inclusionsAmountTextField.setText("3");
        inclusionsSizeTextField.setText("10");
        rule4Probability.setText("10");

//        inclusionsTypeChoiceBox.getItems().addAll("square","circle");
//        inclusionsTypeChoiceBox.setValue("square");
    }

    @FXML
    public void generate() {
        cellSize = cellSizeY = 1;

        width = Integer.parseInt(heightTF.getText());
        height = Integer.parseInt(widthTF.getText());
        generateBoard(width, height, cellSize);
//        checkbox.setSelected(true);
//        nrOfGrains.setText(1 + "");

        choiceBox.getItems().addAll("Moore", "von Neumann");
        choiceBox.setValue("Moore");
        startButton.setText("start");
    }


    @FXML
    public void handleRand() {

        int numberOfCells = Integer.parseInt(nrOfGrains.getText());
        if (numberOfCells > (width * height)) {
            numberOfCells = width * height;
            nrOfGrains.setText(numberOfCells + "");
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
                int i =0;
                while (running) {
                    Platform.runLater(this::startFunction);
                    try {
                        Thread.sleep(50);
                        if((i % 10) == 0) {
                            drawBoard();
                        }
                        i++;

                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            });
            thread.start();
            startButton.setText("stop");
        }else if(startButton.getText().equals("stop")){
            running = false;
            thread.interrupt();
            startButton.setText("start");
            drawBoard();
        }
    }

    @FXML
    public void handleClear(){
        for(int i=0; i<width; i++){
            for(int j=0;j<height;j++){
                board.setCellState(i,j,false);
                board.setCellGrainType(i,j,0);
                gc.setFill(backgroundColor);
                gc.fillRect(i*cellSize,j*cellSizeY,cellSize,cellSizeY);
            }
        }
        colors.clear();
    }

    public void startFunction(){
        boolean isFinished;
        board.setPeriod(checkbox.isSelected());
        board.setNeighbourhoodType((String) choiceBox.getValue());
        board.setProbability(Integer.parseInt(rule4Probability.getText()));

        isFinished = board.nextCycle();
        if(isFinished) {
            drawBoard();
            running = false;
            thread.interrupt();
            startButton.setText("start");
        }

    }

    @FXML
    public void oneStep(){
        board.setPeriod(checkbox.isSelected());
        board.setNeighbourhoodType((String) choiceBox.getValue());
        board.nextCycle();
        drawBoard();
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



    public void exportToBMP(){
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save canvas");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
        File file = fileChooser.showSaveDialog(dialogStage);
        if (file != null) {
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

                BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                        bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);

                Boolean isFinish = ImageIO.write(newBufferedImage, "bmp", file);
                System.out.println(isFinish);

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    public void importFromBMP(){
        Map<Integer,Integer> colorsMap = new HashMap<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
            File file = fileChooser.showOpenDialog(dialogStage);
            if (file != null) {

                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(file);
                    System.out.println(bufferedImage.getWidth());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int width = bufferedImage.getWidth()/cellSize;
                int height = bufferedImage.getHeight()/cellSizeY;
                xTextField.setText(String.valueOf(width));
                yTextField.setText(String.valueOf(height));
                generateBoard(width, height, cellSize);
                int counter = 0;
                for(int i =0; i <bufferedImage.getWidth();i += cellSize){
                    for (int j = 0; j<bufferedImage.getHeight(); j += cellSize){
                        int xi = i/cellSize;
                        int yj = j/cellSizeY;
                        int colorRGB = bufferedImage.getRGB(i,j);
                        int  r   = (colorRGB & 0x00ff0000) >> 16;
                        int  g = (colorRGB & 0x0000ff00) >> 8;
                        int  b  =  colorRGB & 0x000000ff;
                        Color color = Color.rgb(r,g,b);
                        if (colorsMap.containsKey(colorRGB)) {
                            board.setCellColor(xi, yj, color);
                        } else if(colorRGB != -16777216){
                            board.setCellColor(xi, yj, color);
                            colorsMap.put(colorRGB, counter++);
                        } else {
                            board.setCellColor(xi, yj, color);
                            colorsMap.put(colorRGB, -1);
                        }
                        board.setCellGrainType(xi,yj,colorsMap.get(colorRGB));
                        board.setCellState(xi,yj,true);
                        gc.setFill(color);
                        gc.fillRect(xi * cellSize , yj * cellSizeY , cellSize, cellSizeY);
                    }
                }

            }

    }

    @FXML
    public void addInclusions() {
        int numberOfInclusion = Integer.parseInt(inclusionsAmountTextField.getText());
        int sizeOfInclusion = Integer.parseInt(inclusionsSizeTextField.getText());

        if (numberOfInclusion > (width * height)) {
            numberOfInclusion = width * height;
            nrOfGrains.setText(numberOfInclusion + "");
        }
        if(!board.isFinished()) {
            randInclusion(numberOfInclusion, sizeOfInclusion, false);
        }else{
            randInclusion(numberOfInclusion, sizeOfInclusion, true);
        }


    }

    public void randInclusion(int numberOfCells, int sizeOfInclusion, boolean onBorders){
        try {
            List<Point> grainsOnBorder = null;
            if(onBorders){
                grainsOnBorder = findGrainsOnBorders();
            }

            //colors = new Color[numberOfCells];
            for (int i = 0; i < numberOfCells; i++) {
                int x, y;

                if(!onBorders) {
                    x = rand.nextInt(width);
                    y = rand.nextInt(height);
                }else{
                    int point = rand.nextInt(grainsOnBorder.size());
                    x = grainsOnBorder.get(point).x;
                    y = grainsOnBorder.get(point).y;
                }

                for(int k = x - sizeOfInclusion; k<x+sizeOfInclusion; k++){
                    for(int l = y - sizeOfInclusion; l<y+sizeOfInclusion; l++){
                        if (k>=0 && l>=0 && k<width && l<height) {
                            if(countDistance(x,y,k,l,sizeOfInclusion)) {
                                board.setCellState(k, l, true);
                                board.setCellGrainType(k, l, -1);

                                board.setCellColor(k, l, Color.BLACK);
                                gc.setFill(board.getCellColor(k, l));

                                //colors[i]=board.getCellColor(x,y);
                                colors.add(board.getCellColor(k, l));
                                gc.fillRect(k * cellSize , l * cellSizeY , cellSize, cellSizeY);
                            }
                        } //else i--;
                    }
                }
                //Color randomColor = new Color.(r, g, b);


            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR Dialog");
            alert.setHeaderText("Error");
            alert.setContentText("Not a number");
            alert.showAndWait();
        }
    }

    private List<Point> findGrainsOnBorders() {
        List<Point> grainsOnBorder = new ArrayList();
        for(int i=0;i<width;i++) {
            for (int j = 0; j < height; j++) {
                if(board.isCellOnBorder(i,j))
                    grainsOnBorder.add(new Point(i,j));
            }
        }
        return grainsOnBorder;
    }

    public boolean countDistance(int x, int y, int k, int l, int size){
        boolean cond=true;
        double distance=0;
        distance= sqrt(pow((k-x),2) + pow((l-y),2));
        if((distance)>(size-1))
            cond=false;
        return cond;
    }
}
