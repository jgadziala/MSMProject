package sample;

import javafx.scene.paint.Color;

public class Cell implements Cloneable {
    private boolean alive;
    private int grainType;
    private Color typeColor;

    public Color getTypeColor() {
        return typeColor;
    }

    public void setTypeColor(Color typeColor) {
        this.typeColor = typeColor;
    }

    public Cell(boolean alive, int grainType, Color typeColor) {
        this.alive = alive;
        this.grainType = grainType;
        this.typeColor = typeColor;
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

    @Override
    public Cell clone() {
        return new Cell(alive, grainType, typeColor);
    }

    public void changeState(int neighboursCount, int type, Color color) {

        if (!alive) {
            if (neighboursCount > 0) {
                alive = true;
                grainType = type;
                typeColor = color;
            }

        }

    }
}
