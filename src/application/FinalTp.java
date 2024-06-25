package application;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FinalTp extends Application {

    @SuppressWarnings("unused")
	private Pane root;
	@SuppressWarnings("unused")
	private double scale;
	@SuppressWarnings("unused")
	private Text avgTurnaroundTimeText;

	@Override
    public void start(Stage primaryStage) {
        Scanner sc = new Scanner(System.in);
        char restart;

        do {
            System.out.println("----------------------------------------");
            System.out.println("     Shortest Remaining Time First      ");
            System.out.println("----------------------------------------");

            int numProcesses = 0;
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.print("Enter the number of processes: ");
                    numProcesses = sc.nextInt();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("\nPlease enter a numerical number only.");
                    sc.nextLine();
                }
            }

            int[] arrivalTimes = new int[numProcesses];
            int[] burstTimes = new int[numProcesses];
            int[] remainingTimes = new int[numProcesses];
            int[] completionTimes = new int[numProcesses];
            int[] waitingTimes = new int[numProcesses];
            int[] turnaroundTimes = new int[numProcesses];

            for (int i = 0; i < numProcesses; i++) {
                int arrivalTime;
                int burstTime;
                boolean validArrivalInput = false;
                boolean validBurstInput = false;

                while (!validArrivalInput) {
                    try {
                        System.out.print("Enter Arrival Time for Process " + (i + 1) + ": ");
                        arrivalTime = sc.nextInt();
                        validArrivalInput = true;
                        arrivalTimes[i] = arrivalTime;
                    } catch (InputMismatchException e) {
                        System.out.println("\nPlease enter a numerical number only for Arrival Time.");
                        sc.nextLine();
                    }
                }

                while (!validBurstInput) {
                    try {
                        System.out.print("Enter Burst Time for Process " + (i + 1) + ": ");
                        burstTime = sc.nextInt();
                        validBurstInput = true;
                        burstTimes[i] = burstTime;
                        remainingTimes[i] = burstTime;
                    } catch (InputMismatchException e) {
                        System.out.println("\nPlease enter a numerical number only for Burst Time.");
                        sc.nextLine();
                    }
                }
            }

            int currentTime = 0;
            int completedProcesses = 0;

            while (completedProcesses < numProcesses) {
                int shortestTime = Integer.MAX_VALUE;
                int shortestIndex = -1;

                for (int i = 0; i < numProcesses; i++) {
                    if (arrivalTimes[i] <= currentTime && remainingTimes[i] < shortestTime && remainingTimes[i] > 0) {
                        shortestTime = remainingTimes[i];
                        shortestIndex = i;
                    }
                }

                if (shortestIndex == -1) {
                    currentTime++;
                } else {
                    remainingTimes[shortestIndex]--;
                    currentTime++;

                    if (remainingTimes[shortestIndex] == 0) {
                        completedProcesses++;
                        completionTimes[shortestIndex] = currentTime;
                        turnaroundTimes[shortestIndex] = completionTimes[shortestIndex] - arrivalTimes[shortestIndex];
                        waitingTimes[shortestIndex] = turnaroundTimes[shortestIndex] - burstTimes[shortestIndex];
                    }
                }
            }

            System.out.println("\nProcess Table:");
            System.out.println("|-----|--------------|------------|----------------|-----------------|--------------|");
            System.out.println("| PID | Arrival Time | Burst Time | Completed Time | Turnaround Time | Waiting Time |");
            System.out.println("|-----|--------------|------------|----------------|-----------------|--------------|");

            for (int i = 0; i < numProcesses; i++) {
                System.out.printf("| %-3d | %-12d | %-10d | %-14d | %-15d | %-12d |%n",
                        i + 1, arrivalTimes[i], burstTimes[i], completionTimes[i], turnaroundTimes[i], waitingTimes[i]);
            }

            System.out.println("|-----|--------------|------------|----------------|-----------------|--------------|");
            System.out.println();

            float totalTime = currentTime;
            float averageWaitingTime = 0;
            float averageTurnaroundTime = 0;

            for (int i = 0; i < numProcesses; i++) {
                averageWaitingTime += waitingTimes[i];
                averageTurnaroundTime += turnaroundTimes[i];
            }

            averageWaitingTime /= numProcesses;
            averageTurnaroundTime /= numProcesses;

            System.out.printf("Total Time: %.0f%n%n", totalTime);
            System.out.printf("Average Waiting Time: %.2fms%n", averageWaitingTime);
            System.out.printf("Average Turnaround Time: %.2fms%n%n", averageTurnaroundTime);

            launchGanttChartWindow(primaryStage, completionTimes, (int) totalTime, averageWaitingTime, averageTurnaroundTime);

            System.out.print("Process completed. Do you want to restart? (Y/N): ");
            restart = Character.toLowerCase(sc.next().charAt(0));

            while (restart != 'y' && restart != 'n') {
                System.out.println("Please enter a valid answer (Y/N): ");
                restart = Character.toLowerCase(sc.next().charAt(0));
            }
        } while (restart == 'y');

        System.out.println("\nProgram terminated. Showing the Gantt Chart Table in 5 seconds...\n");
        System.out.println("Created by Belen, Joemer M.");
        System.out.println("From: BSCpE-2A, a Task Performance in Operating System (Finals)");
        System.out.println("Powered by JavaFX");

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sc.close();
    }
    

    private void launchGanttChartWindow(Stage primaryStage, int[] completionTimes, int totalTime,
                                        float averageWaitingTime, float averageTurnaroundTime) {
        root = new Pane();
        scale = 50.0;

        Text title = new Text("Gantt Chart Table");
        title.setFont(new Font("Times New Roman", 20));
        title.setX(30);
        title.setY(30);

        Text totalTimeText = new Text("Total Time: " + totalTime);
        totalTimeText.setFont(new Font("Times New Roman", 12));
        totalTimeText.setX(30);
        totalTimeText.setY(120);

        Text avgWaitingTimeText = new Text("Average Waiting Time: " + String.format("%.2fms", averageWaitingTime));
        avgWaitingTimeText.setFont(new Font("Times New Roman", 12));
        avgWaitingTimeText.setX(30);
        avgWaitingTimeText.setY(140);

        avgTurnaroundTimeText = new Text(
                "Average Turnaround Time: " + String.format("%.2fms", averageTurnaroundTime)); 
    }    
    }