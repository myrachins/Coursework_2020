package ru.hse.edu.myurachinskiy;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.hse.edu.myurachinskiy.utils.scenes.SceneMediator;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneMediator.showFirstScene(primaryStage);
    }
}