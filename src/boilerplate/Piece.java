package boilerplate;

import java.util.List;
import java.util.Objects;

public abstract class Piece {
    char color;
    boolean isBotPiece;
    String ident;
    int currentX;
    int currentY;
    Board board;

    public Piece(char c, boolean b, int x, int y, Board brd, String image) {
        color = c;
        isBotPiece = b;
        currentX = x;
        currentY = y;
        board = brd;
        this.ident = image;
    }


    protected void getValidVerticalMoves(List<Move> moves) {
        
        generateMove(1, 0, moves);  
        generateMove(-1, 0, moves); 
      
    }

    protected void getValidHorizontalMoves(List<Move> moves) {
        generateMove(0, 1, moves);  
        generateMove(0, -1, moves);
    }

    public void getValidDiagonalMoves(List<Move> moves) {
        generateMove(1, 1, moves);   
        generateMove(1, -1, moves);  
        generateMove(-1, 1, moves); 
        generateMove(-1, -1, moves); 
    }

    private void generateMove(int dx, int dy, List<Move> validMoves) {
       
        Piece[][] b = board.getBoard();

        int x = currentX + dx;
        int y = currentY + dy;

        while (x >= 0 && x < 8 && y >= 0 && y < 8) {
            Piece p = b[x][y];
            if (p != null) {
                if (p.getColor() != color) {
                    validMoves.add(new Move(this, p, x, y, currentX, currentY));
                }
                break;
            }
            validMoves.add(new Move(this, null, x, y, currentX, currentY));
            x += dx;
            y += dy;
        }
    }

    abstract boolean isValidMove(int x, int y);

    protected boolean validHorizontalOrVerticalMove(int x, int y, Piece[][] b) {
        if (b[x][y] != null && b[x][y].getColor() == color)
            return false;
        
        if (x == currentX) {
            int stepY = y > currentY ? 1 : -1;
            for (int j = currentY + stepY; j != y; j += stepY) {
                if (b[x][j] != null) return false;
            }
            return true;
        } else if (y == currentY) {
            int stepX = x > currentX ? 1 : -1;
            for (int i = currentX + stepX; i != x; i += stepX) {
                if (b[i][y] != null) return false;
            }
            return true;
        }
        return false;
    }

    protected boolean validDiagonalMove(int x, int y, Piece[][] b) {
        if (b[x][y] != null && b[x][y].getColor() == color)
            return false;
        
        int dx = x - currentX;
        int dy = y - currentY;

        if (Math.abs(dx) != Math.abs(dy)) return false;

        int stepX = dx > 0 ? 1 : -1;
        int stepY = dy > 0 ? 1 : -1;

        int checkX = currentX + stepX;
        int checkY = currentY + stepY;

        while (checkX != x && checkY != y) {
            if (b[checkX][checkY] != null) return false;
            checkX += stepX;
            checkY += stepY;
        }

        return true;
    }

    public char getColor() {
        return color;
    }

    public boolean isBotPiece() {
        return isBotPiece;
    }

    public String getIdentifier() {
        return ident;
    }

    public void setX(int x) {
        this.currentX = x;
    }

    public void setY(int y) {
        this.currentY = y;
    }

    public int getX() {
        return currentX;
    }

    public int getY() {
        return currentY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, ident, currentX, currentY,  isBotPiece);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Piece other = (Piece) obj;
        return color == other.color &&
                this.currentX == other.currentX &&
                this.currentY == other.currentY &&
                Objects.equals(ident, other.ident) &&
                isBotPiece == other.isBotPiece;
    }


    public void getValidMoves(List<Move> moves) {
        
    }
    
    public void getValidMoves() {
        
    }
}
