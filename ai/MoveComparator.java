package ai;

import java.util.Comparator;

import boilerplate.King;
import boilerplate.Move;
import boilerplate.Pawn;
import boilerplate.Queen;

class MoveComparator implements Comparator<Move> {
 
    @Override
    public int compare(Move m1, Move m2) {          
        return score(m2)-score(m1);       
    }
    
    private int score (Move m) {
        int score = 0;
        if (m.captures() && m.getCapturedPiece() instanceof King)
            score += 20000;
        if (m.captures() && m.getCapturedPiece() instanceof Queen)
            score += 1000;
        if (m.captures() && m.getMovedPiece() instanceof Pawn)
            score += 500;
        if (m.isPromotion())
            score += 500;
        
        return score;
              
    } 
}
