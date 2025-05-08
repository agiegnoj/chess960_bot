package ai;

import java.util.ArrayList;
import java.util.List;

import boilerplate.Board;
import boilerplate.King;
import boilerplate.Move;
import boilerplate.Pawn;
import boilerplate.Queen;
import boilerplate.Rook;

public class AlphaBetaSearch {
    private int depth;
    FixedSizeHashMap <Long, Integer> evaluations;
    BoardEvaluation be;
    MoveComparator compare;
    Board board;
    int currentEval;

    public AlphaBetaSearch(int depth, Board board) {
        this.depth = depth;
        be = new BoardEvaluation();
        evaluations = new FixedSizeHashMap<>(1000000);
        this.board = board;
        compare = new MoveComparator();
    }
    
    public Move getBestMove() {
        currentEval = be.evaluate(board);
        List <Move> moves = getFilteredMoves();
        Move bestMove = null;       
        int s = moves.size();      
        
        for (int iterativeDeepening = 1; iterativeDeepening <= depth; iterativeDeepening++) {
            int value = Integer.MIN_VALUE;
            
            for (int j = 0; j<s; j++) {
                
                Move m = moves.get(j);

                board.makeMove(m);
                int eval = alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, iterativeDeepening-1, false);
                board.undoLastMove();
                if (eval > value) {
                    value = eval;
                    bestMove = m;
                }    
        }
            
        }
                  
        return bestMove;     
    }
    

    public int alphaBeta(int alpha, int beta, int remainingDepth, boolean maximizingPlayer) {
        long prime = maximizingPlayer ? 31 : 37;
        long nodeHash = prime*(long)board.hashCode()+(long)remainingDepth;
        
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
            List <Move> moves = board.getMoves(true);
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
            List <Move> moves = board.getMoves(false);
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
        List<Move> moves = board.getMoves(true);
        List<Move> fallBack = new ArrayList<>();
        List<Move> filtered = new ArrayList<>();
        moves.sort(compare);
        
        for (Move m : moves) {
            if (!blunder(m)) {
                filtered.add(m);
                fallBack.add(m);
            }else if (fallBack.size() < 4) {
                fallBack.add(m);
            }  
            
        }
        return filtered.size() >= 4 ? filtered : fallBack;
    }

    private boolean blunder(Move m) {
          
        if (m.getMovedPiece() instanceof King ||(!(m.getMovedPiece() instanceof Pawn) && (!m.captures() || m.getCapturedPiece()instanceof Pawn ))) {
            board.makeMove(m);
            int x = m.getCurrentX();
            int y = m.getCurrentY();
            List<Move> moves= board.getMoves(false);
            board.undoLastMove();
            
            for (Move mv : moves) {
                if (mv.getCurrentX() == x && mv.getCurrentY() == y){
                    return true;
                }         
            }
        }else {
            board.makeMove(m);
            int temp = alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, depth/2, false);
            board.undoLastMove();
            if (temp < currentEval -5000)return true;
        }
       
        return false;  
              
    }
    

  
}