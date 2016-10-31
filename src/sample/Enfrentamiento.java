package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jag on 27/10/16.
 */
public class Enfrentamiento {

    int id;
    int jornada;
    int idEquipoLocal;
    int idEquipoVisitante;
    String nomEquipoLocal;
    String nomEquipoVisitante;
    Map<Integer,String> jLocales;
    Map<Integer,String> jVisitante;
    private Connection conexion;


    public Map<Integer, String> getjLocales() {
        return jLocales;
    }

    public Map<Integer, String> getjVisitante() {
        return jVisitante;
    }


    public Enfrentamiento(int id, int jornada, String EqLocal, String EqVisitante) {


        conectaBBDD();
        this.jornada=jornada;
        this.id=id;
        rellenaIdEquipos();
        nomEquipoLocal = EqLocal;
        nomEquipoVisitante=EqVisitante;
        rellenaJugadores();

        System.out.println(jLocales);
        System.out.println(jVisitante);
    }

    public void rellenaIdEquipos() {
        try {
            Statement s = conexion.createStatement();
            ResultSet rs = s.executeQuery("SELECT EquipoLocal,EquipoVisitante From Enfrentamiento where idEnfrentamiento=" + id);

            rs.next();
            idEquipoLocal = rs.getInt("EquipoLocal");
            System.out.println(idEquipoLocal);
            idEquipoVisitante = rs.getInt("EquipoVisitante");
            System.out.println(idEquipoVisitante);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }


    public void rellenaJugadores(){
        jLocales=new HashMap<Integer,String>();
        jVisitante=new HashMap<Integer,String>();

        try {
            Statement s = conexion.createStatement();
            ResultSet rs = s.executeQuery("SELECT idJugador,Nombre,Apellidos From Jugador where Equipo="+idEquipoLocal);
            while (rs.next()){
                jLocales.put(rs.getInt("idJugador"),rs.getString("Nombre")+" "+rs.getString("Apellidos"));

            }
            Statement s2 = conexion.createStatement();
            ResultSet rs2 = s2.executeQuery("SELECT idJugador,Nombre,Apellidos From Jugador where Equipo="+idEquipoVisitante);
            while (rs2.next()){
                jVisitante.put(rs2.getInt("idJugador"),rs2.getString("Nombre")+" "+rs2.getString("Apellidos"));

            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }
         private void conectaBBDD() {

             try {
                 Class.forName("com.mysql.jdbc.Driver");
                 System.out.println("Driver loaded!");
                 conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/AgpoolTeamData", "jag", "");

             } catch (SQLException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             } catch (ClassNotFoundException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
}
