package ai;

import java.util.ArrayList;
import java.util.List;

import boilerplate.Board;
import boilerplate.King;
import boilerplate.Move;
import boilerplate.Pawn;

public class AlphaBetaSearch {
    private int depth;
    private FixedSizeHashMap <Long, Integer> evaluations;
    private BoardEvaluation be;
    private MoveComparator compare;
    private Board board;

    public AlphaBetaSearch(int depth, Board board) {
        this.depth = depth;
        be = new BoardEvaluation();
        evaluations = new FixedSizeHashMap<>(1000000);
        this.board = board;
        compare = new MoveComparator();
    }
    
    public Move getBestMove() {
        
        List <Move> moves = getFilteredMoves();
        Move bestMoveLastDepth = null;       
        int s = moves.size();  
        int iterativeDeepening = 1;
        long endTime = System.currentTimeMillis()+1000;
        
        while(true) {
            int value = Integer.MIN_VALUE;
            Move bestMove = null;
            if (iterativeDeepening >depth)return bestMoveLastDepth;
            for (int j = 0; j<s; j++) {
                if(System.currentTimeMillis()> endTime)return bestMoveLastDepth;
                Move m = moves.get(j);

                board.makeMove(m);
                int eval = alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, iterativeDeepening-1, false);
                board.undoLastMove();
                if (eval > value) {
                    value = eval;
                    bestMove = m;
                }
        }
            
            if(bestMove != null)
            bestMoveLastDepth = bestMove;
            iterativeDeepening++;
        }
                       
    }
    

    public int alphaBeta(int alpha, int beta, int remainingDepth, boolean maximizingPlayer) {
        long prime = maximizingPlayer ? 31 : 37;
        long nodeHash = (prime*(long)board.hashCode())<<4+(long)remainingDepth;
        
        if (evaluations.containsKey(nodeHash)) {
            return evaluations.get(nodeHash);
        }
            
     
        if (remainingDepth == 0) {
            int eval = be.evaluate(board);
            evaluations.put(nodeHash, eval);
            return eval;     
        }
        
        
        if (maximizingPlayer) {
            int val = alpha;
            List <Move> moves = board.getMoves(true, false);
            if (remainingDepth != 1)
            moves.sort(compare);
                
            for (Move m : moves) {
                
                board.makeMove(m);
                val = Math.max(val, alphaBeta(alpha, beta, remainingDepth-1, false));
                board.undoLastMove();
                          
                if (val >= beta)
                    break;
                
                alpha = Math.max(val, alpha);
            }  
            evaluations.put(nodeHash, val);
            return val;
            
        }else {
            int val = beta;
            List <Move> moves = board.getMoves(false, false);
            if (remainingDepth != 1)
            moves.sort(compare);
                 
            for (Move m : moves) { 
                board.makeMove(m);
                val = Math.min(val, alphaBeta(alpha, beta, remainingDepth-1, true));
                board.undoLastMove();
                                     
                if (val <= alpha)
                    break;
                
                beta = Math.min(val, beta);
            }  
            evaluations.put(nodeHash, val);
            return val;
        }
    }
      
    private List<Move> getFilteredMoves() {
        List<Move> moves = board.getMoves(true, true);
        
        List<Move> fallBack = new ArrayList<>();
        List<Move> filtered = new ArrayList<>();
        moves.sort(compare);
        
        for (Move m : moves) {
            if (!blunder(m)) {
                filtered.add(m);
                fallBack.add(m);
            }else if (fallBack.size() < 2) {
                fallBack.add(m);
            }  
            
        }
        return filtered.size() >= 4 ? filtered : fallBack;
    }

    private boolean blunder(Move m) {
        
        if (m.getMovedPiece() instanceof King ||(!(m.getMovedPiece() instanceof Pawn) && (!m.captures()))) {
            board.makeMove(m);
            int x = m.getCurrentX();
            int y = m.getCurrentY();
            List<Move> moves= board.getMoves(false, false);
            board.undoLastMove();
            
            for (Move mv : moves) {
                if (mv.getCurrentX() == x && mv.getCurrentY() == y){
                    return true;
                }         
            }
        }
       
        return false;  
              
    }
    

  
}