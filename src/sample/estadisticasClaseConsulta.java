package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import com.sun.javafx.css.CalculatedValue;


/**
 * Created by jag on 22/11/16.
 */
public class estadisticasClaseConsulta {



        private Connection conexion;


     estadisticasClaseConsulta() {
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

        public void insertCalendarData() {

            try {

                PreparedStatement preparedStmt = conexion
                        .prepareStatement("INSERT INTO AgpoolTeamData.Temporada (idTemporada, Descripcion) VALUES (?,?)");
                LocalDate calendario = LocalDate.of(2010, 1, 1);
                while (calendario.isBefore(LocalDate.of(2020, 12, 31))) {

                    // rs=s.executeUpdate("INSERT INTO swimming.time
                    // (year,month,day,summer,winter,holiday) VALUES
                    // ("+calendario.getYear()+","+calendario.getMonthValue()+","+calendario.getDayOfMonth()+",0,0,1)");
                    preparedStmt.setInt(1, calendario.getYear());
                    preparedStmt.setString(2, calendario.getYear() + "-" + ((calendario.getYear()) + 1));
                    preparedStmt.execute();
                    System.out.println(
                            calendario.getYear() + "  " + calendario.getYear() + "-" + ((calendario.getYear()) + 1));
                    calendario = calendario.plusYears(1);
                }
                // s = conexion.createStatement();
                // rs= s.executeQuery ("insert");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void insertaJugadores(String NombreArchivo) {

            String fichero = NombreArchivo;
            try {
                FileReader fr = new FileReader(fichero);
                BufferedReader br = new BufferedReader(fr);

                Statement s = conexion.createStatement();
                ;
                ResultSet rs;
                String linea;
                PreparedStatement preparedStmt = conexion.prepareStatement(
                        "INSERT INTO AgpoolTeamData.Jugador(Nombre,Apellidos,Mote,CategoriaIndividual,Equipo) VALUES (?,?,?,?,?)");
                while ((linea = br.readLine()) != null) {
                    String[] partes = linea.split(",");

                    preparedStmt.setString(1, partes[0].trim());
                    preparedStmt.setString(2, partes[1].trim());
                    preparedStmt.setString(3, partes[2].trim());
                    preparedStmt.setInt(4, Integer.parseInt(partes[3].trim()));
                    rs = s.executeQuery("SELECT idEquipo FROM Equipo WHERE Nombre='" + partes[4] + "'");
                    if (rs.next()) {
                        preparedStmt.setInt(5, rs.getInt(1));
                        System.out.println("Existe el equipo");
                        preparedStmt.execute();
                    }

                }
                fr.close();
            } catch (Exception e) {
                System.out.println("Excepcion leyendo fichero " + fichero + ": " + e);
            }
        }

        public void insertaEquipos(String NombreArchivo) {

            String fichero = NombreArchivo;
            try {
                FileReader fr = new FileReader(fichero);
                BufferedReader br = new BufferedReader(fr);

                String linea;
                PreparedStatement preparedStmt = conexion
                        .prepareStatement("INSERT INTO AgpoolTeamData.Equipo(Nombre,Categoria,Temporada) VALUES (?,?,?)");
                while ((linea = br.readLine()) != null) {
                    String[] partes = linea.split(",");

                    preparedStmt.setString(1, partes[0].trim());
                    preparedStmt.setInt(2, Integer.parseInt(partes[1].trim()));
                    preparedStmt.setInt(3, 2016);
                    preparedStmt.execute();

                }
                fr.close();
            } catch (Exception e) {
                System.out.println("Excepcion leyendo fichero " + fichero + ": " + e);
            }
        }

        public void insertaEnfrentamientos(String NombreArchivo) {

            String fichero = NombreArchivo;
            try {
                FileReader fr = new FileReader(fichero);
                BufferedReader br = new BufferedReader(fr);

                Statement s = conexion.createStatement();
                ;
                ResultSet rs;
                String linea;
                PreparedStatement preparedStmt = conexion.prepareStatement(
                        "INSERT INTO AgpoolTeamData.Enfrentamiento(EquipoLocal,EquipoVisitante,Fecha,Jornada,ResultadoLocal,ResultadoVisitante) VALUES (?,?,?,?,?,?)");
                while ((linea = br.readLine()) != null) {
                    String[] partes = linea.split(",");

                    preparedStmt.setInt(6, Integer.parseInt(partes[5].trim()));
                    preparedStmt.setInt(5, Integer.parseInt(partes[4].trim()));
                    preparedStmt.setInt(4, Integer.parseInt(partes[3].trim()));
                    preparedStmt.setString(3, partes[2].trim());

                    rs = s.executeQuery("SELECT idEquipo FROM Equipo WHERE Nombre='" + partes[1] + "'");
                    if (rs.next()) {
                        preparedStmt.setInt(2, rs.getInt(1));
                    }
                    rs = s.executeQuery("SELECT idEquipo FROM Equipo WHERE Nombre='" + partes[0] + "'");
                    if (rs.next()) {
                        preparedStmt.setInt(1, rs.getInt(1));
                    }

                    preparedStmt.execute();
                }
                fr.close();
            } catch (Exception e) {
                System.out.println("Excepcion leyendo fichero " + fichero + ": " + e);
            }
        }
        String temp;
        public String imprimePositivosJugadores() {
            temp="";
            try {

                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();

                array = mapaIdNombreJugadores();

                PreparedStatement preparedStmt = conexion
                        .prepareStatement("SELECT((SELECT sum(ResultadoJugadorSinSaque-ResultadoJugadorSaque)"
                                + " as Positivos1 FROM AgpoolTeamData.PartidaIndividual where JugadorSinSaque=?) +(SELECT "
                                + "sum(ResultadoJugadorSaque-ResultadoJugadorSinSaque) as Positivos2 FROM AgpoolTeamData.PartidaIndividual"
                                + " where JugadorSaque=?)) as Positivos");

                SortedMap<Integer,ArrayList<String>> ordenado = new TreeMap<Integer,ArrayList<String>>();
                array.forEach((k, v) -> {
                    ArrayList<String> temp=new ArrayList<String>();
                    try {
                        preparedStmt.setInt(1, k);
                        preparedStmt.setInt(2, k);
                        ResultSet rs2 = preparedStmt.executeQuery();
                        if (rs2.next()) {
                            v.add(((Integer) rs2.getInt("Positivos")).toString());
                            if (ordenado.containsKey(rs2.getInt("Positivos")))
                            {
                                temp=ordenado.get(rs2.getInt("Positivos"));

                            }

                            temp.add(v.get(0)+" "+v.get(1));
                            ordenado.put(rs2.getInt("Positivos"),temp);
                        }
                        //System.out.println(v);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });
                ordenado.forEach((k, v) -> {
                    //System.out.println(k+"="+v);
                    temp+=k;
                    temp+="=";
                    temp+=v.toString();
                    temp+="\n";

                });



            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // preparedStmt.setInt(1,1);
            // preparedStmt.setInt(2,1);
            return temp;
        }

        public String imprimeTacadasPorJugador() {
            temp="";
            try {
                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();

                array = mapaIdNombreJugadores();
                PreparedStatement preparedStmt = conexion
                        .prepareStatement("SELECT count(*) as Tacadas FROM PartidaIndividual where JugadorSaque=?"
                                + " and ResultadoJugadorSaque=10 and ResultadoJugadorSinSaque<=2");

                array.forEach((k, v) -> {
                    try {
                        preparedStmt.setInt(1, k);
                        ResultSet rs2 = preparedStmt.executeQuery();
                        if (rs2.next()) {
                            v.add(((Integer) rs2.getInt("Tacadas")).toString());
                        }
                        //System.out.println(v);
                        temp+=v.toString()+"\n";
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return temp;
        }

        public String imprimeSeriesPorJugador() {

            temp="";
            try {
                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();

                array = mapaIdNombreJugadores();
                PreparedStatement preparedStmt = conexion
                        .prepareStatement("SELECT count(*) as Series FROM PartidaIndividual where JugadorSinSaque=?"
                                + " and ResultadoJugadorSinSaque=10 and ResultadoJugadorSaque<=2");

                array.forEach((k, v) -> {
                    try {
                        preparedStmt.setInt(1, k);
                        ResultSet rs2 = preparedStmt.executeQuery();
                        if (rs2.next()) {
                            v.add(((Integer) rs2.getInt("Series")).toString());
                        }
                        //System.out.println(v);
                        temp+=v.toString()+"\n";
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return temp;
        }

        public String porcentajeGanadasConSaquePorJugador() {

            temp="";
            try {

                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();

                array = mapaIdNombreJugadores();
                PreparedStatement preparedStmtTotales = conexion
                        .prepareStatement("SELECT count(*) as Totales FROM PartidaIndividual where JugadorSaque=?");
                PreparedStatement preparedStmtGanadas = conexion
                        .prepareStatement("SELECT count(*) as Ganadas FROM PartidaIndividual where JugadorSaque=?"
                                + " and ResultadoJugadorSaque>ResultadoJugadorSinSaque");

                array.forEach((k, v) -> {
                    try {
                        preparedStmtTotales.setInt(1, k);
                        preparedStmtGanadas.setInt(1, k);
                        ResultSet rs2 = preparedStmtTotales.executeQuery();
                        if (rs2.next()) {
                            v.add(((Integer) rs2.getInt("Totales")).toString());

                        }
                        ResultSet rs3 = preparedStmtGanadas.executeQuery();
                        if (rs3.next()) {
                            v.add(((Integer) rs3.getInt("Ganadas")).toString());
                        }
                        Float porcentajeVictorias = (Float.parseFloat(v.get(3)) / (Float.parseFloat(v.get(2))));
                        v.add(porcentajeVictorias.toString());
                       // System.out.println(v);
                        temp+=v.toString()+"\n";

                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return temp;
        }

        public Map<Integer, ArrayList<String>> mapaIdNombreJugadores() {

            Statement s;
            ResultSet rs;
            try {
                s = conexion.createStatement();

                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();
                ArrayList<String> temp;

                rs = s.executeQuery("SELECT idJugador,Nombre, Apellidos FROM Jugador");
                while (rs.next()) {
                    temp = new ArrayList<String>();
                    temp.add(rs.getString("Nombre"));
                    temp.add(rs.getString("Apellidos"));

                    array.put(rs.getInt("idJugador"), temp);

                }
                return array;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

        }

        public String enfrentamientosJugadasEntreJornadas(int JornadaInicio,int JornadaFin){


            temp="";
            PreparedStatement preparedStmtEnfrentamientosJugados;
            ResultSet rs;
            Map<Integer, ArrayList<String>> array = mapaIdNombreEquipos(2016);

            try {
                preparedStmtEnfrentamientosJugados = conexion.prepareStatement("SELECT EquipoLocal,EquipoVisitante,ResultadoLocal,ResultadoVisitante,Jornada "
                        + " FROM Enfrentamiento where Jornada>=? and Jornada<=? and ResultadoLocal!=0 or ResultadoVisitante!=0");
                preparedStmtEnfrentamientosJugados.setInt(1, JornadaInicio);
                preparedStmtEnfrentamientosJugados.setInt(2, JornadaFin);

                rs=preparedStmtEnfrentamientosJugados.executeQuery();
                while (rs.next()) {
                    String local,visitante;

                    local=array.get(rs.getInt("EquipoLocal")).get(0);
                    visitante=array.get(rs.getInt("EquipoVisitante")).get(0);
                    //System.out.println("Jornada "+rs.getInt("Jornada")+" "+local+":"+rs.getInt("ResultadoLocal")+"--"+visitante+":"+rs.getInt("ResultadoVisitante") );
                    temp+="Jornada "+rs.getInt("Jornada")+" "+local+":"+rs.getInt("ResultadoLocal")+"--"+visitante+":"+rs.getInt("ResultadoVisitante")+"\n";
                }




            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        return temp;
        }
        public String enfrentamientosNoJugadasEntreJornadas(int JornadaInicio,int JornadaFin){

            temp="";
            PreparedStatement preparedStmtEnfrentamientosJugados;
            ResultSet rs;
            Map<Integer, ArrayList<String>> array = mapaIdNombreEquipos(2016);

            try {
                preparedStmtEnfrentamientosJugados = conexion.prepareStatement("SELECT EquipoLocal,EquipoVisitante,ResultadoLocal,ResultadoVisitante,Jornada "
                        + " FROM Enfrentamiento where Jornada>=? and Jornada<=? and ResultadoLocal=0 and ResultadoVisitante=0");
                preparedStmtEnfrentamientosJugados.setInt(1, JornadaInicio);
                preparedStmtEnfrentamientosJugados.setInt(2, JornadaFin);

                rs=preparedStmtEnfrentamientosJugados.executeQuery();
                while (rs.next()) {
                    String local,visitante;

                    local=array.get(rs.getInt("EquipoLocal")).get(0);
                    visitante=array.get(rs.getInt("EquipoVisitante")).get(0);
                    //System.out.println("Jornada "+rs.getInt("Jornada")+" "+local+":"+rs.getInt("ResultadoLocal")+"--"+visitante+":"+rs.getInt("ResultadoVisitante") );
                    temp+="Jornada "+rs.getInt("Jornada")+" "+local+":"+rs.getInt("ResultadoLocal")+"--"+visitante+":"+rs.getInt("ResultadoVisitante")+"\n";
                }




            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        return temp;

        }

        public Map<Integer, ArrayList<String>> mapaIdNombreEquipos(int temporada){
            Statement s;
            ResultSet rs;
            try {
                s = conexion.createStatement();

                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();
                ArrayList<String> temp;

                rs = s.executeQuery("SELECT idEquipo,Nombre,Categoria FROM Equipo where Temporada="+temporada);
                while (rs.next()) {
                    temp = new ArrayList<String>();
                    temp.add(rs.getString("Nombre"));
                    temp.add(rs.getString("Categoria"));

                    array.put(rs.getInt("idEquipo"), temp);

                }
                return array;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }




        }

        public String imprimeClasificacion(){

            temp="";
            PreparedStatement s;
            ResultSet rs;
            Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();
            ArrayList<ArrayList<String>> clasificacion=new ArrayList<ArrayList<String>>();

            array=mapaIdNombreEquipos(2016);

            array.forEach((k, v) -> {
                Integer jugadas,ganadas,perdidas,empatadas,nopresentadas,bolasFavor,bolasContra;
                ArrayList<String> temp=v;

                jugadas=partidosJugadosEquipo(k);
                ganadas=partidosGanadosEquipo(k);
                perdidas=partidosPerdidosEquipo(k);
                empatadas=partidosEmpatadosEquipo(k);
                nopresentadas=partidosNoPresentadosEquipo(k);
                bolasFavor=bolasFavor(k);
                bolasContra=bolasContra(k);
                temp.add(jugadas.toString());
                temp.add(ganadas.toString());
                temp.add(perdidas.toString());
                temp.add(empatadas.toString());
                temp.add(nopresentadas.toString());
                temp.add(bolasFavor.toString());
                temp.add(bolasContra.toString());
                temp.add(((Integer)(((ganadas*3)+(empatadas*2)+(perdidas))-nopresentadas)).toString());

                clasificacion.add(temp);
                //System.out.println("Nombre,Categoria,Jugadas,Ganadas,Perdidas,Empatadas,NoPresentadas,BolasFavor,BolasContra,Puntos");
                //System.out.println(temp);
            });

            // Sorting


            Collections.sort(clasificacion, new Comparator<ArrayList<String>>() {
                @Override
                public int compare(ArrayList<String> uno, ArrayList<String> dos)
                {

                    return  Integer.parseInt(uno.get(9))-Integer.parseInt(dos.get(9));
                }
            });

            for (int i=clasificacion.size()-1;i>=0;i--) {
                //System.out.println(arrayList);
                //System.out.println(clasificacion.get(i));
                temp+=clasificacion.get(i)+"\n";
            }

        return temp;
        }

        public int bolasFavor(int idEquipo){

            PreparedStatement s;
            ResultSet rs;

            try {
                s = conexion.prepareStatement("Select ((SELECT sum(ResultadoLocal) as suma1 from "
                        + "Enfrentamiento where EquipoLocal=?)+(SELECT sum(ResultadoVisitante) as sum2 from "
                        + "Enfrentamiento where EquipoVisitante=?)) as sumaTotal");

                s.setInt(1, idEquipo);
                s.setInt(2, idEquipo);
                rs=s.executeQuery();
                rs.next();
                return rs.getInt("sumaTotal");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 0;
            }


        }
        public int bolasContra(int idEquipo){

            PreparedStatement s;
            ResultSet rs;

            try {
                s = conexion.prepareStatement("Select ((SELECT sum(ResultadoVisitante) as suma1 from "
                        + "Enfrentamiento where EquipoLocal=?)+(SELECT sum(ResultadoLocal) as sum2 from "
                        + "Enfrentamiento where EquipoVisitante=?)) as sumaTotal");

                s.setInt(1, idEquipo);
                s.setInt(2, idEquipo);
                rs=s.executeQuery();
                rs.next();
                return rs.getInt("sumaTotal");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 0;
            }


        }

        public int partidosJugadosEquipo(int idEquipo){

            PreparedStatement s;
            ResultSet rs;

            try {
                s = conexion.prepareStatement("Select count(*) as jugados from Enfrentamiento where (EquipoLocal=? and (ResultadoLocal!=0 or ResultadoVisitante=-1)) or (EquipoVisitante=? and (ResultadoVisitante!=0 or ResultadoLocal=-1))");

                s.setInt(1, idEquipo);
                s.setInt(2, idEquipo);
                rs=s.executeQuery();
                rs.next();
                return rs.getInt("jugados");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 0;
            }


        }

        public int partidosGanadosEquipo(int idEquipo){

            PreparedStatement s;
            ResultSet rs;

            try {
                s = conexion.prepareStatement("SELECT count(*) as ganados FROM AgpoolTeamData.Enfrentamiento where "
                        + "(EquipoLocal=? and ResultadoLocal>ResultadoVisitante) or "
                        + "(EquipoVisitante=? and ResultadoLocal<ResultadoVisitante)");

                s.setInt(1, idEquipo);
                s.setInt(2, idEquipo);
                rs=s.executeQuery();
                rs.next();
                return rs.getInt("ganados");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 0;
            }


        }

        public int partidosPerdidosEquipo(int idEquipo){

            PreparedStatement s;
            ResultSet rs;

            try {
                s = conexion.prepareStatement("SELECT count(*) as perdidos FROM AgpoolTeamData.Enfrentamiento where "
                        + "(EquipoLocal=? and ResultadoLocal<ResultadoVisitante and ResultadoLocal!=-1) or "
                        + "(EquipoVisitante=? and ResultadoLocal>ResultadoVisitante and ResultadoVisitante!=-1)");

                s.setInt(1, idEquipo);
                s.setInt(2, idEquipo);
                rs=s.executeQuery();
                rs.next();
                return rs.getInt("perdidos");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 0;
            }


        }

        public int partidosEmpatadosEquipo(int idEquipo){

            PreparedStatement s;
            ResultSet rs;

            try {
                s = conexion.prepareStatement("SELECT count(*) as empatados FROM AgpoolTeamData.Enfrentamiento where (EquipoLocal=? or EquipoVisitante=?)"
                        + " and ResultadoLocal=ResultadoVisitante and ResultadoLocal!=0");

                s.setInt(1, idEquipo);
                s.setInt(2, idEquipo);
                rs=s.executeQuery();
                rs.next();
                return rs.getInt("empatados");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 0;
            }


        }

        public int partidosNoPresentadosEquipo(int idEquipo){

            PreparedStatement s;
            ResultSet rs;

            try {
                s = conexion.prepareStatement("SELECT count(*) as nopresentados FROM AgpoolTeamData.Enfrentamiento where "
                        + "(EquipoLocal=? and ResultadoLocal=-1) or "
                        + "(EquipoVisitante=? and ResultadoVisitante=-1)");

                s.setInt(1, idEquipo);
                s.setInt(2, idEquipo);
                rs=s.executeQuery();
                rs.next();
                return rs.getInt("nopresentados");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 0;
            }


        }

        public String imprimePositivosJugadoresMismaCategoria() {

            temp="";
            try {

                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();

                array = mapaIdNombreJugadores();

                PreparedStatement preparedStmt = conexion.prepareStatement(
                        "SELECT(("
                                + "SELECT sum(ResultadoJugadorSinSaque-ResultadoJugadorSaque) as Positivos1 "
                                + "FROM AgpoolTeamData.PartidaIndividual,Enfrentamiento,Equipo s1,Equipo s2 where JugadorSinSaque=?"
                                + " and Enfrentamiento.idEnfrentamiento=PartidaIndividual.Enfrentamiento"
                                + " and Enfrentamiento.EquipoLocal=s1.idEquipo and Enfrentamiento.EquipoVisitante=s2.idEquipo"
                                + " and s1.Categoria=s2.Categoria) +"
                                + "(SELECT sum(ResultadoJugadorSaque-ResultadoJugadorSinSaque) as Positivos2 "
                                + "FROM AgpoolTeamData.PartidaIndividual,Enfrentamiento,Equipo s1,Equipo s2 where JugadorSaque=?"
                                + " and Enfrentamiento.idEnfrentamiento=PartidaIndividual.Enfrentamiento"
                                + " and Enfrentamiento.EquipoLocal=s1.idEquipo and Enfrentamiento.EquipoVisitante=s2.idEquipo"
                                + " and s1.Categoria=s2.Categoria)) as Positivos");

                SortedMap<Integer, ArrayList<String>> ordenado = new TreeMap<Integer, ArrayList<String>>();
                array.forEach((k, v) -> {
                    ArrayList<String> temp = new ArrayList<String>();
                    try {
                        preparedStmt.setInt(1, k);
                        preparedStmt.setInt(2, k);
                        ResultSet rs2 = preparedStmt.executeQuery();
                        if (rs2.next()) {
                            v.add(((Integer) rs2.getInt("Positivos")).toString());
                            if (ordenado.containsKey(rs2.getInt("Positivos"))) {
                                temp = ordenado.get(rs2.getInt("Positivos"));

                            }

                            temp.add(v.get(0) + " " + v.get(1));
                            ordenado.put(rs2.getInt("Positivos"), temp);
                        }
                        // System.out.println(v);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });
                ordenado.forEach((k, v) -> {
                    //System.out.println(k + "=" + v);
                    temp+=k;
                    temp+="=";
                    temp+=v.toString();
                    temp+="\n";
                });

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // preparedStmt.setInt(1,1);
            // preparedStmt.setInt(2,1);
            return temp;
        }

        public String porcentajeSobreTotalBolasAFavor(){

            temp="";
            try{
                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();

                array = mapaIdNombreJugadores();

                PreparedStatement preparedStmt = conexion
                        .prepareStatement("SELECT((SELECT sum(ResultadoJugadorSinSaque)"
                                + " as Positivos1 FROM AgpoolTeamData.PartidaIndividual where JugadorSinSaque=?) +(SELECT "
                                + "sum(ResultadoJugadorSaque) as Positivos2 FROM AgpoolTeamData.PartidaIndividual"
                                + " where JugadorSaque=?)) as Positivos");



                SortedMap<Integer,ArrayList<String>> ordenado = new TreeMap<Integer,ArrayList<String>>();
                array.forEach((k, v) -> {
                    ArrayList<String> temp=new ArrayList<String>();
                    try {
                        preparedStmt.setInt(1, k);
                        preparedStmt.setInt(2, k);
                        ResultSet rs2 = preparedStmt.executeQuery();
                        if (rs2.next()) {
                            v.add(((Integer) rs2.getInt("Positivos")).toString());


                            Float calculo= ((float)rs2.getInt("Positivos")/(float)bolasFavor(IdJugadorIdEquipo(k)))*100;


                            if (ordenado.containsKey(Math.round(calculo)))
                            {
                                temp=ordenado.get(Math.round(calculo));

                            }

                            temp.add(v.get(0)+" "+v.get(1));
                            //temp.add(((Integer)IdJugadorIdEquipo(k)).toString());
                            temp.add(calculo.toString());
                            ordenado.put(Math.round(calculo),temp);

                        }
                        //System.out.println(v);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });
                ordenado.forEach((k, v) -> {
                    //System.out.println(k+"="+v);
                    temp+=k;
                    temp+="=";
                    temp+=v.toString();
                    temp+="\n";
                });



            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // preparedStmt.setInt(1,1);
            // preparedStmt.setInt(2,1);
        return temp;
        }

        public String porcentajeSobreTotalBolasEnContra(){

            temp="";
            try{
                Map<Integer, ArrayList<String>> array = new HashMap<Integer, ArrayList<String>>();

                array = mapaIdNombreJugadores();

                PreparedStatement preparedStmt = conexion
                        .prepareStatement("SELECT((SELECT sum(ResultadoJugadorSaque)"
                                + " as Positivos1 FROM AgpoolTeamData.PartidaIndividual where JugadorSinSaque=?) +(SELECT "
                                + "sum(ResultadoJugadorSinSaque) as Positivos2 FROM AgpoolTeamData.PartidaIndividual"
                                + " where JugadorSaque=?)) as Positivos");



                SortedMap<Integer,ArrayList<String>> ordenado = new TreeMap<Integer,ArrayList<String>>();
                array.forEach((k, v) -> {
                    ArrayList<String> temp=new ArrayList<String>();
                    try {
                        preparedStmt.setInt(1, k);
                        preparedStmt.setInt(2, k);
                        ResultSet rs2 = preparedStmt.executeQuery();
                        if (rs2.next()) {
                            v.add(((Integer) rs2.getInt("Positivos")).toString());


                            Float calculo= ((float)rs2.getInt("Positivos")/(float)bolasContra(IdJugadorIdEquipo(k)))*100;


                            if (ordenado.containsKey(Math.round(calculo)))
                            {
                                temp=ordenado.get(Math.round(calculo));

                            }

                            temp.add(v.get(0)+" "+v.get(1));
                            //temp.add(((Integer)IdJugadorIdEquipo(k)).toString());
                            temp.add(calculo.toString());
                            ordenado.put(Math.round(calculo),temp);

                        }
                        //System.out.println(v);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });
                ordenado.forEach((k, v) -> {
                    //System.out.println(k+"="+v);
                    temp+=k;
                    temp+="=";
                    temp+=v.toString();
                    temp+="\n";
                });



            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // preparedStmt.setInt(1,1);
            // preparedStmt.setInt(2,1)
            return temp;
        }

        public int IdJugadorIdEquipo(int idJugador){

            try {
                PreparedStatement preparedStmt2 = conexion
                        .prepareStatement("select Equipo from Jugador where idJugador=?");
                preparedStmt2.setInt(1, idJugador);
                ResultSet rsId=preparedStmt2.executeQuery();
                if (rsId.next()) {

                    return rsId.getInt("Equipo");
                }


            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return -1;
        }

        Map<Integer, ArrayList<String>> array;
        SortedMap<Integer,ArrayList<String>> ordenadoPartidasPositivo = new TreeMap<Integer,ArrayList<String>>();
        public String enfrentamientosEnPositivoPorJugador() {

            temp="";
            try {
                array = new HashMap<Integer, ArrayList<String>>();

                array = mapaIdNombreJugadores();


                PreparedStatement preparedStmt = conexion.prepareStatement(
                        "Select idEnfrentamiento from Enfrentamiento where (ResultadoLocal!=0 or ResultadoVisitante=-1) "
                                + "or (EquipoVisitante=1 and (ResultadoVisitante!=0 or ResultadoLocal=-1))");

                ResultSet rs2 = preparedStmt.executeQuery();

                while (rs2.next()) {


                    HashMap<Integer,Integer> nuevo=calculaPuntosEnfrentamiento(rs2.getInt("idEnfrentamiento"));

                    //Ver positivos y negativos por jugador y enfrentamiento
                    //System.out.println(nuevo);
                    nuevo.forEach((k,v) -> {

                        if (array.get(k).size()==2){
                            //no existe la puntuacion dentro del arraylist.Añadimos el campo por primera vez
                            if (v>0){
                                array.get(k).add("1");

                            }
                            else {

                                array.get(k).add("0");
                            }

                        }
                        else{

                            Integer temp;
                            temp=Integer.parseInt(array.get(k).get(2));
                            if (v>0){

                                temp++;

                            }
				/*		else if(v<0){

							temp--;
						}
						*/
                            temp=Integer.parseInt(array.get(k).set(2, temp.toString()));
                        }


                    });

                }




                array.forEach((k,v) -> {
                    //ordenadoPartidasPositivo = new TreeMap<Integer,ArrayList<String>>();
                    ArrayList<String> temporalInterno;
                    //System.out.println(k+" "+v);ç
                    temp+=k;
                    temp+="=";
                    temp+=v.toString();
                    temp+="\n";

                    if (v.size()>2){
                        temporalInterno=ordenadoPartidasPositivo.get(Integer.parseInt(v.get(2)));
                        if (temporalInterno==null){

                            temporalInterno= new ArrayList<>();
                        }
                        temporalInterno.add(v.get(0)+" "+v.get(1));
                        ordenadoPartidasPositivo.put(Integer.parseInt(v.get(2)), temporalInterno);
                    }

                });

                System.out.println("***************Ordenados***********");
                ordenadoPartidasPositivo.forEach((k,v) -> {
                    System.out.println(k+""+v);

                });
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // preparedStmt.setInt(1,1);
            // preparedStmt.setInt(2,1);
        return temp;
        }

        public HashMap<Integer, Integer> calculaPuntosEnfrentamiento(int idEnfrentamiento) {

            HashMap<Integer, Integer> array = new HashMap<Integer, Integer>();

            try {

                PreparedStatement preparedStmt = conexion.prepareStatement(
                        "Select JugadorSaque,JugadorSinSaque,ResultadoJugadorSaque,ResultadoJugadorSinSaque"
                                + " from PartidaIndividual where Enfrentamiento=?;");

                // SortedMap<Integer,ArrayList<String>> ordenado = new
                // TreeMap<Integer,ArrayList<String>>();

                ArrayList<String> temp = new ArrayList<String>();

                preparedStmt.setInt(1, idEnfrentamiento);

                ResultSet rs2 = preparedStmt.executeQuery();
                while (rs2.next()) {
                    // v.add(((Integer) rs2.getInt("Positivos")).toString());

                    // Jugador Local
                    if (array.containsKey(rs2.getInt("JugadorSaque"))) {
                        array.put(rs2.getInt("JugadorSaque"), array.get(rs2.getInt("JugadorSaque"))
                                + (rs2.getInt("ResultadoJugadorSaque") - rs2.getInt("ResultadoJugadorSinSaque")));

                    } else {

                        array.put(rs2.getInt("JugadorSaque"),
                                (rs2.getInt("ResultadoJugadorSaque") - rs2.getInt("ResultadoJugadorSinSaque")));

                    }

                    // JugadorVisitante

                    if (array.containsKey(rs2.getInt("JugadorSinSaque"))) {
                        array.put(rs2.getInt("JugadorSinSaque"), array.get(rs2.getInt("JugadorSinSaque"))
                                + (rs2.getInt("ResultadoJugadorSinSaque") - rs2.getInt("ResultadoJugadorSaque")));

                    } else {

                        array.put(rs2.getInt("JugadorSinSaque"),
                                (rs2.getInt("ResultadoJugadorSinSaque") - rs2.getInt("ResultadoJugadorSaque")));
                    }

                    // temp.add(((Integer)IdJugadorIdEquipo(k)).toString());

                }

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // preparedStmt.setInt(1,1);
            // preparedStmt.setInt(2,1);

            return array;
        }







    }

