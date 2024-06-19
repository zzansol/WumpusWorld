package org.example;

import java.util.Random;
import java.util.Set;

public class World {

    private static final int SIZE = 4;
    private static Cell[][] grid;
    private static int goldX, goldY;

    public World() {
        grid = new Cell[SIZE][SIZE];
        initializeGrid();
        placeWumpus();
        placePits();
        placeGold();
    }

    private void initializeGrid() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    private void placeWumpus() {
        placeRandomly('W');
    }

    private void addStench(int x, int y) {
        if (x > 0) grid[x - 1][y].hasStench = true;
        if (x < SIZE - 1) grid[x + 1][y].hasStench = true;
        if (y > 0) grid[x][y - 1].hasStench = true;
        if (y < SIZE - 1) grid[x][y + 1].hasStench = true;
    }

    public void removeStenchAround(int x, int y) {
        if (x > 0) grid[x - 1][y].hasStench = false;
        if (x < SIZE - 1) grid[x + 1][y].hasStench = false;
        if (y > 0) grid[x][y - 1].hasStench = false;
        if (y < SIZE - 1) grid[x][y + 1].hasStench = false;
    }

    private void placePits() {
        Random rand = new Random();
        int numPits = rand.nextInt(2) + 1; // 무작위로 1 또는 2개 피트 배치
        for (int i = 0; i < numPits; i++) {
            placeRandomly('P');
        }
    }

    private void addBreeze(int x, int y) {
        if (x > 0) grid[x - 1][y].hasBreeze = true;
        if (x < SIZE - 1) grid[x + 1][y].hasBreeze = true;
        if (y > 0) grid[x][y - 1].hasBreeze = true;
        if (y < SIZE - 1) grid[x][y + 1].hasBreeze = true;
    }

    private void placeGold() {
        placeRandomly('G');
    }

    private void placeRandomly(char item) {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(SIZE);
            y = rand.nextInt(SIZE);
        } while ((x == 0 && y == 0) || grid[x][y].hasWumpus || grid[x][y].hasPit || grid[x][y].hasGold);

        switch (item) {
            case 'W':
                grid[x][y].hasWumpus = true;
                addStench(x, y);
                break;
            case 'P':
                grid[x][y].hasPit = true;
                addBreeze(x, y);
                break;
            case 'G':
                grid[x][y].hasGold = true;
                goldX = x;
                goldY = y;
                break;
        }
    }

    public static void resetGold() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j].hasGold = false;
            }
        }
        grid[goldX][goldY].hasGold = true;
    }

    public void printGrid(Agent agent) {
        Experience experience = agent.getExperience();
        Set<Cell> dangerCells = experience.getDangerCells();

        for (int i = SIZE - 1; i >= 0; i--) {
            for (int j = 0; j < SIZE; j++) {
                boolean isDangerCell = false;
                Cell dangerCell = null;
                for (Cell cell : dangerCells) {
                    if (cell.x == i && cell.y == j) {
                        isDangerCell = true;
                        dangerCell = cell;
                        break;
                    }
                }

                if (i == agent.getX() && j == agent.getY()) {
                    System.out.print("[A]");
                } else if (isDangerCell) {
                    if (dangerCell.hasWumpus) {
                        System.out.print("[W]");
                    } else if (dangerCell.hasPit) {
                        System.out.print("[P]");
                    }
                } else {
                    System.out.print("[ ]");
                }
            }
            System.out.println();
        }
    }

    public static int getSize() {
        return SIZE;
    }

    public static Cell getCell(int x, int y) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            return grid[x][y];
        }
        return null;
    }
}
