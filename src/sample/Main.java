package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {

    public static final ArrayList<String> listaNumeros = new ArrayList<String>(){{
        for (int i=0;i<=7;i++)
        {add(((Integer)i).toString());}
        add("10");
    }};
    @Override
    public void start(Stage primaryStage) throws Exception{


       // Parent root = FXMLLoader.load(getClass().getResource("enfrentamiento.fxml"));

        Button btn = new Button();
       // btn.setLayoutX(225);
       // btn.setLayoutY(225);
        btn.setText("Introduce nuevos enfrentamientos");
        btn.setOnAction((ActionEvent event) -> {
            //Label secondLabel = new Label("Hello");

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("enfrentamiento.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root1, 650, 760));
                stage.show();
            } catch(Exception e) {
                e.printStackTrace();
            }


        });

        Button btn2 = new Button();
      //  btn2.setLayoutX(375);
      //  btn2.setLayoutY(375);
        btn2.setText("Visualiza enfrentamientos");
        btn2.setOnAction((ActionEvent event) -> {
            //Label secondLabel = new Label("Hello");

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("visualiza_enfrentamiento.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root1, 650, 760));
                stage.show();
            } catch(Exception e) {
                e.printStackTrace();
            }


        });


        Button btn3 = new Button();
        //  btn2.setLayoutX(375);
        //  btn2.setLayoutY(375);
        btn3.setText("Estadisticas");
        btn3.setOnAction((ActionEvent event) -> {
            //Label secondLabel = new Label("Hello");

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("estadisticas.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root1, 800, 600));
                stage.show();
            } catch(Exception e) {
                e.printStackTrace();
            }


        });

        GridPane root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        RowConstraints fila1= new RowConstraints();
        fila1.setPercentHeight(25);
        RowConstraints fila2= new RowConstraints();
        fila2.setPercentHeight(25);
        RowConstraints fila3= new RowConstraints();
        fila3.setPercentHeight(25);
        root.getRowConstraints().addAll(fila1,fila2,fila3);



        root.add(btn, 0, 0);
        root.add(btn2, 0, 1);
        root.add(btn3, 0, 2);


        //root.getChildren().add(btn);
        //root.getChildren().add(btn2);
        primaryStage.setTitle("Agpool gestion liga de equipos @BY_JAG");
        primaryStage.setScene(new Scene(root,  550, 550));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }


    public static void main(String[] args) {
        launch(args);
    }

}
