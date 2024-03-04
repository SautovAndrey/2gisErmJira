package com.example.userverification;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.concurrent.Task;

public class UserCheckApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Проверка пользователя");

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Label userLabel = new Label("Логин пользователя:");
        TextField userTextField = new TextField();
        Button checkButton = new Button("Проверить");
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);

        grid.add(userLabel, 0, 0);
        grid.add(userTextField, 1, 0);
        grid.add(checkButton, 2, 0);
        grid.add(resultArea, 0, 1, 3, 1);

        checkButton.setOnAction(e -> {
            String userLogin = userTextField.getText();
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    UserCheckService service = new UserCheckService();
                    boolean inJira = service.checkUserInJira(userLogin, "adminUsername", "adminPassword");
                    boolean inErm = service.checkUserInERM(userLogin, "ermToken");
                    updateMessage("Jira: " + (inJira ? "Найден" : "Не найден") + "\nERM: " + (inErm ? "Найден" : "Не найден"));
                    return null;
                }
            };

            resultArea.textProperty().bind(task.messageProperty());
            new Thread(task).start();
        });

        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
