package com.ludoelite.view;

import com.ludoelite.model.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for rendering the Ludo board on a JavaFX Canvas.
 * 
 * Demonstrates:
 * - Single Responsibility: Only handles board rendering
 * - Encapsulation: Rendering logic is encapsulated
 * - Polymorphism: Uses GamePiece.render() polymorphically
 */
public class BoardRenderer {
    
    private static final int BOARD_SIZE = 600;
    private static final int CELL_SIZE = 40;
    private static final int PIECE_SIZE = 28;
    private static final Color BOARD_BG = Color.rgb(15, 23, 42);
    private static final Color TRACK_COLOR = Color.rgb(226, 232, 240);
    private static final Color SAFE_ZONE_COLOR = Color.rgb(245, 158, 11);
    
    // Track positions mapped to canvas coordinates
    private final Map<Integer, Position> trackPositions;
    
    public BoardRenderer() {
        this.trackPositions = new HashMap<>();
        initializeTrackPositions();
    }
    
    /**
     * Initializes all 52 track positions on the board.
     * This maps logical positions (0-51) to canvas coordinates.
     */
    private void initializeTrackPositions() {
        int center = BOARD_SIZE / 2;
        int offset = CELL_SIZE / 2;
        
        // Bottom row (Red starting area) - positions 0-5
        for (int i = 0; i < 6; i++) {
            trackPositions.put(i, new Position(
                offset + CELL_SIZE * (1 + i),
                center + CELL_SIZE * 3 + offset
            ));
        }
        
        // Left side going up - positions 6-12
        for (int i = 0; i < 7; i++) {
            trackPositions.put(6 + i, new Position(
                offset + CELL_SIZE,
                center + CELL_SIZE * (2 - i) + offset
            ));
        }
        
        // Top row (Green starting) - positions 13-18
        for (int i = 0; i < 6; i++) {
            trackPositions.put(13 + i, new Position(
                offset + CELL_SIZE * (2 + i),
                offset + CELL_SIZE
            ));
        }
        
        // Right side going up - positions 19-25
        for (int i = 0; i < 7; i++) {
            trackPositions.put(19 + i, new Position(
                center + CELL_SIZE * 2 + offset,
                offset + CELL_SIZE * (2 + i)
            ));
        }
        
        // Top row right side (Yellow starting) - positions 26-31
        for (int i = 0; i < 6; i++) {
            trackPositions.put(26 + i, new Position(
                center + CELL_SIZE * (1 + i) + offset,
                center - CELL_SIZE * 3 + offset
            ));
        }
        
        // Right side going down - positions 32-38
        for (int i = 0; i < 7; i++) {
            trackPositions.put(32 + i, new Position(
                center + CELL_SIZE * 2 + offset,
                center - CELL_SIZE * (2 - i) + offset
            ));
        }
        
        // Bottom row right side (Blue starting) - positions 39-44
        for (int i = 0; i < 6; i++) {
            trackPositions.put(39 + i, new Position(
                center + CELL_SIZE * (1 - i) + offset,
                center + CELL_SIZE * 2 + offset
            ));
        }
        
        // Left side going down - positions 45-51
        for (int i = 0; i < 7; i++) {
            trackPositions.put(45 + i, new Position(
                offset + CELL_SIZE,
                center + CELL_SIZE * (1 - i) + offset
            ));
        }
    }
    
    /**
     * Main render method. Draws complete board state.
     */
    public void render(GraphicsContext gc, GameEngine engine) {
        // Clear canvas
        gc.setFill(BOARD_BG);
        gc.fillRect(0, 0, BOARD_SIZE, BOARD_SIZE);
        
        // Draw board components
        drawBases(gc);
        drawTrack(gc);
        drawHomeLanes(gc);
        drawCenter(gc);
        
        // Draw all pieces (demonstrates Polymorphism)
        for (Player player : engine.getPlayers()) {
            for (GamePiece piece : player.getPieces()) {
                drawPiece(gc, piece);
            }
        }
        
        // Highlight current player
        highlightCurrentPlayer(gc, engine.getCurrentPlayer());
    }
    
    /**
     * Draws the four home bases.
     */
    private void drawBases(GraphicsContext gc) {
        int baseSize = CELL_SIZE * 6;
        int margin = 20;
        
        // Red base (bottom-left)
        drawBase(gc, margin, BOARD_SIZE - baseSize - margin, baseSize, PlayerColor.RED);
        
        // Green base (top-left)
        drawBase(gc, margin, margin, baseSize, PlayerColor.GREEN);
        
        // Yellow base (top-right)
        drawBase(gc, BOARD_SIZE - baseSize - margin, margin, baseSize, PlayerColor.YELLOW);
        
        // Blue base (bottom-right)
        drawBase(gc, BOARD_SIZE - baseSize - margin, BOARD_SIZE - baseSize - margin, 
                baseSize, PlayerColor.BLUE);
    }
    
    /**
     * Draws a single base.
     */
    private void drawBase(GraphicsContext gc, double x, double y, double size, PlayerColor color) {
        // Background
        gc.setFill(Color.web(color.getHexColor()).deriveColor(0, 1, 0.3, 1));
        gc.fillRoundRect(x, y, size, size, 15, 15);
        
        // Border
        gc.setStroke(Color.web(color.getHexColor()));
        gc.setLineWidth(3);
        gc.strokeRoundRect(x, y, size, size, 15, 15);
        
        // Draw 4 piece slots
        double slotSize = size * 0.3;
        double spacing = size * 0.2;
        
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                double slotX = x + spacing + col * (slotSize + spacing);
                double slotY = y + spacing + row * (slotSize + spacing);
                
                gc.setFill(Color.rgb(30, 41, 59));
                gc.fillOval(slotX, slotY, slotSize, slotSize);
                gc.setStroke(Color.web(color.getHexColor()));
                gc.setLineWidth(2);
                gc.strokeOval(slotX, slotY, slotSize, slotSize);
            }
        }
    }
    
    /**
     * Draws the main track (52 positions).
     */
    private void drawTrack(GraphicsContext gc) {
        for (int i = 0; i < 52; i++) {
            Position pos = trackPositions.get(i);
            if (pos != null) {
                boolean isSafe = BoardTrack.isSafeZone(i);
                
                // Draw cell
                gc.setFill(isSafe ? SAFE_ZONE_COLOR : TRACK_COLOR);
                gc.fillRect(pos.getX() - CELL_SIZE/2, pos.getY() - CELL_SIZE/2, 
                           CELL_SIZE, CELL_SIZE);
                
                // Border
                gc.setStroke(Color.rgb(51, 65, 85));
                gc.setLineWidth(1);
                gc.strokeRect(pos.getX() - CELL_SIZE/2, pos.getY() - CELL_SIZE/2, 
                             CELL_SIZE, CELL_SIZE);
                
                // Draw star on safe zones
                if (isSafe) {
                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.fillText("★", pos.getX(), pos.getY() + 5);
                }
            }
        }
    }
    
    /**
     * Draws the four home lanes leading to center.
     */
    private void drawHomeLanes(GraphicsContext gc) {
        int center = BOARD_SIZE / 2;
        
        // Draw each colored home lane
        drawHomeLane(gc, center - CELL_SIZE * 2, center + CELL_SIZE / 2, 
                    true, PlayerColor.RED);
        drawHomeLane(gc, center - CELL_SIZE / 2, center - CELL_SIZE * 2, 
                    false, PlayerColor.GREEN);
        drawHomeLane(gc, center + CELL_SIZE, center - CELL_SIZE / 2, 
                    true, PlayerColor.YELLOW);
        drawHomeLane(gc, center + CELL_SIZE / 2, center + CELL_SIZE, 
                    false, PlayerColor.BLUE);
    }
    
    /**
     * Draws a single home lane.
     */
    private void drawHomeLane(GraphicsContext gc, double startX, double startY, 
                             boolean horizontal, PlayerColor color) {
        gc.setFill(Color.web(color.getHexColor()).deriveColor(0, 1, 0.6, 1));
        
        for (int i = 0; i < 5; i++) {
            double x = horizontal ? startX + i * CELL_SIZE : startX;
            double y = horizontal ? startY : startY + i * CELL_SIZE;
            
            gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            gc.setStroke(Color.rgb(51, 65, 85));
            gc.setLineWidth(1);
            gc.strokeRect(x, y, CELL_SIZE, CELL_SIZE);
        }
    }
    
    /**
     * Draws the center finish area.
     */
    private void drawCenter(GraphicsContext gc) {
        int center = BOARD_SIZE / 2;
        int size = CELL_SIZE * 2;
        
        // Draw triangular sections for each color
        gc.setFill(Color.rgb(30, 41, 59));
        gc.fillRect(center - size/2, center - size/2, size, size);
        
        // Gold border
        gc.setStroke(Color.web("#f59e0b"));
        gc.setLineWidth(3);
        gc.strokeRect(center - size/2, center - size/2, size, size);
        
        // Center circle
        gc.setFill(Color.web("#f59e0b"));
        gc.fillOval(center - 15, center - 15, 30, 30);
    }
    
    /**
     * Draws a piece using polymorphism.
     * 
     * Demonstrates: Polymorphism (calls render() on GamePiece, which may be
     * RedPiece, BluePiece, GreenPiece, or YellowPiece)
     */
    private void drawPiece(GraphicsContext gc, GamePiece piece) {
        Position pos = getPiecePosition(piece);
        if (pos != null) {
            // Polymorphic call - each piece type renders differently
            piece.render(gc, 
                        pos.getX() - PIECE_SIZE / 2, 
                        pos.getY() - PIECE_SIZE / 2, 
                        PIECE_SIZE);
        }
    }
    
    /**
     * Gets the canvas position for a piece based on its state.
     */
    private Position getPiecePosition(GamePiece piece) {
        PieceState state = piece.getState();
        
        if (state == PieceState.IN_BASE) {
            return getBasePosition(piece);
        } else if (state == PieceState.ON_TRACK) {
            int absPos = BoardTrack.getAbsolutePosition(
                piece.getOwnerColor(), piece.getTrackPosition()
            );
            return trackPositions.get(absPos);
        } else if (state == PieceState.IN_HOME_LANE) {
            return getHomeLanePosition(piece);
        } else if (state == PieceState.FINISHED) {
            return new Position(BOARD_SIZE / 2, BOARD_SIZE / 2);
        }
        
        return null;
    }
    
    /**
     * Gets position for piece in base.
     */
    private Position getBasePosition(GamePiece piece) {
        int baseSize = CELL_SIZE * 6;
        int margin = 20;
        int slotSize = (int)(baseSize * 0.3);
        int spacing = (int)(baseSize * 0.2);
        
        int pieceNum = piece.getPieceNumber();
        int row = pieceNum / 2;
        int col = pieceNum % 2;
        
        int baseX = 0, baseY = 0;
        
        switch (piece.getOwnerColor()) {
            case RED:
                baseX = margin;
                baseY = BOARD_SIZE - baseSize - margin;
                break;
            case GREEN:
                baseX = margin;
                baseY = margin;
                break;
            case YELLOW:
                baseX = BOARD_SIZE - baseSize - margin;
                baseY = margin;
                break;
            case BLUE:
                baseX = BOARD_SIZE - baseSize - margin;
                baseY = BOARD_SIZE - baseSize - margin;
                break;
        }
        
        int x = baseX + spacing + col * (slotSize + spacing) + slotSize / 2;
        int y = baseY + spacing + row * (slotSize + spacing) + slotSize / 2;
        
        return new Position(x, y);
    }
    
    /**
     * Gets position for piece in home lane.
     */
    private Position getHomeLanePosition(GamePiece piece) {
        int center = BOARD_SIZE / 2;
        int pos = piece.getTrackPosition();
        
        switch (piece.getOwnerColor()) {
            case RED:
                return new Position(
                    center - CELL_SIZE * 2 + pos * CELL_SIZE + CELL_SIZE / 2,
                    center + CELL_SIZE / 2 + CELL_SIZE / 2
                );
            case GREEN:
                return new Position(
                    center - CELL_SIZE / 2 + CELL_SIZE / 2,
                    center - CELL_SIZE * 2 + pos * CELL_SIZE + CELL_SIZE / 2
                );
            case YELLOW:
                return new Position(
                    center + CELL_SIZE + pos * CELL_SIZE + CELL_SIZE / 2,
                    center - CELL_SIZE / 2 + CELL_SIZE / 2
                );
            case BLUE:
                return new Position(
                    center + CELL_SIZE / 2 + CELL_SIZE / 2,
                    center + CELL_SIZE + pos * CELL_SIZE + CELL_SIZE / 2
                );
        }
        
        return null;
    }
    
    /**
     * Highlights the current player's area.
     */
    private void highlightCurrentPlayer(GraphicsContext gc, Player player) {
        if (player == null) return;
        
        // Draw glow effect around current player's name area
        gc.setStroke(Color.web("#f59e0b"));
        gc.setLineWidth(3);
        
        // This will be used in the controller to highlight UI elements
    }
    
    public int getBoardSize() {
        return BOARD_SIZE;
    }
}
