package minesweeper;

import java.util.Random;

public class Board {
    private final int BoardSize;
    private static boolean[][] fieldMines;
    private static CellState[][] field;
    private static short[][] fieldNumbers;

    public Board(int boardSize) {
        BoardSize = boardSize;

        // Initialize arrays
        fieldMines = new boolean[BoardSize][];
        field = new CellState[BoardSize][];
        fieldNumbers = new short[BoardSize][];
        for (int i = 0; i < BoardSize; i++) {
            fieldMines[i] = new boolean[BoardSize];
            field[i] = new CellState[BoardSize];
            fieldNumbers[i] = new short[BoardSize];

            for (int j = 0; j < BoardSize; j++) {
                field[i][j] = CellState.Hidden;
            }
        }
    }

    public void generateBoard(Random random, int mineCount, int invalidX, int invalidY) {
        // Generate Mines
        for (int i = 0; i < mineCount; i++) {
            int mineX;
            int mineY;

            do {
                mineX = random.nextInt(BoardSize);
                mineY = random.nextInt(BoardSize);
            } while (fieldMines[mineX][mineY] || (mineX == invalidX && mineY == invalidY));

            fieldMines[mineX][mineY] = true;
        }

        // Generate Numbers
        for (int x = 0; x < BoardSize; x++) {
            for (int y = 0; y < BoardSize; y++) {
                if (fieldMines[x][y]) {
                    continue;
                }

                for (int deltaX = -1; deltaX <= 1; deltaX++) {
                    for (int deltaY = -1; deltaY <= 1; deltaY++) {
                        if (deltaX == 0 && deltaY == 0) continue;

                        int posX = x + deltaX;
                        int posY = y + deltaY;

                        if (posX < 0 || posX >= BoardSize || posY < 0 || posY >= BoardSize)
                            continue;

                        if (fieldMines[posX][posY])
                            fieldNumbers[x][y]++;
                    }
                }
            }
        }
    }

    public boolean isMine(int x, int y) {
        return fieldMines[x][y];
    }

    public boolean isGameWon() {
        // If marked all mines
        boolean allMinesMarked = true;
        for (int x = 0; x < BoardSize; x++) {
            for (int y = 0; y < BoardSize; y++) {
                if ((fieldMines[x][y] && field[x][y] != CellState.Marked) ||
                        (!fieldMines[x][y] && field[x][y] == CellState.Marked)) {
                    allMinesMarked = false;
                    break;
                }
            }
        }

        // If explored all safe cells
        boolean allSafeCellsExplored = true;
        for (int x = 0; x < BoardSize; x++) {
            for (int y = 0; y < BoardSize; y++) {
                if (!fieldMines[x][y] && field[x][y] != CellState.Explored) {
                    allSafeCellsExplored = false;
                    break;
                }
            }
        }

        return allMinesMarked || allSafeCellsExplored;
    }

    public void displayBoard(boolean showMines) {

        displayBoardTop();
        displayTopSeparator();

        for (int x = 0; x < BoardSize; x++) {
            System.out.print((x + 1) + " |");
            for (int y = 0; y < BoardSize; y++) {
                char element;

                if (showMines && fieldMines[x][y]) {
                    element = 'X';
                } else {
                    switch (field[x][y]) {
                        case Hidden:
                            element = '.';
                            break;
                        case Marked:
                            element = '*';
                            break;
                        case Explored:
                            if (fieldNumbers[x][y] != 0)
                                element = Character.forDigit(fieldNumbers[x][y], 10);
                            else
                                element = '/';

                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + field[x][y]);
                    }
                }

                System.out.print(element);
                if (y != 8)
                    System.out.print(' ');
            }
            System.out.println("|");
        }

        displayBottomSeparator();
    }

    public void displayDefaultBoard() {
        displayBoardTop();
        displayTopSeparator();
        for (int x = 0; x < BoardSize; x++) {
            System.out.print((x + 1) + " |");
            for (int y = 0; y < BoardSize; y++) {
                System.out.print(".");
                if (y != 8)
                    System.out.print(' ');
            }
            System.out.println("|");
        }
        displayBottomSeparator();
    }

    private void displayTopSeparator() {
        System.out.print("Y |");

        for (int i = 1; i <= BoardSize; i++) {
            System.out.print("-");
            if (i != 9)
                System.out.print('-');
        }

        System.out.println("|");
    }

    private void displayBottomSeparator() {
        System.out.print("--|");

        for (int i = 1; i <= BoardSize; i++) {
            System.out.print("-");
            if (i != 9)
                System.out.print('-');
        }

        System.out.println("|");
    }

    private void displayBoardTop() {
        System.out.print(" X|");
        for (int i = 1; i <= BoardSize; i++) {
            System.out.print(i);
            if (i != 9)
                System.out.print(' ');
        }
        System.out.println("|");
    }

    public boolean markMine(int x, int y) {
        switch (field[x][y]) {
            case Marked:
                field[x][y] = CellState.Hidden;
                return true;
            case Hidden:
                field[x][y] = CellState.Marked;
                return true;
            case Explored:
                return false;
            default:
                throw new IllegalStateException("Unexpected value: " + field[x][y]);
        }
    }

    public void exploreCell(int x, int y) {
        // Return if already explored
        if (field[x][y] == CellState.Explored) return;

        field[x][y] = CellState.Explored;

        // Cell is empty
        if (fieldNumbers[x][y] == 0) {
            for (int deltaX = -1; deltaX <= 1; deltaX++) {
                for (int deltaY = -1; deltaY <= 1; deltaY++) {
                    if (deltaX == 0 && deltaY == 0) continue;

                    int posX = x + deltaX;
                    int posY = y + deltaY;

                    if (posX < 0 || posX >= BoardSize || posY < 0 || posY >= BoardSize)
                        continue;

                    exploreCell(posX, posY);
                }
            }
        }
    }


    public enum CellState {
        Hidden, Explored, Marked
    }
}
