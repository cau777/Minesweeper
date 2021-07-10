package minesweeper;

import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final int BoardSize = 9;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        System.out.println("Welcome to Minesweeper");
        System.out.println("Type 'help' for help with the syntax");
        System.out.println("Type 'rules' for the rules of the game");

        int mineCount = 0;

        while (true) {
            System.out.print("How many mines do you want on the field? ");
            String line = scanner.nextLine().strip().toLowerCase(Locale.ROOT);

            if ("help".equals(line)) {
                showHelp();
                continue;
            } else if ("rules".equals(line)) {
                showRules();
                continue;
            }

            try {
                int userMines = Integer.parseInt(line);
                mineCount = (int) Math.min(userMines, BoardSize * BoardSize * 0.8);
                break;
            } catch (NumberFormatException err) {
                System.out.println("Please enter a number or the help commands");
            }
        }

        Board board = new Board(BoardSize);
        board.displayDefaultBoard();

        Random random = new Random();

        // First cell is never a mine
        boolean firstTime = true;

        while (true) {
            Object[] userCommands = readUserInput(scanner);
            int userX = (int) userCommands[0];
            int userY = (int) userCommands[1];
            String userAction = (String) userCommands[2];

            if ("free".equals(userAction)) {
                if (firstTime) {
                    board.generateBoard(random, mineCount, userX, userY);
                    firstTime = false;
                }

                if (board.isMine(userX, userY)) {
                    board.displayBoard(true);
                    System.out.println("You stepped on a mine and failed!");
                    break;
                }

                board.exploreCell(userX, userY);

            } else {
                boolean mineMarked = board.markMine(userX, userY);
                if (!mineMarked) {
                    System.out.printf("Couldn't mark cell %d %d as mine%n", userX + 1, userY + 1);
                }
            }

            if (!firstTime && board.isGameWon()) {
                board.displayBoard(true);
                System.out.println("Congratulations! You found all mines!");
                break;
            } else {
                board.displayBoard(false);
            }
        }
    }

    private static Object[] readUserInput(Scanner scanner) {
        int userX;
        int userY;
        String userAction;

        while (true) {
            System.out.print("Set/unset mine marks or claim a cell as free (xCoord yCoord free/mine): ");
            String line = scanner.nextLine().strip();

            if ("help".equals(line)) {
                showHelp();
                continue;
            } else if ("rules".equals(line)) {
                showRules();
                continue;
            }

            String[] inputParts = line.split(" ");

            try {
                userY = Integer.parseInt(inputParts[0]) - 1;
                userX = Integer.parseInt(inputParts[1]) - 1;
                userAction = inputParts[2].strip().toLowerCase();

                if (userX < 0 || userX >= BoardSize || userY < 0 || userY >= BoardSize) {
                    System.out.println("Invalid cell!");
                } else if (!"free".equals(userAction) && !"mine".equals(userAction)) {
                    System.out.println("Invalid action!");
                } else {
                    break;
                }
            } catch (NumberFormatException | IndexOutOfBoundsException err) {
                System.out.println("Please provide 2 numbers or the help commands");
            }
        }

        return new Object[]{userX, userY, userAction};
    }

    private static void showHelp() {
        System.out.println("To play this game, type the x coordinate (the board column), the y coordinate (the board row) and the action you want to perform");
        System.out.println("'free' explores the cell and 'mine' marks the cell as a mine");
        System.out.println("Examples: ");
        System.out.println("8 8 free");
        System.out.println("1 1 mine");
        System.out.println();
    }

    private static void showRules() {
        System.out.println("In the board, there are mines randomly placed.");
        System.out.println("The objective of the game is to explore all safe cells or mark all cells with mines.");
        System.out.println("The numbers indicate how many mines there are in the vicinity.");
        System.out.println("If the player explores a cell containing a mine, the game ends.");
        System.out.println("The first cell explored will never be a mine.");
        System.out.println();
    }
}
