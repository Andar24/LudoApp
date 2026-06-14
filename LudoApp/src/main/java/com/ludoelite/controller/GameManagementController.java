package com.ludoelite.controller;

import com.ludoelite.model.Game;
import com.ludoelite.service.GameService;
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

    private final GameService            gameService = new GameService();
    private final ObservableList<Game>   gameList    = FXCollections.observableArrayList();
    private Game                         selectedGame = null;   // null → create mode

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    }

    // ── CRUD handlers ─────────────────────────────────────────────────────────

    @FXML
    private void handleSave() {
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
    }

    @FXML
    private void handleDelete() {
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
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleRefresh() {
        loadAllGames();
    }

    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToDashboard();
    }
    
    /**
     * Handles Join button click - navigates to game board.
     */
    @FXML
    private void handleJoinGame() {
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
    }
    
    /**
     * Handles Play button click - navigates to game board for in-progress game.
     */
    @FXML
    private void handlePlayGame() {
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
    }
    
    /**
     * Navigates to the game board view with game context.
     */
    private void navigateToGameBoard(String gameId) {
        try {
            ViewNavigator.navigateToLudoBoard(gameId);
        } catch (Exception e) {
            showError("Navigation Error", "Failed to open game board: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void loadAllGames() {
        runAsync(
            () -> gameService.findAll(),
            games -> {
                gameList.setAll(games);
                gameTable.refresh();
            },
            ex -> showError("Load Failed", ex.getMessage()),
            spinner
        );
    }

    // ── Form helpers ──────────────────────────────────────────────────────────

    private void onRowSelected(Game game) {
        selectedGame = game;
        if (game == null) {
            clearForm();
            return;
        }
        formTitleLabel.setText("Edit Game");
        fieldGameName.setText(game.getGameName());
        comboStatus.setValue(game.getStatus());
        fieldMaxPlayers.setText(String.valueOf(game.getMaxPlayers()));
        fieldHostUsername.setText(game.getHostUsername());
        btnDelete.setDisable(false);
        btnSave.setText("Update");
    }

    private void clearForm() {
        selectedGame = null;
        formTitleLabel.setText("New Game");
        fieldGameName.clear();
        comboStatus.setValue(null);
        fieldMaxPlayers.clear();
        fieldHostUsername.clear();
        btnDelete.setDisable(true);
        btnSave.setText("Create");
        gameTable.getSelectionModel().clearSelection();

        // Remove validation error styles
        Validator.markField(fieldGameName,     true);
        Validator.markField(fieldMaxPlayers,   true);
        Validator.markField(fieldHostUsername, true);
    }

    private boolean validateForm() {
        String nameErr    = Validator.requireNonBlank(fieldGameName.getText(), "Game Name");
        String maxErr     = Validator.validateIntRange(fieldMaxPlayers.getText(), "Max Players", 2, 4);
        String hostErr    = Validator.requireNonBlank(fieldHostUsername.getText(), "Host Username");
        String statusErr  = (comboStatus.getValue() == null) ? "Please select a status." : null;

        Validator.markField(fieldGameName,     nameErr == null);
        Validator.markField(fieldMaxPlayers,   maxErr  == null);
        Validator.markField(fieldHostUsername, hostErr == null);

        String first = nameErr != null ? nameErr
                     : maxErr  != null ? maxErr
                     : hostErr != null ? hostErr
                     : statusErr;
        if (first != null) { showWarning("Validation Error", first); return false; }
        return true;
    }

    private Game buildGameFromForm() {
        Game game = new Game();
        game.setGameName(fieldGameName.getText().trim());
        game.setStatus(comboStatus.getValue());
        game.setMaxPlayers(Integer.parseInt(fieldMaxPlayers.getText().trim()));
        game.setHostUsername(fieldHostUsername.getText().trim());
        return game;
    }

    // ── Search/filter ─────────────────────────────────────────────────────────

    private void filterTable(String query) {
        if (query == null || query.isBlank()) {
            gameTable.setItems(gameList);
            return;
        }
        String lower = query.toLowerCase();
        ObservableList<Game> filtered = gameList.filtered(g ->
            g.getGameName().toLowerCase().contains(lower) ||
            g.getStatus().toLowerCase().contains(lower)   ||
            g.getHostUsername().toLowerCase().contains(lower)
        );
        gameTable.setItems(filtered);
    }
}
