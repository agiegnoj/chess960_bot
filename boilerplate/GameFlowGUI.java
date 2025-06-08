package boilerplate;

import javax.swing.*;

import ai.AlphaBetaSearch;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameFlowGUI {
    private Board board;
    private boolean playersTurn;
    private char playerColor;
    private char botColor;
    private AlphaBetaSearch alphaBeta;
    private JFrame frame;
    private JPanel panel;
    private JButton[][] buttons;
    private Piece selectedPiece;
    private JButton restartButton; 
    private int lastBotMoveX;
    private int lastBotMoveY;

    boolean checkmate;

    public static void main(String[] args) {
        new GameFlowGUI();
    }

    public GameFlowGUI() {
        selectDifficulty();
    }
    
    void selectDifficulty(){
        frame = new JFrame("Chess 960 Bot");
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel tempPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton easyButton = new JButton("start");;
        
        easyButton.setBackground(Color.WHITE);
        easyButton.setFont(new Font("Arial", Font.PLAIN, 30));
          
        easyButton.addActionListener(e -> initialize(8));
        tempPanel.add(easyButton);

        frame.add(tempPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        
    }
    

    void initialize (int depth) {
       
        frame = new JFrame("Chess 960 Bot");
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
   
        Random r = new Random();
        int num = r.nextInt(2);
        playerColor = (num == 1) ? 'b' : 'w';
        if (playerColor == 'b') {
            botColor = 'w';
            playersTurn = true;
        } else {
            botColor = 'b';
            playersTurn = false;
        }

        board = new Board(playerColor, botColor);
        alphaBeta = new AlphaBetaSearch(depth, board);
        lastBotMoveX = -1;
        lastBotMoveY = -1;
        
        panel = new JPanel();
        panel.setLayout(new GridLayout(9, 9)); 
        
        buttons = new JButton[8][8];
        
        initializeBoard();
        
        restartButton = new JButton("Restart");
        restartButton.setPreferredSize(new Dimension(50, 50));
        restartButton.setLocation(700, 350);
        restartButton.setBackground(Color.WHITE);
        restartButton.setFont(new Font("Arial", Font.PLAIN, 15));
        
        
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame(); 
            }
        });
        panel.add(restartButton); 
        
        frame.add(panel);
        frame.setVisible(true);

        if (!playersTurn) {
            SwingUtilities.invokeLater(this::botPlay);
        }
        
    }


    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setPreferredSize(new Dimension(80, 80));
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setFont(new Font("Arial Unicode MS", Font.PLAIN, 50));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setEnabled(true);

                int x = i;
                int y = j;

                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handlePlayerMove(x, y);
                    }
                });

                panel.add(buttons[i][j]);
            }
        }
        updateBoardDisplay();
    }

    private void handlePlayerMove(int x, int y) {
        if (checkmate)return;
        if (selectedPiece == null) {
            Piece piece = board.getPiece(x, y);
            if (piece != null && !piece.isBotPiece()) {
                selectedPiece = piece;
                buttons[x][y].setBackground(Color.GREEN);
            }
        } else {
            
            if (selectedPiece.isValidMove(x, y)) {
                
                Move m = new Move(selectedPiece, board.getPiece(x, y), x, y, selectedPiece.getX(), selectedPiece.getY());
                board.makeMove(m);
                selectedPiece = null;
                lastBotMoveX = -1;
                updateBoardDisplay();
                
                if (board.checkMate(false)) {
                    endScreen();
                    checkmate = true;
                    return; 
                }
               
                playersTurn = !playersTurn;

                botPlay();
            } else {
                selectedPiece = null;
                updateBoardDisplay();
            }
        }
    }

    private void updateBoardDisplay() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i %2 == 1 ^ j % 2 == 1)) {
                    buttons[i][j].setBackground(Color.WHITE);    
                }else {
                    buttons[i][j].setBackground(Color.LIGHT_GRAY);
                }              
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.WHITE));

                Piece piece = board.getPiece(i, j);
                if (piece != null) {
                    buttons[i][j].setText(piece.getIdentifier());
                }else {
                    buttons[i][j].setText("");
                }
            }
        }
        if (lastBotMoveX != -1)
        buttons[lastBotMoveX][lastBotMoveY].setBackground(Color.cyan);
    }
    

    private void botPlay() {
        if (checkmate)return;
        Move m = alphaBeta.getBestMove();

        if (m != null) {
            board.makeMove(m); 
            lastBotMoveX = m.getCurrentX();
            lastBotMoveY = m.getCurrentY();
           }
        
        updateBoardDisplay();
        if (board.checkMate(true)) {
            endScreen();
            checkmate = true;
            return; 
        }
            
        playersTurn = true;
    }


    private void endScreen() {
        JButton checkMateButton = new JButton("Checkmate");
        checkMateButton.setPreferredSize(new Dimension(50, 50));
        checkMateButton.setLocation(700, 700);
        checkMateButton.setBackground(Color.WHITE);
        checkMateButton.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(checkMateButton); 
        
        frame.add(panel);
    }

    private void restartGame() {
        selectDifficulty();
    }
}

