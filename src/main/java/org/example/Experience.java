package org.example;

import java.util.HashSet;
import java.util.Set;

public class Experience {
    private Set<int[]> visitedCells; // 방문한 셀들의 좌표
    private Set<Cell> dangerCells; // 위험한 셀들
    private Set<int[]> expectedWumpusCells; // 예상되는 Wumpus 셀들
    private Set<int[]> expectedPitCells; // 예상되는 Pit 셀들

    private static final int GRID_SIZE = 4; // 격자의 크기

    public Experience() {
        this.visitedCells = new HashSet<>(); // 방문한 셀 집합 초기화
        this.dangerCells = new HashSet<>(); // 위험한 셀 집합 초기화
        this.expectedWumpusCells = new HashSet<>(); // 예상되는 Wumpus 셀 집합 초기화
        this.expectedPitCells = new HashSet<>(); // 예상되는 Pit 셀 집합 초기화
    }

    public Set<int[]> getVisitedCells() {
        return visitedCells;
    }

    public Set<Cell> getDangerCells() {
        return dangerCells;
    }

    public void addVisitedCell(int x, int y) {
        if (x == 0 && y == 0) {
            return;
        }
        int[] coordinates = new int[]{x, y};
        for (int[] visited : visitedCells) {
            if (visited[0] == x && visited[1] == y) {
                return;
            }
        }
        visitedCells.add(coordinates);
    }

    public boolean isVisited(int x, int y) {
        for (int[] coordinates : visitedCells) {
            if (coordinates[0] == x && coordinates[1] == y) {
                return true;
            }
        }
        return false;
    }

    public void addDangerCell(int x, int y, boolean hasWumpus, boolean hasPit) {
        for (Cell cell : dangerCells) {
            if (cell.x == x && cell.y == y) {
                return; // 이미 존재하는 위험한 셀이라면 추가하지 않음
            }
        }
        Cell dangerCell = new Cell(x, y);
        dangerCell.hasWumpus = hasWumpus;
        dangerCell.hasPit = hasPit;
        dangerCells.add(dangerCell);
    }

    public void removeDangerCell(int x, int y, boolean hasWumpus, boolean hasPit) {
        dangerCells.removeIf(cell -> cell.x == x && cell.y == y && cell.hasWumpus == hasWumpus && cell.hasPit == hasPit);
    }

    public void updateExpectedDangerCells(int x, int y, boolean detectStench, boolean detectBreeze) {
        if (detectStench || detectBreeze) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int[] direction : directions) {
                int newX = x + direction[0];
                int newY = y + direction[1];
                if (isWithinGrid(newX, newY) && !isVisited(newX, newY)) {
                    int[] coordinates = new int[]{newX, newY};
                    if (detectStench) {
                        if (!isExpectedWumpus(newX, newY)) {
                            expectedWumpusCells.add(coordinates);
                        } else {
                            addDangerCell(newX, newY, true, false);
                        }
                    }
                    if (detectBreeze) {
                        if (!isExpectedPit(newX, newY)) {
                            expectedPitCells.add(coordinates);
                        } else {
                            addDangerCell(newX, newY, false, true);
                        }
                    }
                }
            }
        }
    }

    public boolean checkDanger(int x, int y, boolean detectStench, boolean detectBreeze) {
        if (detectStench || detectBreeze) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int[] direction : directions) {
                int newX = x + direction[0];
                int newY = y + direction[1];
                if (isWithinGrid(newX, newY)) {
                    for (Cell cell : dangerCells) {
                        if (cell.x == newX && cell.y == newY) {
                            return true; // 주변에 이미 알고 있는 위험 요소가 있음
                        }
                    }
                }
            }
            updateExpectedDangerCells(x, y, detectStench, detectBreeze);
        }
        return false; // 주변에 알고 있는 위험 요소가 없음
    }

    private boolean isWithinGrid(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }

    private boolean isExpectedWumpus(int x, int y) {
        for (int[] coordinates : expectedWumpusCells) {
            if (coordinates[0] == x && coordinates[1] == y) {
                return true;
            }
        }
        return false;
    }

    private boolean isExpectedPit(int x, int y) {
        for (int[] coordinates : expectedPitCells) {
            if (coordinates[0] == x && coordinates[1] == y) {
                return true;
            }
        }
        return false;
    }

    public void printDangerCells() {
        System.out.print("Danger cells: ");
        for (Cell cell : dangerCells) {
            System.out.print("[" + cell.x + ", " + cell.y + "] ");
        }
        System.out.println(); // 줄바꿈
    }

    public String checkNextCell(int x, int y, String direction) {
        int nextX = x;
        int nextY = y;

        switch (direction) {
            case "N":
                nextX += 1;
                break;
            case "E":
                nextY += 1;
                break;
            case "S":
                nextX -= 1;
                break;
            case "W":
                nextY -= 1;
                break;
        }

        for (Cell cell : dangerCells) {
            if (cell.x == nextX && cell.y == nextY) {
                if (cell.hasWumpus) {
                    return "WUMPUS";
                } else if (cell.hasPit) {
                    return "PIT";
                }
            }
        }

        return "SAFE";
    }


}
