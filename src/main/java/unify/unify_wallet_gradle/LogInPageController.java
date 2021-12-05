package unify.unify_wallet_gradle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
* This is the Login Page Controller. This file contains the a method that validates the username and password enterd and
* then sends to check the information with the Unify Wallet Database. The code sends the username and password to the check credetials method
* in the user database and then it will return an integer that will either be 0, 1, 2. 0 being that the username was incorrect and the check credentials method 
* did not check the username bacause there was no point to check the password if there is no valid username to associate it with. If 1 is what was retuned, that 
* means that the password was incorrect and the username in the database has a failed log in attempt added to its account. if 2 is returned then that means that both
* username and password were correct and then the code checks the database to see if the account is locked based off of failed login attempts. If there were more than
* three falied attempts then the code will send the user to the mnemonic page to unlock their account. However, if there weren't falied login attempts the user will be 
* taken to their main page in the wallet.
*/

public class LogInPageController {

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button logInButton;

    Stage stage;
    Scene scene;

    @FXML
    public void LogIn(ActionEvent event) throws IOException, SQLException {
        int loginAttempt;
        boolean loginSuccessful = false;
        // getting the username and password and making an instance of the database
        String username = usernameField.getText();
        String password = passwordField.getText();
        UserDatabase db = new UserDatabase();
        // checking the credenitals 
        loginAttempt = db.checkCredentials(username, password);
        if(loginAttempt == 2){
            // if the account is blocked then the if statement will run
            if (db.isBlocked(username, password)){
                Alert blocked = new Alert(Alert.AlertType.ERROR);
                blocked.setHeaderText("Account Has Been Blocked");
                blocked.setContentText("ATTENTION USER\nYour Account has been blocked due to too many incorrect login attempts.\n Please hit the 'OK' button to enter your Mnemonic Phrase to unlock your Account.");
                blocked.showAndWait();
                // going to another page
                Parent root = FXMLLoader.load(getClass().getResource("mnemonicPage.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                try {
                    stage.setUserData(db.getUserInfo(username, password));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }
            loginSuccessful = true;
        } else if (loginAttempt == 0){
            // printing an alert and clearing the username and password fields
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Invalid Username");
            error.setContentText("The Username cannot be located in the database\nPlease re-enter the credentials");
            error.showAndWait();
            usernameField.clear();
            passwordField.clear();
            loginSuccessful = false;
        } else if (loginAttempt == 1){
            // printing an alert and clearing the username and password fields
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Invalid Password");
            error.setContentText("The Password is entered incorrectly\nPlease re-enter the credentials");
            error.showAndWait();
            usernameField.clear();
            passwordField.clear();

            loginSuccessful = false;
        }


        if (loginSuccessful) {
            // going to the users main wallet page
            Parent root = FXMLLoader.load(getClass().getResource("mainPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            try {
                stage.setUserData(db.getUserInfo(username, password));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    }
}

