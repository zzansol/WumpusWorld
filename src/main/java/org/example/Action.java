package org.example;

public class Action {
    public enum ActionType {
        GO_FORWARD,
        TURN_LEFT,
        TURN_RIGHT,
        SHOOT_ARROW,
        CLIMB
    }

    public void goForward(Cell currentCell, Agent agent) {
        int currentX = agent.getX();
        int currentY = agent.getY();
        boolean bumpDetected = false;
        int nextX = currentX, nextY = currentY;

        switch (agent.getDirection()) {
            case "N":
                nextX = currentX + 1;
                break;
            case "E":
                nextY = currentY + 1;
                break;
            case "S":
                nextX = currentX - 1;
                break;
            case "W":
                nextY = currentY - 1;
                break;
        }

        if (nextX >= 0 && nextX < World.getSize() && nextY >= 0 && nextY < World.getSize()) {
            agent.setX(nextX);
            agent.setY(nextY);
        } else {
            currentCell.hasBump = true;
            bumpDetected = true;
        }

        // 새로운 위치의 셀 가져오기
        Cell nextCell = World.getCell(agent.getX(), agent.getY());

        // 이동 후 생존 상태 확인
        if (nextCell.hasWumpus || nextCell.hasPit) {
            agent.setAlive(false);
            System.out.println("Agent died! Returning to start...");
            agent.getExperience().addDangerCell(nextCell.x, nextCell.y, nextCell.hasWumpus, nextCell.hasPit); // 위험한 셀 추가
            agent.setX(0);
            agent.setY(0);
            agent.setDirection("E"); // 초기 방향으로 설정
            agent.setAlive(true);
            agent.setHasGold(false); // 에이전트가 금을 잃음
            World.resetGold(); // 금을 원래 자리로 되돌림
        }

        if (bumpDetected) {
            agent.setDetectBump(true); // bump 감지
        }
    }

    public void turnLeft(Agent agent) {
        switch (agent.getDirection()) {
            case "N":
                agent.setDirection("W");
                break;
            case "E":
                agent.setDirection("N");
                break;
            case "S":
                agent.setDirection("E");
                break;
            case "W":
                agent.setDirection("S");
                break;
        }
    }

    public void turnRight(Agent agent) {
        switch (agent.getDirection()) {
            case "N":
                agent.setDirection("E");
                break;
            case "E":
                agent.setDirection("S");
                break;
            case "S":
                agent.setDirection("W");
                break;
            case "W":
                agent.setDirection("N");
                break;
        }
    }

    public void grab(Agent agent, Cell cell) {
        if (cell.hasGold) {
            agent.setHasGold(true);
            cell.hasGold = false;
            System.out.println("Agent grabbed the gold!");
        }
    }

    public void shootArrow(Agent agent) {
        int x = agent.getX();
        int y = agent.getY();
        String direction = agent.getDirection();
        boolean wumpusKilled = false;

        switch (direction) {
            case "N":
                for (int i = x + 1; i < World.getSize(); i++) {
                    Cell cell = World.getCell(i, y);
                    if (cell.hasWumpus) {
                        cell.hasWumpus = false;
                        removeStenchAround(i, y);
                        agent.getExperience().removeDangerCell(i, y, true, false);
                        wumpusKilled = true;
                        break;
                    }
                }
                break;
            case "E":
                for (int i = y + 1; i < World.getSize(); i++) {
                    Cell cell = World.getCell(x, i);
                    if (cell.hasWumpus) {
                        cell.hasWumpus = false;
                        removeStenchAround(x, i);
                        agent.getExperience().removeDangerCell(x, i, true, false);
                        wumpusKilled = true;
                        break;
                    }
                }
                break;
            case "S":
                for (int i = x - 1; i >= 0; i--) {
                    Cell cell = World.getCell(i, y);
                    if (cell.hasWumpus) {
                        cell.hasWumpus = false;
                        removeStenchAround(i, y);
                        agent.getExperience().removeDangerCell(i, y, true, false);
                        wumpusKilled = true;
                        break;
                    }
                }
                break;
            case "W":
                for (int i = y - 1; i >= 0; i--) {
                    Cell cell = World.getCell(x, i);
                    if (cell.hasWumpus) {
                        cell.hasWumpus = false;
                        removeStenchAround(x, i);
                        agent.getExperience().removeDangerCell(x, i, true, false);
                        wumpusKilled = true;
                        break;
                    }
                }
                break;
        }

        if (wumpusKilled) {
            System.out.println("Wumpus killed!");
        } else {
            System.out.println("Missed! No Wumpus in the direction.");
        }

        agent.useArrow();
    }

    public void climb() {
        System.out.println("Agent climbed out of the cave with the gold!");
        System.exit(0); // 프로그램 종료
    }

    private void removeStenchAround(int x, int y) {
        if (x > 0) World.getCell(x - 1, y).hasStench = false;
        if (x < World.getSize() - 1) World.getCell(x + 1, y).hasStench = false;
        if (y > 0) World.getCell(x, y - 1).hasStench = false;
        if (y < World.getSize() - 1) World.getCell(x, y + 1).hasStench = false;
    }
}
