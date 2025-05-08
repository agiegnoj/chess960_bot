package boilerplate;

import java.util.List;

public class Pawn extends Piece {
    
    public Pawn(char c, boolean b, int x, int y, Board brd) {
        super(c, b, x, y, brd, c == 'w' ? "♙" : "♟"); 
    }

    @Override
    public  void getValidMoves(List<Move> validMoves) {
        int offset = isBotPiece ? 1 : -1;
        int newX = currentX + offset;
        
        Piece[][] b = board.getBoard();
        if (newX < 8 && newX >= 0) {
            if (b[newX][currentY] == null) {
                Move m = new Move(this, null, newX, currentY, currentX, currentY);
                validMoves.add(m);
            }
            
            int newY = currentY-1; 
            if (newY >= 0) {
                Piece temp = b[newX][newY];
                
                if ( temp != null && temp.getColor() != color) {
                    Move m = new Move(this, temp, newX, newY, currentX, currentY);
                    validMoves.add(m);
                }    
            }
            
            newY = currentY +1;
            
            if (newY < 8) {
                Piece temp = b[newX][newY];
                
                if ( temp != null && temp.getColor() != color) {
                    Move m = new Move(this, temp, newX, newY, currentX, currentY);
                    validMoves.add(m);
                }    
            }            
        }   
        
        if ((this.getX() == 1 && isBotPiece) || (this.getX() == 6 && !isBotPiece)) {
            offset = isBotPiece ? 2 : -2;
            int offset2 = isBotPiece ? 1 : -1;
            
            newX = currentX+offset;
            
            if (newX < 8 && newX >= 0 && b[offset2+this.getX()][this.getY()] == null
                    && b[newX][currentY] == null) {
                Move m = new Move(this, null, newX, currentY, currentX, currentY);
                validMoves.add(m);
            }         
        }  
        
    }


    @Override
    public boolean isValidMove(int x, int y) {      
        int offset = isBotPiece ? 1 : -1;
        if (x == currentX+offset && y == currentY && board.getBoard()[x][y] == null) {
            return true;
        }
        
        if (x == currentX+offset && (y == currentY-1 || y == currentY+1)
                && board.getBoard()[x][y]!= null && board.getBoard()[x][y].getColor() != this.color)
            return true;
        
        if (board.getBoard()[currentX+offset][currentY] == null && (currentX == 1 || currentX == 6)) {
            if (((isBotPiece && x == 3) || (!isBotPiece && x == 4)) && board.getBoard()[x][y] == null)
                return true;
        }
       
        return false;        
    }    
      
}
