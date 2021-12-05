package unify.unify_wallet_gradle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;

/*
The MainPageContoller is in charge of 5 separate tabs, appearing after the user logs in:
-Home, Send, Receive, add ADA, and Ledger

The Home tab:
-This displays the current market price of Cardano, and the Accounts total amount of cardano
-This includes a refresh button, which refreshes both of the things mentioned above.

The Send tab:
-Has 4 fields for user input: an amount, a wallet address, their spending password, and an additional note
-A send button that takes the above input and sends it to the designated wallet address
-When sent, the controller will also confirm that the amount being sent is actually available in the users wallet

The Receive tab:
-This has a "Generate Address" button that will display a wallet address when pushed
-The user will give someone this address, and that person will be able to send crypto using the generated address.

The Add ADA Tab:
-This has a field requiring an amount to be inputted, and an ADD button.
-This allows a user to add ADA to their account by pushing the "ADD" button.

The Ledger Tab:
-Contains a table with three columns: From Account ID, Amount, and To Account ID
-Refresh button that refreshes the ledger to display any new transactions
-This serves as the transaction history between a user, displaying sent and received transactions from other users.
*/

public class MainPageController  {

    @FXML
    Label cardanoPrice;
    @FXML
    Label displayTotal;
    @FXML
    TextField receivingAddress;
    @FXML
    TextField ADATextField;
    @FXML
    PasswordField spendingPasswordField;
    @FXML
    Label addressLabel;
    @FXML
    TextField enterAmountHereTextField;
    @FXML
    TableView<Transactions> transactionHistory;
    @FXML
    TableColumn<Transactions, String> toAccountID;
    @FXML
    TableColumn<Transactions, String> fromAccountID;
    @FXML
    TableColumn<Transactions, String> amount;


    protected User user;
    double cardanoCurrentPrice;

    /**
     * Update the cardano price
     * @param event
     */
    @FXML
    public void refreshButtonPushed(ActionEvent event) throws SQLException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        user = (User) stage.getUserData();
        cardanoCurrentPrice = user.currentADAMarketPrice();
        cardanoPrice.setText(String.valueOf(cardanoCurrentPrice));
        user.updateTotal();
        displayTotal.setText(String.valueOf(user.getAccountTotal()));
    }


    /**
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    public void sendButtonPushed(ActionEvent event) throws SQLException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        user = (User) stage.getUserData();

        UserTransactions userTransactions = new UserTransactions();

        String toAddress = receivingAddress.getText();
        double amount = Double.parseDouble(ADATextField.getText());
        String spendingPass = spendingPasswordField.getText();

        if (spendingPass.equals(user.getSpendingPassword())){
            if (userTransactions.processSendingTransaction(user, toAddress, amount)){
                Alert processed = new Alert(Alert.AlertType.CONFIRMATION);
                processed.setHeaderText("UserTransactions Complete");
                processed.setContentText("The UserTransactions has been processed!!");
                processed.showAndWait();
            } else {
                Alert notProcessed = new Alert(Alert.AlertType.ERROR);
                notProcessed.setHeaderText("UserTransactions Not Able to Process");
                notProcessed.setContentText(" The UserTransactions has not been able to be processed.\n" +
                                "There is a Database Connection issue. Please check your internet connection.\n" +
                        "If not the case, please email philip.savov@utdallas.edu");
                notProcessed.showAndWait();
            }

        } else {
            Alert wrongPass = new Alert(Alert.AlertType.ERROR);
            wrongPass.setHeaderText("Incorrect Password");
            wrongPass.setContentText("The spending password you entered is incorrect");
            wrongPass.showAndWait();
            spendingPasswordField.clear();
        }
    }

    @FXML
    public void generateAddressButtonPushed(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        user = (User) stage.getUserData();

        user.setAddress();

        try {
            UserDatabase User = new UserDatabase();
            User.updateAddress(user, user.getAddress());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        addressLabel.setText(user.getAddress());
    }

    @FXML
    public void ADDButtonPushed(ActionEvent event) throws SQLException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        user = (User) stage.getUserData();

        double amount = Double.valueOf(enterAmountHereTextField.getText());
        UserDatabase database = new UserDatabase();

        if (amount <= 0){
            Alert toLow = new Alert(Alert.AlertType.ERROR);
            toLow.setHeaderText("Incorrect Input");
            toLow.setHeaderText("The amount entered was negative or 0.\n Please enter a number that is greater than 0.");
            toLow.showAndWait();
        } else {
            if (database.addFunds(user, amount)){
                displayTotal.setText(String.valueOf(user.getAccountTotal()));
                Alert worked = new Alert(Alert.AlertType.CONFIRMATION);
                worked.setHeaderText("Success!!!");
                worked.setContentText("The Funds have been successfully added to your account.\n" +
                        "To see just click on the 'Home' Tab.");
            } else {
                Alert notProcessed = new Alert(Alert.AlertType.ERROR);
                notProcessed.setHeaderText("The funds were not able to be processed");
                notProcessed.setContentText("The add funds has not been able to be processed.\n" +
                        "There is a Database Connection issue. Please check your internet connection.\n" +
                        "If not the case, please email philip.savov@utdallas.edu");
                notProcessed.showAndWait();
            }
        }
    }


    @FXML
    public void transactionRefreshButtonPushed(ActionEvent event) throws SQLException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        user = (User) stage.getUserData();
        TransactionDatabase database = new TransactionDatabase();
        ObservableList<Transactions> list = database.history(user);

        fromAccountID.setCellValueFactory(new PropertyValueFactory<>("fromAccountID"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        toAccountID.setCellValueFactory(new PropertyValueFactory<>("toAccountID"));
        transactionHistory.setItems(list);

    }
}
