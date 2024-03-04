module com.example.userverification {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.userverification to javafx.fxml;
    exports com.example.userverification;
}