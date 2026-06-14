package com.ludoelite.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Centralised navigation utility.
 * All screen transitions are routed through here so controllers
 * never need direct references to each other.
 */
public class ViewNavigator {

    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    // ── Navigation helpers ───────────────────────────────────────────────────

    public static void navigateToLogin() {
        loadScene("/fxml/LoginView.fxml", "Ludo Elite – Login", 500, 620);
    }

    public static void navigateToRegister() {
        loadScene("/fxml/RegisterView.fxml", "Ludo Elite – Register", 520, 700);
    }

    public static void navigateToDashboard() {
        loadScene("/fxml/DashboardView.fxml", "Ludo Elite – Dashboard", 1200, 780);
    }

    public static void navigateToGameManagement() {
        loadScene("/fxml/GameManagementView.fxml", "Ludo Elite – Game Management", 1200, 780);
    }

    public static void navigateToLudoBoard() {
        loadScene("/fxml/LudoBoardView.fxml", "Ludo Elite – Game Board", 1400, 850);
    }
    
    /**
     * Navigate to Ludo Board with game context for multiplayer mode.
     * 
     * @param gameId The game ID from backend
     */
    public static void navigateToLudoBoard(String gameId) {
        loadSceneWithContext("/fxml/LudoBoardView.fxml", "Ludo Elite – Game Board", 
                            1400, 850, gameId);
    }

    // ── Internal loader ──────────────────────────────────────────────────────

    private static void loadScene(String fxmlPath, String title, double width, double height) {
        try {
            URL resource = ViewNavigator.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML not found: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);

            // Attach global stylesheet
            URL css = ViewNavigator.class.getResource("/css/styles.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            AlertHelper.showError("Navigation Error", "Cannot load screen: " + fxmlPath, e.getMessage());
        }
    }
    
    /**
     * Load scene with game context for LudoBoardController.
     */
    private static void loadSceneWithContext(String fxmlPath, String title, 
                                             double width, double height, String gameId) {
        try {
            URL resource = ViewNavigator.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML not found: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            
            // Get controller and set game context
            Object controller = loader.getController();
            if (controller instanceof com.ludoelite.controller.LudoBoardController) {
                ((com.ludoelite.controller.LudoBoardController) controller).setGameContext(gameId);
            }
            
            Scene scene = new Scene(root, width, height);

            // Attach global stylesheet
            URL css = ViewNavigator.class.getResource("/css/styles.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            AlertHelper.showError("Navigation Error", "Cannot load screen: " + fxmlPath, e.getMessage());
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
