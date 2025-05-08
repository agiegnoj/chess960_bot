package boilerplate;

import java.util.List;

public class Rook extends Piece{

    public Rook(char c, boolean b, int x, int y, Board brd) {
        super(c, b, x, y, brd, c == 'w' ? "♖":"♜");
    }

    @Override
    public void getValidMoves(List<Move>  validMoves) {
        getValidVerticalMoves(validMoves);
        getValidHorizontalMoves(validMoves);
    }

    @Override
    boolean isValidMove(int x, int y) {
        Piece[][] b = board.getBoard();  
        return validHorizontalOrVerticalMove(x, y, b);
    }

}
