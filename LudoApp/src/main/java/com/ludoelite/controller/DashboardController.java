package com.ludoelite.controller;

import com.ludoelite.model.Game;
import com.ludoelite.service.GameService;
import com.ludoelite.util.SessionManager;
import com.ludoelite.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for DashboardView.fxml.
 * Shows a summary table of active games and sidebar navigation.
 */
public class DashboardController extends BaseController implements Initializable {

    // ── Sidebar ───────────────────────────────────────────────────────────────
    @FXML private Label            welcomeLabel;

    // ── Summary TableView ─────────────────────────────────────────────────────
    @FXML private TableView<Game>        gamesTable;
    @FXML private TableColumn<Game, Long>   colId;
    @FXML private TableColumn<Game, String> colName;
    @FXML private TableColumn<Game, String> colStatus;
    @FXML private TableColumn<Game, String> colHost;
    @FXML private TableColumn<Game, Integer> colPlayers;

    @FXML private ProgressIndicator spinner;
    @FXML private Text              statsTotal;
    @FXML private Text              statsWaiting;
    @FXML private Text              statsPlaying;

    private final GameService gameService = new GameService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Greet the current user
        String name = SessionManager.getInstance().getPersistedUsername();
        welcomeLabel.setText("Welcome back, " + (name.isBlank() ? "Player" : name) + "!");

        // Wire up table columns to Game properties
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("gameName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colHost.setCellValueFactory(new PropertyValueFactory<>("hostUsername"));
        colPlayers.setCellValueFactory(new PropertyValueFactory<>("currentPlayers"));

        // Colour-code status column
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setText(null); setStyle(""); return; }
                setText(status);
                switch (status) {
                    case "WAITING"     -> setStyle("-fx-text-fill: #f0a500; -fx-font-weight: bold;");
                    case "IN_PROGRESS" -> setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    case "FINISHED"    -> setStyle("-fx-text-fill: #95a5a6;");
                    default            -> setStyle("");
                }
            }
        });

        loadGames();
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void loadGames() {
        runAsync(
            () -> gameService.findAll(),
            games -> {
                gamesTable.setItems(FXCollections.observableArrayList(games));
                updateStats(games);
            },
            ex -> showError("Failed to load games", ex.getMessage()),
            spinner
        );
    }

    private void updateStats(List<Game> games) {
        long waiting   = games.stream().filter(g -> "WAITING".equals(g.getStatus())).count();
        long inProgress = games.stream().filter(g -> "IN_PROGRESS".equals(g.getStatus())).count();
        statsTotal.setText(String.valueOf(games.size()));
        statsWaiting.setText(String.valueOf(waiting));
        statsPlaying.setText(String.valueOf(inProgress));
    }

    // ── Sidebar navigation ────────────────────────────────────────────────────

    @FXML
    private void handleManageGames() {
        ViewNavigator.navigateToGameManagement();
    }

    @FXML
    private void handlePlayGame() {
        // Navigate to game board
        ViewNavigator.navigateToLudoBoard();
    }

    @FXML
    private void handleRefresh() {
        loadGames();
    }

    @FXML
    private void handleLogout() {
        if (confirmAction("Logout", "Are you sure you want to logout?")) {
            SessionManager.getInstance().clearSession();
            ViewNavigator.navigateToLogin();
        }
    }
}
