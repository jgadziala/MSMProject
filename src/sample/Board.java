package sample;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Board {
    Random random = new Random();
    private int width;
    private boolean period;
    private int probability;
    private String neighbourhoodType;
    public boolean isPeriod() {
        return period;
    }

    public void setPeriod(boolean period) {
        this.period = period;
    }
    public void setProbability(int probability) {
        this.probability = probability;
    }
    private int height;
    private Cell[][] cells;
//    private String neighbourhoodSelectionType;
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
                cells[i][j] = new Cell(false,0,null);
            }
        }
    }

//    public void addGrainSeeds(int grainSeeds){
//        int x = random.nextInt(width);
//        int y = random.nextInt(height);
//        for (int i=0; i<grainSeeds;i++) {
//            cells[x][y] = new Cell(true, i);
//        }
//    }


    // wykonanie aktualnej tury
    public boolean nextCycle() {
        int[] cellInfo = null;
        Cell[][] newBoard = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newBoard[i][j] = cells[i][j].clone();
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(neighbourhoodType.equals("von Neumann"))
                    cellInfo = getGrainsGrowthType(i, j);
                else if(neighbourhoodType.equals("Moore"))
                    cellInfo = getMooreGrainsGrowthType(i, j);
                newBoard[i][j].changeState(cellInfo[0],cellInfo[1], cells[i][j].getTypeColor());
            }
        }

        cells = newBoard;

        return isFinished();
    }


    public boolean isFinished() {
        boolean value = true;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(!cells[i][j].isAlive()){
                    value = false;
                }
            }
        }
        return value;
    }

//public int[] getMooreGrowthType(int i, int j){
//        int [] info = new int[2];
//        Map<Integer,Integer> neighbourhoods = new HashMap<>();
//}

    public int[] getGrainsGrowthType(int i, int j) {
        int [] info = new int[2];
        Map<Integer,Integer> neighbours = new HashMap<>();

        int type=0;
        if (!period) {
            int left = Math.max(i - 1, 0);
            int up = Math.max(j - 1, 0);
            int right = Math.min(i + 1, width - 1);
            int down = Math.min(j + 1, height - 1);
            int[] x= {left,i,right,i};
            int[] y= {j, up, j, down};
            for (int k = 0; k < 4; k++) {

                type = cells[x[k]][y[k]].getGrainId();
                getNeighboursInfo(neighbours,type);
            }
        } else {
            int left = i - 1;
            int up = j - 1;
            int right = i + 1;
            int down = j + 1;
            int[] x= {left,i,right,i};
            int[] y= {j, up, j, down};
            int tmpX, tmpY;
            for (int k = 0; k < 4; k++) {
                tmpX = x[k];
                tmpY = y[k];
                if (x[k] == -1) tmpX = width - 1;
                if (x[k] == width) tmpX = 0;
                if (y[k] == -1) tmpY = height - 1;
                if (y[k] == height) tmpY = 0;

                type = cells[tmpX][tmpY].getGrainId();
                getNeighboursInfo(neighbours,type);
            }
        }
        //max
        if(neighbours.isEmpty()) {
            info[1] = 0;
            info[0] = 0;
        }else {
            info[1] = neighbours.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            info[0] = neighbours.get(info[1]);
        }

        return info;
    }


    public void getNeighboursInfo(Map<Integer, Integer> neighbours, int type){
        if (neighbours.containsKey(type)) {
            int count = neighbours.get(type);
            neighbours.put(type, count + 1);
        } else if (type != 0 && type != -1) {
            neighbours.put(type, 1);
        }
    }

    public boolean isCellOnBorder(int i, int j){
        boolean isOnBorder = false;
        int type=0;
        int currentType = cells[i][j].getGrainId();
        if (!period) {

            int left = Math.max(i - 1, 0);
            int up = Math.max(j - 1, 0);
            int right = Math.min(i + 1, width - 1);
            int down = Math.min(j + 1, height - 1);
            int[] x= {left,i,right,i};
            int[] y= {j, up, j, down};
            for (int k = 0; k < 4; k++) {

                type = cells[x[k]][y[k]].getGrainId();
                if(type != currentType)
                    isOnBorder = true;
            }
        } else {
            //periodycznie
            int left = i - 1;
            int up = j - 1;
            int right = i + 1;
            int down = j + 1;
            int[] x= {left,i,right,i};
            int[] y= {j, up, j, down};
            int tmpX, tmpY;
            for (int k = 0; k < 4; k++) {
                tmpX = x[k];
                tmpY = y[k];
                if (x[k] == -1) tmpX = width - 1;
                if (x[k] == width) tmpX = 0;
                if (y[k] == -1) tmpY = height - 1;
                if (y[k] == height) tmpY = 0;

                type = cells[tmpX][tmpY].getGrainId();
                if(type != currentType)
                    isOnBorder = true;
            }
        }

        return isOnBorder;
    }

    public int[] getMooreGrainsGrowthType(int i, int j) {
        int [] info = new int[2];
        int [] maxInfo = new int[2];
        Map<Integer,Integer> neighbours = new HashMap<>();

        int type=0;

        int startX = i - 1;
        int startY = j - 1;
        int endX = i + 1;
        int endY = j + 1;


        for(int k =0; k<4; k++){
            int counter = 0;
            int tmpX, tmpY;
            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    if(k == 0 || (k == 1 && counter % 2 == 1) || (k == 2 && counter % 2 == 0 && counter!=4)) {
                        tmpX = x;
                        tmpY = y;
                        if (x == -1) tmpX = width - 1;
                        if (x == width) tmpX = 0;
                        if (y == -1) tmpY = height - 1;
                        if (y == height) tmpY = 0;


                        type = cells[tmpX][tmpY].getGrainId();
                        getMooreNeighboursInfo(neighbours, type);
                    }
                    counter++;
                }
            }


            //max
            if (neighbours.isEmpty()) {
                info[1] = 0;
                info[0] = 0;
            } else {
                info[1] = neighbours.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
                info[0] = neighbours.get(info[1]);
                if(k == 0){
                    maxInfo[0] = info[0];
                    maxInfo[1] = info[1];
                }
            }

            if ((k==0 && info[0] >= 5) || (k==1 && info[0] >=3) || (k==2 && info[0] >=3)) {
                return info;
            }else if(k==3){
                if(random.nextInt(101) < probability){
                    return maxInfo;
                }else{
                    info[0] =0;
                    info[1] = 0;
                    return info;
                }

            }
            neighbours.clear();

        }

        return info;
    }

    public void getMooreNeighboursInfo(Map<Integer, Integer> neighbours, int type){
        if (neighbours.containsKey(type)) {
            int count = neighbours.get(type);
            neighbours.put(type, count + 1);
        } else if (type != 0 && type != -1) {
            neighbours.put(type, 1);
        }
    }

    public Color getCellColor(int x, int y) {
            return cells[x][y].getTypeColor();
    }

    public void setCellState(int i, int j, boolean alive){
        cells[i][j].setAlive(alive);
    }

    public void setCellGrainType(int x, int y, int grainType) {
            cells[x][y].setGrainId(grainType);
    }

    public boolean getCellState(int x, int y) {
            return cells[x][y].isAlive();
    }

    public int getCellGrainType(int x, int y) {
            return cells[x][y].getGrainId();
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
//
//    public String getNeighbourhoodType() {
//        return neighbourhoodSelectionType;
//    }
//
//    public void setNeighbourhoodSelectionType(String neighbourhoodSelectionType) {
//        this.neighbourhoodSelectionType = neighbourhoodSelectionType;
//    }

    public int getGrainSeeds() {
        return grainSeeds;
    }

    public void setGrainSeeds(int grainSeeds) {
        this.grainSeeds = grainSeeds;
    }

    public void setNeighbourhoodType(String neighbourhoodType) {
        this.neighbourhoodType = neighbourhoodType;
    }

}


