package com.ludoelite.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Ludo game.
 * 
 * Demonstrates:
 * - Encapsulation: Private fields with controlled access
 * - Composition: Contains GamePiece objects
 */
public class Player {
    
    private final PlayerColor color;
    private final String playerName;
    private final List<GamePiece> pieces;
    private boolean isCurrentTurn;
    
    public Player(PlayerColor color, String playerName) {
        this.color = color;
        this.playerName = playerName;
        this.pieces = new ArrayList<>();
        this.isCurrentTurn = false;
        
        // Initialize 4 pieces for this player using Factory Pattern
        // Demonstrates: Polymorphism (pieces stored as GamePiece but are specific types)
        for (int i = 0; i < 4; i++) {
            pieces.add(createPiece(color, i));
        }
    }
    
    /**
     * Factory method to create pieces based on color.
     * 
     * Demonstrates: Polymorphism (returns different concrete types as GamePiece)
     */
    private GamePiece createPiece(PlayerColor color, int pieceNumber) {
        switch (color) {
            case RED:    return new RedPiece(pieceNumber);
            case BLUE:   return new BluePiece(pieceNumber);
            case GREEN:  return new GreenPiece(pieceNumber);
            case YELLOW: return new YellowPiece(pieceNumber);
            default:     throw new IllegalArgumentException("Unknown color: " + color);
        }
    }
    
    // ── Getters ──────────────────────────────────────────────────────────────
    
    public PlayerColor getColor() {
        return color;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public List<GamePiece> getPieces() {
        return new ArrayList<>(pieces); // Return copy for encapsulation
    }
    
    public GamePiece getPiece(int index) {
        if (index >= 0 && index < pieces.size()) {
            return pieces.get(index);
        }
        return null;
    }
    
    public boolean isCurrentTurn() {
        return isCurrentTurn;
    }
    
    public void setCurrentTurn(boolean currentTurn) {
        isCurrentTurn = currentTurn;
    }
    
    /**
     * Checks if this player has won (all pieces finished).
     */
    public boolean hasWon() {
        return pieces.stream()
                     .allMatch(piece -> piece.getState() == PieceState.FINISHED);
    }
    
    /**
     * Gets count of pieces still in base.
     */
    public int getPiecesInBase() {
        return (int) pieces.stream()
                           .filter(piece -> piece.getState() == PieceState.IN_BASE)
                           .count();
    }
    
    /**
     * Gets moveable pieces (not in base, not finished).
     */
    public List<GamePiece> getMoveablePieces(int diceValue) {
        List<GamePiece> moveable = new ArrayList<>();
        
        for (GamePiece piece : pieces) {
            if (piece.getState() == PieceState.IN_BASE && diceValue == 6) {
                moveable.add(piece);
            } else if (piece.getState() != PieceState.IN_BASE && 
                       piece.getState() != PieceState.FINISHED) {
                moveable.add(piece);
            }
        }
        
        return moveable;
    }
    
    @Override
    public String toString() {
        return color.getDisplayName() + " Player: " + playerName;
    }
}
