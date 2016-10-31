package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.util.*;


public class Controller implements Initializable {

    @FXML
    private ChoiceBox<String> jornada;
    @FXML
    private ChoiceBox<String> equiposEnfrentamiento;
    @FXML
    private CheckBox checkBoxCambios;
    @FXML
    private ChoiceBox<String> l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12,l13,l14,l15,l16,l17,l18;
    @FXML
    private ChoiceBox<String> v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15,v16,v17,v18;
    @FXML
    private TextField puntosLocal1,puntosLocal2,puntosLocal3,puntosLocal4,puntosLocal5,puntosLocal6,puntosLocal7,
            puntosLocal8,puntosLocal9,puntosLocal10,puntosLocal11,puntosLocal12,puntosLocal13,puntosLocal14,
            puntosLocal15,puntosLocal16,puntosLocal17,puntosLocal18;
    @FXML
    private TextField sumaLocal1,sumaLocal2,sumaLocal3,sumaLocal4,sumaLocal5,sumaLocal6,sumaLocal7,sumaLocal8,
            sumaLocal9,sumaLocal10,sumaLocal11,sumaLocal12,sumaLocal13,sumaLocal14,
            sumaLocal15,sumaLocal16,sumaLocal17,sumaLocal18;

    @FXML
    private TextField puntosVisitante1,puntosVisitante2,puntosVisitante3,puntosVisitante4,puntosVisitante5,puntosVisitante6,
            puntosVisitante7,puntosVisitante8,puntosVisitante9,puntosVisitante10,puntosVisitante11,puntosVisitante12,
            puntosVisitante13, puntosVisitante14,puntosVisitante15,puntosVisitante16,puntosVisitante17,puntosVisitante18;

    @FXML
    private TextField sumaVisitante1,sumaVisitante2,sumaVisitante3,sumaVisitante4,sumaVisitante5,sumaVisitante6,sumaVisitante7,
            sumaVisitante8,sumaVisitante9,sumaVisitante10,sumaVisitante11,sumaVisitante12,sumaVisitante13,sumaVisitante14,
            sumaVisitante15,sumaVisitante16,sumaVisitante17,sumaVisitante18;





    private Connection conexion;
    private List<String> listaJornadas;
    private List<String> enfrentamientoJornadas;
    private Map<Integer, Enfrentamiento> mapaEnfrentamientos;
    private List<ChoiceBox> listaCombosLocales, listaCombosVisitantes;
    private List<TextField> puntosLocal,sumaLocal,puntosVisitante,sumaVisitante;
    private Enfrentamiento actual;

    private int numJorndaSeleccionada;
    private int numEnfrentamientoSeleccionado;


    @Override
    public void initialize(URL location, ResourceBundle resources) {



        assert jornada != null : "fx:id=\"jornada\" was not injected: check your FXML file 'enfrentamiento.fxml'.";
        assert equiposEnfrentamiento != null : "fx:id=\"equiposEnfrentamiento\" was not injected: check your FXML file 'enfrentamiento.fxml'.";

        mapaEnfrentamientos = new HashMap<Integer, Enfrentamiento>();

        inicializarListas();


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

              //  limpiaCombos();

                String t[];
                Integer id;
                t = newValue.split(":");
                id = Integer.parseInt(t[0].trim());
                t = t[1].split("-");
           //     limpiaCombos();

                //System.out.println(id+"   "+t[0].trim()+"     "+t[1].trim());

                mapaEnfrentamientos.put(id, new Enfrentamiento(id, numJorndaSeleccionada, t[0].trim(), t[1].trim()));
                numEnfrentamientoSeleccionado=id;
                //System.out.println(mapaEnfrentamientos);
                ArrayList<String> jl=new ArrayList<String>(mapaEnfrentamientos.get(id).getjLocales().values());
                ArrayList<String> jv=new ArrayList<String>(mapaEnfrentamientos.get(id).getjVisitante().values());
                //limpiaCombos();
                ObservableList<String> observableListjl= FXCollections.observableArrayList(jl);
                for (ChoiceBox<String> c: listaCombosLocales) {
                 //   c.getItems().clear();
                    c.setItems(observableListjl);
                    c.valueProperty().addListener(new MiChangeListener<String>(c,jl));
                }
                ObservableList<String> observableListjv= FXCollections.observableArrayList(jv);
                for (ChoiceBox<String> c: listaCombosVisitantes) {
                    c.getItems().clear();
                    c.setItems(observableListjv);
                }
                checkBoxCambios.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    public void changed(ObservableValue ov, Boolean old_val, Boolean new_val) {
                       if (old_val==false && new_val==true){
                           for (int i=0;i<18;i++){

                               listaCombosLocales.get(i).setDisable(false);
                               listaCombosVisitantes.get(i).setDisable(false);
                           }

                       }
                    }
                });
                //l1.getSelectionModel().select(2);
                //l1.valueProperty().addListener(new MiChangeListener<String>(l1,jl));
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

    private ObservableList<String> inicializaComboJLocales(int idEnfrentamiento){

            actual=mapaEnfrentamientos.get(idEnfrentamiento);
            actual.getjLocales();
            List<String> listaJlocal = new ArrayList<String>(actual.getjLocales().values());
            ObservableList<String> observableJugadoresLocales = FXCollections.observableList(listaJlocal);
        System.out.println(listaJlocal);
        return  observableJugadoresLocales;
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
    private void limpiaCombos(){
        System.out.println("Lipio");
        for(int i=0;i<18;i++){
            listaCombosLocales.get(i).getSelectionModel().clearSelection();
            listaCombosVisitantes.get(i).getSelectionModel().clearSelection();
        }

    }

    private void inicializarListas(){

        listaCombosLocales=new ArrayList<ChoiceBox>(){{add(l1);add(l2);add(l3);add(l4);add(l5);add(l6);add(l7);add(l8);
            add(l9);add(l10);add(l11);add(l12);add(l13);add(l14);add(l15);add(l16);add(l17);add(l18);}};
        listaCombosVisitantes=new ArrayList<ChoiceBox>(){{add(v1);add(v2);add(v3);add(v4);add(v5);add(v6);add(v7);
            add(v8);add(v9);add(v10);add(v11);add(v12);add(v13);add(v14);add(v15);add(v16);add(v17);add(v18);}};
        puntosLocal=new ArrayList<TextField>(){{add(puntosLocal1);add(puntosLocal2);add(puntosLocal3);add(puntosLocal4);
            add(puntosLocal5);add(puntosLocal6);add(puntosLocal7);add(puntosLocal8);add(puntosLocal9);
            add(puntosLocal10);add(puntosLocal11);add(puntosLocal12);add(puntosLocal13);add(puntosLocal14);
            add(puntosLocal15);add(puntosLocal16);add(puntosLocal17);add(puntosLocal18);}};
        puntosVisitante=new ArrayList<TextField>(){{add(puntosVisitante1);add(puntosVisitante2);add(puntosVisitante3);
            add(puntosVisitante4);add(puntosVisitante5);add(puntosVisitante6);add(puntosVisitante7);
            add(puntosVisitante8);add(puntosVisitante9);add(puntosVisitante10);add(puntosVisitante11);
            add(puntosVisitante12);add(puntosVisitante13);add(puntosVisitante14);add(puntosVisitante15);
            add(puntosVisitante16);add(puntosVisitante17);add(puntosVisitante18);}};
        sumaLocal=new ArrayList<TextField>(){{add(sumaLocal1);add(sumaLocal2);add(sumaLocal3);add(sumaLocal4);
            add(sumaLocal5);add(sumaLocal6);add(sumaLocal7);add(sumaLocal8);add(sumaLocal9);add(sumaLocal10);
            add(sumaLocal11);add(sumaLocal12);add(sumaLocal13);add(sumaLocal14);add(sumaLocal15);add(sumaLocal16);
            add(sumaLocal17);add(sumaLocal18);}};
        sumaVisitante=new ArrayList<TextField>(){{add(sumaVisitante1);add(sumaVisitante2);add(sumaVisitante3);
            add(sumaVisitante4);add(sumaVisitante5);add(sumaVisitante6);add(sumaVisitante7);add(sumaVisitante8);
            add(sumaVisitante9);add(sumaVisitante10);add(sumaVisitante11);add(sumaVisitante12);add(sumaVisitante13);
            add(sumaVisitante14);add(sumaVisitante15);add(sumaVisitante16);add(sumaVisitante17);add(sumaVisitante18);}};
    }



    public class MiChangeListener<String> implements ChangeListener<String>{

        private  ChoiceBox combo ;
        private  ArrayList<String> j;
        MiChangeListener(ChoiceBox combo, ArrayList<String> lista) {
            this.combo = combo ;
            j=new ArrayList<>(lista);
            System.out.println(j);

        }

        @Override
        public void changed (ObservableValue < ? extends String > observable, String oldValue, String newValue){

            System.out.println("New Value-->"+newValue);
            System.out.println("Old Value-->"+oldValue);
            int indiceElementoSeleccionado=j.indexOf(newValue);
            if (!checkBoxCambios.isSelected()) {
                if (combo.getId().substring(0, 1).equals("l")) {


                    //Integer numChoiceBox=Integer.parseInt(combo.getId().replaceAll("\\D+",""));
                    int numChoiceBox;
                    if (combo.getId().length() == 2) {

                        numChoiceBox = Integer.parseInt(combo.getId().substring(1, 2));

                    } else {

                        numChoiceBox = Integer.parseInt(combo.getId().substring(1, 3));

                    }
                    numChoiceBox--;
                    int sw = numChoiceBox % 3;
                    switch (sw) {

                        case 0:
                            for (int i = 0; i < 18; i = i + 3) {
                      //          System.out.println("Elemento seleccionado-->"+indiceElementoSeleccionado);
                                listaCombosLocales.get(i).getSelectionModel().select(indiceElementoSeleccionado);
                                if (i > numChoiceBox) {
                                    listaCombosLocales.get(i).setDisable(true);
                                }
                        //        System.out.println("Indice seleccionado"+listaCombosLocales.get(i).getSelectionModel().getSelectedIndex());

                            }

                            break;
                        case 1:
                            for (int i = 1; i < 18; i = i + 3) {
                                listaCombosLocales.get(i).getSelectionModel().select(indiceElementoSeleccionado);
                                if (i > numChoiceBox) {
                                    listaCombosLocales.get(i).setDisable(true);
                                }

                            }
                            break;
                        case 2:
                            for (int i = 2; i < 18; i = i + 3) {
                                listaCombosLocales.get(i).getSelectionModel().select(indiceElementoSeleccionado);
                                if (i > numChoiceBox) {
                                    listaCombosLocales.get(i).setDisable(true);
                                }
                            }
                            break;
                    }


                } else {


                }
            }
            else{
                //checkbox cambios selected
            }




        }
    }



}