package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    public static final ArrayList<String> listaNumeros = new ArrayList<String>(){{
        for (int i=0;i<=7;i++)
        {add(((Integer)i).toString());}
        add("10");
    }};
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("enfrentamiento.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root,  650, 760));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

}
