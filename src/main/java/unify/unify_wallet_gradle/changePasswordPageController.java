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

/**
* The change password page controller class is to execute actions if buttons were pressed withint the chagnepasswordpage.fxml file.
* This class has one method that exectues if the chagne password button was pressed. first it checks if the two passwords are the 
* same, if they are not the same then the application will output an error message that the passwords do not match and try again.
* if teu are the same then the program will chagne the password in the userdatabase and update everyting and then send the user back
* to the main page where thye have the login or create account buttons.
*/

public class changePasswordPageController {

    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField passwordConfirmField;
    @FXML
    Button enterButton;

    User user;
    Stage stage;
    Scene scene;

    @FXML
    public void changePassword(ActionEvent event) throws SQLException, IOException {
        String password, confirmPassword;

        UserDatabase database = new UserDatabase();
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        user = (User) stage.getUserData();

        password = passwordField.getText();
        confirmPassword = passwordConfirmField.getText();
        // checking to see if the passwords match
        if(password.equals(confirmPassword)){
            user.changePassword(password);
            database.changePassword(user); // updating the password within the database

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setHeaderText("Password Reset Confirmation");
            confirmation.setContentText("You have successfully changed your password!\n" +
                    "Once you hit 'OK' you will be redirected to the home page to log back in.");
            confirmation.showAndWait(); // showing the user that the update was successful

            Parent root = FXMLLoader.load(getClass().getResource("homePage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show(); // going to the next page

        } else {
            Alert diffPasswords = new Alert(Alert.AlertType.ERROR);
            diffPasswords.setHeaderText("Incorrect Inputs");
            diffPasswords.setContentText("Password does not match confirmation");
            diffPasswords.showAndWait(); // showing the error message
            passwordField.clear();
            passwordConfirmField.clear();
            //clearing the two password fields
        }
    }
}
