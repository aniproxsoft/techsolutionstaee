/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEstadias;

import ConnectionDB.ConnectionDB;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vacantes.CarreraVO;
import vacantes.ConocimientoVO;
import vacantes.HabilidadVO;
import vacantes.NivelAcademicoVO;
import vacantes.PerfilVO;

/**
 *
 * @author ANIPROXTOART
 */
public class BuscaEstadiasDAOImplements implements BuscaEstadiasDAO {

    ConnectionDB conexion;

    @Override
    public List<VacanteVO> searchVacantes(Integer perfil, String jsonHabilidades, String jsonConociminetos) {
        List<VacanteVO> vacantes = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_vacantes_estadia(?,?,?)}");
            query.setInt(1, perfil);
            query.setString(2, jsonConociminetos);
            query.setString(3, jsonHabilidades);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            VacanteVO vacante = null;
            while (respuesta.next()) {
                vacante = new VacanteVO();
                vacante.setId_vacante(respuesta.getInt(1));
                vacante.setTitulo(respuesta.getString(2));
                vacante.setVacante_desc(respuesta.getString(3));
                if (vacante.getVacante_desc().length() > 40) {
                    Integer num = 0;
                    num = vacante.getVacante_desc().length() / 2;
                    String s = vacante.getVacante_desc().substring(0, vacante.getVacante_desc().length() - num);
                    vacante.setVacante_descCorta(s);
                } else {
                    vacante.setVacante_descCorta(vacante.getVacante_desc());
                }
                vacante.setNombre_nivel(respuesta.getString(4));
                vacante.setCarrera_desc(respuesta.getString(5));
                vacante.setId_perfil(respuesta.getInt(6));
                vacante.setNombre_perfil(respuesta.getString(7));
                vacante.setEdad_min(respuesta.getInt(8));
                vacante.setEdad_max(respuesta.getInt(9));
                vacante.setSalario_min(respuesta.getDouble(10));
                vacante.setSalario_max(respuesta.getDouble(11));
                if(vacante.getSalario_max()>0.0 || vacante.getSalario_min()>0.0){
                    vacante.setRenderSalario(true);
                }
                vacante.setHora_inicial(respuesta.getString(12));
                vacante.setHora_final(respuesta.getString(13));
                vacante.setExperiencia(respuesta.getString(14));
                vacante.setId_empresa(respuesta.getInt(15));
                vacante.setNombre(respuesta.getString(16));
                vacante.setStatus(respuesta.getString(17));
                vacante.setDireccion(respuesta.getString(18));
                vacante.setConocimientos(getConocimientoDetail(vacante.getId_vacante()));
                vacante.setHabilidades(getHabilidadesDetail(vacante.getId_vacante()));
                vacante.setNum_telefono(respuesta.getString(19));
                vacante.setCorreo_empresa(respuesta.getString(20));
                vacantes.add(vacante);

            }
            return vacantes;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public List<NivelAcademicoVO> getNiveles() {
        List<NivelAcademicoVO> niveles = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_listas_estadias('n',0)}");
            query.execute();
            ResultSet respuesta = query.getResultSet();
            NivelAcademicoVO nivel = null;
            while (respuesta.next()) {
                nivel = new NivelAcademicoVO();
                nivel.setId_nivel(respuesta.getInt(1));
                nivel.setNombre_nivel(respuesta.getString(2));
                niveles.add(nivel);

            }
            return niveles;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public List<CarreraVO> getCarreras(Integer clave) {
        List<CarreraVO> carreras = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_listas_estadias('ca',?)}");
            query.setInt(1, clave);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            CarreraVO carrera = null;
            while (respuesta.next()) {
                carrera = new CarreraVO();
                carrera.setId_carrera(respuesta.getInt(1));
                carrera.setCarrera_desc(respuesta.getString(2));
                carreras.add(carrera);

            }
            return carreras;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public List<PerfilVO> getPerfiles(Integer clave) {
        List<PerfilVO> perfiles = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_listas_estadias('p',?)}");
            query.setInt(1, clave);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            PerfilVO perfil = null;
            while (respuesta.next()) {
                perfil = new PerfilVO();
                perfil.setId_perfil(respuesta.getInt(1));
                perfil.setNombre_perfil(respuesta.getString(2));
                perfiles.add(perfil);

            }
            return perfiles;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<HabilidadVO> getHabilidades() {
        List<HabilidadVO> habilidades = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_listas_estadias('h',0)}");
            query.execute();
            ResultSet respuesta = query.getResultSet();
            HabilidadVO habilidad = null;
            while (respuesta.next()) {
                habilidad = new HabilidadVO();
                habilidad.setId_habilidad(respuesta.getInt(1));
                habilidad.setHabilidad_desc(respuesta.getString(2));
                habilidades.add(habilidad);

            }
            return habilidades;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<ConocimientoVO> getConocimientos(Integer clave) {
        List<ConocimientoVO> conocimientos = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_listas_estadias('co',?)}");
            query.setInt(1, clave);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            ConocimientoVO conocimiento = null;
            while (respuesta.next()) {
                conocimiento = new ConocimientoVO();
                conocimiento.setId_conocimiento(respuesta.getInt(1));
                conocimiento.setConoc_desc(respuesta.getString(2));

                conocimientos.add(conocimiento);

            }
            return conocimientos;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<ConocimientoVO> getConocimientoDetail(Integer claveVacante) {
        List<ConocimientoVO> conocimientos = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_conco_habil(?,?)}");
            query.setInt(1, claveVacante);
            query.setInt(2, 1);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            ConocimientoVO conocimiento = null;
            while (respuesta.next()) {
                conocimiento = new ConocimientoVO();
                conocimiento.setId_conocimiento(respuesta.getInt(2));
                conocimiento.setConoc_desc(respuesta.getString(3));

                conocimientos.add(conocimiento);

            }
            return conocimientos;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<HabilidadVO> getHabilidadesDetail(Integer claveVacante) {
         List<HabilidadVO> habilidades = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_conco_habil(?,?)}");
            query.setInt(1, claveVacante);
            query.setInt(2, 2);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            HabilidadVO habilidad = null;
            while (respuesta.next()) {
                habilidad = new HabilidadVO();
                habilidad.setId_habilidad(respuesta.getInt(2));
                habilidad.setHabilidad_desc(respuesta.getString(3));
                habilidades.add(habilidad);

            }
            return habilidades;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
