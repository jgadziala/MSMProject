package sample;

import javafx.scene.paint.Color;

public class Cell implements Cloneable {
    private boolean alive;
    private int grainId;
    private Color typeColor;

    public Color getTypeColor() {
        return typeColor;
    }

    public void setTypeColor(Color typeColor) {
        this.typeColor = typeColor;
    }

    public Cell(boolean alive, int grainId, Color typeColor) {
        this.alive = alive;
        this.grainId = grainId;
        this.typeColor = typeColor;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getGrainId() {
        return grainId;
    }

    public void setGrainId(int grainId) {
        this.grainId = grainId;
    }

    @Override
    public Cell clone() {
        return new Cell(alive, grainId, typeColor);
    }

    public void changeState(int neighboursCount, int type, Color color) {

        if (!alive) {
            if (neighboursCount > 0) {
                alive = true;
                grainId = type;
                typeColor = color;
            }

        }

    }
}
