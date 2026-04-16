package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.chart.*;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class PieChartSample extends Application {
    
    static String gameName = "";
    static double posPercent = 0;
    @Override public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle(gameName + " Review Percentage");
        stage.setWidth(500);
        stage.setHeight(500);
 
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Positive Reviews", posPercent),
                new PieChart.Data("Negative Reviews", 100-posPercent));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle(gameName + " Review Percentage");

        final Label caption = new Label("");
        caption.setTextFill(Color.BLACK);
        caption.setStyle("-fx-font: 24 arial;");

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            caption.setTranslateX(e.getSceneX());
                            caption.setTranslateY(e.getSceneY());
                            caption.setText(String.valueOf(data.getPieValue()) 
                                + "%");
                        }
                    });
        }

        ((Group) scene.getRoot()).getChildren().addAll(chart, caption);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Data raw = new Data();
        raw.run();
        gameName = raw.getHighestTitle();
        posPercent = raw.getPosPercent();
        launch(args);
    }
}