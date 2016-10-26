package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ComboBox<String> jornada;
    @FXML
    private TableView enfrentamientoTabla;
    @FXML
    private TableColumn num;
    @FXML
    private TableColumn jLocal;
    @FXML
    private TableColumn puntosLocal;
    @FXML
    private TableColumn sumaLocal;
    @FXML
    private TableColumn jVisitante;
    @FXML
    private TableColumn puntosVisitante;
    @FXML
    private TableColumn sumaVisitante;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        assert jornada != null : "fx:id=\"jornada\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert enfrentamientoTabla != null : "fx:id=\"enfrentamientoTabla\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert num != null : "fx:id=\"num\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert jLocal != null : "fx:id=\"jLocal\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert puntosLocal != null : "fx:id=\"puntosLocal\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert sumaLocal != null : "fx:id=\"sumaLocal\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert jVisitante != null : "fx:id=\"jVisitante\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert puntosVisitante != null : "fx:id=\"puntosVisitante\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert sumaVisitante != null : "fx:id=\"sumaVisitante\" was not injected: check your FXML file 'enfrentamiento.fxml'.";


        List<String> s=new ArrayList<String>();
        s.add("1");
        s.add("2");
        ObservableList<String> numero=FXCollections.observableList(s);

        jornada.setItems(numero);
        //jornada.setPromptText("Hola");


    }
}
