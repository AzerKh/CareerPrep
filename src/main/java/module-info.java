module com.example.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires itextpdf;

    opens com.example.test to javafx.fxml;
    opens com.example.test.controller to javafx.fxml;
    opens com.example.test.model to javafx.fxml;

    exports com.example.test;
}