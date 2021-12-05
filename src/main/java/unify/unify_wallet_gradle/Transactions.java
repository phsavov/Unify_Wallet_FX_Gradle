package unify.unify_wallet_gradle;

/*
This is in charge of the address and amounts sent and received from a user to another user.
-This information is used to construct the Ledger in the MainPageController.
*/

public class Transactions {

    private String fromAccountID;
    private String amount;
    private String toAccountID;

    /**
     *
     * @param fromAccountID
     * @param amount
     * @param toAccountID
     */
    //Constructor
    public Transactions(String fromAccountID, String amount, String toAccountID){
        this.fromAccountID = fromAccountID;
        this.amount = amount;
        this.toAccountID = toAccountID;
    }

    
    //Getters
    public String getFromAccountID() {
        return fromAccountID;
    }

    public String getAmount() {
        return amount;
    }

    public String getToAccountID() {
        return toAccountID;
    }
}
