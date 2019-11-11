package sample;

public class Cell {
    private boolean alive;
    private int type;

    public Cell(boolean alive, int type) {
        this.alive = alive;
        this.type = type;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
