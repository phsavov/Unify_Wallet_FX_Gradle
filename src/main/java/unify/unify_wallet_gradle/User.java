package unify.unify_wallet_gradle;

/* Apache HTTPComponents ver 4.5.13 */
/**
 * these imports are from https://mvnrepository.com/artifact/org.json/json/20210307
 * this is so that we can get the current ADA price in our desktop application
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
This class defines the variables and methods of a user object.
It handles getting and setting all the variables including the account id, the username, the password, spending password, the mnemonic phrase, account total, and the address.
It also creates an api key and gets the current ADA price.
*/

public class User {


    private static String apiKey = "29ace529-6a3e-4963-84ca-05a92b721ecd";
    private int accountID;
    private String username;
    private String password;
    private String spendingPassword;
    private String mnemonicPhrase;
    private double accountTotal;
    private String address;



    /**
     * Default constructor method that initializes the user class
     * @param accountID
     * @param username
     * @param password
     * @param spendingPassword
     * @param accountTotal
     */
    public User(int accountID, String username, String password, String spendingPassword, double accountTotal) {
        this.accountID = accountID;
        this.username = username;
        this.password = password;
        this.spendingPassword = spendingPassword;
        this.accountTotal = accountTotal;
        this.mnemonicPhrase = generateMnemonicPhrase();
    }

    public User(int accountID, String username, String password, String spendingPassword, String mnemonicPhrase,double accountTotal){
        this.accountID = accountID;
        this.username = username;
        this.password = password;
        this.spendingPassword = spendingPassword;
        this.mnemonicPhrase = mnemonicPhrase;
        this.accountTotal = accountTotal;
    }

    /**
     * this updates the account total by getting the current number from the database
     * and then running the set account total method so that it appears on screen
     * @throws SQLException
     */
    public void updateTotal() throws SQLException {
        UserDatabase database = new UserDatabase();
        setAccountTotal(database.getTotal(getAccountID()));
    }

    
    //Getters and Setters
    public String getUsername() { return username; }

    public double getAccountTotal() {return accountTotal; }

    public void setAccountTotal(double accountTotal) { this.accountTotal = accountTotal; }

    public int getAccountID() { return accountID; }

    public void setAddress() { address = generateAddress(); }

    public String getMnemonicPhrase(){ return mnemonicPhrase; }

    public String getAddress(){
        return address;
    }

    public void setMnemonicPhrase() { mnemonicPhrase = generateMnemonicPhrase(); }

    public String getPassword() { return password; }

    public boolean changePassword(String password){
        this.password = password;
        return true;
    }

    public String getSpendingPassword() { return spendingPassword; }

    /**
     * Creates a random string of characters from the alphabet inorder to create the mnemonic phrase
     * @return phrase: String
     */
    private String generateMnemonicPhrase(){
        // creating a list of lowercase characters from the alphabet
        // and then randomly selecting 5 letters to create the phrase

        String phrase = "";
        char[] alphabet = new char[26];
        int character = 65;
        for (int i = 0; i < 26; i++){
            alphabet[i] = (char) character;
            character++;
        }
        Random random = new Random();
        for (int i = 0; i < 5; i++){
            phrase = phrase + alphabet[random.nextInt(26)];
        }
        return phrase;
    }

    /**
     * Creates an address that has length of 8 characters
     * this is done by generating a random number between 1000 and 5000 for two sections of the address
     * the first section of the address will always be the user account #
     * @return address: String
     */
    private String generateAddress(){
        Random random = new Random();
        int random1 = random.nextInt(1000,5001);
        int random2 = random.nextInt(1000,5001);
        String address = String.valueOf(accountID) + '.' + String.valueOf(random1) + '.' + String.valueOf(random2);
        return address;
    }
    /**
     * These next two methods were copied and modified from a website https://coinmarketcap.com/api/documentation/v1/#
     * Gets the current cardano market price in USD
     * @return price: Double
     */
    public double currentADAMarketPrice(){

        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
        Double price = -1.0;

        // CoinMarketCap api query parameters
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("symbol","ADA"));
        parameters.add(new BasicNameValuePair("convert","USD"));

        try {
            String result = makeAPICall(uri, parameters);

            // Serialize the result of the api call to a JSON object and get the current market price
            String jsonString = result;
            JSONObject obj    = new JSONObject(jsonString);
            price             = obj.getJSONObject("data").getJSONObject("ADA").getJSONObject("quote").getJSONObject("USD").getDouble("price");
            return price;
        } catch (IOException e) {
            System.out.println("Error: cannot access content - " + e.toString());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
            e.printStackTrace();
        }
        return price;
    }

    /**
     * Builds the API call and returns a String with the response
     * @param uri               the absolute server address for the get requests from the api
     * @param parameters        the CoinMarketCap api query parameters
     * @return response_content a string in JSON format detailing the api call
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String makeAPICall(String uri, List<NameValuePair> parameters)
            throws URISyntaxException, IOException{

        String response_content = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        CloseableHttpResponse response = client.execute(request);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return response_content;
    }
}
