package sample;

import java.util.Random;

public class Board {
    Random random = new Random();
    private int width;
    private int height;
    private Cell[][] cells;
    private String neighbourhoodSelectionType;
    private int grainSeeds;

    public Board(int width, int height, String neighbourhoodSelectionType, int grainSeeds) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        this.neighbourhoodSelectionType = neighbourhoodSelectionType;
        this.grainSeeds = grainSeeds;
    }

}


