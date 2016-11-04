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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;
import java.util.*;


public class Controller implements Initializable {

    @FXML
    private ChoiceBox<String> jornada;
    @FXML
    private ChoiceBox<String> equiposEnfrentamiento;
    @FXML
    private CheckBox checkBoxCambios, checkBoxTextoBD;
    @FXML
    private ChoiceBox<String> l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12, l13, l14, l15, l16, l17, l18;
    @FXML
    private ChoiceBox<String> v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18;
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
    private List<ChoiceBox> listaCombosLocales, listaCombosVisitantes;
    private List<TextField> puntosLocal, sumaLocal, puntosVisitante, sumaVisitante;

    private Enfrentamiento actual;
    private ArrayList<String> datosIndividualesSubmit, datosPartidaEquipo;

    private HashMap<Integer, ArrayList<String>> datosSubmit = new HashMap<Integer, ArrayList<String>>();

    private int numJorndaSeleccionada;
    private int numEnfrentamientoSeleccionado;
    private String nombreJornada;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        assert jornada != null : "fx:id=\"jornada\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert equiposEnfrentamiento != null : "fx:id=\"equiposEnfrentamiento\" was not injected: check your FXML file 'enfrentamiento.fxml'.";

        mapaEnfrentamientos = new HashMap<Integer, Enfrentamiento>();

        inicializarListas();
        //MiChangeListenerDigitos cldigitos=new MiChangeListenerDigitos(1);

        for (int i = 0; i < puntosLocal.size(); i++) {

            puntosLocal.get(i).lengthProperty().addListener(new MiChangeListenerDigitos<Number>(i, true));

        }
        for (int i = 0; i < puntosVisitante.size(); i++) {

            puntosVisitante.get(i).lengthProperty().addListener(new MiChangeListenerDigitos<Number>(i, false));

        }
        //puntosLocal1.lengthProperty().addListener(new MiChangeListenerDigitos(0));


        conectaBBDD();

        botonSubmit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                if (!checkBoxTextoBD.isSelected()) {
                    boolean correcto1, correcto2, correcto3, correcto4;
                    correcto1 = borraPartidasEnfrentamiento();

                    correcto2 = introducePartidasIndividualesEnBD(datosSubmit);
                    correcto3 = introducePartidasEquiposEnBD(datosPartidaEquipo);
                    correcto4 = actualizaPuntuacionEnfrentamiento();

                    if (correcto1 && correcto2 && correcto3 && correcto4) {

                        botonSubmit.setStyle("-fx-background-color: green");

                    } else {

                        botonSubmit.setStyle("-fx-background-color: red");


                    }

                } else {


                    imprimePartidasEnFicheroDeTexto(nombreJornada);

                }

            }
        });
        jornada.setItems(inicializaComboJornadas());
        jornada.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {


                numJorndaSeleccionada = Integer.parseInt(t1);
                equiposEnfrentamiento.setItems(inicializaComboEquiposEnfrentamiento(t1));
            }
        });
        equiposEnfrentamiento.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                limpiaCombos();

                String t[];
                Integer id;
                //     System.out.println("Valor nuevo "+ newValue);
                t = newValue.split(":");
                nombreJornada=t[1];
                id = Integer.parseInt(t[0].trim());
                t = t[1].split("-");


                mapaEnfrentamientos.put(id, new Enfrentamiento(id, numJorndaSeleccionada, t[0].trim(), t[1].trim()));
                numEnfrentamientoSeleccionado = id;

                ArrayList<String> jl = new ArrayList<String>(mapaEnfrentamientos.get(id).getjLocales().values());
                ArrayList<String> jv = new ArrayList<String>(mapaEnfrentamientos.get(id).getjVisitante().values());

                //System.out.println(jl);
                //System.out.println(jv);


                //limpiaCombos();
                ObservableList<String> observableListjl = FXCollections.observableArrayList(jl);
                for (ChoiceBox<String> c : listaCombosLocales) {

                    c.getItems().clear();
                    c.setItems(observableListjl);
                    c.valueProperty().addListener(new MiChangeListener<String>(c, jl));

                }

                ObservableList<String> observableListjv = FXCollections.observableArrayList(jv);
                for (ChoiceBox<String> d : listaCombosVisitantes) {

                    d.getItems().clear();
                    //d.valueProperty().removeListener(new MiChangeListener<String>(d, jv));
                    d.setItems(observableListjv);
                    d.valueProperty().addListener(new MiChangeListener<String>(d, jv));
                }
            }

        });

        checkBoxCambios.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                if (old_val == false && new_val == true) {
                    for (int i = 3; i < 18; i++) {

                        listaCombosLocales.get(i).setDisable(false);
                        listaCombosVisitantes.get(i).setDisable(false);
                    }
                } else {
                    for (int i = 3; i < 18; i++) {
                        listaCombosLocales.get(i).setDisable(true);
                        listaCombosVisitantes.get(i).setDisable(true);
                    }
                }

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

    private ObservableList<String> inicializaComboJLocales(int idEnfrentamiento) {

        actual = mapaEnfrentamientos.get(idEnfrentamiento);
        actual.getjLocales();
        List<String> listaJlocal = new ArrayList<String>(actual.getjLocales().values());
        ObservableList<String> observableJugadoresLocales = FXCollections.observableList(listaJlocal);

        return observableJugadoresLocales;
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

    private void limpiaCombos() {

        for (int i = 0; i < 18; i++) {
            listaCombosLocales.get(i).getSelectionModel().clearSelection();
            //  listaCombosLocales.get(i).valueProperty().addListener(new MiChangeListener(listaCombosLocales.get(i),null));

            listaCombosVisitantes.get(i).getSelectionModel().clearSelection();
            //  listaCombosVisitantes.get(i).valueProperty().addListener(new MiChangeListener(listaCombosVisitantes.get(i),null));
            puntosLocal.get(i).setText("");
            puntosLocal.get(i).setStyle("-fx-control-inner-background: red");
            puntosVisitante.get(i).setText("");
            puntosVisitante.get(i).setStyle("-fx-control-inner-background: red");
            sumaLocal.get(i).setText("");
            sumaVisitante.get(i).setText("");


        }

    }

    private void inicializarListas() {

        listaCombosLocales = new ArrayList<ChoiceBox>() {{
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
        listaCombosVisitantes = new ArrayList<ChoiceBox>() {{
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

    public void imprimePartidasEnFicheroDeTexto(String nombreArchivo) {


        URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();
        File archivo = new File("/home/jag/"+nombreArchivo);
        try {

            if (archivo.createNewFile()){
                System.out.println("File is created!");


            }else{
                System.out.println("File already exists.");
            }


            PrintWriter writer = new PrintWriter(archivo);
        datosSubmit.forEach((k, v) -> {

            try {


                for (int i = 0; i < (v.size() - 1); i++) {
                    writer.print(v.get(i) + ",");     System.out.print(v.get(i)+",");
                }
                writer.print(v.get(v.size() - 1));

                writer.print("\n");



            } catch (Exception e) {

                e.printStackTrace();

            }



        });
            for (int i=0;i<(datosPartidaEquipo.size()-1);i++)
            {
                writer.print(datosPartidaEquipo.get(i) + ",");

            }
            writer.print(datosPartidaEquipo.get(datosPartidaEquipo.size() - 1));
            writer.close();
        }
        catch (Exception e) {

            e.printStackTrace();

        };


    }

    public boolean introducePartidasIndividualesEnBD(HashMap<Integer, ArrayList<String>> datos) {

        try {
            PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO PartidaIndividual (Enfrentamiento,JugadorSaque,JugadorSinSaque," +
                    "Victoria,ResultadoJugadorSaque,ResultadoJugadorSinSaque) VALUES (?,?,?,?,?,?)");

            for (ArrayList<String> arraylist : datos.values()) {

                sentencia.setInt(1, Integer.parseInt(arraylist.get(0)));
                sentencia.setInt(2, Integer.parseInt(arraylist.get(1)));
                sentencia.setInt(3, Integer.parseInt(arraylist.get(2)));
                sentencia.setInt(4, Integer.parseInt(arraylist.get(3)));
                sentencia.setInt(5, Integer.parseInt(arraylist.get(4)));
                sentencia.setInt(6, Integer.parseInt(arraylist.get(5)));

                sentencia.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public boolean introducePartidasEquiposEnBD(ArrayList<String> datos) {

        try {
            PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO PartidaEquipos (Enfrentamiento," +
                    "Victoria,ResultadoEquipoLocal,ResultadoEquipoVisitante) VALUES (?,?,?,?)");


            sentencia.setInt(1, Integer.parseInt(datos.get(0)));
            sentencia.setInt(2, Integer.parseInt(datos.get(1)));
            sentencia.setInt(3, Integer.parseInt(datos.get(2)));
            sentencia.setInt(4, Integer.parseInt(datos.get(3)));

            sentencia.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public boolean actualizaPuntuacionEnfrentamiento() {


        try {

            PreparedStatement sentencia = conexion.prepareStatement("Update Enfrentamiento set ResultadoLocal=?,ResultadoVisitante=? where idEnfrentamiento=?");

            sentencia.setInt(1, Integer.parseInt(sumaLocal19.getText()));
            sentencia.setInt(2, Integer.parseInt(sumaVisitante19.getText()));
            sentencia.setInt(3, numEnfrentamientoSeleccionado);

            sentencia.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public boolean borraPartidasEnfrentamiento() {


        try {

            PreparedStatement sentencia1 = conexion.prepareStatement("Delete from PartidaIndividual where Enfrentamiento=?");
            PreparedStatement sentencia2 = conexion.prepareStatement("Delete from PartidaEquipos where Enfrentamiento=?");

            sentencia1.setInt(1, numEnfrentamientoSeleccionado);
            sentencia2.setInt(1, numEnfrentamientoSeleccionado);

            sentencia1.executeUpdate();
            sentencia2.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public class MiChangeListener<String> implements ChangeListener<String> {

        private ChoiceBox combo;
        private ArrayList<String> j;

        MiChangeListener(ChoiceBox combo, ArrayList<String> lista) {

            this.combo = combo;
            j = new ArrayList<>(lista);
          //  System.out.println("constructor: " + j);

        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {


           // System.out.println("Nuevo elemento: " + newValue);
           // System.out.println(j);


            int indiceElementoSeleccionado = j.indexOf(newValue);
            //int indiceElementoSeleccionado = j.indexOf(newValue);

            if (indiceElementoSeleccionado!=-1) {
                if (!checkBoxCambios.isSelected()) {


                    int numChoiceBox;
                    if (combo.getId().length() == 2) {

                        numChoiceBox = Integer.parseInt(combo.getId().substring(1, 2));

                    } else {

                        numChoiceBox = Integer.parseInt(combo.getId().substring(1, 3));

                    }
                    numChoiceBox--;
                    //System.out.println("Choicebox numero: " + numChoiceBox);
                    if (combo.getId().substring(0, 1).equals("l")) {


                        int sw = numChoiceBox % 3;
                        switch (sw) {

                            case 0:
                                for (int i = 0; i < 18; i = i + 3) {

                                    if (i > numChoiceBox) {
                                        listaCombosLocales.get(i).getSelectionModel().select(indiceElementoSeleccionado);
                                        listaCombosLocales.get(i).setDisable(true);
                                    }

                                }

                                break;
                            case 1:
                                for (int i = 1; i < 18; i = i + 3) {

                                    if (i > numChoiceBox) {
                                        listaCombosLocales.get(i).getSelectionModel().select(indiceElementoSeleccionado);
                                        listaCombosLocales.get(i).setDisable(true);
                                    }

                                }
                                break;
                            case 2:
                                for (int i = 2; i < 18; i = i + 3) {

                                    if (i > numChoiceBox) {
                                        listaCombosLocales.get(i).getSelectionModel().select(indiceElementoSeleccionado);
                                        listaCombosLocales.get(i).setDisable(true);
                                    }
                                }
                                break;
                        }


                    } else {


                        switch (numChoiceBox) {

                            case 0:

                                listaCombosVisitantes.get(0).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(5).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(5).setDisable(true);
                                listaCombosVisitantes.get(7).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(7).setDisable(true);
                                listaCombosVisitantes.get(9).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(9).setDisable(true);
                                listaCombosVisitantes.get(14).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(14).setDisable(true);
                                listaCombosVisitantes.get(16).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(16).setDisable(true);


                                break;
                            case 1:

                                listaCombosVisitantes.get(1).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(3).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(3).setDisable(true);
                                listaCombosVisitantes.get(8).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(8).setDisable(true);
                                listaCombosVisitantes.get(10).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(10).setDisable(true);
                                listaCombosVisitantes.get(12).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(12).setDisable(true);
                                listaCombosVisitantes.get(17).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(17).setDisable(true);


                                break;
                            case 2:

                                listaCombosVisitantes.get(2).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(4).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(4).setDisable(true);
                                listaCombosVisitantes.get(6).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(6).setDisable(true);
                                listaCombosVisitantes.get(11).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(11).setDisable(true);
                                listaCombosVisitantes.get(13).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(13).setDisable(true);
                                listaCombosVisitantes.get(15).getSelectionModel().select(indiceElementoSeleccionado);
                                listaCombosVisitantes.get(15).setDisable(true);


                                break;
                        }


                    }
                } else {
                    //checkbox cambios selected
                }


            }
        }
    }

    public class MiChangeListenerDigitos<Integer> implements ChangeListener<Integer> {

        private int numTextField;
        private boolean local = false;


        MiChangeListenerDigitos(int numTextField, boolean local) {

            this.numTextField = numTextField;
            this.local = local;
        }

        @Override
        public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
            java.lang.Integer suma = 0;
            if (local) {

                if (Main.listaNumeros.contains(puntosLocal.get(numTextField).getText())) {

                    puntosLocal.get(numTextField).setStyle("-fx-control-inner-background: green");
                    for (int i = 0; i < 19; i++) {
                        if (puntosLocal.get(i).getText() != null && !puntosLocal.get(i).getText().isEmpty()) {
                            suma += java.lang.Integer.parseInt(puntosLocal.get(i).getText());

                            sumaLocal.get(i).setText(suma.toString());

                        }

                    }


                } else {

                    puntosLocal.get(numTextField).setStyle("-fx-control-inner-background: red");

                }
            } else {               //visitante

                if (Main.listaNumeros.contains(puntosVisitante.get(numTextField).getText())) {

                    puntosVisitante.get(numTextField).setStyle("-fx-control-inner-background: green");
                    for (int i = 0; i < 19; i++) {
                        if (puntosVisitante.get(i).getText() != null && !puntosVisitante.get(i).getText().isEmpty()) {
                            suma += java.lang.Integer.parseInt(puntosVisitante.get(i).getText());

                            sumaVisitante.get(i).setText(suma.toString());
                        }


                    }


                } else {

                    puntosVisitante.get(numTextField).setStyle("-fx-control-inner-background: red");

                }


            }

            for (int i = 0; i < 18; i++) {

                if (puntosLocal.get(i).getText() != null && !puntosLocal.get(i).getText().isEmpty() &&
                        puntosVisitante.get(i).getText() != null && !puntosVisitante.get(i).getText().isEmpty()) {

                    if ((!puntosLocal.get(i).getText().equals("10") && !puntosVisitante.get(i).getText().equals("10")) ||
                            ((java.lang.Integer.parseInt(puntosLocal.get(i).getText())) + (java.lang.Integer.parseInt(puntosVisitante.get(i).getText()))) > 17 ||
                            ((java.lang.Integer.parseInt(puntosLocal.get(i).getText())) + (java.lang.Integer.parseInt(puntosVisitante.get(i).getText()))) < 10 ||
                            listaCombosLocales.get(i).getSelectionModel().getSelectedIndex() == -1 ||
                            listaCombosVisitantes.get(i).getSelectionModel().getSelectedIndex() == -1) {

                        puntosLocal.get(i).setStyle("-fx-control-inner-background: red");
                        puntosVisitante.get(i).setStyle("-fx-control-inner-background: red");


                    } else {

                        datosIndividualesSubmit = new ArrayList<String>();
                        puntosLocal.get(i).setStyle("-fx-control-inner-background: green");
                        puntosVisitante.get(i).setStyle("-fx-control-inner-background: green");

                        //Enfrentamiento -->[0]
                        datosIndividualesSubmit.add(((java.lang.Integer) numEnfrentamientoSeleccionado).toString());

                        //Bloques 1,3,5
                        if ((i < 3) || ((i > 5) && (i < 9)) || ((i > 11) && (i < 15))) {
                            //JugadorSaque   -->[1]
                            datosIndividualesSubmit.add((mapaEnfrentamientos.get(numEnfrentamientoSeleccionado).
                                    getIdJugador(listaCombosVisitantes.get(i).getSelectionModel().getSelectedItem().toString())).toString());
                            //JugadorSinSaque-->[2]
                            datosIndividualesSubmit.add((mapaEnfrentamientos.get(numEnfrentamientoSeleccionado).
                                    getIdJugador(listaCombosLocales.get(i).getSelectionModel().getSelectedItem().toString())).toString());

                            if (puntosLocal.get(i).getText().equals("10")) {
                                datosIndividualesSubmit.add("0");
                            } else {
                                datosIndividualesSubmit.add("1");
                            }
                            datosIndividualesSubmit.add(puntosVisitante.get(i).getText());
                            //ResultadoJugadorSinSaque-->[4]
                            datosIndividualesSubmit.add(puntosLocal.get(i).getText());
                        }
                        //Bloques 2,4,6
                        else {
                            //JugadorSaque   -->[1]
                            datosIndividualesSubmit.add((mapaEnfrentamientos.get(numEnfrentamientoSeleccionado).
                                    getIdJugador(listaCombosLocales.get(i).getSelectionModel().getSelectedItem().toString())).toString());
                            //JugadorSinSaque-->[2]
                            datosIndividualesSubmit.add((mapaEnfrentamientos.get(numEnfrentamientoSeleccionado).
                                    getIdJugador(listaCombosVisitantes.get(i).getSelectionModel().getSelectedItem().toString())).toString());
                            if (puntosLocal.get(i).getText().equals("10")) {
                                datosIndividualesSubmit.add("0");
                            } else {
                                datosIndividualesSubmit.add("1");
                            }
                            datosIndividualesSubmit.add(puntosLocal.get(i).getText());
                            //ResultadoJugadorSinSaque-->[4]
                            datosIndividualesSubmit.add(puntosVisitante.get(i).getText());

                        }

                        //Victoria-->[3]:0=Local 1=Visitante

                        //ResultadoJugadorSaque-->[4]


                        datosSubmit.remove(i);
                        datosSubmit.put(i, datosIndividualesSubmit);


                    }

                }

            }
            if (puntosLocal.get(18).getText() != null && !puntosLocal.get(18).getText().isEmpty() &&
                    puntosVisitante.get(18).getText() != null && !puntosVisitante.get(18).getText().isEmpty()) {

                if ((!puntosLocal.get(18).getText().equals("10") && !puntosVisitante.get(18).getText().equals("10")) ||
                        ((java.lang.Integer.parseInt(puntosLocal.get(18).getText())) + (java.lang.Integer.parseInt(puntosVisitante.get(18).getText()))) > 17 ||
                        ((java.lang.Integer.parseInt(puntosLocal.get(18).getText())) + (java.lang.Integer.parseInt(puntosVisitante.get(18).getText()))) < 10) {
                    puntosLocal.get(18).setStyle("-fx-control-inner-background: red");
                    puntosVisitante.get(18).setStyle("-fx-control-inner-background: red");


                } else {


                    puntosLocal.get(18).setStyle("-fx-control-inner-background: green");
                    puntosVisitante.get(18).setStyle("-fx-control-inner-background: green");

                    datosPartidaEquipo = new ArrayList<String>();
                    datosPartidaEquipo.add(((java.lang.Integer) numEnfrentamientoSeleccionado).toString());
                    if (puntosLocal.get(18).getText().equals("10")) {
                        datosPartidaEquipo.add("0");
                    } else {
                        datosPartidaEquipo.add("1");
                    }
                    datosPartidaEquipo.add(puntosLocal.get(18).getText());
                    datosPartidaEquipo.add(puntosVisitante.get(18).getText());


                }
            }


            int i = 0;
            botonSubmit.setDisable(true);
            while (((i < 19) && puntosLocal.get(i).getStyle().equals("-fx-control-inner-background: green")) &&
                    (puntosVisitante.get(i).getStyle().equals("-fx-control-inner-background: green"))) {

                if (i == 18) {
                    botonSubmit.setDisable(false);
                }

                i++;
            }
        }


    }


}