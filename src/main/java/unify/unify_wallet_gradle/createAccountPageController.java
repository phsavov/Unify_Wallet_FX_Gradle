package unify.unify_wallet_gradle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/*
This class is a controller in charge of the create account button.
A user will enter their desired username, password, and spending password.
This will check if the username is available, that the password matches the confirmed password, and that the spending password matches the confirm spending password.
If all credentials all valid, a new User will be created and subsequently added to the database.
*/

public class createAccountPageController {

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField confirmPasswordField;
    @FXML
    PasswordField spendingPasswordField;
    @FXML
    PasswordField confirmSpendingPasswordField;

    Stage stage;
    Scene scene;


    @FXML
    public void createButtonPressed(ActionEvent event) throws SQLException, IOException {
        String username, password, confirmPass, spendPass, confSpendPass;

        username = usernameField.getText();
        password = passwordField.getText();
        confirmPass = confirmPasswordField.getText();
        spendPass = spendingPasswordField.getText();
        confSpendPass = confirmSpendingPasswordField.getText();
        UserDatabase database = new UserDatabase();

        if (username.isEmpty() || password.isEmpty() || spendPass.isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Incorrect Inputs");
            error.setContentText("Username, Password and Spending Password cannot be left blank.\nPlease re-enter the information correctly.");
            error.showAndWait();
            passwordField.clear();
            confirmPasswordField.clear();
            spendingPasswordField.clear();
            confirmSpendingPasswordField.clear();
        } else if (database.usernameExists(username)) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Incorrect Username Input");
            error.setContentText("The Username entered already exists.\nPlease enter a different Username.");
            error.showAndWait();
            usernameField.clear();
        } else if (password.equals(confirmPass) || spendPass.equals(confSpendPass)) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Incorrect Inputs");
            error.setContentText("Either the Password or Spending Password did not match the confirmation.\nPlease re-enter the information correctly.");
            error.showAndWait();
            passwordField.clear();
            confirmPasswordField.clear();
            spendingPasswordField.clear();
            confirmSpendingPasswordField.clear();
        } else {
            User newUser = new User(database.nextAccountId(), username, password, spendPass, 0.00);
            database.updateUserDB(newUser);

            Alert created = new Alert(Alert.AlertType.CONFIRMATION);
            created.setHeaderText("Created Account");
            created.setContentText("You have successfully created a account!!!");
            created.showAndWait();

            Alert phrase = new Alert(Alert.AlertType.CONFIRMATION);
            phrase.setHeaderText("IMPORTANT!!!!!");
            phrase.setContentText("This is your Mnemonic Phrase: " + newUser.getMnemonicPhrase() + "\n" +
                    "This your Mnemonic Phrase. Write this phrase down because it will not be shown to you again.\n" +
                    "This will allow you to regain access to your account if there are more than three incorrect login attempts.\n" +
                    "You will get a new phrase if you have to use this current phrase.\nDo Not Lose It!!!");
            phrase.showAndWait();

            Parent root = FXMLLoader.load(getClass().getResource("homePage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
}
