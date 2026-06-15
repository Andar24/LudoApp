package com.ludoelite.controller;

import com.ludoelite.model.Game;
// Backend service commented for local mode
// import com.ludoelite.service.GameService;
import com.ludoelite.util.Validator;
import com.ludoelite.util.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Full CRUD controller for GameManagementView.fxml.
 * Handles Create, Read (table), Update, and Delete of Game entities.
 *
 * ⚠️ DISABLED FOR LOCAL MODE - Backend not available
 * This screen is not accessible in local mode.
 *
 * Demonstrates: Encapsulation, Inheritance (extends BaseController),
 *               event-driven programming, input validation.
 */
public class GameManagementController extends BaseController implements Initializable {

    // ── TableView ─────────────────────────────────────────────────────────────
    @FXML private TableView<Game>            gameTable;
    @FXML private TableColumn<Game, Long>    colId;
    @FXML private TableColumn<Game, String>  colName;
    @FXML private TableColumn<Game, String>  colStatus;
    @FXML private TableColumn<Game, Integer> colMax;
    @FXML private TableColumn<Game, Integer> colCurrent;
    @FXML private TableColumn<Game, String>  colHost;

    // ── Form fields ───────────────────────────────────────────────────────────
    @FXML private TextField  fieldGameName;
    @FXML private ComboBox<String> comboStatus;
    @FXML private TextField  fieldMaxPlayers;
    @FXML private TextField  fieldHostUsername;
    @FXML private Label      formTitleLabel;

    // ── Buttons ───────────────────────────────────────────────────────────────
    @FXML private Button     btnSave;
    @FXML private Button     btnClear;
    @FXML private Button     btnDelete;

    @FXML private ProgressIndicator spinner;
    @FXML private TextField         searchField;

    // Backend services commented for local mode
    // private final GameService            gameService = new GameService();
    private final ObservableList<Game>   gameList    = FXCollections.observableArrayList();
    private Game                         selectedGame = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Show local mode message
        gameTable.setPlaceholder(new Label("🎮 Local Mode - Game Management Not Available\n\nUse 'Play Game' button in Dashboard to play!"));

        // Disable all form controls for local mode
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        btnClear.setDisable(true);
        spinner.setVisible(false);

        // Original initialization code (commented for local mode)
        /*
        // Table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("gameName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colMax.setCellValueFactory(new PropertyValueFactory<>("maxPlayers"));
        colCurrent.setCellValueFactory(new PropertyValueFactory<>("currentPlayers"));
        colHost.setCellValueFactory(new PropertyValueFactory<>("hostUsername"));

        gameTable.setItems(gameList);

        // Row selection → populate form
        gameTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> onRowSelected(newVal)
        );

        // ComboBox options
        comboStatus.setItems(FXCollections.observableArrayList(
                "WAITING", "IN_PROGRESS", "FINISHED"
        ));

        // Live search
        searchField.textProperty().addListener((obs, old, val) -> filterTable(val));

        btnDelete.setDisable(true);
        loadAllGames();
        */
    }

    // ── CRUD handlers (disabled for local mode) ───────────────────────────────

    @FXML
    private void handleSave() {
        showInfo("Local Mode", "Game management requires backend server.\n\nUse 'Play Game' in Dashboard for local gameplay!");

        // Original code commented for local mode
        /*
        if (!validateForm()) return;

        Game game = buildGameFromForm();

        if (selectedGame == null) {
            // ── CREATE ──────────────────────────────────────────────────────
            runAsync(
                () -> gameService.create(game),
                created -> {
                    gameList.add(created);
                    clearForm();
                    showInfo("Game Created", "\"" + created.getGameName() + "\" has been added.");
                },
                ex -> showError("Create Failed", ex.getMessage()),
                spinner
            );
        } else {
            // ── UPDATE ──────────────────────────────────────────────────────
            runAsync(
                () -> gameService.update(selectedGame.getId(), game),
                updated -> {
                    int idx = gameList.indexOf(selectedGame);
                    if (idx >= 0) gameList.set(idx, updated);
                    clearForm();
                    showInfo("Game Updated", "\"" + updated.getGameName() + "\" has been updated.");
                },
                ex -> showError("Update Failed", ex.getMessage()),
                spinner
            );
        }
        */
    }

    @FXML
    private void handleDelete() {
        showInfo("Local Mode", "Game management requires backend server.");

        // Original code commented for local mode
        /*
        if (selectedGame == null) return;
        if (!confirmAction("Delete Game",
                "Delete \"" + selectedGame.getGameName() + "\"? This cannot be undone.")) {
            return;
        }

        runAsync(
            () -> { gameService.delete(selectedGame.getId()); return null; },
            ignored -> {
                gameList.remove(selectedGame);
                clearForm();
                showInfo("Deleted", "Game was successfully deleted.");
            },
            ex -> showError("Delete Failed", ex.getMessage()),
            spinner
        );
        */
    }

    @FXML
    private void handleClear() {
        // Disabled for local mode
        showInfo("Local Mode", "Game management not available without backend.");
    }

    @FXML
    private void handleRefresh() {
        // Disabled for local mode
        showInfo("Local Mode", "No games to refresh in local mode.\n\nUse Dashboard → Play Game to start playing!");
    }

    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }

    /**
     * Handles Join button click - disabled in local mode.
     */
    @FXML
    private void handleJoinGame() {
        showInfo("Local Mode", "Join functionality requires backend.\n\nUse Dashboard → Play Game for local gameplay!");

        // Original code commented
        /*
        Game selected = gameTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Game Selected", "Please select a game to join.");
            return;
        }

        if (!"WAITING".equals(selected.getStatus())) {
            showWarning("Cannot Join", "This game is not accepting players.");
            return;
        }

        // Navigate to game board with game ID
        navigateToGameBoard(String.valueOf(selected.getId()));
        */
    }

    /**
     * Handles Play button click - disabled in local mode.
     */
    @FXML
    private void handlePlayGame() {
        showInfo("Local Mode", "Use Dashboard → Play Game for local gameplay!");

        // Original code commented
        /*
        Game selected = gameTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Game Selected", "Please select a game to play.");
            return;
        }

        if ("FINISHED".equals(selected.getStatus())) {
            showWarning("Game Finished", "This game has already finished.");
            return;
        }

        // Navigate to game board with game ID
        navigateToGameBoard(String.valueOf(selected.getId()));
        */
    }

    /**
     * Navigates to the game board view with game context (disabled for local mode).
     */
    private void navigateToGameBoard(String gameId) {
        // Disabled for local mode
        ViewNavigator.navigateToDashboard();
    }

    // ── Data loading (disabled for local mode) ────────────────────────────────

    private void loadAllGames() {
        // Disabled for local mode

        // Original code commented
        /*
        runAsync(
            () -> gameService.findAll(),
            games -> {
                gameList.setAll(games);
                gameTable.refresh();
            },
            ex -> showError("Load Failed", ex.getMessage()),
            spinner
        );
        */
    }

    // ── Form helpers (disabled for local mode) ────────────────────────────────

    private void onRowSelected(Game game) {
        // Disabled for local mode
    }

    private void clearForm() {
        // Disabled for local mode
    }

    private boolean validateForm() {
        // Disabled for local mode
        return false;
    }

    private Game buildGameFromForm() {
        // Disabled for local mode
        return null;
    }

    // ── Search/filter (disabled for local mode) ───────────────────────────────

    private void filterTable(String query) {
        // Disabled for local mode
    }
}