package boilerplate;

import java.util.List;

public class Queen extends Piece{

    public Queen(char c, boolean b, int x, int y, Board brd) {
        super(c, b, x, y, brd, c =='w' ? "♕" : "♛");
    }

    @Override
    public void getValidMoves(List<Move> validMoves) {

        getValidVerticalMoves(validMoves);
        getValidHorizontalMoves(validMoves);
        getValidDiagonalMoves(validMoves);
    }

    @Override
    boolean isValidMove(int x, int y) {
        Piece[][] b = board.getBoard();
        Piece p = b[x][y];
        if ((p != null && p.getColor() == color) || (x == currentX && y == currentY))
            return false;
        
        if (validHorizontalOrVerticalMove(x,y, b))
            return true;
        
        return validDiagonalMove(x, y, b); 
    }
}
