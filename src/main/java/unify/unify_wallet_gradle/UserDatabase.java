package unify.unify_wallet_gradle;
import java.sql.*;

/*
The UserDatabase class is in charge of the backend part of users.
This deals with incorrect passwords, locked accounts, general user info, and general account information.
This has several classes that deal with the credentials, such as checking them, blocking users giving the wrong credentials, unblocking them, etc.
Users that input the incorrect passwords, but give the right username will lock the account, preventing access to malicious hackers.
This also updates the sending address for users, gets the total amount for an account, and updating the User Database.
*/

public class UserDatabase {

    public Connection connection;

    /**
     * This is the default constructor of the class that also establishes a connection to the database
     * @throws SQLException
     */
    public UserDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlserver://unify-db.database.windows.net:1433;database=Unify_Wallet;user=phsavov@unify-db;password=PhiLeTo2001BL;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
    }


    /**
     * This method returns the user based on the username and password
     * if the information entered is correct then the user will be returned otherwise there will be an SQL exception
     * @param username: String
     * @param password: String
     * @return new User
     * @throws SQLException
     */
    public User getUserInfo(String username, String password) throws SQLException {
        // create the statement and then writing the string query to get the information
        // then getting the result of the query in the result list and returning the user object
        connection.createStatement();
        String query = "Select * from Users where userName = ? and password = ?";
        PreparedStatement prepStatement = connection.prepareStatement(query);
        prepStatement.setString(1,username);
        prepStatement.setString(2,password);
        ResultSet result = prepStatement.executeQuery();
        result.next();

        int accountID = result.getInt(1);
        String userName = result.getString(2);
        String pass = result.getString(3);
        String spendPass = result.getString(4);
        String phrase = result.getString(5);
        Double total = result.getDouble(6);

        return new User(accountID, userName, pass, spendPass, phrase, total);
    }


    /**
     * Authenticates the user with their username and password
     * if the username is incorrect return 0
     * if both the username and password is incorrect return 1
     * return 2 if both are correct
     * @param username: String
     * @param password: String
     * @return int
     * @throws SQLException
     */
    public int checkCredentials(String username, String password) throws SQLException {
        // create the statement and then writing the string query to get the information
        // then getting the result of the query in the result list
        // then  comparing the username and password to see if they are correct
        connection.createStatement();

        // Check the username
        String usernameQuery = "Select * from Users where username = ?";
        PreparedStatement usernamePrepStatement = connection.prepareStatement(usernameQuery);
        usernamePrepStatement.setString(1, username);
        ResultSet usernameResult = usernamePrepStatement.executeQuery();
        if (!usernameResult.next()) {
            return 0; // username is incorrect
        }

        // Check the username and password
        String passwordQuery = "Select * from Users where username = ? and password = ?";
        PreparedStatement passwordPrepStatement = connection.prepareStatement(passwordQuery);
        passwordPrepStatement.setString(1, username);
        passwordPrepStatement.setString(2, password);
        ResultSet passwordResult = passwordPrepStatement.executeQuery();
        if (!passwordResult.next()) {
            String failedAttempt = "update Users set loginAttempts = ? where username = ? ";
            PreparedStatement update = connection.prepareStatement(failedAttempt);
            update.setString(1, String.valueOf(Integer.valueOf(usernameResult.getInt(8)) + 1));
            update.setString(2, username);
            update.executeUpdate();

            toBeBlocked(username);
            return 1; // password is incorrect
        }

        return 2; // username and password are correct
    }


    /**
     * Resets the login attempts to 0
     * @param user: User
     * @throws SQLException
     */
    public void resetLoginAttempts(User user) throws SQLException {

        connection.createStatement();
        String resetQuery = "update Users set loginAttempts = ? where username = ? ";
        PreparedStatement update = connection.prepareStatement(resetQuery);
        update.setString(1, "0");
        update.setString(2, user.getUsername());
        update.executeUpdate();
    }

    /**
     *
     * @param username
     * @return boolean
     * @throws SQLException
     */
    public boolean toBeBlocked(String username) throws SQLException {
        connection.createStatement();
        String query = "select * from Users where username = ?";
        PreparedStatement prep = connection.prepareStatement(query);
        prep.setString(1, username);
        ResultSet set = prep.executeQuery();

        set.next();

        if (set.getInt(8) >= 3){
            String lock = "update Users set accountLocked = ? where username = ?";
            prep = connection.prepareStatement(lock);
            prep.setString(1, String.valueOf(1));
            prep.setString(2, username);
            prep.executeUpdate();

            return true;
        }

        return false;
    }

    /**
     *
     * @param username
     * @param password
     * @return boolean
     * @throws SQLException
     */
    public boolean isBlocked(String username, String password) throws SQLException {
        connection.createStatement();
        String query = "Select * from Users where username = ? and password = ?";
        PreparedStatement blocked = connection.prepareStatement(query);
        blocked.setString(1, username);
        blocked.setString(2, password);
        ResultSet set = blocked.executeQuery();

        set.next();

        if (set.getInt(9) == 1){
            return true;
        }
        return false;
    }

    /**
     *
     * @param user
     * @throws SQLException
     */
    public void unBlock(User user) throws SQLException {
        connection.createStatement();
        String unlock = "update Users set accountLocked = ? where accountID = ?";
        PreparedStatement statement = connection.prepareStatement(unlock);
        statement.setString(1, String.valueOf(0));
        statement.setString(2, String.valueOf(user.getAccountID()));
        statement.executeUpdate();
    }

    /**
     *
     * @param user
     * @throws SQLException
     */
    public void updatePhrase(User user) throws SQLException {
        connection.createStatement();
        String update = "update Users set mnemonicPhrase = ? where accountID = ?";
        PreparedStatement prep = connection.prepareStatement(update);
        prep.setString(1, user.getMnemonicPhrase());
        prep.setString(2, String.valueOf(user.getAccountID()));
        prep.executeUpdate();
    }


    /**
     * Creates a new unique accountID for the User
     * we determine the new accountID by getting the largest account ID number and
     * then adding one to it
     * @return newID: int
     * @throws SQLException
     */
    public int nextAccountId() throws SQLException {
        int newID;
        // the query gets the largest account id number and then just adding one to it is the new accountID
        connection.createStatement();
        String query = "SELECT MAX(accountID) FROM Users"; //Query to get the greatest User account IDs in the User DB
        PreparedStatement prep = connection.prepareStatement(query);
        ResultSet result = prep.executeQuery();
        result.next(); //goes to the next result

        //System.out.println(result.getInt(1));
        newID = result.getInt(1) + 1;
        return newID; // returning the new account id
    }

    /**
     *
     * @param username
     * @return boolean
     * @throws SQLException
     */
    public boolean usernameExists(String username) throws SQLException {
        connection.createStatement();
        String query = "select * from Users where username = ?";
        PreparedStatement prep = connection.prepareStatement(query);
        prep.setString(1, username);
        ResultSet resultSet = prep.executeQuery();

        return resultSet.next();
    }


    /**
     * Inserts a new User into the User database when the user makes a new account
     * @param user: User
     * @throws SQLException
     */
    public void updateUserDB(User user) throws SQLException {
        connection.createStatement();
        String query = "INSERT INTO Users (accountID, username, password, spendingPassword, mnemonicPhrase, accountBalance)"
                + "VALUES (?, ?, ?, ?, ?, ?);";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, String.valueOf(user.getAccountID()));
        preparedStatement.setString(2, user.getUsername());
        preparedStatement.setString(3, user.getPassword());
        preparedStatement.setString(4, user.getSpendingPassword());
        preparedStatement.setString(5, user.getMnemonicPhrase());
        preparedStatement.setString(6, String.valueOf(user.getAccountTotal()));
        preparedStatement.executeUpdate();
    }

    /**
     * Modify the user password
     * @param user: User
     * @throws SQLException
     */
    public void changePassword(User user) throws SQLException {
        connection.createStatement();
        String query = "UPDATE Users SET password = ? WHERE accountID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, String.valueOf(user.getPassword()));
        statement.setString(2, String.valueOf(user.getAccountID()));
        statement.executeUpdate();
    }

    /**
     * Inserts an amount of cardano into the User's account
     * @param user: User
     * @param amount: int
     * @return boolean
     * @throws SQLException
     */
    public boolean addFunds(User user, double amount) throws SQLException {
        connection.createStatement();
        String query = "update Users set accountBalance = ? where accountID = ?";
        PreparedStatement prep = connection.prepareStatement(query);
        prep.setString(1, String.valueOf(user.getAccountTotal() + amount));
        prep.setString(2, String.valueOf(user.getAccountID()));
        prep.executeUpdate();
        user.setAccountTotal(user.getAccountTotal() + amount);
        return true;
    }

    /**
     * Updates the address of the user
     * @param user
     * @param address
     * @return boolean
     */
    public boolean updateAddress(User user, String address) throws SQLException {
        connection.createStatement();
        String query = "update Users set address = ? where accountID = ?";
        PreparedStatement prep = connection.prepareStatement(query);
        prep.setString(1, address);
        prep.setString(2, String.valueOf(user.getAccountID()));
        prep.executeUpdate();
        return true;
    }

    /**
     *
     * @param accountID
     * @return
     * @throws SQLException
     */
    public double getTotal(int accountID) throws SQLException {
        connection.createStatement();
        String query = "select * from Users where accountID = ?";
        PreparedStatement prep = connection.prepareStatement(query);
        prep.setString(1, String.valueOf(accountID));
        ResultSet set = prep.executeQuery();
        set.next();
        return set.getDouble(6);
    }
}
