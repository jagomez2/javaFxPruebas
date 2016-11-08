package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.*;


public class ControllerVisualizaEnfrentamiento implements Initializable {

    @FXML
    private ChoiceBox<String> jornada;
    @FXML
    private ChoiceBox<String> equiposEnfrentamiento;
    @FXML
    private TextField l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12, l13, l14, l15, l16, l17, l18;
    @FXML
    private TextField v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18;
    @FXML
    private TextField puntosLocal1, puntosLocal2, puntosLocal3, puntosLocal4, puntosLocal5, puntosLocal6, puntosLocal7,
            puntosLocal8, puntosLocal9, puntosLocal10, puntosLocal11, puntosLocal12, puntosLocal13, puntosLocal14,
            puntosLocal15, puntosLocal16, puntosLocal17, puntosLocal18, puntosLocal19;
    @FXML
    private TextField sumaLocal1, sumaLocal2, sumaLocal3, sumaLocal4, sumaLocal5, sumaLocal6, sumaLocal7, sumaLocal8,
            sumaLocal9, sumaLocal10, sumaLocal11, sumaLocal12, sumaLocal13, sumaLocal14,
            sumaLocal15, sumaLocal16, sumaLocal17, sumaLocal18, sumaLocal19;

    @FXML
    private TextField puntosVisitante1, puntosVisitante2, puntosVisitante3, puntosVisitante4, puntosVisitante5, puntosVisitante6,
            puntosVisitante7, puntosVisitante8, puntosVisitante9, puntosVisitante10, puntosVisitante11, puntosVisitante12,
            puntosVisitante13, puntosVisitante14, puntosVisitante15, puntosVisitante16, puntosVisitante17, puntosVisitante18, puntosVisitante19;

    @FXML
    private TextField sumaVisitante1, sumaVisitante2, sumaVisitante3, sumaVisitante4, sumaVisitante5, sumaVisitante6, sumaVisitante7,
            sumaVisitante8, sumaVisitante9, sumaVisitante10, sumaVisitante11, sumaVisitante12, sumaVisitante13, sumaVisitante14,
            sumaVisitante15, sumaVisitante16, sumaVisitante17, sumaVisitante18, sumaVisitante19;
    @FXML
    private Button botonSubmit;


    private Connection conexion;
    private List<String> listaJornadas;
    private List<String> enfrentamientoJornadas;
    private Map<Integer, Enfrentamiento> mapaEnfrentamientos;
    private List<TextField> listaTextoLocales, listaTextoVisitantes;
    private List<TextField> puntosLocal, sumaLocal, puntosVisitante, sumaVisitante;

    private Enfrentamiento actual;
    private ArrayList<String> datosIndividualesSubmit, datosPartidaEquipo;

    private HashMap<Integer, String> jugadoresMap = new HashMap<Integer, String>();

    private int numJorndaSeleccionada;
    private int numEnfrentamientoSeleccionado;
    private String nombreJornada;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        assert jornada != null : "fx:id=\"jornada\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert equiposEnfrentamiento != null : "fx:id=\"equiposEnfrentamiento\" was not injected: check your FXML file 'enfrentamiento.fxml'.";

        mapaEnfrentamientos = new HashMap<Integer, Enfrentamiento>();

        inicializarListas();
        desabilitaEdicion();
        //MiChangeListenerDigitos cldigitos=new MiChangeListenerDigitos(1);


        conectaBBDD();

        botonSubmit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {


            }
        });
        jornada.setItems(inicializaComboJornadas());
        jornada.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {


                numJorndaSeleccionada = Integer.parseInt(t1);
                equiposEnfrentamiento.setItems(inicializaComboEquiposEnfrentamiento(t1));
                jugadoresMap = getJugadoresEnMap();
            }
        });

        equiposEnfrentamiento.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                //   limpiaCombos();

                String t[];
                Integer id;
                //     System.out.println("Valor nuevo "+ newValue);
                t = newValue.split(":");
                nombreJornada = t[1];
                id = Integer.parseInt(t[0].trim());
                t = t[1].split("-");


                mapaEnfrentamientos.put(id, new Enfrentamiento(id, numJorndaSeleccionada, t[0].trim(), t[1].trim()));
                numEnfrentamientoSeleccionado = id;


                rellenaEnfrentamientos(numEnfrentamientoSeleccionado);


            }

        });


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


    private void inicializarListas() {

        listaTextoLocales = new ArrayList<TextField>() {{
            add(l1);
            add(l2);
            add(l3);
            add(l4);
            add(l5);
            add(l6);
            add(l7);
            add(l8);
            add(l9);
            add(l10);
            add(l11);
            add(l12);
            add(l13);
            add(l14);
            add(l15);
            add(l16);
            add(l17);
            add(l18);
        }};
        listaTextoVisitantes = new ArrayList<TextField>() {{
            add(v1);
            add(v2);
            add(v3);
            add(v4);
            add(v5);
            add(v6);
            add(v7);
            add(v8);
            add(v9);
            add(v10);
            add(v11);
            add(v12);
            add(v13);
            add(v14);
            add(v15);
            add(v16);
            add(v17);
            add(v18);
        }};
        puntosLocal = new ArrayList<TextField>() {{
            add(puntosLocal1);
            add(puntosLocal2);
            add(puntosLocal3);
            add(puntosLocal4);
            add(puntosLocal5);
            add(puntosLocal6);
            add(puntosLocal7);
            add(puntosLocal8);
            add(puntosLocal9);
            add(puntosLocal10);
            add(puntosLocal11);
            add(puntosLocal12);
            add(puntosLocal13);
            add(puntosLocal14);
            add(puntosLocal15);
            add(puntosLocal16);
            add(puntosLocal17);
            add(puntosLocal18);
            add(puntosLocal19);
        }};
        puntosVisitante = new ArrayList<TextField>() {{
            add(puntosVisitante1);
            add(puntosVisitante2);
            add(puntosVisitante3);
            add(puntosVisitante4);
            add(puntosVisitante5);
            add(puntosVisitante6);
            add(puntosVisitante7);
            add(puntosVisitante8);
            add(puntosVisitante9);
            add(puntosVisitante10);
            add(puntosVisitante11);
            add(puntosVisitante12);
            add(puntosVisitante13);
            add(puntosVisitante14);
            add(puntosVisitante15);
            add(puntosVisitante16);
            add(puntosVisitante17);
            add(puntosVisitante18);
            add(puntosVisitante19);
        }};
        sumaLocal = new ArrayList<TextField>() {{
            add(sumaLocal1);
            add(sumaLocal2);
            add(sumaLocal3);
            add(sumaLocal4);
            add(sumaLocal5);
            add(sumaLocal6);
            add(sumaLocal7);
            add(sumaLocal8);
            add(sumaLocal9);
            add(sumaLocal10);
            add(sumaLocal11);
            add(sumaLocal12);
            add(sumaLocal13);
            add(sumaLocal14);
            add(sumaLocal15);
            add(sumaLocal16);
            add(sumaLocal17);
            add(sumaLocal18);
            add(sumaLocal19);
        }};
        sumaVisitante = new ArrayList<TextField>() {{
            add(sumaVisitante1);
            add(sumaVisitante2);
            add(sumaVisitante3);
            add(sumaVisitante4);
            add(sumaVisitante5);
            add(sumaVisitante6);
            add(sumaVisitante7);
            add(sumaVisitante8);
            add(sumaVisitante9);
            add(sumaVisitante10);
            add(sumaVisitante11);
            add(sumaVisitante12);
            add(sumaVisitante13);
            add(sumaVisitante14);
            add(sumaVisitante15);
            add(sumaVisitante16);
            add(sumaVisitante17);
            add(sumaVisitante18);
            add(sumaVisitante19);
        }};
    }


    public HashMap<Integer, String> getJugadoresEnMap() {

        Statement s;
        ResultSet rs;
        HashMap<Integer, String> array = new HashMap<Integer, String>();

        try {
            s = conexion.createStatement();

            rs = s.executeQuery("SELECT idJugador,Nombre, Apellidos FROM Jugador");
            while (rs.next()) {

                array.put(rs.getInt("idJugador"), rs.getString("Nombre") + " " + rs.getString("Apellidos"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return array;

    }

    public void rellenaEnfrentamientos(int idEnfrentamiento) {


        PreparedStatement compruebaEnfrentamientos, s, equipos;
        ResultSet compruebaEnfrentamientors, rs, equiposrs;
        try {

            compruebaEnfrentamientos = conexion.prepareStatement("select  * from Enfrentamiento where idEnfrentamiento=? ");
            compruebaEnfrentamientos.setInt(1, numEnfrentamientoSeleccionado);
            compruebaEnfrentamientors = compruebaEnfrentamientos.executeQuery();
            if (compruebaEnfrentamientors.next()) {
                if (compruebaEnfrentamientors.getInt("ResultadoLocal") != 0 && compruebaEnfrentamientors.getInt("ResultadoVisitante") != 0) {

                    s = conexion.prepareStatement("SELECT JugadorSaque,JugadorSinSaque,ResultadoJugadorSaque,ResultadoJugadorSinSaque FROM AgpoolTeamData.PartidaIndividual where Enfrentamiento=? order by idPartida;");
                    s.setInt(1, numEnfrentamientoSeleccionado);
                    rs = s.executeQuery();
                    int cont = 0;
                    Integer sumaLocalParcial = 0;
                    Integer sumaVisitanteParcial = 0;

                    while (rs.next()) {
                        int calculo = (cont / 3) % 2;
                        //saqueVisitante
                        if (calculo == 0) {
                            listaTextoLocales.get(cont).setText(jugadoresMap.get(rs.getInt("JugadorSinSaque")));
                            puntosLocal.get(cont).setText(rs.getString("ResultadoJugadorSinSaque"));
                            sumaLocalParcial += rs.getInt("ResultadoJugadorSinSaque");
                            sumaLocal.get(cont).setText(sumaLocalParcial.toString());
                            listaTextoVisitantes.get(cont).setText(jugadoresMap.get(rs.getInt("JugadorSaque")));
                            puntosVisitante.get(cont).setText(rs.getString("ResultadoJugadorSaque"));
                            sumaVisitanteParcial += rs.getInt("ResultadoJugadorSaque");
                            sumaVisitante.get(cont).setText(sumaVisitanteParcial.toString());


                        }
                        //saque local
                        else {

                            listaTextoLocales.get(cont).setText(jugadoresMap.get(rs.getInt("JugadorSaque")));
                            puntosLocal.get(cont).setText(rs.getString("ResultadoJugadorSaque"));
                            sumaLocalParcial += rs.getInt("ResultadoJugadorSaque");
                            sumaLocal.get(cont).setText(sumaLocalParcial.toString());
                            listaTextoVisitantes.get(cont).setText(jugadoresMap.get(rs.getInt("JugadorSinSaque")));
                            puntosVisitante.get(cont).setText(rs.getString("ResultadoJugadorSinSaque"));
                            sumaVisitanteParcial += rs.getInt("ResultadoJugadorSinSaque");
                            sumaVisitante.get(cont).setText(sumaVisitanteParcial.toString());

                        }
                        cont++;
                    }

                    equipos = conexion.prepareStatement("SELECT ResultadoEquipoLocal,ResultadoEquipoVisitante  FROM AgpoolTeamData.PartidaEquipos where Enfrentamiento=?");
                    equipos.setInt(1, numEnfrentamientoSeleccionado);
                    equiposrs = equipos.executeQuery();
                    if (equiposrs.next()) {


                        puntosLocal19.setText(equiposrs.getString("ResultadoEquipoLocal"));
                        sumaLocalParcial += equiposrs.getInt("ResultadoEquipoLocal");
                        sumaLocal19.setText(sumaLocalParcial.toString());
                        puntosVisitante19.setText(equiposrs.getString("ResultadoEquipoVisitante"));
                        sumaVisitanteParcial += equiposrs.getInt("ResultadoEquipoVisitante");
                        sumaVisitante19.setText(sumaVisitanteParcial.toString());


                    }


                } else { //No se ha jugado esa jornada
                    for (int i = 0; i < 18; i++) {
                        listaTextoLocales.get(i).setText("No jugado");
                        listaTextoVisitantes.get(i).setText("No jugado");
                        puntosLocal.get(i).setText("0");
                        puntosVisitante.get(i).setText("0");
                        sumaLocal.get(i).setText("0");
                        sumaVisitante.get(i).setText("0");

                    }
                    puntosLocal19.setText("0");
                    sumaLocal19.setText("0");
                    puntosVisitante19.setText("0");
                    sumaVisitante19.setText("0");


                    System.out.println("Enfrentamiento no jugado");
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void desabilitaEdicion() {

        for (int i = 0; i < 18; i++) {

            listaTextoLocales.get(i).setDisable(true);
            listaTextoVisitantes.get(i).setDisable(true);
            puntosLocal.get(i).setDisable(true);
            puntosVisitante.get(i).setDisable(true);
            sumaLocal.get(i).setDisable(true);
            sumaVisitante.get(i).setDisable(true);

        }
        puntosLocal19.setDisable(true);
        puntosVisitante19.setDisable(true);
        sumaLocal19.setDisable(true);
        sumaVisitante19.setDisable(true);
    }

}
