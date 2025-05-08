package boilerplate;

import java.util.Objects;

public class Move {
    private boolean captures;
    private Piece movedPiece;
    private Piece capturedPiece;
    private int currentX;
    private int currentY;
    private int prevX;
    private int prevY;
    private boolean isEnPassant;
    private boolean isPromotion;
    
    public Move(Piece mP, Piece cP, int cX, int cY, int pX, int pY) {
        this.captures = cP != null ? true : false;
        capturedPiece = cP;
        movedPiece = mP;
        currentX = cX;
        currentY = cY;
        prevX = pX;
        prevY = pY;
    }

    public boolean captures() {
        return captures;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public int getPrevX() {
        return prevX;
    }

    public int getPrevY() {
        return prevY;
    }
    
    public void setEnPassant() {
        isEnPassant = true;
    }
    
    public boolean isEnPassant() {
        return isEnPassant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(capturedPiece, captures, currentX, currentY, movedPiece, prevX, prevY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Move other = (Move) obj;
        return Objects.equals(capturedPiece, other.capturedPiece) && captures == other.captures
                && currentX == other.currentX && currentY == other.currentY
                && Objects.equals(movedPiece, other.movedPiece) && prevX == other.prevX && prevY == other.prevY;
    }

    public void setPromotion() {
        isPromotion = true;
        
    }
    
    public boolean isPromotion() {
        return isPromotion;
    }
    

     
}
