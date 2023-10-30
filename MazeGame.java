import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MazeGame {
    private final char WALL = '#';
    private final char PATH = '.';
    private final char USER = 'X';
    private char[][] maze;
    private int userX, userY;

    public MazeGame() {
    }

    public void generateMaze(int rows, int cols) {
        maze = new char[rows][cols];
        initializeMaze(rows, cols);
        generateMazeUsingDFS(1, 1, rows - 2, cols - 2);
        userX = 1; // Initial user position
        userY = 0;
        printMazeGrid();
    }

    private void initializeMaze(int rows, int cols) {
        for (char[] row : maze) {
            Arrays.fill(row, WALL);
        }

        maze[1][0] = USER;
        maze[rows - 2][cols - 1] = PATH;
    }

    private void generateMazeUsingDFS(int x, int y, int destX, int destY) {
        maze[x][y] = PATH;

        int[] dx = {2, -2, 0, 0};
        int[] dy = {0, 0, 2, -2};

        ArrayList<Integer> directions = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(directions);

        for (int dir : directions) {
            int newX = x + dx[dir];
            int newY = y + dy[dir];

            if (newX >= 1 && newX <= destX && newY >= 1 && newY <= destY && maze[newX][newY] == WALL) {
                maze[(x + newX) / 2][(y + newY) / 2] = PATH;
                generateMazeUsingDFS(newX, newY, destX, destY);
            }
        }
    }

    private void playGame() {
        Scanner scanner = new Scanner(System.in);
        char input;
        while (true) {
            System.out.print("Enter your move (W/A/S/D to move, H for hint, C to close): ");
            input = scanner.next().charAt(0);
            input = Character.toUpperCase(input);
            
            if (input == 'W' && userX > 0 && maze[userX - 1][userY] == PATH) {
                moveUser(-1, 0);
            } else if (input == 'S' && userX < maze.length - 1 && maze[userX + 1][userY] == PATH) {
                moveUser(1, 0);
            } else if (input == 'A' && userY > 0 && maze[userX][userY - 1] == PATH) {
                moveUser(0, -1);
            } else if (input == 'D' && userY < maze[0].length - 1 && maze[userX][userY + 1] == PATH) {
                moveUser(0, 1);
            } else if (input == 'H') {
                System.out.println("Hint here.");
            }else if (input == 'C') {
                System.out.println("You exited the game.");
                break;
            }
            
            printMazeGrid();
            
            if (userX == maze.length - 2 && userY == maze[0].length - 1) {
                System.out.println("Congratulations! You reached the destination.");
                break;
            }
        }
        
        scanner.close();
    }

    private void moveUser(int dx, int dy) {
        maze[userX][userY] = PATH;
        userX += dx;
        userY += dy;
        maze[userX][userY] = USER;
    }

    private void printMazeGrid() {
        System.out.println();
        for (char[] row : maze) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        MazeGame mazeGame = new MazeGame();
        mazeGame.generateMaze(11, 21);
        mazeGame.playGame();
    }
}
