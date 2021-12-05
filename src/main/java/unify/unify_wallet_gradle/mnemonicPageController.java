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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/*
This class handles the mnemonic page. 
The unlockAccount method receives the mnemonic phrase from the user and checks to see if it is accurate. 
If it is, it unlocks the account and sends an alert. 
If not, it tells the user to try again.
*/

public class mnemonicPageController {

    @FXML
    PasswordField phraseField;
    @FXML
    Button unlockButton;


    User user;
    Stage stage;
    Scene scene;


    @FXML
    public void unlockAccount(ActionEvent event) throws SQLException, IOException {
        UserDatabase database = new UserDatabase();
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        user = (User) stage.getUserData();

        String phrase = phraseField.getText().toUpperCase();

        if (phrase.equals(user.getMnemonicPhrase().toUpperCase())){
            database.unBlock(user);
            database.resetLoginAttempts(user);
            user.setMnemonicPhrase();
            database.updatePhrase(user);
            Alert newPhrase = new Alert(Alert.AlertType.CONFIRMATION);
            newPhrase.setHeaderText("Your Account Is Unlocked!");
            newPhrase.setContentText("Your account is now unlocked.\n" +
                    "Here is your new Mnemonic Phrase: "+user.getMnemonicPhrase()+"\n" +
                    "Write this phrase down, This is your new phrase in case your account gets locked again.");
            newPhrase.showAndWait();

            Parent root = FXMLLoader.load(getClass().getResource("changePassword.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setUserData(user);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Incorrect Phrase");
            error.setContentText("You have entered the wrong mnemonic phrase\nPlease try again");
            error.showAndWait();
            phraseField.clear();
        }
    }
}
