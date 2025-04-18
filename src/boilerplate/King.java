package boilerplate;

import java.util.List;

public class King extends Piece {

    public King(char c, boolean b, int x, int y, Board brd) {
        super(c, b, x, y, brd, c == 'w' ? "♔" : "♚");  
    }

    @Override
    public  void getValidMoves(List<Move> validMoves) {
        int[][] coord = getCoordinates();
        
        Piece[][] b = board.getBoard();
        
        for (int[] c : coord) {
            int x = c[0]; int y = c[1];
            if (x < 0 || x > 7 || y < 0 || y > 7 )
                continue;
            if (b[x][y] == null || (b[x][y] != null && b[x][y].getColor() != color && !(b[x][y] instanceof King))) {
            
                Move m = new Move(this, b[x][y], x, y, currentX, currentY);
                
                validMoves.add(m);   
            }          
        }
    }

    @Override
    boolean isValidMove(int x, int y) {
        int[][] coord = getCoordinates();
        boolean validC = false;
        for (int [] c : coord) {
            if (c[0] == x && c[1] == y) {
                validC = true;
                break;
            }
        }
        Piece [][] b = board.getBoard();
        if (!validC || (b[x][y] != null && (b[x][y].getColor() == color) || b[x][y] instanceof King)) 
                return false;
        
        return true;
    }
    
    int[][] getCoordinates(){
        int[][] coord = {
                {currentX+1, currentY-1}, {currentX+1, currentY+1},
                {currentX-1, currentY-1}, {currentX-1, currentY+1},
                {currentX, currentY+1}, {currentX, currentY-1},
                {currentX+1, currentY}, {currentX-1, currentY}         
          };
        
        return coord;
    }
    

}
