import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

// Andy Wang

public class MineSweeper extends JPanel {
    private Square[][] board;
    private static int mouseR, mouseC;
    private boolean gameStart, useRandomGeneration, gameOver, victory;
    private int time, flags, correct;
    private Timer t;

    /* JButtons */
    private JButton restart;

    public static final int SIZE = 30, MINES = 5;

    public MineSweeper(int width, int height) {
        setSize(width, height);
        gameStart = false;
        useRandomGeneration = false;

        resetBoard();

        t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time++;
                repaint();
            }
        });
        t.start();

        setupKeyListener();
        setupMouseListener();
        setupButtons();
    }

    public void resetBoard() {
        board = new Square[15][15];
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = new Square(false, i, j, board);
            }
        }
    }

    public void generateMines(int row, int col) {
        int[] bannedY = new int[]{row-1,row,row+1};
        int[] bannedX = new int[]{col-1,col,col+1};
        if(useRandomGeneration) {
            for (int i = 0; i < board.length; i++){
                for (int j = 0; j < board[0].length; j++) {
                    //you should modify this so a set number of mines are placed, not
                    //a random number of mines.
                    if (Math.random() < .2 && Arrays.binarySearch(bannedX, j) < 0 && Arrays.binarySearch(bannedY, i) < 0) {
                        board[i][j] = new Square(true, i, j, board);
                    } else {
                        board[i][j] = new Square(false, i, j, board);
                    }
                }
            }
        }else {
            int mineCount = 0;
            while(mineCount < MINES) {
                int i = (int)(Math.random() * 15);
                int j = (int)(Math.random() * 15);
                while((Arrays.binarySearch(bannedX, j) >= 0 && Arrays.binarySearch(bannedY, i) >= 0) || board[i][j].getIsMine()) {
                    i = (int)(Math.random() * 15);
                    j = (int)(Math.random() * 15);
                }
                board[i][j].setIsMine(true);
                mineCount++;
            }
        }

        for(Square[] arr : board) {
            for(Square sq : arr) {
                sq.calcNeighborMines();
            }
        }
    }

    public void setupButtons() {
        restart = new JButton("Try Again");
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time = 0;
                flags = 0;
                gameStart = false;
                resetBoard();
                gameOver = false;
                restart.setVisible(false);
                repaint();
            }
        });
        restart.setBounds(475, 100, 100, 25);
        restart.setVisible(false);
        this.add(restart);
    }

    public void setupKeyListener() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    int[] info = board[mouseR][mouseC].toggleFlag();
                    flags += info[0];
                    correct += info[1];
                }

                if(correct >= MINES) {
                    t.stop();
                    victory = true;
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public void setupMouseListener() {
        addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                int r = y / SIZE;
                int c = x / SIZE;

                if(!gameStart) {
                    generateMines(r, c);
                    gameStart = true;
                }

                if(r >= 0 && r < board.length && c >= 0 && c < board.length) {
                    board[r][c].click();
                    if(board[r][c].getIsMine()) {
                        t.stop();
                        gameOver = true;
                    }
                }

                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseR = e.getY() / SIZE;
                mouseC = e.getX() / SIZE;
            }
        });
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 600, 600);

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                board[r][c].draw(g2);
            }
        }

        g2.setColor(Color.WHITE);
        g2.drawString("Time: " + time, 500, 25);
        g2.drawString("Flags : " + flags, 500, 50);
        if(gameOver) {
            g2.setColor(Color.RED);
            g2.drawString("Game Over", 500, 75);
            restart.setVisible(true);
        }else if(victory) {
            g2.setColor(Color.GREEN);
            g2.drawString("Well Done", 500, 75);
            restart.setVisible(true);
        }
    }

    //sets ups the panel and frame.  Probably not much to modify here.
    public static void main(String[] args) {
        JFrame window = new JFrame("Minesweeper");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, 600, 600 + 22); //(x, y, w, h) 22 due to title bar.

        MineSweeper panel = new MineSweeper(600, 600);

        window.add(panel);
        window.setVisible(true);
        window.setResizable(false);

        panel.setFocusable(true);
        panel.grabFocus();
        panel.setLayout(null);
    }

}
