package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.*;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private ComboBox<String> jornada;
    @FXML
    private ComboBox<String> equiposEnfrentamiento;
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


    private Connection conexion;
    private List<String> listaJornadas;
    private List<String> enfrentamientoJornadas;
    private Map<Integer, Enfrentamiento> mapaEnfrentamientos;

    private int numJorndaSeleccionada;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        assert jornada != null : "fx:id=\"jornada\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert equiposEnfrentamiento != null : "fx:id=\"equiposEnfrentamiento\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert enfrentamientoTabla != null : "fx:id=\"enfrentamientoTabla\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert num != null : "fx:id=\"num\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert jLocal != null : "fx:id=\"jLocal\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert puntosLocal != null : "fx:id=\"puntosLocal\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert sumaLocal != null : "fx:id=\"sumaLocal\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert jVisitante != null : "fx:id=\"jVisitante\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert puntosVisitante != null : "fx:id=\"puntosVisitante\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert sumaVisitante != null : "fx:id=\"sumaVisitante\" was not injected: check your FXML file 'enfrentamiento.fxml'.";

        mapaEnfrentamientos = new HashMap<Integer, Enfrentamiento>();

        conectaBBDD();

        jornada.setItems(inicializaComboJornadas());
        jornada.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                //System.out.println(t1);
                numJorndaSeleccionada = Integer.parseInt(t1);
                equiposEnfrentamiento.setItems(inicializaComboEquiposEnfrentamiento(t1));
            }
        });
        equiposEnfrentamiento.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String t[];
                Integer id;
                t = newValue.split(":");
                id = Integer.parseInt(t[0].trim());
                t = t[1].split("-");

                //System.out.println(id+"   "+t[0].trim()+"     "+t[1].trim());

                mapaEnfrentamientos.put(id, new Enfrentamiento(id, numJorndaSeleccionada, t[0].trim(), t[1].trim()));
                System.out.println(mapaEnfrentamientos);
            }
        });

        System.out.println(numJorndaSeleccionada);


        //jornada.setPromptText("Hola");

        // equiposEnfrentamiento;
    }

    private ObservableList<String> inicializaComboJornadas() {

        listaJornadas = new ArrayList<String>();

        try {
            Statement s = conexion.createStatement();
            ResultSet rs = s.executeQuery("SELECT distinct Jornada From AgpoolTeamData.Enfrentamiento");
            while (rs.next()) {
                listaJornadas.add(rs.getString("Jornada"));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        ObservableList<String> numero = FXCollections.observableList(listaJornadas);
        return numero;


    }

    private ObservableList<String> inicializaComboEquiposEnfrentamiento(String numJornada) {

        enfrentamientoJornadas = new ArrayList<String>();


        try {
            Statement s = conexion.createStatement();
            Statement j = conexion.createStatement();

            ResultSet rs = s.executeQuery("SELECT EquipoLocal,EquipoVisitante From Enfrentamiento Where Jornada='" + numJornada + "'");
            ResultSet equipos;
            while (rs.next()) {

                String cadena = "";
                int equipoLocal = +rs.getInt("EquipoLocal");
                int equipoVisitante = +rs.getInt("EquipoVisitante");

                equipos = j.executeQuery("SELECT Nombre From Equipo Where IdEquipo=" + equipoLocal);
                equipos.next();
                cadena = equipos.getString("Nombre");
                equipos = j.executeQuery("SELECT Nombre From Equipo Where IdEquipo=" + equipoVisitante);
                equipos.next();
                cadena = cadena + " - " + equipos.getString("Nombre");
                equipos = j.executeQuery("SELECT idEnfrentamiento From Enfrentamiento Where EquipoLocal=" + equipoLocal + " and EquipoVisitante=" + equipoVisitante);
                equipos.next();
                enfrentamientoJornadas.add(((Integer) equipos.getInt("idEnfrentamiento")).toString() + ":    " + cadena);


            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }


        ObservableList<String> enfrentamientos = FXCollections.observableList(enfrentamientoJornadas);
        return enfrentamientos;


    }


    private void conectaBBDD() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded!");
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/AgpoolTeamData", "jag", "");


        } catch (SQLException e) {
            // TODO Auto-generated catch block

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




    }
}