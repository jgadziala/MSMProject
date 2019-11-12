package sample;

import javafx.scene.paint.Color;

public class Cell {
    private boolean alive;
    private int grainType;
    private Color typeColor;

    public Color getTypeColor() {
        return typeColor;
    }

    public void setTypeColor(Color typeColor) {
        this.typeColor = typeColor;
    }

    public Cell(boolean alive, int grainType) {
        this.alive = alive;
        this.grainType = grainType;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getGrainType() {
        return grainType;
    }

    public void setGrainType(int grainType) {
        this.grainType = grainType;
    }
}
