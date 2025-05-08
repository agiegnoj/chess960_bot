package boilerplate;

import java.util.List;

public class Bishop extends Piece{

    public Bishop(char c, boolean b, int x, int y, Board brd) {
        super(c, b, x, y, brd, c == 'w' ? "♗" : "♝");
    }

    @Override
    public void getValidMoves(List<Move> validMoves) {
        getValidDiagonalMoves(validMoves);
    }

    @Override
    boolean isValidMove(int x, int y) {
        Piece[][] b = board.getBoard();
                
        return validDiagonalMove(x, y, b); 
    }
    
    

}
