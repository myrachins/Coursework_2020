package ru.hse.edu.myurachinskiy.utils.alerts;

import javafx.scene.control.Alert;

public class AlertFactory {
    private AlertFactory() { }

    public static Alert getErrorAlert(String title, String header, String content) {
        return new ErrorAlert(title, header, content);
    }
}
