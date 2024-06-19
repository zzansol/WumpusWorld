package org.example;

public class Cell {
    int x, y;
    boolean hasWumpus = false;
    boolean hasPit = false;
    boolean hasGold = false;
    boolean hasStench = false;
    boolean hasBreeze = false;
    boolean hasBump = false; // hasBump 필드 추가

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
