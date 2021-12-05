package unify.unify_wallet_gradle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/*
This is the first page of our application, the first thing a user will see.
This displays our logo, our product name, (Unify Wallet), a log in button, and create account button
The login button allows users to log in with their credentials into an account already existing in the database.
The Create Account will allow a user to create a new account with whatever credentials they want, given they do not already exist.
*/

public class WalletApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WalletApplication.class.getResource("homePage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Unify Wallet");
        Image logo = new Image(WalletApplication.class.getResourceAsStream("Unify_Wallet_Logo.jpg"));
        stage.getIcons().add(logo);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
