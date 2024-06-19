package org.example;

public class Agent {
    private int x, y;
    private String direction; // 방향 추가 (N, E, S, W)
    private static Action action; // static Action 객체 추가
    private Experience experience; // 경험 객체 추가

    // 감지 필드 추가
    private boolean detectStench;
    private boolean detectBreeze;
    private boolean detectGlitter;
    private boolean detectBump;
    private boolean isAlive; // 생존 상태 추가
    private boolean hasGold; // 금 소지 상태 추가
    private int arrows; // 화살 개수

    public Agent() {
        this.x = 0;
        this.y = 0;
        this.direction = "E"; // 초기 방향은 동쪽
        action = new Action(); // Action 객체 생성
        this.isAlive = true; // 초기 상태는 살아 있음
        this.hasGold = false; // 초기 상태는 금을 소지하지 않음
        this.arrows = 1; // 초기 화살 개수는 1개
        this.experience = new Experience(); // 경험 객체 초기화
    }

    // 나머지 getter/setter 및 메서드는 동일
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean getDetectStench() {
        return detectStench;
    }

    public boolean getDetectBreeze() {
        return detectBreeze;
    }

    public boolean getDetectGlitter() {
        return detectGlitter;
    }

    public boolean getDetectBump() {
        return detectBump;
    }

    public void setDetectBump(boolean detectBump) {
        this.detectBump = detectBump;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean hasGold() {
        return hasGold;
    }

    public void setHasGold(boolean hasGold) {
        this.hasGold = hasGold;
    }

    public int getArrows() {
        return arrows;
    }

    public void useArrow() {
        if (arrows > 0) {
            arrows--;
        }
    }

    public Experience getExperience() {
        return experience;
    }

    public void sensor(Cell cell) {
        this.detectStench = cell.hasStench;
        this.detectBreeze = cell.hasBreeze;
        this.detectGlitter = cell.hasGold;
        this.detectBump = cell.hasBump;

        StringBuilder sensorReadings = new StringBuilder();
        sensorReadings.append("Percept: ");
        if (getDetectStench()) {
            sensorReadings.append("stench ");
            experience.checkDanger(this.x, this.y, this.detectStench, this.detectBreeze);
        }
        if (getDetectBreeze()) {
            sensorReadings.append("breeze ");
            experience.checkDanger(this.x, this.y, this.detectStench, this.detectBreeze);
        }
        if (getDetectGlitter()) {
            System.out.println("Action: GRAB");
            action.grab(this, cell);
            sensorReadings.append("glitter ");
        }
        if (getDetectBump()) sensorReadings.append("bump ");

        System.out.println(sensorReadings.toString().trim());
    }

    public void action(Cell currentCell) {
        // 방문한 셀을 추가
        experience.addVisitedCell(this.x, this.y);

        // 다음 셀에 wumpus나 pit가 있는지 확인
        String nextCellStatus = experience.checkNextCell(this.x, this.y, this.direction);
        if ("PIT".equals(nextCellStatus)) { //다음 셀에 pit가 있을시
            System.out.println("Pit detected, turning left.");
            action.turnLeft(this);
        } else if ("WUMPUS".equals(nextCellStatus)) {// 다음셀에 wumpus가 있을 시
            if (this.getArrows() > 0) { // 화살 >0
                System.out.println("Wumpus detected, shooting arrow.");
                action.shootArrow(this);
            } else { //화살 없음
                System.out.println("Wumpus detected, but no arrows left. Turning left.");
                action.turnLeft(this);
            }
        } else {
            if (this.getDetectBump()) { //bump 감지시
                System.out.println("Action: TURN_LEFT");
                action.turnLeft(this);
                currentCell.hasBump = false; // bump 초기화
                this.setDetectBump(false); // bump 초기화
            } else { //그외 상황들
                System.out.println("Action: GO_FORWARD");
                action.goForward(currentCell, this);
            }
        }

        // 금을 가지고 시작 위치(0,0)로 돌아왔을 때 climb
        if (this.hasGold && this.x == 0 && this.y == 0) {
            System.out.println("Action: CLIMB");
            action.climb();
        }
    }
}
