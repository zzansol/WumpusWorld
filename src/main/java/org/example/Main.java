package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        World world = new World();
        Agent agent = new Agent();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            world.printGrid(agent);
            Cell currentCell = World.getCell(agent.getX(), agent.getY());
            agent.sensor(currentCell);

            System.out.println("Press Enter to move the agent forward...");
            scanner.nextLine(); // 사용자로부터 엔터 입력 받기

            agent.action(currentCell);

            // 위험한 셀의 좌표 출력
            agent.getExperience().printDangerCells();
        }
    }
}
