 package ai;

import java.util.ArrayList;
import java.util.List;

import boilerplate.*;

public class BoardEvaluation {
    
    private int gamePhase;
    List<Move> botMoves;
    List<Move> playerMoves;
    Board board;
   
    public int evaluate(Board board) {
        if (board.checkMate(true))return 20000;
        if (board.checkMate(false))return -20000;

        initialize(board);
        
        int eval = 0;
        eval += pieceBalance();
        eval += squareControl();
        eval += kingSafety();
        eval += pawnStructure(true);
        eval -= pawnStructure(false);
        eval += connectedRooks(true);
        eval -= connectedRooks(false);
        eval += bishopPair();
        eval += kingTropism();
  
        return eval;
    }
    

    
    private int squareControl() {
       
        int squareControl = 0;
        for (Move m : botMoves) {
            int x = m.getCurrentX();
            int y = m.getCurrentY();
            if (x >= 2 && x <= 5 && y >= 2 && y <= 5) {
                squareControl += 5;
                if(x >= 3 && x <= 4 && y >= 3 && y <= 4)
                squareControl += 6;
            }        
            squareControl++;
        }
        
        for (Move m : playerMoves) {
            int x = m.getCurrentX();
            int y = m.getCurrentY();
            if (x >= 2 && x <= 5 && y >= 2 && y <= 5) {
                squareControl -= 5;
                if(x >= 3 && x <= 4 && y >= 3 && y <= 4)
                squareControl -= 6;
            }        
            squareControl--;
        }
        
         
        return squareControl*4; 
    }

    int pieceBalance() {
        int[] factors = {9, 10, 9};
        int pieceBalance = 0;
        for (Piece p : board.getPieces(true)) {
            pieceBalance += getPieceValue(p, true);
            if (p instanceof Pawn || p instanceof King || p instanceof Rook)addToList(p, true);
            else if (p instanceof Bishop) bishopCountBot++;
        }
        for (Piece p : board.getPieces(false)) {
            pieceBalance -= getPieceValue(p, false);
            if (p instanceof Pawn || p instanceof King || p instanceof Rook)addToList(p, false);
            else if (p instanceof Bishop) bishopCountPlayer++;
        }
        
        return pieceBalance*factors[gamePhase];
    }
    
    
    
    private int kingSafety() {
        int kingSafety = 0;
        int [] factors = {6, 9, 5};
                
        int x = botKing.getX()-1;
        int x2 = botKing.getX()+3;
        int y = botKing.getY()-1;
        int y2 = botKing.getY()+1;
               
        int count = 0;
        int val = 0;
        for (Move m : playerMoves) {
            if (m.getCurrentX()>= x && m.getCurrentX() <= x2
                    && m.getCurrentY() >= y && m.getCurrentY() <=y2) {
                val += getHeuristicPenalty(m.getMovedPiece());
                count++;
            }           
            
        }
        
        kingSafety -= val * attackWeight(count)/100;
             
        x = playerKing.getX()+1;
        x2 = playerKing.getX()-3;
        y = playerKing.getY()-1;
        y2 = playerKing.getY()+1;
 
        count = 0;
        val = 0;
          
        for (Move m : botMoves) {
            if (m.getCurrentX()<= x && m.getCurrentX() >= x2
                    && m.getCurrentY() >= y && m.getCurrentY() <=y2) {
                val += getHeuristicPenalty(m.getMovedPiece());
                count++;
            }
                
        }
        
        kingSafety += val * attackWeight(count)/100;
        
              
        return kingSafety * factors[gamePhase]; 
    }
    
    public int pawnStructure(boolean isBot) {
        List<Piece> myPawns = isBot ? botPawns : playerPawns;
        List<Piece> oppPawns = isBot ? playerPawns : botPawns;

        int[] myPawnFileCount = new int[8];
        int[][] oppPawnMap = new int[8][8]; 

        for (Piece p : myPawns)
            myPawnFileCount[p.getX()]++;

        for (Piece p : oppPawns)
            oppPawnMap[p.getX()][p.getY()] = 1;

        int isolatedPenalty = -15;
        int doubledPenalty = -10;
        int backwardPenalty = -8;
        int[] passedBonus = {0, 5, 10, 20, 30, 50, 70, 100};

        int score = 0;
        for (Piece p : myPawns) {
            int x = p.getX();
            int y = p.getY();

            if (myPawnFileCount[x] > 1)
                score += doubledPenalty;

            boolean isolated = true;
            for (int dx = -1; dx <= 1; dx += 2) {
                int nx = x + dx;
                if (nx >= 0 && nx < 8 && myPawnFileCount[nx] > 0) {
                    isolated = false;
                    break;
                }
            }
            if (isolated) score += isolatedPenalty;

            boolean backward = true;
            int dir = isBot ? 1 : -1;
            for (int dx = -1; dx <= 1; dx += 2) {
                int nx = x + dx;
                int ny = y + dir;
                while (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                    if (hasOwnPawnAt(nx, ny, myPawns)) {
                        backward = false;
                        break;
                    }
                    ny += dir;
                }
            }
            if (backward) score += backwardPenalty;

            boolean passed = true;
            for (int dx = -1; dx <= 1; dx++) {
                int nx = x + dx;
                if (nx < 0 || nx >= 8) continue;
                for (int ry = y + dir; ry >= 0 && ry < 8; ry += dir) {
                    if (oppPawnMap[nx][ry] == 1) {
                        passed = false;
                        break;
                    }
                }
            }
            if (passed) {
                int rank = isBot ? y : 7 - y;
                score += passedBonus[rank];
            }
        }

        return score;
    }

    private boolean hasOwnPawnAt(int x, int y, List<Piece> pawns) {
        for (Piece p : pawns)
            if (p.getX() == x && p.getY() == y)
                return true;
        return false;
    }
    
    private int kingTropism() {
        if (gamePhase != 2)return 0;
        
        int val = 0;
        
        int x = botKing.getX();
        int y= botKing.getY();
        
        for (Piece p : board.getPieces(false)) {
            int manhattanDistance = Math.abs(x-p.getX())+Math.abs(y-p.getY());
            val += manhattanDistance*getHeuristicPenalty(p);
        }
        
        x = playerKing.getX();
        y= playerKing.getY();
        
        for (Piece p : board.getPieces(true)) {
            int manhattanDistance = Math.abs(x-p.getX())+Math.abs(y-p.getY());
            val -= manhattanDistance*getHeuristicPenalty(p);
        }
        
        return val;
            
    }
    
    private int connectedRooks(boolean bot) {
        if (gamePhase != 1)return 0;
        int val = 0;
        
        List<Piece> rooks = bot ? botRooks : playerRooks;
        
        if (rooks.size() == 2) {
            int x1 = rooks.get(0).getX();
            int x2 = rooks.get(1).getX();
            int y1 = rooks.get(0).getY();
            int y2 = rooks.get(1).getY();
                  
            if (y1 == y2) {
                int minX = Math.min(x1, x2);
                int maxX = Math.max(x1,  x2);
                boolean connected = true;
                for (int i = minX+1; i < maxX; i++) {
                    if (board.getPiece(i, y1)!= null) {
                        connected = false;
                        break;
                    }      
                }
                if (connected)
                val += 280;      
            }else if (x1 == x2) {
                int minY = Math.min(y1, y2);
                int maxY = Math.max(y1,  y2);
                boolean connected = true;
                for (int i = minY+1; i < maxY; i++) {
                    if (board.getPiece(x1, i)!= null) {
                        connected = false;
                        break;
                    }      
                }
                if (connected)
                val += 280;      
            }          
        }
        
        return val;
               
    }
    
    private int bishopPair() {
        int val = 0; 
        if (bishopCountBot == 2)val += 200;
        if(bishopCountPlayer == 2)val -= 200;
        return val;
    }

    
    private int attackWeight(int count) {
        switch (count) {
        case 0 : return 0;
        case 1 : return 0;
        case 2: return 50;
        case 3: return 75;
        case 4: return 88;
        case 5: return 94;
        case 6: return 97;
        default: return 99;
        }
    }



    private int getHeuristicPenalty(Piece p) {
        if (p instanceof Knight || p instanceof Bishop) return 20;
        if (p instanceof Rook) return 40;
        if (p instanceof Queen)return 80;
        return 0;
        
    }
    
    private int getPieceValue(Piece p, boolean isBot) {
        int x = p.getX();
        int y = p.getY();
        
        if (isBot) {
            
            if (p instanceof Pawn) return 100 + (gamePhase != 2 ? PAWN_OPENING[x][y] : PAWN_ENDGAME[x][y]);
            if (p instanceof Knight) return 325 + KNIGHT_TABLE[x][y];
            if (p instanceof Bishop) return 325 +  BISHOP_TABLE[x][y];
            if (p instanceof Rook) return 500 + (gamePhase != 2 ? ROOK_OPENING[x][y] : ROOK_ENDGAME[x][y]);
            if (p instanceof Queen) return 950 + (gamePhase != 2 ? QUEEN_OPENING[x][y] : QUEEN_ENDGAME[x][y]);
            if (p instanceof King) return  4000 + (gamePhase != 2 ? KING_OPENING[x][y] : KING_ENDGAME[x][y]);
            
        }else {
            if (p instanceof Pawn) return 100 + (gamePhase != 2 ? PAWN_OPENING_FLIPPED[x][y] : PAWN_ENDGAME_FLIPPED[x][y]) ;
            if (p instanceof Knight) return 325 + KNIGHT_TABLE_FLIPPED[x][y];
            if (p instanceof Bishop) return 325 +  BISHOP_TABLE_FLIPPED[x][y];
            if (p instanceof Rook) return 500 + (gamePhase != 2 ? ROOK_OPENING_FLIPPED[x][y] : ROOK_ENDGAME_FLIPPED[x][y]);
            if (p instanceof Queen) return 950 + (gamePhase != 2 ? QUEEN_OPENING_FLIPPED[x][y] : QUEEN_ENDGAME_FLIPPED[x][y]);
            if (p instanceof King) return  4000 + (gamePhase != 2 ? KING_OPENING_FLIPPED[x][y] : KING_ENDGAME_FLIPPED[x][y]);
            
        }    
        return 0;
    }
    
    
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int[][] PAWN_OPENING = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {5, 10, 10, -20, -20, 10, 10, 5},
        {5, -5, -10, 0, 0, -10, -5, 5},
        {0, 0, 0, 20, 20, 0, 0, 0},
        {5, 5, 10, 25, 25, 10, 5, 5},
        {10, 10, 20, 30, 30, 20, 10, 10},
        {50, 50, 50, 50, 50, 50, 50, 50},
        {0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    public static final int[][] PAWN_OPENING_FLIPPED = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
        };
    
    public static final int[][] PAWN_ENDGAME = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {10, 10, 10, 20, 20, 10, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, 5, 10, 20, 20, 10, 5, 5},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {0, 0, 0, 0, 0, 0, 0, 0}
        };
    
    public static final int[][] PAWN_ENDGAME_FLIPPED = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 20, 20, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {10, 10, 10, 20, 20, 10, 10, 10},
            {0, 0, 0, 0, 0, 0, 0, 0}
        };
    
   
    
    public static final int[][] KNIGHT_TABLE = {
        {-50, -40, -30, -30, -30, -30, -40, -50},
        {-40, -20, 0, 0, 0, 0, -20, -40},
        {-30, 0, 10, 15, 15, 10, 0, -30},
        {-30, 5, 15, 20, 20, 15, 5, -30},
        {-30, 0, 15, 20, 20, 15, 0, -30},
        {-30, 5, 10, 15, 15, 10, 5, -30},
        {-40, -20, 0, 5, 5, 0, -20, -40},
        {-50, -40, -30, -30, -30, -30, -40, -50}
    };
    
    public static final int[][] KNIGHT_TABLE_FLIPPED = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
        };


    public static final int[][] BISHOP_TABLE = {
        {-20, -10, -10, -10, -10, -10, -10, -20},
        {-10, 0, 0, 0, 0, 0, 0, -10},
        {-10, 0, 5, 10, 10, 5, 0, -10},
        {-10, 5, 5, 10, 10, 5, 5, -10},
        {-10, 0, 10, 10, 10, 10, 0, -10},
        {-10, 10, 10, 10, 10, 10, 10, -10},
        {-10, 5, 0, 0, 0, 0, 5, -10},
        {-20, -10, -10, -10, -10, -10, -10, -20}
    };
    
    public static final int[][] BISHOP_TABLE_FLIPPED = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
        };
    
    
    public static final int[][] ROOK_OPENING = {
        {0, 0, 0, 5, 5, 0, 0, 0},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {5, 10, 10, 10, 10, 10, 10, 5},
        {0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    public static final int[][] ROOK_OPENING_FLIPPED = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}
        };

    public static final int[][] ROOK_ENDGAME = {
        {0, 0, 0, 5, 5, 0, 0, 0},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {-5, 0, 0, 0, 0, 0, 0, -5},
        {10, 10, 10, 10, 10, 10, 10, 10},
        {0, 0, 0, 5, 5, 0, 0, 0}
    };
    
    public static final int[][] ROOK_ENDGAME_FLIPPED = {
            {0, 0, 0, 5, 5, 0, 0, 0},
            {10, 10, 10, 10, 10, 10, 10, 10},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}
        };

    public static final int[][] QUEEN_OPENING = {
        {-20, -10, -10, -5, -5, -10, -10, -20},
        {-10, 0, 0, 0, 0, 0, 0, -10},
        {-10, 0, 5, 5, 5, 5, 0, -10},
        {-5, 0, 5, 5, 5, 5, 0, -5},
        {0, 0, 5, 5, 5, 5, 0, -5},
        {-10, 5, 5, 5, 5, 5, 0, -10},
        {-10, 0, 5, 0, 0, 0, 0, -10},
        {-20, -10, -10, -5, -5, -10, -10, -20}
    };
    
    public static final int[][] QUEEN_OPENING_FLIPPED = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}
        };
    
    public static final int[][] QUEEN_ENDGAME = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 0, 5, 5, 0, 0, -10},
            {-5, 0, 5, 10, 10, 5, 0, -5},
            {0, 0, 5, 10, 10, 5, 0, -5},
            {-10, 0, 0, 5, 5, 0, 0, -10},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}
        };
    
    public static final int[][] QUEEN_ENDGAME_FLIPPED = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 0, 5, 5, 0, 0, -10},
            {0, 0, 5, 10, 10, 5, 0, -5},
            {-5, 0, 5, 10, 10, 5, 0, -5},
            {-10, 0, 0, 5, 5, 0, 0, -10},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}
        };


    public static final int[][] KING_OPENING = {
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-30, -40, -40, -50, -50, -40, -40, -30},
        {-20, -30, -30, -40, -40, -30, -30, -20},
        {-10, -20, -20, -20, -20, -20, -20, -10},
        {20, 20, 0, 0, 0, 0, 20, 20},
        {20, 30, 10, 0, 0, 10, 30, 20}
    };
    
    public static final int[][] KING_OPENING_FLIPPED = {
            {20, 30, 10, 0, 0, 10, 30, 20},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30}
        };

    public static final int[][] KING_ENDGAME = {
        {-50, -30, -30, -30, -30, -30, -30, -50},
        {-30, -10, 0, 0, 0, 0, -10, -30},
        {-30, 0, 10, 15, 15, 10, 0, -30},
        {-30, 0, 15, 20, 20, 15, 0, -30},
        {-30, 0, 15, 20, 20, 15, 0, -30},
        {-30, 0, 10, 15, 15, 10, 0, -30},
        {-30, -10, 0, 0, 0, 0, -10, -30},
        {-50, -30, -30, -30, -30, -30, -30, -50}
    };
    
    public static final int[][] KING_ENDGAME_FLIPPED = {
            {-50, -30, -30, -30, -30, -30, -30, -50},
            {-30, -10, 0, 0, 0, 0, -10, -30},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, -10, 0, 0, 0, 0, -10, -30},
            {-50, -30, -30, -30, -30, -30, -30, -50}
        };
    
    
    
    private void addToList(Piece p, boolean b) {
        
        if (b) {
                if (p instanceof Pawn) botPawns.add(p);
                else if (p instanceof Rook)botRooks.add(p);
                else if (p instanceof King) botKing = p;
        }else {
            if (p instanceof Pawn) playerPawns.add(p);
            else if (p instanceof Rook)playerRooks.add(p);
            else if (p instanceof King) playerKing = p;
            
        }    
    }
    
    private void initialize(Board b) {
        this.board = b;
        gamePhase = board.getGamePhase();
        botMoves = board.getMoves(true, false);
        playerMoves = board.getMoves(false, false);
        botPawns = new ArrayList<>();
        playerPawns = new ArrayList<>();
        botRooks = new ArrayList<>();
        playerRooks = new ArrayList<>();
        bishopCountBot = 0;
        bishopCountPlayer = 0;
    }
    
    List<Piece> botPawns;
    List<Piece> playerPawns;
    List<Piece> botRooks;
    List<Piece> playerRooks;

    Piece botKing;
    Piece playerKing;
    
    int bishopCountBot;
    int bishopCountPlayer;
    

}






