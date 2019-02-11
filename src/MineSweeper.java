import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.util.Arrays;

// Andy Wang - starter code by Michael Hopps

public class MineSweeper extends JPanel {
    private Square[][] board;
    private static int mouseR, mouseC;
    private boolean gameStart, useRandomGeneration, gameOver, victory;
    private int time, flags, correct;
    private Timer t;

    private Color[] colors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.BLACK, Color.WHITE};

    /* JButtons */
    private JButton restart;

    /* Images */
    private BufferedImage hidden, mine, flag, reset, incorrect;

    public static final int SIZE = 30, MINES = 1;

    public MineSweeper(int width, int height) {
        setSize(width, height);
        gameStart = false;
        useRandomGeneration = false;

        /* Read Images */
        try {
            hidden = ImageIO.read(new File("res/icon.jpg"));
            mine = ImageIO.read(new File("res/mine.jpg"));
            flag = ImageIO.read(new File("res/flag.jpg"));
            reset = ImageIO.read(new File("res/reset.png"));
            incorrect = ImageIO.read(new File("res/incorrect.png"));
        } catch(Exception e) {
            System.out.println(e);
        }

        resetBoard();

        t = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time++;
                repaint();
            }
        });

        setupKeyListener();
        setupMouseListener();
        setupButtons();
    }

    public void resetBoard() {
        board = new Square[15][15];
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = new Square(false, i, j, board, hidden, mine, flag, incorrect);
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
                        board[i][j] = new Square(true, i, j, board, hidden, mine, flag, incorrect);
                    } else {
                        board[i][j] = new Square(false, i, j, board, hidden, mine, flag, incorrect);
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
        restart = new JButton("");
        restart.setIcon(new ImageIcon(reset));
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time = 0;
                flags = 0;
                correct = 0;
                gameStart = false;
                victory = false;
                resetBoard();
                gameOver = false;
                restart.setVisible(false);
                repaint();
            }
        });
        restart.setBounds(210, 485, 30, 30);
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
                if(e.getKeyCode() == KeyEvent.VK_SPACE && mouseR >= 0 && mouseR < board.length && mouseC >= 0 && mouseC < board[0].length) {
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
                    t.start();
                }

                if(r >= 0 && r < board.length && c >= 0 && c < board.length && !board[r][c].getIsFlagged()) {
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

        /* Auxiliary Text */
        g2.setFont(new Font("Helvetica", Font.BOLD, 24));
        FontMetrics metrics = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        /* Timer */
        g2.drawString("Time", 350 - metrics.stringWidth("Time")/2, 485);
        g2.drawString("" + time, 350 - metrics.stringWidth("" + time)/2, 515);
        /* Flagger */
        g2.drawString("Flagged", 100 - metrics.stringWidth("Flagged")/2, 485);
        g2.drawString("" + flags, 100 - metrics.stringWidth("" + flags)/2, 515);

        /* End Game Text */
        g2.setFont(new Font("Helvetica", Font.BOLD, 48));
        metrics = g2.getFontMetrics();
        if (gameOver) {
            g2.setColor(Color.WHITE);
            g2.drawString("Game Over", 225 - metrics.stringWidth("Game Over")/2, 275 - metrics.getHeight() / 2);
            restart.setVisible(true);
        } else if (victory) {
            g2.setColor(colors[(int)(Math.random() * colors.length)]);
            g2.drawString("Well Done", 225 - metrics.stringWidth("Well Done")/2, 275 - metrics.getHeight() / 2);
            restart.setVisible(true);
        }
    }

    //sets ups the panel and frame.  Probably not much to modify here.
    public static void main(String[] args) {
        JFrame window = new JFrame("Minesweeper");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, 450, 550 + 22); //(x, y, w, h) 22 due to title bar.

        MineSweeper panel = new MineSweeper(450, 550);

        window.add(panel);
        window.setVisible(true);
        window.setResizable(false);

        panel.setFocusable(true);
        panel.grabFocus();
        panel.setLayout(null);
    }

}
