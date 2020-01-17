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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
    int cellSize = 1;
    int cellSizeY = 1;
    Color backgroundColor = Color.WHITE;
    private volatile boolean running = true;
    private volatile boolean mcRunning = true;
    private Stage dialogStage;
    private Main main;
    @FXML
    TextField boundaryWidth;
    @FXML
    CheckBox onlySelectedGrains;
    @FXML
    ChoiceBox caAlgorithmType;
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
        caAlgorithmType.getItems().addAll("Substructure", "Dual phase");
        caAlgorithmType.setValue("Substructure");
        boundaryWidth.setText("1");

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
//        clickOnCanvas();
        //drawLines();
        gc.setFill(cellColor);
        canvas.setOnMouseClicked(event -> {
            try {
                double x = event.getX();
                double y = event.getY();
                System.out.println("Clicked on: " + x + ", " + y);

                int cell_x = (int) x * width / (width * cellSize) - 1;
                int cell_y = (int) y * height / (height * cellSize) - 1;
                System.out.println("Cell nr: " + cell_x + ", " + cell_y);
                if (board.getCellState(cell_x, cell_y)) {

                    //Color color = board.getCellColor(cell_x, cell_y);

                    int type = board.getCellGrainType(cell_x,cell_y);
                    //Color color = colors.get(type);
                    if(board.getSelectedGrains().get(type) == null) {
                        Color selectingCOlor = Color.DARKMAGENTA;
//                        if(!onlySelectedGrains.isSelected()) {
                            if (caAlgorithmType.getValue().toString() == "Substructure") {
                                board.getSelectedGrains().put(type, 0);
                                selectingCOlor = Color.MAGENTA;
                            } else {
                                board.getSelectedGrains().put(type, 1);
                            }
//                        }else{
//                            selectingCOlor = Color.GOLD;
//                            board.getSelectedGrains().put(type, 2);
//                        }

                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < height; j++) {
                                if (board.getCellGrainType(i, j) == type) {
                                    //board.setCellGrainType(i,j, Integer.MAX_VALUE);
                                    //board.setCellColor(i,j,Color.DARKMAGENTA);
                                    gc.setFill(selectingCOlor);
                                    gc.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                                }
                            }
                        }
                    }else{
                        board.getSelectedGrains().remove(Integer.valueOf(type));
                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < height; j++) {
                                if (board.getCellGrainType(i, j) == type && type != Integer.MAX_VALUE) {
                                    //board.setCellGrainType(i,j, Integer.MAX_VALUE);
                                    //board.setCellColor(i,j,Color.DARKMAGENTA);
                                    gc.setFill(colors.get(type-1));
                                    gc.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                                }
                            }
                        }
                    }
                    //colors.add(board.getCellColor(cell_x, cell_y));
                    gc.setFill(board.getCellColor(cell_x, cell_y));
                    gc.fillRect(cell_x * cellSize, cell_y * cellSize, cellSize, cellSize);
                } else {
                    //board.setCellValue(cell_x, cell_y, false);
                    //gc.setFill(backgroundColor);
                    //gc.fillRect(x + 1 - (x % cellSize), y + 1 - (y % cellSize), cellSize-1, cellSize-1);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void handleStart() {

        if (startButton.getText().equals("start")) {
            running = true;
            thread = new Thread(() -> {
                int i = 0;
                while (running) {
                    Platform.runLater(this::startFunction);
                    try {
                        Thread.sleep(50);
                        if ((i % 10) == 0) {
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
            drawBoard();
        } else if (startButton.getText().equals("stop")) {
            running = false;
            thread.interrupt();
            startButton.setText("start");
            drawBoard();
        }

    }

    @FXML
    public void handleClear() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                board.setCellState(i, j, false);
                board.setCellGrainType(i, j, 0);
                gc.setFill(backgroundColor);
                gc.fillRect(i * cellSize, j * cellSizeY, cellSize, cellSizeY);
            }
        }
        colors.clear();
        board.getSelectedGrains().clear();

    }

    public void startFunction() {
        boolean isFinished;
        board.setPeriod(checkbox.isSelected());
        board.setNeighbourhoodType((String) choiceBox.getValue());
        board.setProbability(Integer.parseInt(rule4Probability.getText()));

        isFinished = board.nextCycle();
        if (isFinished) {
            drawBoard();
            running = false;
            thread.interrupt();
            startButton.setText("start");
        }

    }

    @FXML
    public void oneStep() {
        board.setPeriod(checkbox.isSelected());
        board.setNeighbourhoodType((String) choiceBox.getValue());
        board.nextCycle();
        drawBoard();
    }


    public void drawBoard() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (board.getCellState(i, j) && board.getCellGrainType(i, j) != -1) {
                    int type = board.getCellGrainType(i, j);
                    if (type != Integer.MAX_VALUE)
                        gc.setFill(colors.get(type - 1));
                    else
                        gc.setFill(Color.DARKMAGENTA);
                    gc.fillRect(i * cellSize, j * cellSizeY, cellSize, cellSizeY);
                } else {
                    if (board.getCellGrainType(i, j) == -1)
                        gc.setFill(Color.BLACK);
                    else {
                        gc.setFill(backgroundColor);
                    }
                    gc.fillRect(i * cellSize, j * cellSizeY, cellSize, cellSizeY);
                }
            }
        }
    }

    public void exportToTXT() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );
        File file = fileChooser.showSaveDialog(dialogStage);
        if (file != null) {
            try {
                PrintWriter writer;
                writer = new PrintWriter(file);
                writer.println(width + " " + height + " " + cellSize);
                for (int i = 0; i < board.getWidth(); i++) {
                    for (int j = 0; j < board.getHeight(); j++) {
                        String cell = i + " " + j + " " + board.getCellGrainType(i, j) + " " + board.getCellState(i, j) + " ";
                        writer.println(cell);
                    }
                }
                writer.close();

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }


    public void importFromTXT() {
        Map<Integer, Color> colorsMap = new HashMap<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );

        try {
            File file = fileChooser.showOpenDialog(dialogStage);
            if (file != null) {
                Scanner myReader = new Scanner(file);
                String data = myReader.nextLine();

                String[] parts = data.split(" ");
                xTextField.setText(parts[0]);
                yTextField.setText(parts[1]);
                generateBoard(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));

                while (myReader.hasNextLine()) {
                    data = myReader.nextLine();
                    parts = data.split(" ");
                    int i = Integer.valueOf(parts[0]);
                    int j = Integer.valueOf(parts[1]);
                    int type = Integer.valueOf(parts[2]);
                    boolean isALive = Boolean.valueOf(parts[3]);
                    board.setCellGrainType(i, j, type);
                    board.setCellState(i, j, isALive);
                    if (colorsMap.containsKey(type)) {
                        board.setCellColor(i, j, colorsMap.get(type));
                    } else {
                        if (type != -1) {
                            float r = rand.nextFloat();
                            float g = rand.nextFloat();
                            float b = rand.nextFloat();
                            board.setCellColor(i, j, Color.color(r, g, b));
                        } else {
                            board.setCellColor(i, j, Color.BLACK);
                        }
                        colorsMap.put(type, board.getCellColor(i, j));
                    }
                    gc.setFill(colorsMap.get(type));
                    gc.fillRect(i * cellSize, j * cellSizeY, cellSize, cellSizeY);
                }
                myReader.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void exportToBMP() {
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


    public void importFromBMP() {
        Map<Integer, Integer> colorsMap = new HashMap<>();
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
            int width = bufferedImage.getWidth() / cellSize;
            int height = bufferedImage.getHeight() / cellSizeY;
            xTextField.setText(String.valueOf(width));
            yTextField.setText(String.valueOf(height));
            generateBoard(width, height, cellSize);
            int counter = 0;
            for (int i = 0; i < bufferedImage.getWidth(); i += cellSize) {
                for (int j = 0; j < bufferedImage.getHeight(); j += cellSize) {
                    int xi = i / cellSize;
                    int yj = j / cellSizeY;
                    int colorRGB = bufferedImage.getRGB(i, j);
                    int r = (colorRGB & 0x00ff0000) >> 16;
                    int g = (colorRGB & 0x0000ff00) >> 8;
                    int b = colorRGB & 0x000000ff;
                    Color color = Color.rgb(r, g, b);
                    if (colorsMap.containsKey(colorRGB)) {
                        board.setCellColor(xi, yj, color);
                    } else if (colorRGB != -16777216) {
                        board.setCellColor(xi, yj, color);
                        colorsMap.put(colorRGB, counter++);
                    } else {
                        board.setCellColor(xi, yj, color);
                        colorsMap.put(colorRGB, -1);
                    }
                    board.setCellGrainType(xi, yj, colorsMap.get(colorRGB));
                    board.setCellState(xi, yj, true);
                    gc.setFill(color);
                    gc.fillRect(xi * cellSize, yj * cellSizeY, cellSize, cellSizeY);
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
        if (!board.isFinished()) {
            randInclusion(numberOfInclusion, sizeOfInclusion, false);
        } else {
            randInclusion(numberOfInclusion, sizeOfInclusion, true);
        }


    }

    public void randInclusion(int numberOfCells, int sizeOfInclusion, boolean onBorders) {
        try {
            List<Point> grainsOnBorder = null;
            if (onBorders) {
                grainsOnBorder = findGrainsOnBorders();
            }

            //colors = new Color[numberOfCells];
            for (int i = 0; i < numberOfCells; i++) {
                int x, y;

                if (!onBorders) {
                    x = rand.nextInt(width);
                    y = rand.nextInt(height);
                } else {
                    int point = rand.nextInt(grainsOnBorder.size());
                    x = grainsOnBorder.get(point).x;
                    y = grainsOnBorder.get(point).y;
                }

                for (int k = x - sizeOfInclusion; k < x + sizeOfInclusion; k++) {
                    for (int l = y - sizeOfInclusion; l < y + sizeOfInclusion; l++) {
                        if (k >= 0 && l >= 0 && k < width && l < height) {
                            if (countDistance(x, y, k, l, sizeOfInclusion)) {
                                board.setCellState(k, l, true);
                                board.setCellGrainType(k, l, -1);

                                board.setCellColor(k, l, Color.BLACK);
                                gc.setFill(board.getCellColor(k, l));

                                //colors[i]=board.getCellColor(x,y);
                                colors.add(board.getCellColor(k, l));
                                gc.fillRect(k * cellSize, l * cellSizeY, cellSize, cellSizeY);
                            }
                        } //else i--;
                    }
                }
                //Color randomColor = new Color.(r, g, b);


            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private List<Point> findGrainsOnBorders() {
        List<Point> grainsOnBorder = new ArrayList();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (board.isCellOnBorder(i, j))
                    grainsOnBorder.add(new Point(i, j));
            }
        }
        return grainsOnBorder;
    }

    public boolean countDistance(int x, int y, int k, int l, int size) {
        boolean cond = true;
        double distance = 0;
        distance = sqrt(pow((k - x), 2) + pow((l - y), 2));
        if ((distance) > (size - 1))
            cond = false;
        return cond;
    }


    public void clickOnCanvas() {
        canvas.setOnMouseClicked(event -> {
            try {
                double x = event.getX();
                double y = event.getY();

                int cell_x = (int) x * width / (width * cellSize) - 1;
                int cell_y = (int) y * height / (height * cellSize) - 1;
                System.out.println("Cell nr: " + cell_x + ", " + cell_y);
                if (board.getCellState(cell_x, cell_y)) {

                    Color color = board.getCellColor(cell_x, cell_y);
                    int type = board.getCellGrainType(cell_x, cell_y);
//                    board.getSelectedGrains().add(type);
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            if (board.getCellGrainType(i, j) == type) {
                                gc.setFill(Color.DARKMAGENTA);
                                gc.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                            }
                        }
                    }
                    //colors.add(board.getCellColor(cell_x, cell_y));
                    gc.setFill(board.getCellColor(cell_x, cell_y));
                    gc.fillRect(x - (x % cellSize), y - (y % cellSize), cellSize, cellSize);
                } else {
                    //board.setCellValue(cell_x, cell_y, false);
                    //gc.setFill(backgroundColor);
                    //gc.fillRect(x + 1 - (x % cellSize), y + 1 - (y % cellSize), cellSize-1, cellSize-1);


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void clearUnselectedGrains(){
        for(int i=0;i<width;i++) {
            for (int j = 0; j < height; j++) {
                int type = board.getCellGrainType(i,j);
                if(board.getCellState(i,j) && board.getSelectedGrains().get(type) == null){
                    board.setCellGrainType(i, j, 0);
                    board.setCellState(i, j, false);
                    //gc.setFill(Color.BEIGE);
                    //gc.fillRect(i * cellSize , j * cellSize , cellSize , cellSize );
                }else if(board.getCellState(i,j) && board.getSelectedGrains().get(type) == 0){
                    //gc.setFill(board.getCellColor(i,j));
                    //c.fillRect(i * cellSize , j * cellSize , cellSize , cellSize );
                } else if(board.getCellState(i,j) && board.getSelectedGrains().get(type) == 1){

                }
            }
        }
        for(int i=0;i<width;i++) {
            for (int j = 0; j < height; j++) {
                if (board.getSelectedGrains().get(board.getCellGrainType(i, j)) != null){
                    if (board.getSelectedGrains().get(board.getCellGrainType(i, j)) == 1) {
                        board.setCellGrainType(i, j, Integer.MAX_VALUE);
                        board.setCellColor(i, j, Color.DARKMAGENTA);
                    }
                }
            }
        }
        //board.getSelectedGrains().clear();
        board.getSelectedGrains().put(Integer.MAX_VALUE, 1);
        drawBoard();


    }


    @FXML
    public void drawBoundaries(){
            int type;
            Map<Integer, Integer> neighbours = new HashMap<>();
            int bw = Integer.parseInt(boundaryWidth.getText());
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (board.isCellOnBorder(i, j)) {
                        int counter = 1;
                        int ijType = board.getCellGrainType(i, j);
                        boolean alone = true;
                        while (counter <= bw) {
                            int left = i - 1 * counter;
                            int up = j - 1 * counter;
                            int right = i + 1 * counter;
                            int down = j + 1 * counter;
                            int[] x = {left, i, right, i};
                            int[] y = {j, up, j, down};
                            int tmpX, tmpY;


                            for (int k = 0; k < 4; k++) {
                                tmpX = x[k];
                                tmpY = y[k];
                                if (x[k] < 0) tmpX = width - 1 * counter;
                                if (x[k] >= width) tmpX = 0 + counter - 1;
                                if (y[k] < 0) tmpY = height - 1 * counter;
                                if (y[k] >= height) tmpY = 0 + counter - 1;

                                type = board.getCellGrainType(tmpX, tmpY);
                                if (type == ijType && !(tmpX == i && tmpY == j) && counter == 1)
                                    alone = false;
                                if (neighbours.containsKey(type)) {
                                    int count = neighbours.get(type);
                                    neighbours.put(type, count + 1);
                                } else if (type != 0 && type != -1) {
                                    neighbours.put(type, 1);
                                }
                            }
                            counter++;
                        }
//                        if (onlySelectedGrains.isSelected()){
//                            if(board.getSelectedGrains().get(ijType) == null) {
//                                neighbours.clear();
//                            }else if(board.getSelectedGrains().get(ijType) != 2){
//                                neighbours.clear();
//                            }
//                        }

                        if (neighbours.size() > 1 || alone) {
                            board.setCellGrainType(i, j, -1);
                            gc.setFill(Color.BLACK);
                            gc.fillRect(i * cellSize, j * cellSizeY, cellSize, cellSizeY);
                        }
                        neighbours.clear();
                    }
                }
            }

    }

}
