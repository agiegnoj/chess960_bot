package boilerplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Stack;

public class Board {
    private Piece[][] board;
    private Stack<Move> lastMoves;   
    private List<Piece> botPieces;
    private List<Piece> playerPieces;  
    private King botKing;
    private King playerKing;
    
          
    public Board(char botColor, char playerColor) {
        board = new Piece[8][8]; 
        botPieces = new ArrayList<>();
        playerPieces = new ArrayList<>();
        setPieces(playerColor, botColor);
        lastMoves = new Stack<Move>();
    }
    

    private void setPieces(char playerColor, char botColor) {
       
        for (int i = 0; i < 8; i++) {
            Pawn pBot = new Pawn(botColor, true, 1, i, this);
            board[1][i] = pBot;

            Pawn pPlayer = new Pawn(playerColor, false, 6, i, this);
            board[6][i] = pPlayer;
        }
        
        boolean [] used = new boolean [8];

        Random rand = new Random();

        int bishop1 = rand.nextInt(4) * 2; 
        int bishop2 = rand.nextInt(4) * 2 + 1; 
        used[bishop1] = true;
        used[bishop2] = true;
        
        board[7][bishop1] = new Bishop(playerColor, false, 7, bishop1, this);
        board[0][bishop1] = new Bishop(botColor, true, 0, bishop1, this);
        board[7][bishop2] = new Bishop(playerColor, false, 7, bishop2, this);
        board[0][bishop2] = new Bishop(botColor, true, 0, bishop2, this);
        
        int queenPos = rand.nextInt(8);
        
        while (used[queenPos]) {
            if (queenPos < 7) {
                queenPos++;
            }else {
                queenPos = 0;
            }    
        }
        
        used[queenPos] = true;
        
        board[7][queenPos] = new Queen(playerColor,false, 7, queenPos, this);
        board[0][queenPos] = new Queen(botColor,true, 0, queenPos, this);
        
        for (int i = 0; i< 2; i++) {
            int knightPos = rand.nextInt(8);
            
            while (used[knightPos]) {
                if (knightPos < 7) {
                    knightPos++;
                }else {
                    knightPos = 0;
                }    
            } 
            
         used[knightPos] = true;
            
         board[7][knightPos] = new Knight(playerColor, false, 7, knightPos, this);
         board[0][knightPos] = new Knight(botColor, true, 0, knightPos, this);    
        }
        
        int[] remaining = new int[3];
        int index = 0;
        
        for (int i = 0; i< 8; i++) {
            if (!used[i])
            remaining[index++] = i;   
        }
        
        board[7][remaining[0]] = new Rook(playerColor, false, 7, remaining[0], this);
        board[0][remaining[0]] = new Rook(botColor, true, 0, remaining[0], this);  
        
        board[7][remaining[1]] = new King(playerColor, false, 7, remaining[1], this);
        playerKing = (King) board[7][remaining[1]];
        board[0][remaining[1]] = new King(botColor, true, 0, remaining[1], this); 
        botKing = (King) board[0][remaining[1]];
        board[7][remaining[2]] = new Rook(playerColor, false, 7, remaining[2], this);
        board[0][remaining[2]] = new Rook(botColor, true, 0, remaining[2], this);  
        
        
        for (int i = 0; i< 8; i++) {
            botPieces.add(board[0][i]);
            botPieces.add(board[1][i]);
            playerPieces.add(board[6][i]);
            playerPieces.add(board[7][i]);
        }
    }

   
    public List<Move> getMoves(boolean isBot, boolean inCheckCheck){
        List<Move> moves = new ArrayList<>();
        List<Piece> pieces = isBot ? botPieces : playerPieces;

        for (Piece p : pieces) {
            p.getValidMoves(moves);
        }
        
        if(inCheckCheck && isInCheck(true)) {
            List<Move> legalMoves = new ArrayList<>();

            for (Move move : moves) {
                makeMove(move);
                boolean stillInCheck = isInCheck(true);
                undoLastMove();

                if (!stillInCheck) {
                    legalMoves.add(move);
                }
            }
            
            return legalMoves.size() != 0 ? legalMoves : moves;
        }
        
        return moves;
    }
    
    public List<Piece> getPieces(boolean bot){
        return bot ? botPieces : playerPieces;
    }
    
    
    public void undoLastMove() {
        if (!lastMoves.isEmpty()) {
            Move lastMove = lastMoves.pop();
            int cX = lastMove.getCurrentX();
            int cY = lastMove.getCurrentY();
            int pX = lastMove.getPrevX();
            int pY = lastMove.getPrevY();
            
            Piece movedPiece = lastMove.getMovedPiece(); 
            Piece currentPiece = board[cX][cY]; 
            Piece capturedPiece = lastMove.getCapturedPiece();
            
           
            if (lastMove.isPromotion()) {
                if (currentPiece.isBotPiece()) {
                    botPieces.remove(currentPiece);
                    botPieces.add(movedPiece);
                } else {
                    playerPieces.remove(currentPiece);
                    playerPieces.add(movedPiece);
                }
            }
            
            board[cX][cY] = capturedPiece;
            if (board[cX][cY] != null) {
                board[cX][cY].setX(cX);
                board[cX][cY].setY(cY);
                if (board[cX][cY].isBotPiece()) {
                    botPieces.add(board[cX][cY]);
                } else {
                    playerPieces.add(board[cX][cY]);
                }
            }

            board[pX][pY] = movedPiece;
            movedPiece.setX(pX);
            movedPiece.setY(pY);           
            
        }
    }

    public void makeMove(Move move) {
        board[move.getPrevX()][move.getPrevY()] = null;
        Piece p = move.getCapturedPiece();
        
        if (p != null) {
            if (p.isBotPiece()) {
                botPieces.remove(p);
            }else {
                playerPieces.remove(p);
            }
        } 
        
        int x = move.getCurrentX();
        int y = move.getCurrentY();
        
        p = move.getMovedPiece(); 
        
        if ((p.isBotPiece() && p instanceof Pawn &&  x == 7)
                || (!p.isBotPiece() && p instanceof Pawn &&  x == 0)){
            move.setPromotion();
            promote(x, y, p.isBotPiece(), p);
        }else {
            p.setX(x);
            p.setY(y);
            board[x][y] = p;   
        }  
              
        lastMoves.add(move);            
    }
    
    private void promote(int x,int y, boolean bot, Piece pawn) {
        
        if (bot) {
            Queen q = new Queen(pawn.getColor(), true, x, y, this); 
            botPieces.remove(pawn);
            botPieces.add(q); 
            board[x][y] = q;
        }else {
            Queen q = new Queen(pawn.getColor(), false, x, y, this); 
            playerPieces.remove(pawn);
            playerPieces.add(q);   
            board[x][y] = q; 
        }  
    }
    
    public Piece getPiece(int x, int y) {
        return board[x][y];
    }


    public Piece[][] getBoard() {
        return board;
    }
    
    public int getGamePhase() {
        int totalPieces = botPieces.size() + playerPieces.size();
        
        return totalPieces > 25 ? 0 : totalPieces >= 10 ?  1 : 2;
    }
    
    
    public boolean isInCheck(boolean isBot) {
        
        List<Piece> enemyPieces = isBot ? playerPieces : botPieces;
        Piece king = null;

        for (Piece piece : isBot ? botPieces : playerPieces) {
            if (piece instanceof King) {
                king = piece;
                break;
            }
        }

        if (king == null) return false; 

        int kingX = king.getX();
        int kingY = king.getY();
        List<Move> possibleMoves = new ArrayList<>();

        for (Piece enemy : enemyPieces) {
            enemy.getValidMoves(possibleMoves);
        }

        for (Move move : possibleMoves) {
            if (move.getCurrentX() == kingX && move.getCurrentY() == kingY) {
                return true;
            }
        }

        return false;
    }

    
    public boolean checkMate(boolean botWin) {
        if (botWin) {
            return !playerPieces.contains(playerKing);
        }else {
            return !botPieces.contains(botKing);
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        for (Piece piece : botPieces) {
            hash ^= piece.hashCode(); 
        }
        
        for (Piece piece : playerPieces) {
            hash ^= piece.hashCode(); 
        }
        
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Board other = (Board) obj;
        return Arrays.deepEquals(board, other.board) && Objects.equals(botPieces, other.botPieces)
                && Objects.equals(lastMoves, other.lastMoves) && Objects.equals(playerPieces, other.playerPieces);
    }


    
}
