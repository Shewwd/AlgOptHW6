import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MazeGame {
    private final char WALL = '#';
    private final char PATH = '.';
    private final char USER = 'X';
    private final char HINT_PATH = '+';
    private char[][] maze;
    private char[][] hintMaze; // Memoized maze for hints
    private int userX, userY;
    private static Scanner scanner;

    public MazeGame() {
        scanner = new Scanner(System.in);
    }

    public void generateMaze(int rows, int cols) {
        maze = new char[rows][cols];
        hintMaze = new char[rows][cols];
        initializeMaze(rows, cols);
        generateMazeUsingDFS(1, 1, rows - 2, cols - 2);
    }

    private void initializeMaze(int rows, int cols) {
        // Initialize the maze with walls and the user's starting position.
        for (char[] row : maze) {
            Arrays.fill(row, WALL);
        }
        for (char[] row : hintMaze) {
            Arrays.fill(row, WALL);
        }

        maze[1][0] = USER; // Starting position
        userX = 1;
        userY = 0;

        maze[rows - 2][cols - 1] = PATH; // Destination
    }

    private void generateMazeUsingDFS(int x, int y, int destX, int destY) {
        // Generate the maze using a depth-first search (DFS) approach.
        maze[x][y] = PATH;
        hintMaze[x][y] = PATH;

        int[] dx = {2, -2, 0, 0};
        int[] dy = {0, 0, 2, -2};

        ArrayList<Integer> directions = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(directions);

        for (int dir : directions) {
            int newX = x + dx[dir];
            int newY = y + dy[dir];

            if (newX >= 1 && newX <= destX && newY >= 1 && newY <= destY && maze[newX][newY] == WALL) {
                maze[(x + newX) / 2][(y + newY) / 2] = PATH;
                hintMaze[(x + newX) / 2][(y + newY) / 2] = PATH;

                generateMazeUsingDFS(newX, newY, destX, destY);
            }
        }
    }

    private int playGame() {
        printMazeGrid();
        System.out.print("Press any key then enter to start: ");
        String temp = scanner.next();
        System.out.println();

        printMazeWithBlankAroundUser();
        while (true) {
            System.out.print("Enter your move (W/A/S/D to move, H for hint, C to close): ");
            char input = scanner.next().toUpperCase().charAt(0);

            if (input == 'W' && userX > 0 && maze[userX - 1][userY] == PATH) {
                moveUser(-1, 0);
            } else if (input == 'S' && userX < maze.length - 1 && maze[userX + 1][userY] == PATH) {
                moveUser(1, 0);
            } else if (input == 'A' && userY > 0 && maze[userX][userY - 1] == PATH) {
                moveUser(0, -1);
            } else if (input == 'D' && userY < maze[0].length - 1 && maze[userX][userY + 1] == PATH) {
                moveUser(0, 1);
            } else if (input == 'H') {
                showHint();
            } else if (input == 'C') {
                System.out.println("You exited the game.");
                return -1;
            }

            if (input != 'H') {
                printMazeWithBlankAroundUser();
            }

            if (userX == maze.length - 2 && userY == maze[0].length - 1) {
                return 1;
            }
        }
    }

    private void moveUser(int dx, int dy) {
        // Move the user within the maze.
        maze[userX][userY] = PATH;
        hintMaze[userX][userY] = PATH;
        userX += dx;
        userY += dy;
        maze[userX][userY] = USER;
    }

    private void showHint() {
        // Initialize the hint maze and find the optimal path.
        initializeHintMaze();
        findOptimalPath(userX, userY, maze.length - 2, maze[0].length - 1);
        printHint();
    }

    private void initializeHintMaze() {
        // Initialize the hint maze to match the current state of the maze.
        for (int i = 0; i < hintMaze.length; i++) {
            for (int j = 0; j < hintMaze[i].length; j++) {
                hintMaze[i][j] = maze[i][j];
            }
        }
    }

    private boolean findOptimalPath(int x, int y, int destX, int destY) {
        // Find and mark the optimal path from the current position to the destination.
        if (x == destX && y == destY) {
            hintMaze[x][y] = HINT_PATH;
            return true;
        }

        if (x < 0 || x >= maze.length || y < 0 || y >= maze[0].length || hintMaze[x][y] == WALL || hintMaze[x][y] == HINT_PATH) {
            return false;
        }

        hintMaze[x][y] = HINT_PATH;

        if (findOptimalPath(x - 1, y, destX, destY) || findOptimalPath(x + 1, y, destX, destY) ||
            findOptimalPath(x, y - 1, destX, destY) || findOptimalPath(x, y + 1, destX, destY)) {
            return true;
        }

        hintMaze[x][y] = PATH;
        return false;
    }

    private void printMazeGrid() {
        // Print the maze grid.
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void printMazeWithBlankAroundUser() {
        // Print the maze with blank spaces around the user's position.
        int startRow = Math.max(0, userX - 1);
        int endRow = Math.min(maze.length - 1, userX + 1);
        int startCol = Math.max(0, userY - 1);
        int endCol = Math.min(maze[0].length - 1, userY + 1);

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (i >= startRow && i <= endRow && j >= startCol && j <= endCol) {
                    System.out.print(maze[i][j] + " ");
                } else {
                    System.out.print("  "); // Two spaces to represent blank
                }
            }
            System.out.println();
        }
    }

    private void printHint() {
        // Print the maze with blank spaces around the user's position.
        int startRow = Math.max(0, userX - 1);
        int endRow = Math.min(maze.length - 1, userX + 1);
        int startCol = Math.max(0, userY - 1);
        int endCol = Math.min(maze[0].length - 1, userY + 1);

        for (int i = 0; i < hintMaze.length; i++) {
            for (int j = 0; j < hintMaze[i].length; j++) {
                if (i >= startRow && i <= endRow && j >= startCol && j <= endCol) {
                    if (i == userX && j == userY) {
                        System.out.print(USER + " ");
                    } else {
                        System.out.print(hintMaze[i][j] + " ");
                    }
                } else {
                    System.out.print("  "); // Two spaces to represent blank
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        MazeGame mazeGame = new MazeGame();
        while (true) {
            mazeGame.generateMaze(11, 21);
            int result = mazeGame.playGame();
            if (result == 1) {
                System.out.print("You won, would you like to play again (y/n): ");
                char input = scanner.next().toUpperCase().charAt(0);
                System.out.println();

                if (input != 'Y') {
                    break;
                }
            } else {
                break;
            }
        }
    }
}

// both the maze generation and the hint solution have an asymptotic time complexity of O(rows * cols), which means that their execution time is proportional to the number of cells in the maze.