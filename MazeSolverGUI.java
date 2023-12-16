import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Scanner;

/**
 *
 * @author PC
 */
public class MazeSolverGUI extends javax.swing.JFrame {

    public MazeSolverGUI() {
        initComponents();
    }

    private static final int CELL_SIZE = 100; // Size of each cell in pixels

    private int threadId;
    private JButton submitButton;
    private JPanel mazePanel;
    private int mazeSize;
    private int[][] mazeMatrix;
    private boolean[][] visitedCells;
    private static boolean solutionFound = false;
    private volatile boolean stopThreads; // Shared flag to stop threads

    public MazeSolverGUI(int mazeSize) {
        this.mazeSize = mazeSize;
        mazeMatrix = new int[mazeSize][mazeSize];
        visitedCells = new boolean[mazeSize][mazeSize];

        // Initialize maze matrix with all cells set to 1 (accessible || free)
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                mazeMatrix[i][j] = 1;
                visitedCells[i][j] = false;
            }
        }
        initializeGUI();
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Rat-In-Maze Solver");
        setResizable(false);

        mazePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMaze(g);
            }
        };
        mazePanel.setPreferredSize(new Dimension(mazeSize * CELL_SIZE, mazeSize * CELL_SIZE));
        mazePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / CELL_SIZE;
                int col = e.getX() / CELL_SIZE;
                toggleCell(row, col);
                mazePanel.repaint();
            }
        });

        submitButton = new JButton("Submit");
        submitButton.addActionListener((ActionEvent e) -> {
            startMazeSolver();
        });
        getContentPane().add(submitButton, BorderLayout.SOUTH);

        getContentPane().add(mazePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        startMazeSolver();
    }

    private void drawMaze(Graphics g) {
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                int cellValue = mazeMatrix[i][j];
                if (cellValue == 0) {
                    g.setColor(Color.BLACK);
                } else {
                    if (visitedCells[i][j]) {
                        g.setColor(Color.BLUE);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                }
                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void toggleCell(int row, int col) {
        if (row >= 0 && row < mazeSize && col >= 0 && col < mazeSize) {
            mazeMatrix[row][col] = 1 - mazeMatrix[row][col]; // Toggle cell value (0 to 1 or 1 to 0)
        }
    }

    class RatThread extends Thread {
        private int row, col;
        private int[][] localMaze;

        RatThread(int row, int col, int[][] maze) {
            this.row = row;
            this.col = col;
            this.localMaze = maze;
        }

        @Override
        public void run() {
            solveMaze(row, col, localMaze);
        }

        private boolean isSafe(int r, int c) {
            return r < mazeSize && c < mazeSize && localMaze[r][c] == 1;
        }

        private void solveMaze(int r, int c, int[][] maze) {
            if (r == mazeSize - 1 && c == mazeSize - 1) {
                maze[r][c] = 2;
                System.out.println("FOUND: \n");
                displayMaze(maze);
                visitedCells[row][col] = true;
                mazePanel.repaint(); // Update the GUI
                solutionFound = true;
                stopThreads = true; // Set the flag to stop other threads
                JOptionPane.showMessageDialog(MazeSolverGUI.this, "Maze solved!");
//                stopAllThreads();
                return;
            }

            if (isSafe(r, c) && !solutionFound) {
                maze[r][c] = 2;
                visitedCells[row][col] = true;
                mazePanel.repaint(); // Update the GUI
                
                exploreDirection(r + 1, c, maze.clone());
                exploreDirection(r, c + 1, maze.clone());
            }
            maze[r][c] = 1;
        }

        private void exploreDirection(int r, int c, int[][] maze) {
            if (isSafe(r, c) && !solutionFound) {
                RatThread thread = new RatThread(r, c, maze);
                thread.run();
            }
        }

        private void stopAllThreads() {
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                t.interrupt();
            }
        }
    }

    private void displayMaze(int[][] maze) {
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void startMazeSolver() {
        // Check if any cell has a value of 0
        boolean hasObstacle = false;
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                if (mazeMatrix[i][j] == 0) {
                    hasObstacle = true;
                    break;
                }
            }
            if (hasObstacle) {
                break;
            }
        }

        if (mazeMatrix[0][0] == 0) {
            JOptionPane.showMessageDialog(this, "First cell can't be dead. Try again!!");
            return;
        }

        if (!hasObstacle) {
            JOptionPane.showMessageDialog(this, "Please set at least one obstacle (cell with value 0).");
            return;
        }

        RatThread ratThread = new RatThread(0, 0, mazeMatrix);
        stopThreads = false; // Reset the flag
        ratThread.run();

        try {
            ratThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!solutionFound) {
                System.out.println("There are no possible solutions");
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MazeSolverGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        System.out.print("Enter the dimensions of the maze (N x N): ");
        try (Scanner input = new Scanner(System.in)) {
            int mazeSize = input.nextInt();
            SwingUtilities.invokeLater(() -> new MazeSolverGUI(mazeSize));
        }
    }
}
