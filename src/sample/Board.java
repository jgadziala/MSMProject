package sample;

import javafx.scene.paint.Color;

import java.util.Random;

public class Board {
    Random random = new Random();
    private int width;
    private int height;
    private Cell[][] cells;
    private String neighbourhoodSelectionType;
    private int grainSeeds;

//    public Board(int width, int height, String neighbourhoodSelectionType, int grainSeeds) {
//        this.width = width;
//        this.height = height;
//        this.cells = new Cell[width][height];
//        this.neighbourhoodSelectionType = neighbourhoodSelectionType;
//        this.grainSeeds = grainSeeds;
//    }

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];
        random = new Random();
        generateEmptyBoard();
    }

    public void generateEmptyBoard(){
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                cells[i][j] = new Cell(false,0);
            }
        }
    }

    public void addGrainSeeds(int grainSeeds){
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        for (int i=0; i<grainSeeds;i++) {
            cells[x][y] = new Cell(true, i);
        }
    }

    public Color getCellColor(int x, int y) {
            return cells[x][y].getTypeColor();
    }

    public void setCellState(int i, int j, boolean alive){
        cells[i][j].setAlive(alive);
    }

    public void setCellGrainType(int x, int y, int grainType) {
            cells[x][y].setGrainType(grainType);
    }

    public boolean getCellState(int x, int y) {
            return cells[x][y].isAlive();
    }

    public int getCellGrainType(int x, int y) {
            return cells[x][y].getGrainType();
    }


    public void setCellColor(int x, int y, Color typeColor) {
            cells[x][y].setTypeColor(typeColor);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public String getNeighbourhoodSelectionType() {
        return neighbourhoodSelectionType;
    }

    public void setNeighbourhoodSelectionType(String neighbourhoodSelectionType) {
        this.neighbourhoodSelectionType = neighbourhoodSelectionType;
    }

    public int getGrainSeeds() {
        return grainSeeds;
    }

    public void setGrainSeeds(int grainSeeds) {
        this.grainSeeds = grainSeeds;
    }
}


