package unify.unify_wallet_gradle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/*
The HomePageController
This manages the Home Page of our GUI
LogInButton:
-The LogInButton manages the Log In button, users will enter their username and password, then click the login button.
-This will then get the information entered and check the database to give access to the desired account
createAccountButton
-This function will take the credentials entered by the user, double check that they are not alredy in use, then create a new account with those credentials.
*/

public class HomePageController {

    private Scene scene;
    private Parent root;
    private Stage stage;

    /**
     *
     * @param event
     * @throws IOException
     */
    @FXML
    public void logInButton(ActionEvent event) throws IOException {

        root = FXMLLoader.load(getClass().getResource("logInPage.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param event
     * @throws IOException
     */
    @FXML
    public void createAccountButton(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("createAccountPage.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
