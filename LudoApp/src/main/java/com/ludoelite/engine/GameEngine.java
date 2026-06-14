package com.ludoelite.engine;

import com.ludoelite.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game engine that handles all Ludo game logic and rules.
 * 
 * Demonstrates:
 * - Encapsulation: Game state is encapsulated and modified through methods
 * - Single Responsibility: Handles only game logic (not UI)
 * - Composition: Composed of Players, Dice, etc.
 */
public class GameEngine {
    
    private final List<Player> players;
    private final Dice dice;
    private Player currentPlayer;
    private int currentPlayerIndex;
    private GameState gameState;
    private int consecutiveSixes;
    private boolean hasRolledDice;
    private int lastDiceValue;
    
    public GameEngine() {
        this.players = new ArrayList<>();
        this.dice = new Dice();
        this.currentPlayerIndex = 0;
        this.gameState = GameState.NOT_STARTED;
        this.consecutiveSixes = 0;
        this.hasRolledDice = false;
        this.lastDiceValue = 0;
    }
    
    // ── Game Setup ───────────────────────────────────────────────────────────
    
    /**
     * Adds a player to the game.
     */
    public void addPlayer(Player player) {
        if (players.size() < 4) {
            players.add(player);
        }
    }
    
    /**
     * Starts the game. Must have at least 2 players.
     */
    public boolean startGame() {
        if (players.size() < 2) {
            return false;
        }
        
        gameState = GameState.IN_PROGRESS;
        currentPlayerIndex = 0;
        currentPlayer = players.get(0);
        currentPlayer.setCurrentTurn(true);
        hasRolledDice = false;
        
        return true;
    }
    
    // ── Dice Rolling ─────────────────────────────────────────────────────────
    
    /**
     * Rolls the dice for the current player.
     */
    public int rollDice() {
        if (gameState != GameState.IN_PROGRESS || hasRolledDice) {
            return -1;
        }
        
        lastDiceValue = dice.roll();
        hasRolledDice = true;
        
        // Track consecutive sixes
        if (lastDiceValue == 6) {
            consecutiveSixes++;
        } else {
            consecutiveSixes = 0;
        }
        
        // Three consecutive sixes = skip turn
        if (consecutiveSixes >= 3) {
            consecutiveSixes = 0;
            nextTurn();
            return 0; // Indicate turn skipped
        }
        
        return lastDiceValue;
    }
    
    // ── Piece Movement ───────────────────────────────────────────────────────
    
    /**
     * Attempts to move a piece. Returns true if move was valid and executed.
     */
    public boolean movePiece(GamePiece piece, int steps) {
        if (!hasRolledDice || piece == null) {
            return false;
        }
        
        // Check if piece belongs to current player
        if (piece.getOwnerColor() != currentPlayer.getColor()) {
            return false;
        }
        
        // Handle piece in base
        if (piece.getState() == PieceState.IN_BASE) {
            if (steps == 6) {
                piece.setState(PieceState.ON_TRACK);
                piece.setTrackPosition(0);
                
                // Check for capture at starting position
                checkAndCaptureAt(piece, 0);
                
                finishMove(steps);
                return true;
            }
            return false;
        }
        
        // Handle piece on track or in home lane
        if (piece.getState() == PieceState.ON_TRACK || 
            piece.getState() == PieceState.IN_HOME_LANE) {
            
            int newPosition = piece.getTrackPosition() + steps;
            
            // Check if entering home lane
            if (piece.getState() == PieceState.ON_TRACK) {
                int homeEntry = BoardTrack.getHomeEntryPosition(piece.getOwnerColor());
                int absolutePos = BoardTrack.getAbsolutePosition(
                    piece.getOwnerColor(), piece.getTrackPosition()
                );
                
                if (BoardTrack.shouldEnterHomeLane(piece.getOwnerColor(), absolutePos)) {
                    piece.setState(PieceState.IN_HOME_LANE);
                    piece.setTrackPosition(0);
                    newPosition = steps;
                }
            }
            
            // Check if finishing
            if (piece.getState() == PieceState.IN_HOME_LANE && 
                newPosition >= BoardTrack.HOME_LANE_SIZE) {
                if (newPosition == BoardTrack.HOME_LANE_SIZE) {
                    piece.setState(PieceState.FINISHED);
                    piece.setTrackPosition(BoardTrack.HOME_LANE_SIZE);
                    
                    // Check win condition
                    if (currentPlayer.hasWon()) {
                        gameState = GameState.FINISHED;
                    }
                    
                    finishMove(steps);
                    return true;
                } else {
                    // Exact count needed to finish
                    return false;
                }
            }
            
            // Normal move
            piece.setTrackPosition(newPosition);
            
            // Check for capture (only on main track)
            if (piece.getState() == PieceState.ON_TRACK) {
                checkAndCaptureAt(piece, newPosition);
            }
            
            finishMove(steps);
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks if there's an opponent piece at the position and captures it.
     */
    private void checkAndCaptureAt(GamePiece movingPiece, int position) {
        int absolutePos = BoardTrack.getAbsolutePosition(
            movingPiece.getOwnerColor(), position
        );
        
        // Safe zones prevent capture
        if (BoardTrack.isSafeZone(absolutePos)) {
            return;
        }
        
        // Check all opponent pieces
        for (Player player : players) {
            if (player.getColor() == movingPiece.getOwnerColor()) {
                continue;
            }
            
            for (GamePiece opponentPiece : player.getPieces()) {
                if (opponentPiece.getState() == PieceState.ON_TRACK) {
                    int opponentAbsPos = BoardTrack.getAbsolutePosition(
                        opponentPiece.getOwnerColor(), 
                        opponentPiece.getTrackPosition()
                    );
                    
                    if (opponentAbsPos == absolutePos) {
                        // Capture!
                        opponentPiece.sendToBase();
                    }
                }
            }
        }
    }
    
    /**
     * Completes the move and handles turn logic.
     */
    private void finishMove(int diceValue) {
        hasRolledDice = false;
        
        // If rolled 6, player gets another turn
        if (diceValue != 6) {
            nextTurn();
        }
    }
    
    // ── Turn Management ──────────────────────────────────────────────────────
    
    /**
     * Advances to the next player's turn.
     */
    private void nextTurn() {
        if (currentPlayer != null) {
            currentPlayer.setCurrentTurn(false);
        }
        
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.setCurrentTurn(true);
        hasRolledDice = false;
        lastDiceValue = 0;
    }
    
    /**
     * Skips the current player's turn (e.g., no valid moves).
     */
    public void skipTurn() {
        nextTurn();
    }
    
    // ── Getters ──────────────────────────────────────────────────────────────
    
    public List<Player> getPlayers() {
        return new ArrayList<>(players); // Return copy
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public GameState getGameState() {
        return gameState;
    }
    
    public Dice getDice() {
        return dice;
    }
    
    public int getLastDiceValue() {
        return lastDiceValue;
    }
    
    public boolean hasRolledDice() {
        return hasRolledDice;
    }
    
    /**
     * Gets moveable pieces for current player with last dice value.
     */
    public List<GamePiece> getMoveablePieces() {
        if (currentPlayer == null || !hasRolledDice) {
            return new ArrayList<>();
        }
        return currentPlayer.getMoveablePieces(lastDiceValue);
    }
    
    /**
     * Checks if current player has any valid moves.
     */
    public boolean hasValidMoves() {
        return !getMoveablePieces().isEmpty();
    }
}
