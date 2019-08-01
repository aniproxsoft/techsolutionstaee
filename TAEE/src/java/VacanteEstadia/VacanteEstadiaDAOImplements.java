/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VacanteEstadia;

import ConnectionDB.ConnectionDB;
import SearchEstadias.BuscaEstadiasDAOImplements;
import SearchEstadias.VacanteVO;
import empresa.EmpresaVO;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vacantes.ConocimientoVO;
import vacantes.HabilidadVO;

/**
 *
 * @author ferna
 */
public class VacanteEstadiaDAOImplements implements VacanteEstadiaDAO {

    ConnectionDB conexion;

    @Override
    public List<VacanteVO> getVacantesEmpresario() {
        List<VacanteVO> vacantes = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_vacantes_adminEstadia()}");
            query.execute();
            ResultSet respuesta = query.getResultSet();
            VacanteVO vacante = null;
            while (respuesta.next()) {
                vacante = new VacanteVO();
                vacante.setId_vacante(respuesta.getInt("id_vacante"));
                vacante.setTitulo(respuesta.getString("titulo"));
                vacante.setVacante_desc(respuesta.getString("vacante_desc"));
                vacante.setId_perfil(respuesta.getInt("id_perfil"));

                vacante.setEdad_min(respuesta.getInt("edad_min"));
                vacante.setEdad_max(respuesta.getInt("edad_max"));
                if (vacante.getEdad_max() == 0) {
                    vacante.setEdad_max(null);
                }
                if (vacante.getEdad_min() == 0) {
                    vacante.setEdad_min(null);
                }
                vacante.setSalario_min(respuesta.getDouble("salario_min"));

                vacante.setSalario_max(respuesta.getDouble("salario_max"));
                if (vacante.getSalario_min() == 0) {
                    vacante.setSalario_max(null);
                }
                if (vacante.getSalario_min() == 0) {
                    vacante.setSalario_min(null);
                }
                vacante.setHora_inicial(respuesta.getString("hora_inicial"));
                vacante.setHora_final(respuesta.getString("hora_final"));
                vacante.setExperiencia(respuesta.getString("experiencia"));
                vacante.setId_empresa(respuesta.getInt("id_empresa"));
                vacante.setAyuda_economica(respuesta.getBoolean("ayuda_economica"));
                vacante.setConocimientos(getConocimientoDetail(vacante.getId_vacante()));
                vacante.setHabilidades(getHabilidadesDetail(vacante.getId_vacante()));
                vacante.setNombre_nivel(respuesta.getString("nombre_nivel"));
                vacante.setNombre_perfil(respuesta.getString("nombre_perfil"));
                vacante.setCarrera_desc(respuesta.getString("carrera_desc"));
                vacante.setId_nivel(respuesta.getInt("id_nivel"));
                vacante.setId_carrera(respuesta.getInt("id_carrera"));
                vacante.setNombre(respuesta.getString("nombre"));
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
    public boolean insertUpdateVcanteEmpresario(String json_vacante, String json_conocimiento, String json_habilidades, int opcion) {
        boolean flag = false;
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_insert_update_vacante_egresados(?,?,?,?)}");
            query.setString(1, json_vacante);
            query.setString(2, json_conocimiento);
            query.setString(3, json_habilidades);
            query.setInt(4, opcion);
            query.execute();
            ResultSet respuesta = query.getResultSet();

            while (respuesta.next()) {

                if (respuesta.getBoolean("flag")) {
                    flag = true;
                } else {
                    flag = false;
                }

            }
            return flag;
        } catch (IOException ex) {
            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();

            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            ex.printStackTrace();

            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();

            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            ex.printStackTrace();

            Logger.getLogger(BuscaEstadiasDAOImplements.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean deleteVacanteEmpresario(int vacante_id) {
        boolean flag = false;
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_delete_vacante_estadia(?)}");
            query.setInt(1, vacante_id);

            query.execute();
            ResultSet respuesta = query.getResultSet();

            while (respuesta.next()) {

                if (respuesta.getBoolean("flag")) {
                    flag = true;
                } else {
                    flag = false;
                }

            }
            return flag;
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
        return false;

    }

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

    @Override
    public List<VacanteVO> searchEmresasEgresados(Integer id_empresa) {
        List<VacanteVO> vacantes = new ArrayList<>();
        Connection conn = null;
        try {

            conexion = new ConnectionDB();
            conn = conexion.conectarBD();
            CallableStatement query = conn.prepareCall("{call sp_get_search_adminEstadia(?)}");
            query.setInt(1, id_empresa);
            query.execute();
            ResultSet respuesta = query.getResultSet();
            VacanteVO vacante = null;
            while (respuesta.next()) {
                vacante = new VacanteVO();
                vacante.setId_vacante(respuesta.getInt("id_vacante"));
                vacante.setTitulo(respuesta.getString("titulo"));
                vacante.setVacante_desc(respuesta.getString("vacante_desc"));
                vacante.setId_perfil(respuesta.getInt("id_perfil"));

                vacante.setEdad_min(respuesta.getInt("edad_min"));
                vacante.setEdad_max(respuesta.getInt("edad_max"));
                if (vacante.getEdad_max() == 0) {
                    vacante.setEdad_max(null);
                }
                if (vacante.getEdad_min() == 0) {
                    vacante.setEdad_min(null);
                }
                vacante.setSalario_min(respuesta.getDouble("salario_min"));

                vacante.setSalario_max(respuesta.getDouble("salario_max"));
                if (vacante.getSalario_min() == 0) {
                    vacante.setSalario_max(null);
                }
                if (vacante.getSalario_min() == 0) {
                    vacante.setSalario_min(null);
                }
                vacante.setHora_inicial(respuesta.getString("hora_inicial"));
                vacante.setHora_final(respuesta.getString("hora_final"));
                vacante.setExperiencia(respuesta.getString("experiencia"));
                vacante.setId_empresa(respuesta.getInt("id_empresa"));
                vacante.setAyuda_economica(respuesta.getBoolean("ayuda_economica"));
                vacante.setConocimientos(getConocimientoDetail(vacante.getId_vacante()));
                vacante.setHabilidades(getHabilidadesDetail(vacante.getId_vacante()));
                vacante.setNombre_nivel(respuesta.getString("nombre_nivel"));
                vacante.setNombre_perfil(respuesta.getString("nombre_perfil"));
                vacante.setCarrera_desc(respuesta.getString("carrera_desc"));
                vacante.setId_nivel(respuesta.getInt("id_nivel"));
                vacante.setId_carrera(respuesta.getInt("id_carrera"));
                vacante.setNombre(respuesta.getString("nombre"));
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
}
