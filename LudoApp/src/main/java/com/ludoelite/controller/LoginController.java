package com.ludoelite.controller;

import com.ludoelite.service.AuthService;
import com.ludoelite.util.Validator;
import com.ludoelite.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for LoginView.fxml.
 * Validates input, calls AuthService, then navigates on success.
 *
 * Demonstrates: Inheritance (extends BaseController), Encapsulation.
 */
public class LoginController extends BaseController implements Initializable {

    // ── FXML injected fields ─────────────────────────────────────────────────
    @FXML private TextField        usernameField;
    @FXML private PasswordField    passwordField;
    @FXML private Button           loginButton;
    @FXML private Button           registerLink;
    @FXML private Label            errorLabel;
    @FXML private ProgressIndicator spinner;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinner.setVisible(false);
        errorLabel.setVisible(false);

        // Allow Enter key on password field to trigger login
        passwordField.setOnAction(e -> handleLogin());

        // Real-time validation feedback
        usernameField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) validateUsername();
        });
        passwordField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) validatePassword();
        });
    }

    // ── Event handlers ────────────────────────────────────────────────────────

    @FXML
    private void handleLogin() {
        clearErrors();

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // ── Client-side validation ────────────────────────────────────────────
        String usernameErr = Validator.validateUsername(username);
        String passwordErr = Validator.validatePassword(password);

        Validator.markField(usernameField, usernameErr == null);
        Validator.markField(passwordField, passwordErr == null);

        if (usernameErr != null) { showFieldError(usernameErr); return; }
        if (passwordErr != null) { showFieldError(passwordErr); return; }

        // ── Disable UI while request is in flight ──────────────────────────
        loginButton.setDisable(true);

        runAsync(
            () -> authService.login(username, password),
            authResponse -> {
                loginButton.setDisable(false);
                ViewNavigator.navigateToDashboard();
            },
            ex -> {
                loginButton.setDisable(false);
                showFieldError(ex.getMessage());
            },
            spinner
        );
    }

    @FXML
    private void handleNavigateToRegister() {
        ViewNavigator.navigateToRegister();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private boolean validateUsername() {
        String err = Validator.validateUsername(usernameField.getText().trim());
        Validator.markField(usernameField, err == null);
        return err == null;
    }

    private boolean validatePassword() {
        String err = Validator.validatePassword(passwordField.getText());
        Validator.markField(passwordField, err == null);
        return err == null;
    }

    private void showFieldError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void clearErrors() {
        errorLabel.setVisible(false);
        usernameField.getStyleClass().remove("field-error");
        passwordField.getStyleClass().remove("field-error");
    }
}
