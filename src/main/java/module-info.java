module unify.unify_wallet_gradle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;


    opens unify.unify_wallet_gradle to javafx.fxml;
    exports unify.unify_wallet_gradle;
}