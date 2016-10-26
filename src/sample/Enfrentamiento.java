package sample;

import java.util.List;
import java.util.Map;

/**
 * Created by jag on 27/10/16.
 */
public class Enfrentamiento {

    int jornada;
    int idEquipoLocal;
    int idEquipoVisitante;
    String nomEquipoLocal;
    String nomEquipoVisitante;
    Map<Integer,String> jLocales;
    Map<Integer,String> jVisitante;

    public Enfrentamiento(int j){

        jornada=j;
    }


}
