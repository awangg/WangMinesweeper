import java.awt.*;
import java.awt.image.*;

public class Square {

    private boolean isMine, isRevealed, isFlagged, isWrong;
    private int neighborMines;
    private int r, c;
    private Square[][] board;

    /* Images */
    private BufferedImage hidden, mine, flag, incorrect;

    public Square(boolean isMine, int r, int c, Square[][] board, BufferedImage h, BufferedImage m, BufferedImage f, BufferedImage i) {
        this.isMine = isMine;
        this.r = r;
        this.c = c;
        this.isRevealed = false;
        this.isFlagged = false;
        this.board = board;
        neighborMines = 0;

        this.hidden = h;
        this.mine = m;
        this.flag = f;
        this.incorrect = i;
    }

    public void calcNeighborMines(){
        int[] dx = new int[]{-1, 0, 1};
        int[] dy = new int[]{-1, 0, 1};
        int count = 0;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                int ddx = dx[i]; int ddy = dy[j];
                if(ddx == 0 && ddy == 0) continue;
                if(r + ddx >= 0 && r + ddx < board.length) {
                    if(c + ddy >= 0 && c + ddy < board.length) {
                        if(board[r+ddx][c+ddy].isMine) count++;
                    }
                }
            }
        }
        neighborMines = count;
    }

    public int[] toggleFlag() {
        if(isRevealed) return new int[]{0, 0};
        int[] ret = new int[]{0, 0};

        isFlagged = !isFlagged;
        if(!isFlagged) {
            ret[0] = -1;
            if(isMine) ret[1] = -1;
        }else {
            ret[0] = 1;
            if(isMine) ret[1] = 1;
        }

        return ret;
    }

    public void draw(Graphics2D g2){
        g2.setFont(new Font("TimesRoman", Font.PLAIN, 14));
        FontMetrics metrics = g2.getFontMetrics();
        int size = MineSweeper.SIZE;

        if (isRevealed) {
            if(isMine) {
                g2.drawImage(mine, c * size, r* size, size, size, null);
            }else{
                g2.setColor(new Color(189, 189, 189));
                g2.fillRect(c * size, r * size, size, size);
            }

            if(!isMine && neighborMines > 0) {
                g2.setColor(Color.BLACK);
                g2.drawString("" + neighborMines, c * size + size/2 - metrics.stringWidth("" + neighborMines)/2, r * size + 3*metrics.getHeight()/2);
            }

            if(isWrong) {
                g2.drawImage(incorrect, c * size, r* size, size, size, null);
            }
        }else{
            if(isFlagged) {
                g2.drawImage(flag, c * size, r* size, size, size, null);
            }else {
                g2.drawImage(hidden, c * size, r* size, size, size, null);
            }
        }
        g2.setColor(Color.BLACK);
        g2.drawRect(c * size, r * size, size, size);
    }

    public void click(){
        if(isMine) {
            isRevealed = true;
            /* Game Over */
            System.out.println("Game Over");
            revealEntireBoard();
        }else {
            floodCells(r, c);
        }
    }

    public void floodCells(int row, int col) {
        if(board[row][col].isRevealed) return;
        board[row][col].isRevealed = true;

        /* Zeroes are special - we try to expose as many as possible */
        if(board[row][col].neighborMines == 0) {
            /* Find the other zero neighbors and continue floodfill */
            int[] dx = new int[]{-1, 0, 1};
            int[] dy = new int[]{-1, 0, 1};
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    int ddx = dx[i]; int ddy = dy[j];
                    if(ddx == 0 && ddy == 0) continue;
                    if(row + ddx >= 0 && row + ddx < board.length) {
                        if(col + ddy >= 0 && col + ddy < board[0].length) {
                            floodCells(row+ddx, col+ddy);
                        }
                    }
                }
            }
        }
    }

    public void revealEntireBoard() {
        for(Square[] arr : board) {
            for(Square sq : arr) {
                if(sq.isMine) {
                    sq.isRevealed = true;
                }
                if(sq.isFlagged && !sq.isMine) {
                    sq.isRevealed = true;
                    sq.isWrong = true;
                }
            }
        }
    }

    public void setIsMine(boolean b) {
        isMine = b;
    }

    public boolean getIsMine() {
        return isMine;
    }

    public boolean getIsFlagged() {
        return isFlagged;
    }
}
