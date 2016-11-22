package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jag on 8/11/16.
 */



public class ControllerEstadisticas  implements Initializable {


    @FXML
    private TextArea texto;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        estadisticasClaseConsulta claseConsulta=new estadisticasClaseConsulta();

        texto.appendText("\n\n            Positivos Por Jugador \n\n");
        texto.appendText(claseConsulta.imprimePositivosJugadores());

        texto.appendText("\n\n            Tacadas Por Jugador \n\n");
        texto.appendText(claseConsulta.imprimeTacadasPorJugador());

        texto.appendText("\n\n            Series Por Jugador \n\n");
        texto.appendText(claseConsulta.imprimeSeriesPorJugador());

        texto.appendText("\n\n            Porcentaje partidas ganadas con el saque \n\n");
        texto.appendText(claseConsulta.porcentajeGanadasConSaquePorJugador());

        texto.appendText("\n\n            Positivos entre los equipos de la misma categoria \n\n");
        texto.appendText(claseConsulta.imprimePositivosJugadoresMismaCategoria());

        texto.appendText("\n\n            Porcentaje sobre las bolas totales a favor del jugador \n\n");
        texto.appendText(claseConsulta.porcentajeSobreTotalBolasAFavor());

        texto.appendText("\n\n            Porcentaje sobre las bolas totales en contra del jugador \n\n");
        texto.appendText(claseConsulta.porcentajeSobreTotalBolasEnContra());

        texto.appendText("\n\n            Partidas en positivo por jugador \n\n");
        texto.appendText(claseConsulta.enfrentamientosEnPositivoPorJugador());

        texto.appendText("\n\n            Clasificacion Parcial \n\n");
        texto.appendText(claseConsulta.imprimeClasificacion());

        texto.appendText("\n\n            Enfentamientos Jugados \n\n");
        texto.appendText(claseConsulta.enfrentamientosJugadasEntreJornadas(1,22));

        texto.appendText("\n\n            Enfentamientos No Jugados \n\n");
        texto.appendText(claseConsulta.enfrentamientosNoJugadasEntreJornadas(1,7));













    }

}
