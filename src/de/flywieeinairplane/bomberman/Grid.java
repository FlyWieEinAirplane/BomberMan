package de.flywieeinairplane.bomberman;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by flywieeinairplane on 23.03.17.
 */
public class Grid implements Serializable {
    private Field[][] grid;
    private int sizeX;
    private int sizeY;

    public Grid(int sizeX, int sizeY) {
        this.grid = new Field[sizeY][sizeX];
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        Random random = new Random();
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                String type = (random.nextBoolean() ? "weg" : "rock");
                this.grid[i][j] = new Field(type);
//                Wände in die Mitte
                if (i % 2 == 0 && j % 2 == 0) {
                    this.grid[i][j].type = "wall";
                }
            }
        }

//        Rand um das Spielfeld
        for (int i = 0; i < sizeX; i++) {
            this.grid[0][i].type = "wall";
            this.grid[sizeY - 1][i].type = "wall";
        }
        for (int i = 0; i < sizeY; i++) {
            this.grid[i][0].type = "wall";
            this.grid[i][sizeX - 1].type = "wall";
        }
        this.grid[1][1].type = "weg";
        this.grid[1][2].type = "weg";
        this.grid[2][1].type = "weg";

        this.grid[sizeY - 2][1].type = "weg";
        this.grid[sizeY - 2][2].type = "weg";
        this.grid[sizeY - 3][1].type = "weg";

        this.grid[1][sizeX - 2].type = "weg";
        this.grid[1][sizeX - 3].type = "weg";
        this.grid[2][sizeX - 2].type = "weg";

        this.grid[sizeY - 2][sizeX - 2].type = "weg";
        this.grid[sizeY - 2][sizeX - 3].type = "weg";
        this.grid[sizeY - 3][sizeX - 2].type = "weg";

    }

    public Field[][] getGrid() {
        return this.grid;
    }

    public Field[] getRow(int row) {
        return this.grid[row];
    }

    public Field getField(int x, int y) {
        return this.grid[y][x];
    }

    public Field getField(float x, float y) {

        double restX = x % 1.0;
        double restY = y % 1.0;

        return this.grid[(int) y][(int) x];


    }


    public int getSizeY() {
        return sizeY;
    }

    public int getSizeX() {
        return sizeX;
    }
}
